/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Macro;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class MacroExpansion extends Expression
{
    private final Macro macro;
    private List<Expression> arguments = null;

    public MacroExpansion( Macro macro )
    {
        this.macro = macro;
    }

    public void addArgument( Expression arg )
    {
        if( arguments == null )
            arguments = new ArrayList<Expression>();

        arguments.add( arg );
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Expression expr : arguments )
            if( ! expr.addDependencies( deps ) )
                return false;
        return true;
    }

    @Override
    protected Type createType()
    {
        Expression replacement = macro.getReplacement();
        return replacement == null ? null : replacement.getType();
    }

    @Override
    public Precedence getPrecedence()
    {
        Expression replacement = macro.getReplacement();
        return replacement == null ? Precedence.Default : replacement.getPrecedence();
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( ! fmt.write( macro.getName() ) )
            return false;

        if( arguments == null )
        {
            if( macro.expectsArguments()
             && ! fmt.write( "()" ) )
                return false;
            return true;
        }

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
