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

public class LogicalComparison extends Expression
{
    private final Operator operator;
    private final Expression lhs;
    private final Expression rhs;

    public LogicalComparison( Expression lhs, Operator operator, Expression rhs )
    {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public static enum Operator
    {
        AND( "&&", Precedence.Precedence13 ),
        OR(  "||", Precedence.Precedence14 ),

        LESS_THAN(           "<", Precedence.Precedence08 ),
        GREATER_THAN(        ">", Precedence.Precedence08 ),
        LESS_THAN_EQUAL(    "<=", Precedence.Precedence08 ),
        GREATER_THAN_EQUAL( ">=", Precedence.Precedence08 ),

        EQUIVALENT(     "==", Precedence.Precedence09 ),
        NOT_EQUIVALENT( "!=", Precedence.Precedence09 );

        private Operator( String token, Precedence precedence ) { this.token = token; this.precedence = precedence; }
        private final String token;
        public final Precedence precedence;

        public boolean write( CppFormatter fmt )
        {
            return fmt.write( token );
        }
    }

    @Override
    protected Type createType()
    {
        return PrimitiveType.BOOL;
    }

    @Override public Precedence getPrecedence() { return operator.precedence; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return lhs.addDependencies( deps )
            && rhs.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return lhs.write( fmt, this )
            && fmt.space()
            && operator.write( fmt )
            && fmt.space()
            && rhs.write( fmt, this );
    }
}
