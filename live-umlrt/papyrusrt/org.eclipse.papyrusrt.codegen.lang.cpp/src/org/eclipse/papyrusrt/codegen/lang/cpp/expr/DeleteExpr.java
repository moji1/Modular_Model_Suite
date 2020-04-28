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
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class DeleteExpr extends Expression
{
    private final Expression expr;

    public DeleteExpr( Expression expr ) { this.expr = expr; }

    @Override
    protected Type createType()
    {
        return PrimitiveType.VOID;
    }

    @Override public Precedence getPrecedence() { return Precedence.Precedence03; }

    /**
     * A delete expression introduces no dependencies of its own.
     */
    @Override public boolean addDependencies( DependencyList deps ) { return expr.addDependencies( deps ); }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( "delete" )
            && fmt.space()
            && expr.write( fmt, this );
    }
}
