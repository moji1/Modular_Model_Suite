package ca.jahed.papyrusrt.mm.umlrtgen;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.UMLRTCodeGenerator;
import org.eclipse.papyrusrt.codegen.config.CodeGenProvider;
import org.eclipse.papyrusrt.codegen.cpp.AbstractUMLRT2CppCodeGenerator;
import org.eclipse.papyrusrt.codegen.cpp.GeneratorManager;
import org.eclipse.papyrusrt.xtumlrt.external.PluginFinder;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;


public final class UMLRTGen {

	// System properties

	public static final String OUTPUT_PATH_PROPERTY = "org.eclipse.papyrusrt.output-path";

	public static final String PLUGINS_PATHS_PROPERTY = "org.eclipse.papyrusrt.plugins-paths";

	public static final String TOP_CAPSULE_PROPERTY = "org.eclipse.papyrusrt.top-capsule";

	// Default values

	public static final String DEFAULT_LOG_LEVEL = "OFF";

	public static final String DEFAULT_TOP = "Top";

	// Command-line options

	private static CommandLine clargs;

	private static final Options OPTIONS = new Options();

	private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

	private static final String CMDLINE_SYNTAX = "umlrtgen [-h] <path>";

	private static final String OPT_HELP = "h";

	private static final String OPT_QUIET = "q";

	private static final String OPT_LOG_LEVEL = "l";

	private static final String OPT_OUTPUT_PATH = "o";

	private static final String OPT_PLUGINS_PATHS = "p";

	private static final String OPT_TOP = "t";

	private static final String OPT_TO_XTUMLRT = "x";

	private static final String OPT_PRINT_STACK_TRACE = "s";

	private static final String OPT_DEV = "d";

	private static final String OPT_COMPONENT_NAMES = "c";

	private static ResourceSet resourceSet;

	private static UMLRTCodeGenerator generator;

	private static Path modelPath = null;

	private static Path outputPath = null;

	private static Path pluginsPath = null;

	private static String top = null;

	private static boolean printStackTrace = false;

	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	private UMLRTGen() {
	}

	public static void main(String[] args) {
		try {
			parseCmdLineArgs(args);
			if (!processCmdLineArgs()) {
				System.exit(1);
			}
			init();
			URI fullURI = URI.createFileURI(modelPath.toString());
			Model model = loadModel(fullURI);
			IStatus result = generate(model);
			if (!clargs.hasOption(OPT_QUIET)) {
				displayStatus(result);
			}
			System.exit(result.getSeverity() < IStatus.ERROR ? 0 : 1);
		} catch (ParseException e) {
			System.out.println("Invalid command-line arguments.");
			HELP_FORMATTER.printHelp(CMDLINE_SYNTAX, OPTIONS);
		} catch (InvalidPathException e) {
			System.out.println("Invalid path");
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IllegalArgumentException e) {
			System.out.println(e);
			e.printStackTrace();
		}

		System.exit(1);
	}

	public static Path getModelPath() {
		return modelPath;
	}

	public static Path getOutputPath() {
		return outputPath;
	}

	public static String getTop() {
		return top;
	}

	private static void parseCmdLineArgs(String[] args) throws ParseException {
		OPTIONS.addOption(OPT_HELP, "help", false, "Prints this message.");
		OPTIONS.addOption(OPT_QUIET, "quiet", false, "Inhibits printing messages during generation.");
		OPTIONS.addOption(OPT_LOG_LEVEL, "loglevel", true,
				"Set the level of logging (OFF, SEVERE, INFO, WARNING, CONFIG, FINE, FINER, FINEST). The default is OFF");
		OPTIONS.addOption(OPT_PRINT_STACK_TRACE, "prtrace", false, "Print the stack trace for exceptions");
		OPTIONS.addOption(OPT_OUTPUT_PATH, "outdir", true,
				"Specifies the output folder. By default it is 'gen' in the same folder as the input model.");
		OPTIONS.addOption(OPT_PLUGINS_PATHS, "plugins", true,
				"Specifies the plugins folders of the PapyrusRT installation.");
		OPTIONS.addOption(OPT_TOP, "top", true, "Specify the name of the top capsule. By default it is \"Top\"");
		OPTIONS.addOption(OPT_TO_XTUMLRT, "toxr", false,
				"Translate an input UML2 model into an xtUMLrt model instead of generating code.");
		OPTIONS.addOption(OPT_DEV, "dev", false,
				"Development mode: this is to be used only when invoking the generator from a development environment.");

		OPTIONS.addOption(OPT_COMPONENT_NAMES, "component", true,
				"Component to generate code for.");

		CommandLineParser parser = new BasicParser();
		clargs = parser.parse(OPTIONS, args);
		if (clargs.getArgList().isEmpty() && !clargs.hasOption(OPT_HELP)) {
			throw new ParseException("There must be at least one argument.");
		}
	}

	private static boolean processCmdLineArgs() throws InvalidPathException, FileNotFoundException {
		boolean result = false;
		if (clargs.hasOption(OPT_HELP)) {
			HELP_FORMATTER.printHelp(CMDLINE_SYNTAX, OPTIONS);
		} else {
			printStackTrace = clargs.hasOption(OPT_PRINT_STACK_TRACE);
			configureLogging();
			outputPath = validateOutputPath(clargs.getOptionValue(OPT_OUTPUT_PATH,
					System.getProperty(OUTPUT_PATH_PROPERTY)));
			modelPath = validateModelPath(clargs.getArgs()[0]);
			setPluginsPathPropertyDefault();
			pluginsPath = validatePluginsPaths(clargs.getOptionValue(OPT_PLUGINS_PATHS,
					System.getProperty(PLUGINS_PATHS_PROPERTY)));
			if (pluginsPath != null) {
				PluginFinder.addSearchPaths(pluginsPath.toString());
			}
			top = clargs.getOptionValue(OPT_TOP, System.getProperty(TOP_CAPSULE_PROPERTY, DEFAULT_TOP));
			result = true;

			String[] targetComponents = clargs.getOptionValues(OPT_COMPONENT_NAMES);

			StringBuilder strBuilder = new StringBuilder();
			if (targetComponents != null)
				for (int i = 0; i < targetComponents.length; i++) {
					strBuilder.append(targetComponents[i]);
					strBuilder.append(",");
				}

			String targetComponentsStr = strBuilder.toString();
			System.setProperty("target_components", targetComponentsStr);
		}
		return result;
	}

	private static Path validateOutputPath(String outputPath) throws InvalidPathException {
		Path path = null;
		if (outputPath != null) {
			path = Paths.get(outputPath);
		} else {
			path = Paths.get("gen");
		}
		return path.toAbsolutePath();
	}

	private static Path validateModelPath(String modelPathString) throws InvalidPathException, FileNotFoundException {
		Path modelPath = Paths.get(modelPathString);
		if (!Files.exists(modelPath)) {
			throw new FileNotFoundException("Model file '" + modelPathString + "' not found");
		}
		return modelPath.toAbsolutePath();
	}

	private static Path validatePluginsPaths(String pluginsPathString) throws InvalidPathException,
			FileNotFoundException {
		if (pluginsPathString == null)
			pluginsPathString = "plugins";

		Path modelPath = Paths.get(pluginsPathString);
		if (!Files.exists(modelPath)) {
			throw new FileNotFoundException("Plugin folder '" + pluginsPathString + "' not found");
		}

		return modelPath.toAbsolutePath();
	}

	private static void configureLogging() {
		// TODO: add a FileHandler if an appropriate option is provided. Also a
		// Formatter.
		Level level = Level.parse(clargs.getOptionValue(OPT_LOG_LEVEL, DEFAULT_LOG_LEVEL).toUpperCase());
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(level);
		CodeGenPlugin codegenPlugin = new CodeGenPlugin();
		CodeGenPlugin.getLogger().addHandler(consoleHandler);
		CodeGenPlugin.getLogger().setLevel(level);
		CodeGenPlugin.setStandalone(printStackTrace);
		XTUMLRTLogger xtumlrtLogger = new XTUMLRTLogger();
		XTUMLRTLogger.getLogger().addHandler(consoleHandler);
		XTUMLRTLogger.getLogger().setLevel(level);
		XTUMLRTLogger.setStandalone(printStackTrace);
	}

	private static void setPluginsPathPropertyDefault() {
		if (clargs.hasOption(OPT_PLUGINS_PATHS)) {
			return;
		}
		// We try to guess the default value for the plugins path by looking for
		// the folder that contains this class.
		if (System.getProperty(PLUGINS_PATHS_PROPERTY) == null) {
			Class<?> thisClass = UMLRTGen.class;
			URL url = thisClass.getResource("UMLRTGen.class");
			// If this class is not in a jar file, it should be in:
			// <plugins>/org.eclipse.papyrusrt.codegen.standalone/bin/org/eclipse/papyrusrt/codegen/standalone/
			// If it is in a jar file, then it should be in:
			// <plugins>/org.eclipse.papyrusrt.codegen.standalone.jar!/org/eclipse/papyrusrt/codegen/standalone/
			// But we'll continue to go up as many folders as necessary.
			Path thisClassPath = Paths.get(url.getPath());
			Path base = thisClassPath;
			String toLookFor = "plugins";
			if (clargs.hasOption(OPT_DEV)) {
				toLookFor = "org.eclipse.papyrus-rt";
			}
			while (base != null && !base.endsWith(toLookFor)) {
				base = base.getParent();
			}
			if (base != null) {
				System.setProperty(PLUGINS_PATHS_PROPERTY, base.toString());
			} else {
				CodeGenPlugin
						.warning(
								"plugins folder not found. The code generator might fail to find the RTS model library. "
										+ "If the model contains references to model library elements, generation can fail with errors. "
										+ "To avoid this warning, please provide the path to the plugins folder using the -p option.");
			}
		}
	}

	private static void updateClassPath() {
		String classpath = System.getProperty("java.class.path");
		String pluginsPathStr = pluginsPath.toString();
		if (pluginsPathStr != null && !("".equals(pluginsPathStr))) {
			classpath = pluginsPathStr + classpath;
			System.setProperty("java.class.path", classpath);
		}
	}

	private static void init() {
		logPaths();
		updateClassPath();
		resourceSet = new ResourceSetImpl();
		CodeGenProvider.getDefault().setModule(new StandaloneUMLRTCodeGenInjectionModule());
		generator = CodeGenProvider.getDefault().get();
		generator.setStandalone(true);
		if (generator instanceof AbstractUMLRT2CppCodeGenerator) {
			((AbstractUMLRT2CppCodeGenerator) generator).setupExternalPackageManagement(resourceSet);
		}
	}

	private static org.eclipse.uml2.uml.Package loadPackage(URI fullURI) {
		Resource resource = resourceSet.getResource(fullURI, true);
		EList<EObject> contents = resource.getContents();
		org.eclipse.uml2.uml.Package pkg = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(contents, UMLPackage.Literals.PACKAGE);
		// displayAllStereotypes( mdl, 0 );
		return pkg;
	}

	private static Model loadModel(URI fullURI) {
		return (Model) loadPackage(fullURI);
	}

	private static IStatus generate(Model model) {
		IStatus status = null;
		if (clargs.hasOption(OPT_TO_XTUMLRT)) {
			if (generator instanceof StandaloneUMLRT2CppCodeGenerator) {
				status = ((StandaloneUMLRT2CppCodeGenerator) generator).generateXTUMLRT(model);
			}
		} else {
			List<EObject> targets = new ArrayList<>();
			targets.add(model);
			GeneratorManager.setInstance(new StandaloneGeneratorManager());
			status = generator.generate(targets, top, true);
		}
		return status;
	}

	private static void displayStatus(IStatus status) {
		String result = "";
		switch (status.getSeverity()) {
		case IStatus.CANCEL:
			result = "Generation cancelled";
			break;
		case IStatus.ERROR:
			result = "Generation failed with an error";
			break;
		case IStatus.OK:
		case IStatus.INFO:
			result = "Generation successful";
			break;
		case IStatus.WARNING:
			result = "Generation successful but with warnings";
			break;
		default:
			result = "Unknown result status";
			break;
		}
		System.out.println(result);

		if (!clargs.hasOption(OPT_QUIET)) {
			displayAll(System.out, "", status);
		}
	}

	private static void displayAll(PrintStream out, String indent, IStatus status) {
		out.print(indent);
		out.println(status.getMessage());
		for (IStatus child : status.getChildren()) {
			displayAll(out, indent + "    ", child);
		}
	}

	private static void displayAllStereotypes(EObject eobj, int depth) {
		if (eobj instanceof Element) {
			Element el = (Element) eobj;
			CodeGenPlugin.info(indent(depth) + "* " + el);
			EList<Stereotype> as = el.getAppliedStereotypes();
			if (as.size() > 0) {
				CodeGenPlugin.info(indent(depth) + "  - Applied stereotypes:");
				for (Stereotype s : as) {
					CodeGenPlugin.info(indent(depth) + "      " + s);
				}
			}
			EList<EObject> sas = el.getStereotypeApplications();
			if (sas.size() > 0) {
				CodeGenPlugin.info(indent(depth) + "  - Stereotype applications:");
				for (EObject sa : sas) {
					CodeGenPlugin.info(indent(depth) + "      " + sa);
				}
			}
			EList<Element> contents = el.getOwnedElements();
			for (Element sub : contents) {
				displayAllStereotypes(sub, depth + 4);
			}
		}
	}

	static String indent(int n) {
		String r = "";
		for (int i = 0; i < n; i++) {
			r = r + " ";
		}
		return r;
	}

	static void logPaths() {
		String mp = modelPath.toString();
		String op = outputPath.toString();
		String pp = pluginsPath.toString();
		CodeGenPlugin.info("model path:   '" + mp + "'");
		CodeGenPlugin.info("output path:  '" + op + "'");
		CodeGenPlugin.info("plugins path: '" + pp + "'");
	}

}
