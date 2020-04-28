/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

/**
 * This class is based on a similar class by Ed Willink part of org.eclipse.ocl.
 *
 * The main difference is that this class doesn't rely on .project files
 * being available, which may not be the case in a deployed application, looks
 * for plugin ids in MANIFEST.MF files, and searches not only the paths in
 * the java class path but also in additional folders provided.
 *
 * The user search paths are added before class path entries so they take
 * priority during the search.
 *
 * @author Ernesto Posse
 */
public class PluginFinder {

	/** Jar file extension. */
	private static final String JAR_EXTENSION = ".jar";

	/** Project file extension. */
	private static final String PROJ_EXTENSION = ".project";

	/** A list of paths to search. */
	private static List<String> searchPaths = new ArrayList<>();

	/** A map recording for each required plugin whether it has been found. */
	private final Map<PluginData, Boolean> requiredMappings;

	/** A map recording the location of each plugin found. */
	private final Map<PluginData, String> resolvedMappings = new HashMap<>();

	/**
	 * Constructor.
	 *
	 * @param requiredProjects
	 *            - A list of projects/plugins to look for.
	 */
	public PluginFinder(String... requiredProjects) {
		this.requiredMappings = new HashMap<>();
		for (String requiredProject : requiredProjects) {
			addRequiredProject(requiredProject);
		}
	}

	/**
	 * Add a plugin to look for, regardless of the version.
	 * 
	 * @param name
	 *            - The name of the plugin to look for.
	 */
	public void addRequiredPlugin(String name) {
		addRequiredPlugin(name, null);
	}

	/**
	 * Add a plugin to look for, requiring a particular version.
	 * 
	 * @param name
	 *            - The name of the plugin to look for.
	 * @param version
	 *            - The version to look for.
	 */
	public void addRequiredPlugin(String name, String version) {
		if (!resolvedMappings.isEmpty()) {
			throw new IllegalStateException("Cannot addRequiredProject to PluginFinder after resolve()"); //$NON-NLS-1$
		}
		if (name != null) {
			requiredMappings.put(new PluginData(name, version), Boolean.FALSE);
		}
	}

	/**
	 * Add a project to look for.
	 * 
	 * @param name
	 *            - The name of the project to look for.
	 */
	public void addRequiredProject(String name) {
		addRequiredPlugin(name, null);
	}

	/**
	 * Add a path to search.
	 * 
	 * @param path
	 *            - The {@link Path} to be added.
	 */
	public static void addSearchPath(Path path) {
		if (path != null) {
			String pathStr = path.toAbsolutePath().toString();
			if (!searchPaths.contains(pathStr)) {
				searchPaths.add(pathStr);
			}
		}
	}

	/**
	 * Add a set of search paths.
	 * 
	 * @param paths
	 *            - The set of search paths given as a ':'-separated {@link String}.
	 */
	public static void addSearchPaths(String paths) {
		String[] entries = paths.split(":");
		for (String entry : entries) {
			addSearchPath(Paths.get(entry));
		}
	}

	/**
	 * Determine whether all required plugins/projects have been found.
	 * 
	 * @return True iff all have been found.
	 */
	public boolean allFound() {
		boolean found = true;
		for (Boolean b : requiredMappings.values()) {
			if (!b.booleanValue()) {
				found = false;
				break;
			}
		}
		return found;
	}

	/**
	 * Get the resolved path of a given plugin if it has been found.
	 * 
	 * It will return the latest version found, if it has been resolved.
	 * 
	 * @param pluginId
	 *            - The plugin id as a {@link String}.
	 * @return The resolved path as a {@link String} if found, or null otherwise.
	 */
	public String get(String pluginId) {
		String latestVersion = null;
		String resolvedPath = null;
		for (Map.Entry<PluginData, String> mapping : resolvedMappings.entrySet()) {
			PluginData found = mapping.getKey();
			String path = mapping.getValue();
			if (found.getName().equals(pluginId)) {
				if (found.getVersion() == null) {
					resolvedPath = path;
					break;
				} else if (latestVersion == null || found.getVersion().compareTo(latestVersion) > 0) {
					latestVersion = found.getVersion();
					resolvedPath = path;
				}
			}
		}
		return resolvedPath;
	}

	/**
	 * Get the resolved path of a given plugin and given version if it has been found.
	 * 
	 * @param pluginId
	 *            - The plugin id as a {@link String}.
	 * @param version
	 *            - The version to look for as a {@link String}.
	 * @return The resolved path as a {@link String} if found, or null otherwise.
	 */
	public String get(String pluginId, String version) {
		PluginData plugin = new PluginData(pluginId, version);
		return resolvedMappings.get(plugin);
	}

	/**
	 * @param pluginId
	 *            - A {@link String}.
	 * @return {@code true} iff a plugin with the given id was found.
	 */
	public boolean found(String pluginId) {
		return get(pluginId) != null;
	}

	/**
	 * @param pluginId
	 *            - A {@link String}.
	 * @param version
	 *            - A {@link String}.
	 * @return {@code true} iff a plugin with the given id and version was found.
	 */
	public boolean found(String pluginId, String version) {
		return get(pluginId, version) != null;
	}

	public boolean isResolved() {
		return !resolvedMappings.isEmpty();
	}

	/**
	 * Rests the plugin finder by removing all required projects/plugins and resolved mappings.
	 */
	public void reset() {
		resolvedMappings.clear();
		requiredMappings.clear();
	}

	/**
	 * Get the path of the plugin/project's manifest.
	 * 
	 * <p>
	 * This assumes the plugin/project is not packaged as a jar.
	 * 
	 * @param file
	 *            - A {@link File} with the path to the project.
	 * @return A {@link Path} to the MANIFEST.MF file.
	 */
	private Path getManifestPath(File file) {
		Path path = file.toPath();
		Path mfpath = null;
		if (path != null && !path.toString().endsWith(JAR_EXTENSION)) {
			mfpath = path.resolve("META-INF").resolve("MANIFEST.MF");
		}
		return mfpath;
	}

	/**
	 * Get the symbolic name of a plugin/project from its manifest.
	 * 
	 * @param manifest
	 *            - A {@link Manifest}.
	 * @return - The symbolic name of the bundle.
	 */
	private String getSymbolicName(Manifest manifest) {
		String symName = null;
		if (manifest != null) {
			String name = manifest.getMainAttributes().getValue("Bundle-SymbolicName"); //$NON-NLS-1$
			if (name != null) {
				int indexOf = name.indexOf(';');
				if (indexOf > 0) {
					symName = name.substring(0, indexOf);
				}
			}
		}
		return symName;
	}

	/**
	 * Get the version of a plugin/project from its manifest.
	 * 
	 * @param manifest
	 *            - A {@link Manifest}.
	 * @return - The version of the bundle.
	 */
	private String getVersion(Manifest manifest) {
		String version = null;
		if (manifest != null) {
			version = manifest.getMainAttributes().getValue("Bundle-Version"); //$NON-NLS-1$
		}
		return version;
	}

	/**
	 * Determine whether the given path is a path to a Jar bundle.
	 * 
	 * @param file
	 *            - A {@link File}.
	 * @return True if it is a Jar bundle.
	 */
	private boolean isJarBundle(File file) {
		boolean result = false;
		if (file.exists() && !file.isDirectory()) {
			Path path = file.toPath();
			result = path != null && path.toString().endsWith(JAR_EXTENSION);
		}
		return result;
	}

	/**
	 * Determine whether the given path is a path to a non-Jar bundle.
	 * 
	 * @param file
	 *            - A {@link File}.
	 * @return True if it is a non-Jar bundle.
	 */
	private boolean isNonJarBundle(File file) {
		boolean result = false;
		if (file.exists() && file.isDirectory()) {
			Path path = getManifestPath(file);
			result = path != null && Files.exists(path);
		}
		return result;
	}

	/**
	 * Determine whether the given plugin is in the required mappings set.
	 * 
	 * @param plugin
	 *            - Some {@link PluginData}.
	 * @return True if the plugin is in the required mappings set.
	 */
	private boolean isRequired(PluginData plugin) {
		boolean isReq = false;
		for (PluginData required : requiredMappings.keySet()) {
			if (required.getName().equals(plugin.getName())
					&& (required.getVersion() == null || required.getVersion().equals(plugin.getVersion()))) {
				isReq = true;
				break;
			}
		}
		return isReq;
	}

	/**
	 * Register a bundle as resolved.
	 * 
	 * @param f
	 *            - The {@link File} with the resolved path.
	 * @param manifest
	 *            - The bundle's {@link Manifest}.
	 * @param type
	 *            - The type of bundle: either "jar" or "" for non-jar.
	 * @return True if the bundle is required, was found, and the manifest and type are non-null.
	 */
	private boolean registerFromManifest(File f, Manifest manifest, String type) {
		boolean result = false;
		if (manifest != null) {
			String name = getSymbolicName(manifest);
			if (name != null) {
				String version = getVersion(manifest);
				PluginData plugin = new PluginData(name, version);
				if (isRequired(plugin)) {
					File canonical = null;
					try {
						canonical = new File(f.getCanonicalPath());
					} catch (IOException e) {
						canonical = f;
					}
					String uri = type + (!"".equals(type) ? ":" : "") + canonical.toURI() + ("jar".equals(type) ? "!/" : "");
					resolvedMappings.put(plugin, uri); // $NON-NLS-1$ //$NON-NLS-2$
					requiredMappings.put(plugin, Boolean.TRUE);
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Register a jar bundle.
	 * 
	 * @param f
	 *            - The {@link File} with the path to the bundle.
	 * @return True if the bundle is required and was found,
	 * @throws IOException
	 *             - If an I/O error occured openning the bundle.
	 */
	private boolean registerJarBundle(File f) throws IOException {
		boolean result = false;
		JarFile jarFile = new JarFile(f);
		try {
			Manifest manifest = jarFile.getManifest();
			result = registerFromManifest(f, manifest, "jar");
		} finally {
			jarFile.close();
		}
		return result;
	}

	/**
	 * Register a non-jar bundle.
	 * 
	 * @param file
	 *            - The {@link File} with the path to the bundle.
	 * @return True if the bundle is required and was found,
	 * @throws IOException
	 *             - If an I/O error occured openning the bundle.
	 */
	private boolean registerNonJarBundle(File file) throws IOException {
		boolean result = false;
		Path path = getManifestPath(file);
		if (path != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(path.toAbsolutePath().toString());
				Manifest manifest = new Manifest(fis);
				result = registerFromManifest(file, manifest, "");
			} catch (IOException e) {
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		}
		return result;
	}

	/**
	 * Register a project.
	 * 
	 * @param file
	 *            - The {@link File} with the path to the project.
	 * @return True if the project is required and was found,
	 */
	private boolean registerProject(File file) {
		boolean result = false;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
			String project = document.getDocumentElement().getElementsByTagName("name").item(0).getTextContent(); //$NON-NLS-1$
			PluginData plugin = new PluginData(project, null);
			if (requiredMappings.keySet().contains(plugin)) {
				resolvedMappings.put(plugin, file.getParentFile().getCanonicalPath() + File.separator);
				result = true;
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}

	/**
	 * Resolve required plugins/projects.
	 * 
	 * @param standalone
	 *            - {@code true} iff the plugin finder is running from a stand-alone Java application or
	 *            from a plugin within a running Eclipse platform.
	 */
	public void resolve(boolean standalone) {
		if (standalone) {
			resolveInStandalone();
		} else {
			resolveInEclipse();
		}
	}

	/**
	 * Resolve the required plugins/projects when running in Eclipse.
	 */
	private void resolveInEclipse() {
		for (PluginData requiredPlugin : requiredMappings.keySet()) {
			String requiredPluginName = requiredPlugin.getName();
			String requiredPluginVersion = requiredPlugin.getVersion();
			Bundle bundle = null;
			if (requiredPluginVersion == null) {
				bundle = Platform.getBundle(requiredPlugin.getName());
			} else {
				Bundle[] bundles = Platform.getBundles(requiredPlugin.getName(), requiredPluginVersion);
				if (bundles != null) {
					// Look for an exact version match.
					for (Bundle candidate : bundles) {
						if (candidate.getSymbolicName().equals(requiredPluginName)
								&& candidate.getVersion().toString().equals(requiredPluginVersion)) {
							bundle = candidate;
						}
					}
					// If no exact version match is found, select the highest version.
					if (bundle == null) {
						bundle = bundles[0];
					}
				}
			}
			if (bundle != null) {
				try {
					URL url = bundle.getEntry("/");
					URI uri = FileLocator.resolve(url).toURI();
					String uriStr = uri.toString(); // TODO: clean-up URI: normalize? absolute? remove file:// ?
					resolvedMappings.put(requiredPlugin, uriStr);
					requiredMappings.put(requiredPlugin, Boolean.TRUE);
				} catch (URISyntaxException | IOException e) {
					XTUMLRTLogger.warning("Unable to find required plugin or project: '" + requiredPlugin.toString() + "'");
				}
			}
		}
	}

	/**
	 * Resolve the required plugins/projects when running in standalone mode.
	 */
	private void resolveInStandalone() {
		String property = System.getProperty("java.class.path"); //$NON-NLS-1$
		if (property == null) {
			return;
		}
		String separator = System.getProperty("path.separator"); //$NON-NLS-1$
		String[] cpEntries = property.split(separator);
		List<String> entries = new ArrayList<>();
		entries.addAll(searchPaths);
		entries.addAll(Arrays.asList(cpEntries));
		for (String entry : entries) {
			File fileEntry = new File(entry);
			try {
				File f = fileEntry.getCanonicalFile();
				if (!f.exists()) {
					continue;
				}
				if (isJarBundle(f)) {
					registerJarBundle(f);
				} else if (isNonJarBundle(f)) {
					registerNonJarBundle(f);
				} else if (f.isDirectory()) {
					scanFolder(f, new HashSet<String>(), 0);
				} else {
					// eclipse bin folder?
					File parentFile = f.getParentFile();
					File dotProject = new File(parentFile, PROJ_EXTENSION);
					if (dotProject.exists()) {
						registerProject(dotProject);
					}
				}
				if (allFound()) {
					break;
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Scans a folder looking for projects, jar and non-jar bundles and register those which are
	 * required.
	 * 
	 * @param f
	 *            - A {@link File} with the folder to scan.
	 * @param alreadyVisited
	 *            - A set of paths which have already been visited.
	 * @param depth
	 *            - The depth with respect to the starting folder.
	 * @return True iff the folder contains at least one project.
	 * @throws IOException
	 *             - If there was an I/O error opening a jar or manifest file.
	 */
	private boolean scanFolder(File f, Set<String> alreadyVisited, int depth) throws IOException {
		if (!alreadyVisited.add(f.getCanonicalPath())) {
			return true;
		}
		File[] files = f.listFiles();
		boolean containsProject = false;
		File dotProject = null;
		if (files != null) {
			for (File file : files) {
				if (!file.exists()) {
					continue;
				}
				if (isJarBundle(file)) {
					registerJarBundle(file);
				} else if (isNonJarBundle(file)) {
					registerNonJarBundle(file);
				} else if (PROJ_EXTENSION.equals(file.getName())) {
					dotProject = file;
				} else if (file.isDirectory() && (depth < 2) && !file.getName().startsWith(".")) {
					containsProject |= scanFolder(file, alreadyVisited, depth + 1);
				}
			}
		}
		if (!containsProject && dotProject != null) {
			registerProject(dotProject);
		}
		return containsProject || dotProject != null;
	}

	/**
	 * Log the resolved paths.
	 */
	public void logResolvedMappings() {
		XTUMLRTLogger.info("resolved mapping start");
		System.out.println("resolved mapping start");
		for (Map.Entry<PluginData, String> entry : resolvedMappings.entrySet()) {
			XTUMLRTLogger.info(entry.getKey() + " |-> " + entry.getValue());
			System.out.println(entry.getKey() + " |-> " + entry.getValue());
		}
		XTUMLRTLogger.info("resolved mapping end");
		System.out.println("resolved mapping end");
	}

	/**
	 * Create a {@link String} to be used as indentation.
	 * 
	 * @param n
	 *            - The number of spaces in the string.
	 * @return - The {@link String} with n spaces.
	 */
	static String indent(int n) {
		StringBuilder r = new StringBuilder();
		for (int i = 0; i < n; i++) {
			r.append(' ');
		}
		return r.toString();
	}

	/**
	 * Basic data for a plugin: name and version.
	 * 
	 * @author epp
	 */
	private static class PluginData {

		/** Plugin name. */
		private String name;

		/** Plugin version. If version==null, then any version is accepted. */
		private String version;

		/**
		 * Constructor.
		 *
		 * @param name
		 *            - A {@link String}.
		 * @param version
		 *            - A {@link String}. If version==null, then any version is accepted.
		 */
		PluginData(String name, String version) {
			this.name = name;
			this.version = version;
		}

		@Override
		public int hashCode() {
			return getName().hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof PluginData)) {
				return false;
			}
			return ((PluginData) other).getName().equals(getName())
					&& ((getVersion() == null || ((PluginData) other).getVersion() == null)
							|| ((PluginData) other).getVersion().equals(getVersion()));
		}

		@Override
		public String toString() {
			return "( " + getName() + ", " + getVersion() + ")";
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}
	}

}
