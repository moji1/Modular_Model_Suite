/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.standalone.internal;

import org.eclipse.papyrusrt.codegen.cpp.AbstractElementGenerator;
import org.eclipse.papyrusrt.codegen.cpp.XTUMLRT2CppCodeGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.cpp.GeneratorManager;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.generator.StateMachineGenerator;
import org.eclipse.papyrusrt.codegen.cpp.structure.CompositionGenerator;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;

/**
 * Generator manager for the stand-alone generator.
 * 
 * @see org.eclipse.papyrusrt.codegen.cpp.GeneratorManager
 * 
 * @author epp
 *
 */
public class StandaloneGeneratorManager extends GeneratorManager {

	/**
	 * Constructor.
	 */
	public StandaloneGeneratorManager() {
	}

	@Override
	public AbstractElementGenerator getGenerator(
			XTUMLRT2CppCodeGenerator.Kind kind,
			CppCodePattern cpp,
			NamedElement element,
			NamedElement context) {
		AbstractElementGenerator generator = null;
		if (element != null) {
			switch (kind) {
			case Structural:
				if (element instanceof Capsule) {
					generator = new CompositionGenerator.Factory()
							.create(cpp, (Capsule) element, null);
				}
				break;
			case StateMachine:
				if (element instanceof StateMachine || context instanceof Capsule) {
					generator = new StateMachineGenerator.Factory()
							.create(cpp, (StateMachine) element, (Capsule) context);
				}
				break;
			default:
				generator = super.getGenerator(kind, cpp, element, context);
				break;
			}
		}
		return generator;
	}

}
