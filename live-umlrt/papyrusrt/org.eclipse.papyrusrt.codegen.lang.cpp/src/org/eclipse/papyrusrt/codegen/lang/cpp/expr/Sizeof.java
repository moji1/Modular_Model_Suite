/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Sizeof extends Expression
{
    private final IGeneratable impl;

    public Sizeof( Type type )       { impl = new SizeofType( type ); }
    public Sizeof( Element element ) { this( element.getType() ); }
    public Sizeof( Expression expr )         { impl = new SizeofExpr( expr ); }
    public Sizeof( Variable var ) { this( new ElementAccess( var ) ); }
    public Sizeof( NamedElement element )    { this( new ElementAccess( element ) ); }

    @Override
    protected Type createType()
    {
        // TODO need a place for std types like std::size_t
        return PrimitiveType.UINT;
    }

    @Override public Precedence getPrecedence() { return Precedence.Default; }
    @Override public boolean addDependencies( DependencyList deps ) { return impl.addDependencies( deps ); }
    @Override public boolean write( CppFormatter fmt ) { return impl.write( fmt ); }

    private static class SizeofExpr implements IGeneratable
    {
        private final Expression expr;

        public SizeofExpr( Expression expr ) { this.expr = expr; }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            return expr.addDependencies( deps );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return fmt.write( "sizeof(" )
                    && fmt.space()
                    && expr.write( fmt )
                    && fmt.space()
                    && fmt.write( ')' );
        }
    }

    private static class SizeofType implements IGeneratable
    {
        private final Type type;

        public SizeofType( Type type ) { this.type = type; }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            return type.addDependencies( deps );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return fmt.write( "sizeof(" )
                    && fmt.space()
                    && type.write( fmt, null )
                    && fmt.space()
                    && fmt.write( ')' );
        }
    }
}
