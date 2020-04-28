/*****************************************************************************
 * Copyright (c) 2016 Zeligsoft (2009) and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ernesto Posse - Initial API and implementation
 *****************************************************************************/

package org.eclipse.papyrusrt.xtumlrt.external.predefined;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.UMLRTStateMachinesPackage;
import org.eclipse.papyrusrt.xtumlrt.external.ExternalPackageMetadata;

/**
 * Meta-data for the UML-RT profile.
 * 
 * @see ExternalPackageMetadata
 * @author epp
 */
public class UMLRTSMProfileMetadata extends ExternalPackageMetadata {


	/** Common instance. */
	public static final UMLRTSMProfileMetadata INSTANCE = new UMLRTSMProfileMetadata();

	/** Profile id. */
	private static final String ID = "org.eclipse.papyrusrt.umlrt.profile.statemachine";

	/** Name-space URI. */
	// It should be "http://www.eclipse.org/papyrus/umlrt"
	private static final String NS_URI = UMLRTStateMachinesPackage.eNS_URI;

	/** Pathmap. */
	private static final String BASEPATHMAP = "pathmap://UML_RT_PROFILE";

	/** Root node id. */
	// TODO: Find out if this can be extracted programatically
	private static final String ROOT_ID = "_KLcn0FDtEeOA4ecmvfqvaw";

	/** Subdirectory containing the model. */
	private static final String FOLDER = "umlProfile";

	/** File containing the model. */
	private static final String FILE = "UMLRealTimeSM-addendum.profile.uml";

	/** {@link EPackage}. */
	private static final UMLRTStateMachinesPackage EPACKAGE = UMLRTStateMachinesPackage.eINSTANCE;


	/**
	 * Constructor.
	 */
	public UMLRTSMProfileMetadata() {
		super(ID, Kind.Profile, NS_URI, BASEPATHMAP, ROOT_ID, FOLDER, FILE, EPACKAGE);
	}

}
