/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public abstract class AbstractFunctionCall extends Expression
{
    private final List<Expression> arguments = new ArrayList<Expression>();

    public AbstractFunctionCall( Expression...args )
    {
        arguments.addAll( Arrays.asList( args ) );
    }

    public void addArgument( Expression arg ) { arguments.add( arg ); }
    public boolean hasArguments() { return ! arguments.isEmpty(); }

    @Override public Precedence getPrecedence() { return Precedence.Precedence02; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Expression expr : arguments )
            if( ! expr.addDependencies( deps ) )
                return false;
        return true;
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( ! fmt.write( '(' ) )
            return false;

        boolean first = true;
        for( Expression arg : arguments )
        {
            if( first )
                first = false;
            else if( ! fmt.write( ',' ) )
                return false;

           if( ! fmt.space()
            || ! arg.write( fmt ) )
               return false;
        }

        return fmt.spaceUnless( '(' )
            && fmt.write( ')' );
    }
}
