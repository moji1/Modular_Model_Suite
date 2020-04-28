/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyBlob;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;

public class DeclarationBlob implements IGeneratableElement
{

    private final DependencyBlob dependencies;
    private final String code;

    public DeclarationBlob( String code )
    {
        this( null, code );
    }

    public DeclarationBlob( DependencyBlob dependencies, String code )
    {
        this.dependencies = dependencies;
        this.code = code;
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return dependencies == null ? true : deps.decl().add( dependencies );
    }

    @Override
    public boolean write( CppWriter out )
    {
        if( code == null ) return true;
        String[] lines = code.split( "\n" );
        for( String text: lines )
        {
            if( ! out.decl().writeLn( text ) ) return false;
        }
        return out.decl().newline();
    }

}
