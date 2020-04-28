/*****************************************************************************
 * Copyright (c) 2016 Zeligsoft (2009) Limited and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ernesto Posse - Initial API and implementation
 *****************************************************************************/

package org.eclipse.papyrusrt.xtumlrt.external;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.papyrusrt.umlrt.uml.util.UMLRTResourcesUtil;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.RTSModelLibraryMetadata;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.UMLRTProfileMetadata;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger;
import org.eclipse.uml2.uml.UMLPlugin;
import org.osgi.framework.BundleContext;

/**
 * This class is used to manage external packages, models and profiles used or refered to in xtUML-RT models.
 * 
 * <p>
 * It takes care of initializing resources, performing package registration etc.
 * 
 * <p>
 * Models can be used in different contexts:
 * 
 * <ol>
 * <li>Within a running Eclipse platform instance.
 * <ol>
 * <li>With a Papyrus UML model opened
 * <li>Without a Papyrus UML model opened (e.g. when invoking an action on a file selected in the Project Explorer)
 * </ol>
 * <li>In stand-alone Java applications.
 * </ol>
 * 
 * <p>
 * This class is useful to deal in particular with the last two cases (1.2 and 2).
 * 
 * @author Ernesto Posse
 */
public final class ExternalPackageManager extends Plugin {

	/** Plugin ID. */
	public static final String ID = "org.eclipse.papyrusrt.xtumlrt.external";

	/** The collection of 'known' packages that must be loaded and registered. */
	private static final ExternalPackageMetadata[] BUILT_IN_PACKAGES = {
			UMLRTProfileMetadata.INSTANCE,
			RTSModelLibraryMetadata.INSTANCE
	};

	/** Default {@link IStatus} for the manager's tasks. */
	private static final IStatus OK_STATUS = new Status(IStatus.OK, XTUMLRTLogger.ID, "ok");

	/** The global instance of this class. */
	private static ExternalPackageManager instance;

	/** The bundle's execution context. */
	private static BundleContext context;

	/** Whether we are running as a Java stand-alone application or in an Eclipse platform instance. */
	private boolean standalone;

	/** The instance of the {@link PluginFinder} used to locate plugins. */
	private PluginFinder pluginFinder;

	/** A map from package IDs (as Strings) to {@link ExternalPackageMetadata} containing the requested packages. */
	private Map<String, ExternalPackageMetadata> requiredPackages;

	/** A map from package IDs (as Strings) to {@link ExternalPackageMetadata} containing the successfuly loaded and registered packages. */
	private Map<String, ExternalPackageMetadata> registeredPackages;

	/** The EMF {@link ResourceSet} where the packages and models are (going to be) loaded. */
	private ResourceSet resourceSet;

	/** Whether setup has already been done. */
	private boolean setup;

	/**
	 * Constructor. Private so that instances can be obtained only with the {@link #getInstance()} factory method.
	 */
	private ExternalPackageManager() {
		super();
		reset();
	}

	/**
	 * Resets the state of the manager.
	 */
	public void reset() {
		this.standalone = false;
		this.pluginFinder = new PluginFinder();
		this.requiredPackages = new HashMap<>();
		this.registeredPackages = new HashMap<>();
		this.resourceSet = new ResourceSetImpl();
		this.setup = false;
		for (ExternalPackageMetadata metadata : BUILT_IN_PACKAGES) {
			addRequiredPackage(metadata);
		}
	}

	/**
	 * Factory method.
	 * 
	 * @return The shared {@link #instance}.
	 */
	public static ExternalPackageManager getInstance() {
		if (instance == null) {
			instance = new ExternalPackageManager();
		}
		return instance;
	}

	public static BundleContext getContext() {
		return context;
	}

	public PluginFinder getPluginFinder() {
		return pluginFinder;
	}

	public Map<String, ExternalPackageMetadata> getRegistry() {
		return registeredPackages;
	}

	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	public boolean isStandalone() {
		return standalone;
	}

	/**
	 * @param packageId
	 *            - A {@link String} with the package ID.
	 * @return The corresponding {@link ExternalPackageMetadata} if the package is registered
	 *         or {@code null} if it isn't.
	 */
	public ExternalPackageMetadata getPackageMetadata(String packageId) {
		return registeredPackages.get(packageId);
	}

	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	@Override
	public void start(BundleContext context) {
		ExternalPackageManager.context = context;
	}

	@Override
	public void stop(BundleContext context) {
		ExternalPackageManager.context = null;
	}

	/**
	 * Adds an external package to the list of required packages.
	 * 
	 * <p>
	 * Only packages added with this method will be loaded and registered. But this method does not
	 * load or registers the package. That task is performed by the {@link #setup} method which is to
	 * be invoked by the framework, not the package's provider.
	 * 
	 * @param packageMetadata
	 *            - An {@link AbstractPackageMetadata} object with the package information.
	 */
	public void addRequiredPackage(ExternalPackageMetadata packageMetadata) {
		if (packageMetadata != null) {
			String key = packageMetadata.getPackageId();
			if (key != null && !key.isEmpty()) {
				if (registeredPackages.containsKey(key)) {
					XTUMLRTLogger.warning("Package '" + key + "' was already registered. The old registration will be overriden.");
				}
				requiredPackages.put(key, packageMetadata);
			} else {
				XTUMLRTLogger.warning("Attempting to register a package with no ID.");
			}
		} else {
			XTUMLRTLogger.warning("Attempting to register null as a package.");
		}
	}

	/**
	 * Initialize UML resources, load added packages, libraries and profiles and perform the necessary EMF registrations.
	 * 
	 * <p>
	 * This method should be invoked by clients only after providers of external packages have added them with
	 * {@link #addRequiredPackage(ExternalPackageMetadata)}.
	 * 
	 * @return An {@link IStatus}.
	 */
	public IStatus setup() {
		IStatus success = OK_STATUS;
		if (!setup) {
			if (standalone) {
				UMLRTResourcesUtil.init(resourceSet);
			}
			searchPlugins();
			for (String requiredPackageId : requiredPackages.keySet()) {
				ExternalPackageMetadata metadata = requiredPackages.get(requiredPackageId);
				boolean loaded = loadPackage(metadata);
				if (!loaded) {
					XTUMLRTLogger.warning("Unable to load package: '" + requiredPackageId + "'");
				} else {
					boolean registered = registerPackage(metadata);
					if (!registered) {
						XTUMLRTLogger.warning("Unable to register package: '" + requiredPackageId + "'");
					} else {
						metadata.setup();
					}
				}
			}
			setup = true;
		}
		return success;
	}

	/**
	 * Search for the plugins containing the packages with the provided package ids.
	 */
	private void searchPlugins() {
		// We only search the first time.
		if (!pluginFinder.isResolved()) {
			for (String pkgId : requiredPackages.keySet()) {
				pluginFinder.addRequiredPlugin(pkgId);
			}
			pluginFinder.resolve(standalone);
			pluginFinder.logResolvedMappings();
		}
	}

	/**
	 * Finds and loads the package described by the given meta-data.
	 * 
	 * <p>
	 * If successful, this method updates the metadata itself by setting its {@link EObject} root element.
	 * 
	 * @param metadata
	 *            - The {@link ExternalPackageMetadata}.
	 * @return {@code true} iff the package is successfully loaded.
	 */
	private boolean loadPackage(ExternalPackageMetadata metadata) {
		boolean success = false;
		URI pathmap = metadata.getPathmapURI();
		Resource existingResource = resourceSet.getResource(pathmap, !standalone);
		if (existingResource != null) {
			loadResourceContents(existingResource, metadata);
			success = true;
		} else {
			String packageId = metadata.getPackageId();
			if (pluginFinder.found(packageId)) {
				resolvePackageLocation(metadata);
				URI fullURI = metadata.getPackageLocationURI();
				Resource resource = resourceSet.getResource(fullURI, true);
				loadResourceContents(resource, metadata);
				success = true;
			} else {
				XTUMLRTLogger.warning("Unable to find plugin with id '" + packageId + "'");
			}
		}
		return success;
	}

	/**
	 * @param existingResource
	 *            - A {@link Resource}.
	 * @param metadata
	 *            - An {@link ExternalPackageMetadata}.
	 */
	private void loadResourceContents(Resource existingResource, ExternalPackageMetadata metadata) {
		metadata.setResource(existingResource);
		EList<EObject> contents = existingResource.getContents();
		EObject root = contents.get(0);
		metadata.setRoot(root);
	}

	/**
	 * Obtain the actual file system location of the package file and update the
	 * package's metadata.
	 * 
	 * <p>
	 * The resolved path is obtained by normalizing the location found by the {@link #pluginFinder}
	 * with {@link #parsePath(String)} and then appending the folder and file name of the package
	 * within the bundle.
	 * 
	 * @param metadata
	 *            - The {@link ExternalPackageMetadata}.
	 */
	private void resolvePackageLocation(ExternalPackageMetadata metadata) {
		String resolvedPath = null;
		String pkgId = metadata.getPackageId();
		if (pkgId != null) {
			String packageLoc = pluginFinder.get(pkgId);
			if (packageLoc != null) {
				Path path = parsePath(packageLoc);
				String subdir = metadata.getFolderName();
				if (subdir != null) {
					path = path.resolve(subdir);
				}
				String pkgModel = metadata.getFileName();
				if (pkgModel != null) {
					path = path.resolve(pkgModel);
				}
				resolvedPath = path.toUri().toString();
			}
		}
		metadata.setPackageLocation(resolvedPath);
	}

	/**
	 * Obtains a normalized path from the given bundle path. The bundle path may be a directory or a jar file and may be
	 * specified with a URI with different schemes (e.g. 'file:', 'jar:', 'jar:file:') and different OS path separators.
	 * Furthermore, if it is a jar bundle, it may or may not have associated path info, e.g. jar:file:/a/b!/c.
	 * 
	 * @param path
	 *            - A {@link String} with the URI of a bundle.
	 * @return The normalized {@link Path} of the given path.
	 */
	private Path parsePath(String path) {
		Path result = null;
		java.net.URI uri = null;
		try {
			uri = new java.net.URI(path);
			if (uri.getScheme().equals("jar")) {
				FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
			}
		} catch (URISyntaxException | IOException | FileSystemAlreadyExistsException e) {
		}
		result = Paths.get(uri);
		return result;
	}

	/**
	 * Perform EMF registration for the package.
	 * 
	 * @param metadata
	 *            - The {@link ExternalPackageMetadata}.
	 * @return {@code true} iff successful.
	 */
	private boolean registerPackage(ExternalPackageMetadata metadata) {
		boolean success = false;
		if (standalone) {
			switch (metadata.getKind()) {
			case Package:
				success = doRegisterNormalPackage(metadata);
				break;
			case Library:
				success = doRegisterLibrary(metadata);
				break;
			case Profile:
				success = doRegisterProfile(metadata);
				break;
			default:
				success = doRegisterNormalPackage(metadata);
				break;
			}
		} else {
			success = true;
		}
		if (success) {
			registeredPackages.put(metadata.getPackageId(), metadata);
		}
		return success;
	}

	/**
	 * @param metadata
	 *            - The {@link ExternalPackageMetadata}.
	 * @return {@code true} iff successful.
	 */
	private boolean doRegisterNormalPackage(ExternalPackageMetadata metadata) {
		// Add a map entry from the logical pathmap URI to the physical location URI.
		URI pathmapUri = metadata.getPathmapURI();
		URI packageLocUri = metadata.getPackageLocationURI();
		resourceSet.getURIConverter()
				.getURIMap()
				.put(pathmapUri, packageLocUri);
		String nsUri = metadata.getNsURI();
		EPackage pkg = metadata.getEPackage();
		// Register package in the resourceSet package registeredPackages
		if (nsUri != null && pkg != null) {
			resourceSet.getPackageRegistry().put(nsUri, pkg);
		}
		return true;
	}

	/**
	 * @param metadata
	 *            - The {@link ExternalPackageMetadata}.
	 * @return {@code true} iff successful.
	 */
	private boolean doRegisterLibrary(ExternalPackageMetadata metadata) {
		return doRegisterNormalPackage(metadata);
	}

	/**
	 * @param metadata
	 *            - The {@link ExternalPackageMetadata}.
	 * @return {@code true} iff successful.
	 */
	private boolean doRegisterProfile(ExternalPackageMetadata metadata) {
		boolean success = false;
		success = doRegisterNormalPackage(metadata);
		if (success) {
			String nsUri = metadata.getNsURI();
			URI rootUri = metadata.getRootURI();
			// Register the profile's NS_URI to its pathmap location
			if (nsUri != null && rootUri != null) {
				UMLPlugin.getEPackageNsURIToProfileLocationMap().put(nsUri, rootUri);
				success = true;
			}
		}
		return success;
	}

}
