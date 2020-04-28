/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type.Pointer;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.IReferencable;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.CodeBlock;

public class Destructor extends UserElement implements IReferencable, IGeneratableElement
{
    private CppClass cls;
    private Name name;
    private final CodeBlock body;
    private boolean isVirtual = false;

    public Destructor()
    {
        this.body = CodeBlock.forceBraces();
    }

    public CppClass getCppClass() { return cls; }
    public void setCppClass( CppClass parent )
    {
        cls = parent;
        name = new Name( "~" + parent.getName().getIdentifier() );
        name.setParent( parent );
    }

    public void add( Statement... stmts ) { body.add( stmts ); }
    public void add( Expression expr ) { body.add( expr ); }
    public void add( Variable var ) { body.add( var ); }

    public void setVirtual() { isVirtual = true; }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return body.addDependencies( deps.defn() );
    }

    protected boolean writeSignature( CppFormatter fmt )
    {
        return fmt.write( name )
            && fmt.write( '(' )
            && fmt.write( ')' );
    }

    @Override
    public boolean write( CppWriter out )
    {
        if( isVirtual
            && ( ! out.decl().write( "virtual" )
                 || ! out.decl().space() ) )
            return false;
        if( !writeSignature( out.decl() )
            || !out.decl().terminate() )
            return false;

        if( !writeSignature( out.defn() ) )
            return false;

        out.defn().enter( name.getParent() );

        boolean ret = body.write( out.defn() );
        out.defn().exit();
        return ret;
    }

    @Override
    public boolean write( CppFormatter fmt, Name name, List<Pointer> pointerSpec, List<Expression> arrayBounds )
    {
        return false;
    }

}
