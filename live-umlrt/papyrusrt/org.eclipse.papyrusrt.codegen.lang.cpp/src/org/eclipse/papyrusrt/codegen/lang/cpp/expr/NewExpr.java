/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class NewExpr extends Expression {
	private final AbstractFunctionCall call;

	public NewExpr(AbstractFunctionCall call) {
		this.call = call;
	}

	@Override
	protected Type createType() {
		throw new UnsupportedOperationException();
		// return call.createType().ptr();
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Precedence03;
	}

	@Override
	public boolean addDependencies(DependencyList deps) {
		return call.addDependencies(deps);
	}

	@Override
	public boolean write(CppFormatter fmt) {
		return fmt.write("new")
				&& fmt.space()
				&& call.write(fmt);
	}
}
