/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependency;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class MemberFunctionCall extends AbstractFunctionCall
{
    private final Impl receiver;
    private final MemberFunction function;

    public MemberFunctionCall( Expression container, MemberFunction function )
    {
        this.receiver = new Expr( container );
        this.function = function;
    }

    public MemberFunctionCall( NamedElement element, MemberFunction function )
    {
        this.receiver = new QualName( element );
        this.function = function;
    }

    @Override
    protected Type createType()
    {
        return function.getReturnType();
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return receiver.addDependencies( deps )
            && deps.add( new ElementDependency( function ) )
            && super.addDependencies( deps );
    }

    private static interface Impl
    {
        public boolean addDependencies( DependencyList deps );
        public boolean write( CppFormatter fmt );
    }

    private class Expr implements Impl
    {
        private final Expression receiver;

        public Expr( Expression receiver ) { this.receiver = receiver; }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            return receiver.addDependencies( deps );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return receiver.write( fmt, MemberFunctionCall.this )
                && fmt.write( receiver.getType().isIndirect() ? "->" : "." );
        }
    }

    private static class QualName implements Impl
    {
        private final NamedElement element;
        private final Name receiver;

        public QualName( NamedElement element )
        {
            this.element = element;
            this.receiver = element.getName();
        }

        @Override public boolean addDependencies( DependencyList deps )
        {
            return element.getType().addDependencies( deps );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return fmt.write( receiver )
                && fmt.write( "::" );
        }
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return receiver.write( fmt )
            && fmt.write( function.getName().getIdentifier() )
            && super.write( fmt );
    }
}
