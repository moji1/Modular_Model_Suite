/*******************************************************************************
 * Copyright (c) 2014-2017 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.structure.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.instance.model.ICapsuleInstance;

/**
 * The controllers allocations specifies the list of controllers and a map associating each
 * capsule role (capsule part + index) to a controller.
 */
public class ControllerAllocations {

	/** The default allocation which maps all capsule roles to the DefaultController. */
	public static final ControllerAllocations DEFAULT = new ControllerAllocations() {
		@Override
		public String getController(ICapsuleInstance capsule) {
			return "DefaultController";
		}
	};

	/** A {@link Map} from capsule role names to controller names. */
	private final Map<String, String> allocations = new HashMap<>();

	/** A {@link List} of {@link Controller}s. */
	private final List<Controller> controllers = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public ControllerAllocations() {

	}

	/** @return An {@link Iterable} of the {@link Controller}s in this allocation. */
	public Iterable<Controller> getControllers() {
		return controllers;
	}

	/**
	 * Load an allocation assignment from a text file. The file consists of lines with the format:
	 * 
	 * <capsule-role-qualified-name> = <controller-name>
	 * 
	 * @param allocationsFile
	 *            - A text {@link File}.
	 * @return A new {@link ControllerAllocations} instance.
	 */
	public static ControllerAllocations load(File allocationsFile) {
		if (allocationsFile == null
				|| !allocationsFile.exists()) {
			return null;
		}

		Properties properties = new Properties();
		FileReader reader = null;
		try {
			reader = new FileReader(allocationsFile);
			properties.load(reader);
		} catch (IOException e) {
			CodeGenPlugin.error(e);
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					CodeGenPlugin.error(e);
				}
			}
		}

		Set<String> controllerNames = new HashSet<>();
		ControllerAllocations allocs = new ControllerAllocations();
		for (String key : properties.stringPropertyNames()) {
			String controllerName = properties.getProperty(key);
			allocs.allocations.put(key, controllerName);

			if (controllerNames.add(controllerName)) {
				allocs.controllers.add(new Controller(controllerName));
			}
		}

		return allocs;
	}

	/**
	 * @param capsule
	 *            - An {@link ICapsuleInstance}.
	 * @return The controller allocated to this capsule instance or {@code null} if there is
	 *         no such allocation.
	 */
	public String getController(ICapsuleInstance capsule) {
		// Look for an allocation for this specific instance.
		String controllerName = allocations.get(capsule.getQualifiedName('.'));
		if (controllerName != null) {
			return controllerName;
		}

		// Otherwise look for a generic allocation for this capsule type.
		String typeName = capsule.getType().getName();
		controllerName = allocations.get(typeName);
		if (controllerName != null) {
			return controllerName;
		}

		// Otherwise there is no allocation so return null.
		return null;
	}
}
