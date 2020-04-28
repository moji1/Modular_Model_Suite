/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
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

public class ConditionalOperator extends Expression
{
    private final Expression expr1;
    private final Expression expr2;
    private final Expression expr3;

    public ConditionalOperator( Expression expr1, Expression expr2, Expression expr3 )
    {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.expr3 = expr3;
    }

    @Override
    protected Type createType()
    {
        // TODO 5.16 of N3337 says conversions can be applied to find something common between
        //      expr2 and expr3.
        return expr2.getType();
    }

    @Override public Precedence getPrecedence() { return Precedence.Precedence15; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return expr1.addDependencies( deps )
            && expr2.addDependencies( deps )
            && expr3.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return expr1.write( fmt, this )
            && fmt.space()
            && fmt.write( '?' )
            && fmt.space()
            && expr2.write( fmt, this )
            && fmt.space()
            && fmt.write( ':' )
            && fmt.space()
            && expr3.write( fmt, this );
    }
}
