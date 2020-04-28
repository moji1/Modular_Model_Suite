/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ExpressionStatement extends Statement
{
    private final Expression expr;

    public ExpressionStatement( Expression expr ) { this.expr = expr; }

    // Allow null expression for reuse by things like ReturnStatement.
    protected ExpressionStatement() { this( null ); }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return expr == null ? true : expr.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( expr == null )
            return fmt.terminate();

        return expr.write( fmt )
            && fmt.terminate();
    }
}
