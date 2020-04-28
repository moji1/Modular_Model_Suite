/*******************************************************************************
 * Copyright (c) 2014-2017 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.structure.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.papyrusrt.codegen.cpp.ConnectorReporter;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.instance.model.CapsuleInstance;
import org.eclipse.papyrusrt.codegen.instance.model.ICapsuleInstance;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;

/**
 * A deployment represents the mapping of capsule instances to controllers.
 */
public final class Deployment {

	/** The {@link ControllerAllocations}. */
	private final ControllerAllocations controllerAllocations;

	/** The default {@link Controller}. */
	private Controller defaultController = null;

	/** A {@link Map} from controller names to {@link Controller}s. */
	private final Map<String, Controller> controllers = new LinkedHashMap<>();

	/** A {@link Map} from {@link ICapsuleInstance}s to {@link Controller}s. */
	private final Map<ICapsuleInstance, Controller> capsules = new LinkedHashMap<>();

	/**
	 * Constructor.
	 *
	 * @param controllerAllocations
	 *            - The {@link ControllerAllocations}.
	 */
	private Deployment(ControllerAllocations controllerAllocations) {
		this.controllerAllocations = controllerAllocations;
	}

	/**
	 * Build a deployment instance by examining the top capsule and creating the structural
	 * connections for all related capsule instances.
	 * 
	 * @param cpp
	 *            - The {@link CppCodePattern}.
	 * @param topType
	 *            - The Top {@link Capsule} model element.
	 * @param controllerAllocations
	 *            - The {@link ControllerAllocations}.
	 * @return The {@link Deployment}.
	 */
	public static Deployment build(CppCodePattern cpp, Capsule topType, ControllerAllocations controllerAllocations) {
		Deployment deployment = new Deployment(controllerAllocations);

		// Create the instance model and then examine all capsule instances to create
		// controllers.
		CapsuleInstance topInstance = new CapsuleInstance(topType);
		ConnectorReporter connReporter = new ConnectorReporter(topInstance);
		topInstance.connect(connReporter, false);
		deployment.allocate(topInstance);
		connReporter.log(cpp.getOutputFolder());

		return deployment;
	}

	/** @return An {@link Iterable} of the {@link Controller}s in the deployment. */
	public Iterable<Controller> getControllers() {
		return controllers.values();
	}

	/**
	 * Recursively allocates each capsule instance to its controller.
	 * 
	 * @param capsule
	 *            - A {@link ICapsuleInstance}.
	 */
	private void allocate(ICapsuleInstance capsule) {
		Controller controller = getController(capsule);
		controller.add(capsule);
		capsules.put(capsule, controller);

		for (ICapsuleInstance contained : capsule.getContained()) {
			allocate(contained);
		}
	}

	/**
	 * @param capsule
	 *            - A {@link ICapsuleInstance}.
	 * @return The {@link Controller} for the given capsule instance. If the capsule instance is {@code null},
	 *         return the default controller. If the capsule instance has been explicitly allocated to a controller
	 *         (in the {@link ControllerAllocations}), return that controller, otherwise, return the controller for
	 *         the capsule instance's container.
	 */
	public Controller getController(ICapsuleInstance capsule) {
		if (capsule == null) {
			if (defaultController == null) {
				// Bug 475980: Make sure this special "DefaultController" is put into the collection
				// that is tracked by this deployment.
				defaultController = new Controller("DefaultController");
				controllers.put(defaultController.getName(), defaultController);
			}

			return defaultController;
		}

		Controller controller = capsules.get(capsule);
		if (controller != null) {
			return controller;
		}

		// First look for a controller allocation for this instance.
		String controllerName = controllerAllocations.getController(capsule);

		// If there isn't a controller for this capsule instance, then use the one
		// for its container.
		if (controllerName == null) {
			controller = getController(capsule.getContainer());
			capsules.put(capsule, controller);
			return controller;
		}

		// Otherwise either create a new controller or reuse one that has already
		// been created for this name.
		controller = controllers.get(controllerName);
		if (controller == null) {
			controller = new Controller(controllerName);
			controllers.put(controllerName, controller);
		}

		capsules.put(capsule, controller);
		return controller;
	}
}
