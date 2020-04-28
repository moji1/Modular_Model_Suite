/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class SwitchStatement extends Statement {
	private final Expression condition;
	private final List<SwitchClause> clauses = new ArrayList<>();

	public SwitchStatement(Expression condition) {
		this.condition = condition;
	}

	public void add(SwitchClause clause) {
		if (!clause.isFallthrough()
				&& clause.clauseFallsthrough())
			clause.add(new BreakStatement());

		clauses.add(clause);
	}

	@Override
	public boolean addDependencies(DependencyList deps) {
		if (!condition.addDependencies(deps))
			return false;

		for (SwitchClause clause : clauses)
			if (!clause.addDependencies(deps))
				return false;

		return true;
	}

	@Override
	public boolean write(CppFormatter fmt) {
		if (!fmt.write("switch(")
				|| !fmt.space()
				|| !condition.write(fmt)
				|| !fmt.space()
				|| !fmt.writeLn(')')
				|| !fmt.openBrace()
				|| !fmt.decIndent())
			return false;

		boolean ret = true;
		for (SwitchClause clause : clauses) {
			ret = clause.write(fmt);
			if (!ret)
				break;
		}

		return fmt.incIndent()
				&& fmt.closeBrace()
				&& ret;
	}
}
