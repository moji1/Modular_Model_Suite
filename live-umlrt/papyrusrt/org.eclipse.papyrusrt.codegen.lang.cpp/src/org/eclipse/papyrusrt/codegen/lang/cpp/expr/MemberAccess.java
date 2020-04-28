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
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependency;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class MemberAccess extends Expression
{
    private final ElementAccess impl;

    public MemberAccess( Expression container, NamedElement field )
    {
        impl = new NonStaticAccess( container, field );
    }

    public MemberAccess( NamedElement container, NamedElement field )
    {
        impl = new StaticAccess( container, field );
    }

    @Override protected Type createType() { return impl.createType(); }
    @Override public Precedence getPrecedence() { return Precedence.Precedence02; }
    @Override public boolean addDependencies( DependencyList deps ) { return impl.addDependencies( deps ); }
    @Override public boolean write( CppFormatter fmt ) { return impl.write( fmt ); }

    private class NonStaticAccess extends ElementAccess
    {
        private final Expression container;

        public NonStaticAccess( Expression container, NamedElement field )
        {
            super( field );
            this.container = container;
        }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            // The container element is needed in order to get a field.  The field is not
            // needed until it is used.
            return container.addDependencies( deps )
                && deps.add( new ElementDependency( container.getType().getElement() ) );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return container.write( fmt, MemberAccess.this )
                && fmt.write( container.getType().isIndirect() ? "->" : "." )
                && super.write( fmt );
        }
    }

    private static class StaticAccess extends ElementAccess
    {
        private final NamedElement container;

        public StaticAccess( NamedElement container, NamedElement field )
        {
            super( field );
            this.container = container;
        }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            // The container element is needed in order to get a field.
            return deps.add( new ElementDependency( container.getType().getElement() ) );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return fmt.write( getName() );
        }
    }
}
