/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;

public class CppNamespace extends NamedElement implements IUserElement
{
    private final List<IGeneratableElement> members = new ArrayList<IGeneratableElement>();

    public CppNamespace( String ident )
    {
        super( ident );
    }

    public CppNamespace( HeaderFile header, Name name )
    {
        super( header, name );
    }

    public void addMember( IGeneratableElement member )
    {
        if( member instanceof NamedElement )
            ( (NamedElement)member ).setParent( this );

        members.add( member );
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        for( IGeneratableElement member : members )
            if( ! member.addDependencies( deps ) )
                return false;

        return true;
    }

    @Override
    public boolean write( CppWriter out )
    {
        if( ! out.decl().write( "namespace" )
         || ! out.decl().space()
         || ! out.decl().write( getName() )
         || ! out.decl().openBrace() )
          return false;

        out.decl().enter( getName() );
        try
        {
            for( IGeneratableElement member : members )
                if( ! member.write( out ) )
                    return false;

            return out.decl().closeBrace( false )
                && out.decl().terminate();
        }
        finally { out.decl().exit(); }
    }
}
