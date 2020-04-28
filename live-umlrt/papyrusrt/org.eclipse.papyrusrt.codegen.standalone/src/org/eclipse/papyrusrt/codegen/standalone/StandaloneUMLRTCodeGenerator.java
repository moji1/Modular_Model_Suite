/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.standalone;

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
import org.eclipse.emf.ecore.EAnnotation;
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
import org.eclipse.papyrusrt.codegen.standalone.internal.StandaloneGeneratorManager;
import org.eclipse.papyrusrt.xtumlrt.external.PluginFinder;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Stand-alone code generator.
 * 
 * This generator does not launch an Eclipse instance.
 * 
 * @author epp
 */
public final class StandaloneUMLRTCodeGenerator {

	// System properties

	/** Name of the property that can be used to specify the path where generated code will be saved. */
	public static final String OUTPUT_PATH_PROPERTY = "org.eclipse.papyrusrt.output-path";

	/** Name of the property that specifies the path to the Papyrus-RT plugins. */
	public static final String PLUGINS_PATHS_PROPERTY = "org.eclipse.papyrusrt.plugins-paths";

	/** Name of the property that specifies the name of the Top capsule. */
	public static final String TOP_CAPSULE_PROPERTY = "org.eclipse.papyrusrt.top-capsule";

	// Default values

	/** Default logging level. */
	public static final String DEFAULT_LOG_LEVEL = "OFF";

	/** Default name of the Top capsule. */
	public static final String DEFAULT_TOP = "Top";

	// Command-line options

	/** Parsed command-line arguments. */
	private static CommandLine clargs;

	/** Command-line options. */
	private static final Options OPTIONS = new Options();

	/** Help formatter. */
	private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

	/** Message showing the command-line syntax. */
	private static final String CMDLINE_SYNTAX = "umlrtgen [-h] <path>";

	/** Short option: help. */
	private static final String OPT_HELP = "h";

	/** Short option: quiet, i.e. inhibit standard output. */
	private static final String OPT_QUIET = "q";

	/** Short option: logging level. */
	private static final String OPT_LOG_LEVEL = "l";

	/** Short option: output path. */
	private static final String OPT_OUTPUT_PATH = "o";

	/** Short option: path to plugins folder. */
	private static final String OPT_PLUGINS_PATHS = "p";

	/** Short option: name of Top capsule. */
	private static final String OPT_TOP = "t";

	/** Short option: generate intermediate representation (xtUML-RT). */
	private static final String OPT_TO_XTUMLRT = "x";

	/** Short option: print stack trace on errors. */
	private static final String OPT_PRINT_STACK_TRACE = "s";

	/** Short option: run in developer mode for debugging. */
	private static final String OPT_DEV = "d";

	/** Resource set containing the model and refered resources. */
	private static ResourceSet resourceSet;

	/** The code generator. */
	private static UMLRTCodeGenerator generator;

	// User-specified variables/options

	/** Path to the user-provided input UML-RT model. */
	private static Path modelPath = null;

	/** Path where code will be generated. */
	private static Path outputPath = null;

	/** Path where Papyrus-RT plugins are located. */
	private static String pluginsPaths = null;

	/** Name of the top capsule. */
	private static String top = null;

	/** Whether to print the stack trace on errors or not. */
	private static boolean printStackTrace = false;

	/** Path separator. */
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	/**
	 * Default constructor is private because this class should not be instantiated.
	 */
	private StandaloneUMLRTCodeGenerator() {
	}

	/**
	 * Main method.
	 * 
	 * 1. Parses the command-line arguments.
	 * 2. Executes the initialization, which
	 * 2.1. Initializes the resource set
	 * 2.2. Loads the necessary packages.
	 * 3. Loads the input model.
	 * 4. Invokes the generator.
	 * 5. If necessary, displays the resulting status and/or errors.
	 * 
	 * @param args
	 *            - Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			parseCmdLineArgs(args);
			if (!processCmdLineArgs()) {
				System.exit(1);
			}
			init();
			URI fullURI = URI.createFileURI(modelPath.toString());
			Model model = loadModel(fullURI);

			if (!clargs.hasOption(OPT_TOP))
				tryFindTop(model);

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

	public static String getPluginsPaths() {
		return pluginsPaths;
	}

	public static String getTop() {
		return top;
	}

	public static void tryFindTop(Model model) {
		EAnnotation topAnnotation = model.getEAnnotation("UMLRT_Default_top");
		if (topAnnotation != null)
			top = model.getEAnnotation("UMLRT_Default_top").getDetails().get("top_name");
	}
	
	/**
	 * Configures the command-line options and parses the command-line arguments.
	 * 
	 * The parsed arguments are stored in the {@link clargs} attribute.
	 * 
	 * @param args
	 *            - Command-line arguments.
	 * @throws ParseException
	 *             - If the command-line arguments could not be parsed.
	 */
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
		CommandLineParser parser = new BasicParser();
		clargs = parser.parse(OPTIONS, args);
		if (clargs.getArgList().isEmpty() && !clargs.hasOption(OPT_HELP)) {
			throw new ParseException("There must be at least one argument.");
		}
	}

	/**
	 * Validates various options from the parsed command-line arguments and sets the corresponding
	 * attributes.
	 * 
	 * @return False if the help option was provided.
	 * @throws InvalidPathException
	 * @throws FileNotFoundException
	 */
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
			pluginsPaths = validatePluginsPaths(clargs.getOptionValue(OPT_PLUGINS_PATHS,
					System.getProperty(PLUGINS_PATHS_PROPERTY)));
			if (pluginsPaths != null) {
				PluginFinder.addSearchPaths(pluginsPaths);
			}
			top = clargs.getOptionValue(OPT_TOP, System.getProperty(TOP_CAPSULE_PROPERTY, DEFAULT_TOP));
			result = true;
		}
		return result;
	}

	/**
	 * Validates and returns the output path as a {@link Path}.
	 * 
	 * @param outputPath
	 *            - The given output path as a {@link String}
	 * @return The output path as a {@link Path} or null if the given path is {@literal null}.
	 * @throws InvalidPathException
	 */
	private static Path validateOutputPath(String outputPath) throws InvalidPathException {
		Path path = null;
		if (outputPath != null) {
			path = Paths.get(outputPath);
		}
		return path;
	}

	/**
	 * Validates and returns the input model path as a {@link Path}.
	 * 
	 * @param modelPathString
	 *            - The given model path as a {@link String}.
	 * @return The model path as a {@link Path} or null if the given path is {@literal null}.
	 * @throws InvalidPathException
	 * @throws FileNotFoundException
	 */
	private static Path validateModelPath(String modelPathString) throws InvalidPathException, FileNotFoundException {
		Path modelPath = Paths.get(modelPathString);
		if (!Files.exists(modelPath)) {
			throw new FileNotFoundException("Model file '" + modelPathString + "' not found");
		}
		return modelPath;
	}

	/**
	 * Validates and returns the plugins path as a {@link Path}.
	 * 
	 * @param pluginsPathString
	 *            - The given plugins path as a {@link String}.
	 * @return The plugins path as a {@link Path} or null if the given path is {@literal null}.
	 * @throws InvalidPathException
	 * @throws FileNotFoundException
	 */
	private static String validatePluginsPaths(String pluginsPathString) throws InvalidPathException,
			FileNotFoundException {
		if (pluginsPathString == null) {
			return null;
		}
		String[] entries = pluginsPathString.split(PATH_SEPARATOR);
		String result = "";
		for (String entry : entries) {
			Path pluginsPath = Paths.get(entry);
			if (!Files.exists(pluginsPath)) {
				throw new FileNotFoundException("Plugins folder '" + pluginsPathString + "' not found");
			} else {
				result += pluginsPath.toAbsolutePath().toString() + PATH_SEPARATOR;
			}
		}
		return result;
	}

	/**
	 * Configure the logger.
	 */
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

	/**
	 * Set a default value for the plugins path property by attempting to find the path based on
	 * the location of this class.
	 */
	private static void setPluginsPathPropertyDefault() {
		if (clargs.hasOption(OPT_PLUGINS_PATHS)) {
			return;
		}
		// We try to guess the default value for the plugins path by looking for
		// the folder that contains this class.
		if (System.getProperty(PLUGINS_PATHS_PROPERTY) == null) {
			Class<?> thisClass = StandaloneUMLRTCodeGenerator.class;
			URL url = thisClass.getResource("StandaloneUMLRTCodeGenerator.class");
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

	/**
	 * Updates the class path to include the plugins path.
	 */
	private static void updateClassPath() {
		String classpath = System.getProperty("java.class.path");
		if (pluginsPaths != null && !("".equals(pluginsPaths))) {
			classpath = pluginsPaths + classpath;
			System.setProperty("java.class.path", classpath);
		}
	}

	/**
	 * Initializes the code generator:
	 * 
	 * <ol>
	 * <li>Creates a resource set.
	 * <li>Initializes the registries for the specified resource set to work with UML2 resources.
	 * <li>Searches for the plugins that contain:
	 * <ul>
	 * <li>The UML-RT profile
	 * <li>The RTCppProperties set profile.
	 * <li>The Ansi C Library.
	 * <li>The UML-RT RTS model library.
	 * </ul>
	 * <li>Registers the UML-RT profile.
	 * <li>Loads and registers the other profiles and libraries:
	 * </ol>
	 */
	private static void init() {
		logPaths();
		// updateClassPath();
		resourceSet = new ResourceSetImpl();
		CodeGenProvider.getDefault().setModule(new StandaloneUMLRTCodeGenInjectionModule());
		generator = CodeGenProvider.getDefault().get();
		generator.setStandalone(true);
		if (generator instanceof AbstractUMLRT2CppCodeGenerator) {
			((AbstractUMLRT2CppCodeGenerator) generator).setupExternalPackageManagement(resourceSet);
		}
	}

	/**
	 * Loads a UML Package given its URI.
	 * 
	 * @param fullURI
	 *            - The {@link URI} of the resource containing the package.
	 * @return The {@link org.eclipse.uml2.uml.Package}.
	 */
	private static org.eclipse.uml2.uml.Package loadPackage(URI fullURI) {
		Resource resource = resourceSet.getResource(fullURI, true);
		EList<EObject> contents = resource.getContents();
		org.eclipse.uml2.uml.Package pkg = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(contents, UMLPackage.Literals.PACKAGE);
		// displayAllStereotypes( mdl, 0 );
		return pkg;
	}

	/**
	 * Loads a UML Model given its URI.
	 * 
	 * @param fullURI
	 *            - The {@link URI} of the resource containing the model.
	 * @return The {@link org.eclipse.uml2.uml.Model}.
	 */
	private static Model loadModel(URI fullURI) {
		return (Model) loadPackage(fullURI);
	}

	/**
	 * Invokes the actual code generator.
	 * 
	 * <p>
	 * If the option to generate xtUML-RT is given, then instead of invoking the standard code
	 * generator, it invokes the generator that only runs the transformation from UML to xtUML-RT.
	 * 
	 * @param model
	 *            - The input UML {@link Model}.
	 * @return The {@link IStatus} with the result status of the generation.
	 */
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

	/**
	 * Prints the generation's result status to standard output.
	 * 
	 * @param status
	 *            - The {@link IStatus}.
	 */
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

	/**
	 * Displays the messages of a composite status.
	 * 
	 * @param out
	 *            - A {@link PrintStream} where the messages will be printed.
	 * @param indent
	 *            - A {@link String} with the prefix for each line.
	 * @param status
	 *            - A composite {@link IStatus}.
	 */
	private static void displayAll(PrintStream out, String indent, IStatus status) {
		out.print(indent);
		out.println(status.getMessage());
		for (IStatus child : status.getChildren()) {
			displayAll(out, indent + "    ", child);
		}
	}

	/**
	 * Displays all the stereotypes and their application for a given UML Element.
	 * 
	 * @param eobj
	 *            - An {@link Element}.
	 * @param depth
	 *            - An int representing the indentation depth.
	 */
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

	/**
	 * Makes an indentation string made of space characters.
	 * 
	 * @param n
	 *            - An int. The length of the indentation.
	 * @return A {@link String} with n space characters.
	 */
	static String indent(int n) {
		String r = "";
		for (int i = 0; i < n; i++) {
			r = r + " ";
		}
		return r;
	}

	/**
	 * Logs the input model, output and plugin paths.
	 */
	static void logPaths() {
		String mp = modelPath.toString();
		String op = outputPath.toString();
		String pp = pluginsPaths;
		CodeGenPlugin.info("model path:   '" + mp + "'");
		CodeGenPlugin.info("output path:  '" + op + "'");
		CodeGenPlugin.info("plugins path: '" + pp + "'");
	}

}
