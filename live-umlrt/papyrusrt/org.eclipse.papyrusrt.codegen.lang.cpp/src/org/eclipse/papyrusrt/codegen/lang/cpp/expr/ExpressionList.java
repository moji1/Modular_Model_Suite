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
 *
 *****************************************************************************/

package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ExpressionList {

    protected final ArrayList<Expression> exprs = new ArrayList<>();

    public ExpressionList(Expression... exprs) {
        this.exprs.addAll(Arrays.asList(exprs));
    }

    public void add(Expression expr) {
        exprs.add(expr);
    }

    public void addAll(Expression... exprs) {
        this.exprs.addAll(Arrays.asList(exprs));
    }

    public Expression set(int index, Expression expr) {
        return exprs.set(index, expr);
    }

    public int size() {
        return exprs.size();
    }

    public boolean addDependencies(DependencyList deps) {
        for (Expression expr : exprs)
            if (!expr.addDependencies(deps))
                return false;
        return true;
    }

    public boolean write(CppFormatter fmt) {
        boolean first = true;
        for (Expression init : exprs) {
            if (first)
                first = false;
            else if (!fmt.writeLn(','))
                return false;
            if (!init.write(fmt))
                return false;
        }
        return true;
    }

}