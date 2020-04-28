/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BlockInitializer;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ConstructorCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.external.ExternalConstructorCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Variable extends NamedElement implements IUserElement
{
    private final LinkageSpec linkage;
    private final Type type;
    private Expression initializer;

    public Variable( Type type, String ident )
    {
        this( type, ident, null );
    }

    public Variable( Type type, String ident, Expression initializer )
    {
        this( LinkageSpec.UNSPECIFIED, type, ident, initializer );
    }

    public Variable( LinkageSpec linkage, Type type, String ident )
    {
        this( linkage, type, ident, null );
    }

    public Variable( LinkageSpec linkage, Type type, String ident, Expression initializer )
    {
        super( ident );
        this.linkage = linkage;
        this.type = type;
        this.initializer = initializer;
    }

    public LinkageSpec getLinkage() { return linkage; }
    @Override public Type getType() { return type; }
    public Expression getInitializer() { return initializer; }

    public void setInitializer( Expression expr ) { initializer = expr; }

    /**
     * Return the number of initialized instances represented by this instance.  If
     * the receiver is not an array type, then it is a single instance.  Otherwise
     * the receiver represents the number of instances in the array initializer.
     */
    public int getNumInitializedInstances()
    {
        if( ! getType().isArray() )
            return 1;

        if( initializer == null )
            return 0;

        if( initializer instanceof BlockInitializer )
            return ( (BlockInitializer)initializer ).getNumInitializers();

        return 1;
    }

    public boolean addDependencies( DependencyList deps )
    {
        return type.addDependencies( deps )
            && ( initializer == null
              || initializer.addDependencies( deps ) );
    }

    public boolean write( CppFormatter fmt )
    {
        return linkage.write( fmt )
            && fmt.space()
            && type.write( fmt, getName() )
            && writeInitializer( fmt )
            && fmt.terminate();
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        // statics are only in the defn, so all dependencies go there
        if( linkage == LinkageSpec.STATIC )
            return type.addDependencies( deps.defn() )
                && ( initializer == null
                  || initializer.addDependencies( deps.defn() ) );

        // otherwise it is split between .hh and .cc
        return type.addDependencies( deps.decl() )
            && ( initializer == null
              || initializer.addDependencies( deps.defn() ) );
    }

    @Override
    public boolean write( CppWriter out )
    {
        // statics are generated only to the defn
        if( linkage == LinkageSpec.STATIC )
            return linkage.write( out.defn() )
                && out.defn().space()
                && type.write( out.defn(), getName() )
                && writeInitializer( out.defn() )
                && out.defn().terminate();

        // otherwise split the variable between the .hh and .cc
        return linkage.write( out.decl() )
            && out.decl().space()
            && type.write( out.decl(), getName() )
            && out.decl().terminate()

            && type.write( out.defn(), getName() )
            && writeInitializer( out.defn() )
            && out.defn().terminate();
    }

    private boolean writeInitializer( CppFormatter fmt )
    {
        if( initializer instanceof ConstructorCall )
            return ( (ConstructorCall)initializer ).writeAsInitializer( fmt );
        if( initializer instanceof ExternalConstructorCall )
            return ( (ExternalConstructorCall)initializer ).writeAsInitializer( fmt );
        if( initializer != null )
            return fmt.space()
                && fmt.write( '=' )
                && fmt.space()
                && initializer.write( fmt );
        return true;
    }
}
