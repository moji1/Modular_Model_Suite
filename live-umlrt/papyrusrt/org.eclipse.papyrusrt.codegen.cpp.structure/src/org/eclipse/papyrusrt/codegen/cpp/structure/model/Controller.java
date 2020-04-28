/*******************************************************************************
 * Copyright (c) 2014-2017 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.structure.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.instance.model.ICapsuleInstance;

/**
 * Instances of this class represent runtime controllers, and store the list of
 * capsule instances ({@link ICapsuleInstance) associated to the controller.
 */
public class Controller {

	/** The controller's name. */
	private final String name;

	/** The {@link List} of {@link ICapsuleInstance}s managed by this controller. */
	private final List<ICapsuleInstance> capsules = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param name
	 *            - The controller's name.
	 */
	public Controller(String name) {
		this.name = name;
	}

	/**
	 * Adds a capsule instance to the controller.
	 * 
	 * @param capsule
	 *            - A {@link ICapsuleInstance}.
	 */
	public void add(ICapsuleInstance capsule) {
		capsules.add(capsule);
	}

	public String getName() {
		return name;
	}

	public Iterable<ICapsuleInstance> getCapsules() {
		return capsules;
	}

	public int getNumCapsules() {
		return capsules.size();
	}
}
