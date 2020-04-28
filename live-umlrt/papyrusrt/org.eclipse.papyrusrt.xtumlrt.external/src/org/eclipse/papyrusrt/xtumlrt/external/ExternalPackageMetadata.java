/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Instances of subclasses of this class contain meta-data for various packages, models and profiles
 * that need to be loaded by the generator.
 * 
 * @author epp
 */
public class ExternalPackageMetadata {

	/** The package id. It must be the same as the bundle id. */
	private String packageId;

	/** The {@link #Kind} of package. */
	private Kind kind;

	/** The package namespace URI. */
	private String nsURI;

	/** The package's base pathmap. */
	private String basePathmap;

	/** The id of the package's root element. */
	private String rootId;

	/** The bundle subdirectory that contains the package's file. */
	private String folderName;

	/** The name of the model file containing the package. */
	private String fileName;

	/** The package's {@link EPackage} instance. */
	private EPackage ePackage;

	// Derived attributes

	/** The EMF resource containing the package. */
	private Resource resource;

	/** The root element of the package. */
	private EObject rootElement;

	/** The actual package location. */
	private String packageLocation;

	/**
	 * Constructor.
	 * 
	 * @param packageId
	 *            - A {@link String}. It must be a bundle id.
	 * @param kind
	 *            - The package's {@link ExternalPackageMetadata.Kind}.
	 * @param nsURI
	 *            - The package's name-space URI as a {@link String}.
	 * @param basePathmap
	 *            - The package's base pathmap as a {@link String}. It should start with "pathmap://".
	 * @param rootId
	 *            - The package's root element's Id as a {@link String}.
	 * @param folderName
	 *            - The sub-folder within the provider bundle.
	 * @param fileName
	 *            - The name of the file containing the package.
	 * @param ePackage
	 *            - The {@link EPackage} of this package.
	 */
	public ExternalPackageMetadata(String packageId, Kind kind, String nsURI, String basePathmap, String rootId, String folderName, String fileName, EPackage ePackage) {
		this.packageId = packageId;
		this.kind = kind;
		this.nsURI = nsURI;
		this.basePathmap = basePathmap;
		this.rootId = rootId;
		this.folderName = folderName;
		this.fileName = fileName;
		this.ePackage = ePackage;
	}

	//
	// Basic getters
	//

	public String getPackageId() {
		return packageId;
	}

	public Kind getKind() {
		return kind;
	}

	public String getNsURI() {
		return nsURI;
	}

	public String getBasePathmap() {
		return basePathmap;
	}

	public String getRootId() {
		return rootId;
	}

	public String getFolderName() {
		return folderName;
	}

	public String getFileName() {
		return fileName;
	}

	//
	// Derived information
	//

	/**
	 * @return The full pathmap concatenating the base pathmap and the file name, or null if the base pathmap is null or empty.
	 */
	public String getPathmap() {
		String pathmap = null;
		String basePathmap = getBasePathmap();
		if (basePathmap != null && !basePathmap.isEmpty()) {
			char last = basePathmap.charAt(basePathmap.length() - 1);
			String separator = last == '/' ? "" : "/";
			pathmap = basePathmap + separator + getFileName();
		}
		return pathmap;
	}

	public String getRootPathmap() {
		return getPathmap() + "#" + getRootId();
	}

	public URI getRootURI() {
		return URI.createURI(getRootPathmap());
	}

	public URI getPathmapURI() {
		return URI.createURI(getPathmap());
	}

	/**
	 * Find the plugin that contains the profile.
	 * 
	 * @return A {@link String} with the path to the package.
	 */
	public String getPackageLocation() {
		return packageLocation;
	}



	public URI getPackageLocationURI() {
		return URI.createURI(getPackageLocation());
	}

	//
	// Ecore elements
	//

	/**
	 * Return the {@link EPackage} for the package. This default implementation looks for the package in the
	 * {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE global} package registry.
	 * 
	 * @return The {@link EPackage}.
	 */
	public EPackage getEPackage() {
		if (ePackage == null) {
			String nsURI = getNsURI();
			ePackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
		}
		return ePackage;
	}

	public Resource getResource() {
		return resource;
	}

	/** @return The root element contained in the package, if it is loaded, or null otherwise. */
	public EObject getRootElement() {
		return rootElement;
	}

	//
	// Setters
	//

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setRoot(EObject root) {
		this.rootElement = root;
	}

	public void setPackageLocation(String loc) {
		this.packageLocation = loc;
	}

	//
	// Custom setup
	//

	/**
	 * This method is intended to be overriden by subclasses to perform custom initialization when the package is loaded and
	 * registered.
	 */
	public void setup() {
		// To be overriden.
	}

	/** The kind of package: normal package or model, model library or profile. */
	public enum Kind {
		Package("package"), Library("library"), Profile("profile");
		/** The symbolic name of the kind. */
		private String name;

		/**
		 * Constructor.
		 *
		 * @param name
		 *            - A {@link String}.
		 */
		Kind(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}
	}

}

