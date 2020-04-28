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

public class UnaryOperation extends Expression
{
    private final Operator operator;
    private final Expression expr;

    public UnaryOperation( Operator operator, Expression expr )
    {
        this.operator = operator;
        this.expr = expr;
    }

    public static enum Operator
    {
        PRE_INCREMENT( "++", Precedence.Precedence03, true ),
        PRE_DECREMENT( "--", Precedence.Precedence03, true ),
        POST_INCREMENT( "++", Precedence.Precedence02 ),
        POST_DECREMENT( "--", Precedence.Precedence02 ),

        LOGICAL_NOT( "!", Precedence.Precedence03, true, true, true ),
        BITWISE_NOT( "~", Precedence.Precedence03, true );

        public final Precedence precedence;
        public final boolean before;
        public final boolean separator;
        public final boolean boolean_op;

        private Operator( String syntax, Precedence p )                 { this( syntax, p, false ); }
        private Operator( String syntax, Precedence p, boolean before ) { this( syntax, p, before, false ); }
        private Operator( String syntax, Precedence p, boolean before, boolean separator ) { this( syntax, p, before, separator, false ); }
        private Operator( String syntax, Precedence p, boolean before, boolean separator, boolean boolean_op )
        {
            this.syntax = syntax;
            this.precedence = p;
            this.before = before; 
            this.separator = separator;
            this.boolean_op = boolean_op;
        }

        private final String syntax;

        public boolean write( CppFormatter fmt )
        {
            return fmt.write( syntax );
        }
    }

    @Override
    protected Type createType()
    {
        return operator.boolean_op ? PrimitiveType.BOOL : expr.getType();
    }

    @Override public Precedence getPrecedence() { return operator.precedence; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return expr.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( operator.before )
            return fmt.write( operator.syntax )
                && ( ! operator.separator || fmt.space() )
                && expr.write( fmt, this );

        return expr.write( fmt, this )
            && ( ! operator.separator || fmt.space() )
            && fmt.write( operator.syntax );
    }
}
