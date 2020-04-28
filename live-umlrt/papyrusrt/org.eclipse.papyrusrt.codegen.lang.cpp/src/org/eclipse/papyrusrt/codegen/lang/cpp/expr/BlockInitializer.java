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

public class BlockInitializer extends Expression
{
    private final Type type;
    protected ExpressionList exprs = new ExpressionList();

    public BlockInitializer( Type type ) { this.type = type; }
    public BlockInitializer( Type type, Expression... exprs )
    {
        this.type = type;
        this.exprs.addAll(exprs);
    }

    public void addExpression( Expression expr ) { exprs.add( expr ); }

    public void addExpressions(Expression... exprs) {
        this.exprs.addAll(exprs);
    }

    public int getNumInitializers() { return exprs.size(); }

    @Override protected Type createType() { return type; }

    @Override public Precedence getPrecedence() { return Precedence.Default; } 

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        if( ! type.isArray()
         || type.dereference().isIndirect() )
        {
            if( ! type.addDependencies( deps ) )
                return false;
        }
        else if( ! type.dereference().addDependencies( deps ) )
            return false;
        return exprs == null ? true : exprs.addDependencies(deps);
    }

    @Override
    public boolean write( CppFormatter fmt ) {
        if( !fmt.openBrace() )
            return false;
        if( !exprs.write( fmt ) )
            return false;
        return fmt.closeBrace( false );
    }
}
