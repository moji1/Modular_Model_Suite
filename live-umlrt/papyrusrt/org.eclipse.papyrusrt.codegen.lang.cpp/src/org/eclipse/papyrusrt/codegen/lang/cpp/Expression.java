/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * Expressions are the leaf for nearly all code.
 *
 * <p>The C++ grammar defines expressions which are contained in statements which are
 * contained in elements.  All expressions have a type.
 *
 * @see Element
 * @see Statement
 * @see Type
 */
public abstract class Expression implements IGeneratable
{
    private Type type;

    public Type getType()
    {
        if( type == null )
            type = createType();
        return type;
    }

    protected abstract Type createType();

    /**
     * Levels of precedence will be specified in order and grouped by category.  The
     * C++11 spec says that order is not defined but can be derived from the syntax.
     */
    public abstract Precedence getPrecedence();// { return Precedence.Default; }
    public static enum Precedence
    {
        // From http://en.cppreference.com/w/cpp/language/operator_precedence
        Default,
        Precedence01, Precedence02, Precedence03, Precedence04,
        Precedence05, Precedence06, Precedence07, Precedence08,
        Precedence09, Precedence10, Precedence11, Precedence12,
        Precedence13, Precedence14, Precedence15, Precedence16,
        Precedence17,
        Lowest;

        public boolean needsParens( Precedence neighbour )
        {
            if( this == Default
             || neighbour == Default )
                return false;

            return ordinal() > neighbour.ordinal();
        }
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return false;
    }

    public boolean write( CppFormatter fmt, Expression containingExpr )
    {
        return containingExpr == null
                ? write( fmt )
                : fmt.write( this, getPrecedence().needsParens( containingExpr.getPrecedence() ) );
    }
}
