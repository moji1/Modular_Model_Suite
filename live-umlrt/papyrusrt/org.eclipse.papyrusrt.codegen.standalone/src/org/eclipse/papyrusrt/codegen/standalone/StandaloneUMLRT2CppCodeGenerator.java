/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
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

/**
 * This class extends the {@link AbstractUMLRT2CppCodeGenerator} by providing a method that executes
 * only the UML-to-xtUMLRT transformaiton.
 * 
 * @author epp
 */
public final class StandaloneUMLRT2CppCodeGenerator extends AbstractUMLRT2CppCodeGenerator {

	/** Resolved output path. */
	private Path commonOutputPath;

	/**
	 * Default constructor. It is private as it does not support extension at this point.
	 *
	 */
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

	/**
	 * Generate the xtUML-RT model from the given UML(-RT) model.
	 * 
	 * @param model
	 *            - A UML {@link Model} with the UML-RT profile applied.
	 * @return The {@link IStatus} of the transformation.
	 */
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

	/**
	 * Obtain the output folder for the given element to be generated.
	 * 
	 * @param eobj
	 *            - An {@link EObject}.
	 * @return - The {@link File} for the output folder.
	 */
	public File getOutputFolder(EObject eobj) {
		return getOutputFolder(eobj, null);
	}

	/**
	 * Obtain the output folder for the given element to be generated.
	 * 
	 * @see org.eclipse.papyrusrt.codegen.cpp.AbstractUMLRT2CppCodeGenerator#getOutputFolder(org.eclipse.emf.ecore.EObject, org.eclipse.papyrusrt.codegen.cpp.XTUMLRT2CppCodeGenerator)
	 *
	 * @param eobj
	 *            - An {@link EObject}
	 * @param codeGen
	 *            - An instance of the {@link XTUMLRT2CppCodeGenerator}.
	 * @return - The {@link File} for the output folder.
	 */
	@Override
	public File getOutputFolder(EObject eobj, XTUMLRT2CppCodeGenerator codeGen) {
		if (commonOutputPath != null) {
			return commonOutputPath.toFile();
		}
		Path outputPath = StandaloneUMLRTCodeGenerator.getOutputPath();
		if (outputPath != null) {
			// If the project path was set either via the
			// "org.eclipse.papyrusrt.output-path" system
			// property, or through the "-o" command-line option, then use this
			// path to create CDT project.
			File project = outputPath.toFile();
			if (project.exists()) {
				try {
					FileVisitor<Path> visitor = new RecursiveFileRemover();
					Files.walkFileTree(outputPath, visitor);
				} catch (IOException e) {
					CodeGenPlugin.error("Failed to delete the previously generated project");
				}
			}
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

	/**
	 * Obtain the folder with the input model for the given element to be generated.
	 * 
	 * @see org.eclipse.papyrusrt.codegen.cpp.AbstractUMLRT2CppCodeGenerator#getModelFolder(org.eclipse.emf.ecore.EObject)
	 *
	 * @param context
	 *            - An {@link EObject}.
	 * @return - The {@link File} for the output folder.
	 */
	@Override
	protected File getModelFolder(EObject context) {
		return StandaloneUMLRTCodeGenerator.getModelPath().getParent().toFile();
	}

	/**
	 * A visitor that removes all files and directories of a given {@link Path}.
	 * 
	 * @author epp
	 */
	private static class RecursiveFileRemover implements FileVisitor<Path> {

		/**
		 * Default Constructor.
		 */
		RecursiveFileRemover() {
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.deleteIfExists(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			Files.deleteIfExists(dir);
			return FileVisitResult.CONTINUE;
		}

	}
}
