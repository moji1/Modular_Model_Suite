/*****************************************************************************
 * Copyright (c) 2016 Codics Corp and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   William Byrne - Initial API and implementation
 *   Ernesto Posse - Refactoring to emulate ConditionalStatements
 *
 *****************************************************************************/

package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * Represents C++ pre-processor conditional directives.
 * 
 * @author epp
 */
public class ConditionalDirective extends Expression {

	public enum Directive {
		IFDEF, IFNDEF, IF;
		@Override
		public final String toString() {
			return "#" + name().toLowerCase();
		}
	}

	private enum BranchDirective {
		ELIF, ELSE, ENDIF;
		@Override
		public final String toString() {
			return "#" + name().toLowerCase();
		}
	}

	/** The kind of directive. */
	private final Directive directive;
	private final List<Branch> branches = new ArrayList<>();
	private Branch defaultBranch = null;

	/**
	 * Constructor to be used then directive is #IF
	 *
	 * @param directive
	 *            - A {@link Directive}
	 */
	public ConditionalDirective() {
		this(Directive.IF);
	}

	public ConditionalDirective(final Directive directive) {
		this.directive = directive;
	}

	public ConditionalDirective(final Directive directive, String condition, Expression... expressions) {
		this.directive = directive;
		ExpressionList list = this.add(new ExpressionBlob(condition));
		list.addAll(expressions);
	}

	@Override
	protected Type createType() {
		return null;
	}

	@Override
	public Precedence getPrecedence() {
		return null;
	}

	/**
	 * Creates a new if or "else if" with the given condition and returns
	 * a Expression for adding statements to the body.
	 */
	public ExpressionList add(Expression condition) {
		Branch branch = new Branch(condition);
		branches.add(branch);
		return branch;
	}

	/** Returns the conditional's single else block. */
	public ExpressionList defaultBlock() {
		if (defaultBranch == null)
			defaultBranch = new Branch(null);
		return defaultBranch;
	}

	@Override
	public boolean addDependencies(DependencyList deps) {
		for (Branch branch : branches)
			if (!branch.addDependencies(deps))
				return false;
		return defaultBranch == null ? true : defaultBranch.addDependencies(deps);
	}

	@Override
	public boolean write(CppFormatter fmt) {
		boolean first = true;
		for (Branch branch : branches) {
			if (first) {
				first = false;
				// directive should be only one of #IF, #IFDEF or #IFNDEF
				if (!fmt.write(directive.toString())) {
					return false;
				}
			} else if (!fmt.write(BranchDirective.ELIF.toString())) {
				return false;
			}
			if (!fmt.space()
					|| !branch.condition.write(fmt)
					|| !fmt.newline()
					|| !branch.write(fmt)
					|| !fmt.newline())
				return false;
		}

		if (defaultBranch != null) {
			if (!fmt.write(BranchDirective.ELSE.toString())
					|| !fmt.newline()
					|| !defaultBranch.write(fmt)
					|| !fmt.newline())
				return false;
		}
		return fmt.write(BranchDirective.ENDIF.toString()) && fmt.newline();
	}

	private static class Branch extends ExpressionList {
		public final Expression condition;

		public Branch(Expression condition) {
			this.condition = condition;
		}

		@Override
		public boolean addDependencies(DependencyList deps) {
			return (condition == null ? true : condition.addDependencies(deps))
					&& super.addDependencies(deps);
		}
	}

}
