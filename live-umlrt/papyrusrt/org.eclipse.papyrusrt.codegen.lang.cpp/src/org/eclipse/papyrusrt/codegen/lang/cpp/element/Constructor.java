/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type.Pointer;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AbstractFunctionCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.IReferencable;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.CodeBlock;

public class Constructor extends UserElement implements IReferencable, IGeneratableElement
{
    // TODO Large parts of this implementation are copy/paste from AbstractFunction.  The problem
    //      with AbsFunction is that it has a name and constructors do not (they use the containing
    //      class name).  The implementations should be combined.

    private CppClass cls;
    private Name name;
    private final List<AbstractFunctionCall> baseInitializers = new ArrayList<AbstractFunctionCall>();
    private final List<Initializer> fieldInitializers = new ArrayList<Initializer>();
    private final List<Parameter> parameters = new ArrayList<Parameter>();
    private final CodeBlock body;

    public Constructor()
    {
        this.body = CodeBlock.forceBraces();
    }

    /**
     * Make it a copy constructor
     * @param cls The {@link CppClass} that owns the constructor
     * @return the {@link Parameter} of the copy constructor
     */
    public Parameter setCopyConstructor( CppClass cls )
    {
        setCppClass( cls );
        Parameter param = new Parameter( cls.getType().const_().ref(), "other" );
        add( param );
        return param;
    }

    public CppClass getCppClass() { return cls; }
    public void setCppClass( CppClass parent )
    {
        cls = parent;
        name = new Name( parent.getName().getIdentifier() );
        name.setParent( parent );
    }

    public void addBaseInitializer( AbstractFunctionCall baseCtor ) { baseInitializers.add( baseCtor ); }

    // TODO validate that field is non-static
    public void addFieldInitializer( MemberField field, Expression... inits )
    {
        fieldInitializers.add( new Initializer( field, inits ) );
    }

    public void add( Statement... stmts )      { body.add( stmts ); }
    public void add( Expression expr )         { body.add( expr ); }
    public void add( Variable var ) { body.add( var ); }

    public void add( Parameter param ) { parameters.add( param ); }

    private static class Initializer implements IGeneratable
    {
        private final MemberField field;
        private final List<Expression> exprs;
        public Initializer( MemberField field, Expression[] inits )
        {
            this.field = field;
            this.exprs = new ArrayList<Expression>( Arrays.asList( inits ) );
        }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            for( Expression expr : exprs )
                if( ! expr.addDependencies( deps ) )
                    return false;

            return true;
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            if( ! fmt.write( field.getName() )
             || ! fmt.write( '(' )
             || ! fmt.pendingSpace() )
                return false;

            boolean first = true;
            for( Expression expr : exprs )
            {
                if( first )
                    first = false;
                else if( ! fmt.write( ',' )
                      || ! fmt.space() )
                    return false;

                if( ! expr.write( fmt ) )
                    return false;
            }

            return fmt.spaceUnless( ' ' )
                && fmt.write( ')' );
        }
    }

    protected boolean writeSignature( CppFormatter fmt )
    {
        if( ! fmt.write( name )
         || ! fmt.write( '(' ) )
            return false;

        boolean first = true;
        for( Parameter param : parameters )
        {
            if( first )
            {
                first = false;
                if( ! fmt.space() )
                    return false;
            }
            else if( ! fmt.write( ", " ) )
                return false;

            if( ! param.write( fmt ) )
                return false;
        }

        return fmt.spaceUnless( '(' )
            && fmt.write( ')' );
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        for( Parameter param : parameters )
            if( ! param.addDependencies( deps.decl() ) )
                return false;

        for( AbstractFunctionCall baseInit : baseInitializers )
            if( ! baseInit.addDependencies( deps.defn() ) )
                return false;

        for( Initializer init : fieldInitializers )
            if( ! init.addDependencies( deps.defn() ) )
                return false;

        return body.addDependencies( deps.defn() );
    }

    @Override
    public boolean write( CppWriter out )
    {
        if( ! writeSignature( out.decl() )
         || ! out.decl().terminate() )
            return false;

        if( ! writeSignature( out.defn() ) )
            return false;

        out.defn().enter( name.getParent() );

        char separator = ':';
        for( AbstractFunctionCall ctor : baseInitializers )
        {
            if( ! out.defn().newline()
             || ! out.defn().write( separator )
             || ! out.defn().space()
             || ! ctor.write( out.defn() ) )
                return false;
            separator = ',';
        }

        // TODO validate that the order of this list matches the order of definition in the class
        // TODO validate that these are not static members
        for( Initializer init : fieldInitializers )
        {
            if( ! out.defn().newline()
             || ! out.defn().write( separator )
             || ! out.defn().space()
             || ! init.write( out.defn() ) )
                return false;

            separator = ',';
        }

        boolean ret = body.write( out.defn() );
        out.defn().exit();
        return ret;
    }

    @Override
    public boolean write( CppFormatter fmt, Name name, List<Pointer> pointerSpec, List<Expression> arrayBounds )
    {
        if( ! fmt.write( name )
         || ! fmt.pendingSpace() )
            return false;

        // TODO is this right?
        boolean first = true;
        for( Type.Pointer ptr : pointerSpec )
        {
            if( first )
                first = false;
            else if( ! fmt.space() )
                return false;

            if( ! ptr.write( fmt ) )
                return false;
        }

        if( ! fmt.pendingSpace()
         || ! fmt.write( name ) )
            return false;

        for( int i = arrayBounds.size(); i > 0; --i )
            if( ! fmt.write( "[]" ) )
                return false;

        return true;
    }
}
