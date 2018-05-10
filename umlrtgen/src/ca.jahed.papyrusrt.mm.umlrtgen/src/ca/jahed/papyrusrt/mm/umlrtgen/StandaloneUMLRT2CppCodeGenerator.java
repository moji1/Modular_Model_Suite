package ca.jahed.papyrusrt.mm.umlrtgen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.AbstractUMLRT2CppCodeGenerator;
import org.eclipse.papyrusrt.codegen.cpp.StandaloneCppProjectGenerator;
import org.eclipse.papyrusrt.codegen.cpp.XTUMLRT2CppCodeGenerator;
import org.eclipse.papyrusrt.codegen.utils.ProjectUtils;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtModelTranslator;

import com.google.inject.Inject;

public final class StandaloneUMLRT2CppCodeGenerator extends AbstractUMLRT2CppCodeGenerator {

	private Path commonOutputPath;

	@Inject
	private StandaloneUMLRT2CppCodeGenerator() {
		super();
		setStandalone(true);
	}

	@Override
	public IStatus generate(List<EObject> elements, String top, boolean uml) {
		CodeGenPlugin.info("Executing standalone code generation");
		return super.generate(elements, top, uml);
	}

	public IStatus generateXTUMLRT(org.eclipse.uml2.uml.Model model) {
		File folder = getOutputFolder(model);
		MultiStatus status = new MultiStatus(CodeGenPlugin.ID, IStatus.INFO, "UML2-to-xtUMLrt Translator Invoked", null);

		long start = System.currentTimeMillis();
		if (model == null) {
			status.add(CodeGenPlugin.error("No input model given"));
		} else {
			try {
				List<EObject> elements = new ArrayList<>();
				elements.add(model);
				UML2xtumlrtModelTranslator translator = new UML2xtumlrtModelTranslator();
				status.addAll(translator.generate(elements, folder.toPath()));

				long writeStart = System.currentTimeMillis();
				if (translator.write()) {
					status.add(CodeGenPlugin
							.info("Updated generated files " + (System.currentTimeMillis() - writeStart) + "ms"));
				} else {
					status.add(CodeGenPlugin.error("Failed to write generated model to disk"));
				}
			} catch (Throwable t) {
				CodeGenPlugin.error("error during translation action", t);
				status.add(CodeGenPlugin.error(t));
			}
		}

		String message = "Translation " + (status.getSeverity() <= IStatus.INFO ? "complete" : "error")
				+ ", elapsed time " + (System.currentTimeMillis() - start) + " ms";

		CodeGenPlugin.info(message);
		MultiStatus result = new MultiStatus(CodeGenPlugin.ID, IStatus.INFO, message, null);
		result.addAll(status);
		return result;
	}

	public File getOutputFolder(EObject eobj) {
		return getOutputFolder(eobj, null);
	}

	@Override
	public File getOutputFolder(EObject eobj, XTUMLRT2CppCodeGenerator codeGen) {
		if (commonOutputPath != null) {
			return commonOutputPath.toFile();
		}
		Path outputPath = UMLRTGen.getOutputPath();
		if (outputPath != null) {
			File project = outputPath.toFile();
			if (!project.exists()) {
				resetResource(eobj, codeGen);

				if (!project.mkdirs()) {
					CodeGenPlugin.error("Failed to create project");
					return null;
				}
			}
			String projectName = project.getName();
			String projectPath = project.getParentFile().getPath();
			if (!new StandaloneCppProjectGenerator().generate(projectPath, projectName)) {
				CodeGenPlugin.error("Failed to create project");
				return null;
			}
			File src = new File(project, "src");
			if (!src.exists() && !src.mkdir()) {
				CodeGenPlugin.error("Failed to create source folder");
				return null;
			}

			commonOutputPath = src.toPath();
			return src;
		}
		// If outputPath is null, then we fallback to the default
		URI uri = eobj.eResource().getURI();

		String projectName = ProjectUtils.getProjectName(eobj);
		projectName = projectName + "_CDTProject";

		Path elementPath = Paths.get(uri.path());
		Path projectPath = elementPath.getParent().resolveSibling(projectName);
		if (!new StandaloneCppProjectGenerator().generate(projectPath.getParent().toString(), projectName)) {
			CodeGenPlugin.error("Failed to create project");
			return null;
		}
		File project = projectPath.toFile();
		File src = new File(project, "src");
		if (!src.exists() && !src.mkdir()) {
			CodeGenPlugin.error("Failed to create source folder");
			return null;
		}
		return src;
	}

	@Override
	protected File getModelFolder(EObject context) {
		return UMLRTGen.getModelPath().getParent().toFile();
	}
}
