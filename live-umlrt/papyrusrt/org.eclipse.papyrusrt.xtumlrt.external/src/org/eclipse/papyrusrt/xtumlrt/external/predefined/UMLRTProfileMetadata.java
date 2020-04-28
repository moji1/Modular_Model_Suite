/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external.predefined;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.UMLRealTimePackage;
import org.eclipse.papyrusrt.xtumlrt.external.ExternalPackageMetadata;

/**
 * Meta-data for the UML-RT profile.
 * 
 * @see ExternalPackageMetadata
 * @author epp
 */
public final class UMLRTProfileMetadata extends ExternalPackageMetadata {

	/** Common instance. */
	public static final UMLRTProfileMetadata INSTANCE = new UMLRTProfileMetadata();

	/** Profile id. */
	private static final String ID = "org.eclipse.papyrusrt.umlrt.profile";

	/** Name-space URI. */
	// It should be "http://www.eclipse.org/papyrus/umlrt"
	private static final String NS_URI = UMLRealTimePackage.eNS_URI;

	/** Pathmap. */
	private static final String BASEPATHMAP = "pathmap://UML_RT_PROFILE";

	/** Root node id. */
	// TODO: Find out if this can be extracted programatically
	private static final String ROOT_ID = "_1h74oEeVEeO0lv5O1DTHOQ";

	/** Subdirectory containing the model. */
	private static final String FOLDER = "umlProfile";

	/** File containing the model. */
	private static final String FILE = "uml-rt.profile.uml";

	/** {@link EPackage}. */
	private static final UMLRealTimePackage EPACKAGE = UMLRealTimePackage.eINSTANCE;

	/**
	 * Default constructor. Private as this class should not be extended.
	 *
	 */
	private UMLRTProfileMetadata() {
		super(ID, Kind.Profile, NS_URI, BASEPATHMAP, ROOT_ID, FOLDER, FILE, EPACKAGE);
	}

}
