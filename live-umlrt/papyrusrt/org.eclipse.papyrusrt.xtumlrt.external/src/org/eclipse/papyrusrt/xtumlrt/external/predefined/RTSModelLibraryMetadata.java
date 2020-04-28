/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external.predefined;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.papyrusrt.xtumlrt.external.ExternalPackageMetadata;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger;
import org.eclipse.uml2.uml.Model;

/**
 * Meta-data for the UML-RT RTS Model Library.
 * 
 * @see ExternalPackageMetadata
 * @author epp
 */
public final class RTSModelLibraryMetadata extends ExternalPackageMetadata {

	/** Common instance. */
	public static final RTSModelLibraryMetadata INSTANCE = new RTSModelLibraryMetadata();

	/** Profile id. */
	private static final String ID = "org.eclipse.papyrusrt.umlrt.common.rts.library";

	/** Name-space URI. */
	private static final String NS_URI = "http://www.eclipse.org/papyrusrt/rts";

	/** Pathmap. */
	private static final String BASEPATHMAP = "pathmap://UMLRTRTSLIB";

	/** Root node id. */
	// TODO: Find out if this can be extracted programatically
	private static final String ROOT_ID = "_mPjAgGXmEeS_4daqvwyFrg";

	/** Subdirectory containing the model. */
	private static final String FOLDER = "libraries";

	/** File containing the model. */
	private static final String FILE = "UMLRT-RTS.uml";

	/** {@link EPackage}. */
	private static final EPackage EPACKAGE = null;

	private RTSModelLibraryMetadata() {
		super(ID, Kind.Library, NS_URI, BASEPATHMAP, ROOT_ID, FOLDER, FILE, EPACKAGE);
	}

	@Override
	public void setup() {
		EObject root = getRootElement();
		if (root instanceof Model) {
			Model model = (Model) root;
			RTSModelLibraryUtils.setRTSModelLibrary(model);
		} else {
			XTUMLRTLogger.error("The RTS model library was not loaded or registered.");
		}
	}

}
