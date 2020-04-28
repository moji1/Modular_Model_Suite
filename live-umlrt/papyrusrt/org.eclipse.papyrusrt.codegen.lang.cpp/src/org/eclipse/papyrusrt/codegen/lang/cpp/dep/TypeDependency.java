/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.dep;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.io.CodeFormatter;

public class TypeDependency extends Dependency
{
    private final Type type;

    public TypeDependency( Type type )
    {
        super( type.isIndirect() ? Dependency.Kind.Reference : Dependency.Kind.Use );
        this.type = type;
    }

    @Override protected Element getComparisonElement() { return type.getElement(); }

    @Override public HeaderFile getHeader() { return type.getElement().getDefinedIn(); }

    private static boolean writeInclusion( CodeFormatter fmt, Element element )
    {
        HeaderFile header = element.getDefinedIn();
        return header == null ? true : header.writeInclude( fmt );
    }

    @Override
    protected int compareReference( Dependency dep )
    {
        Element element1 = getComparisonElement();
        Element element2 = dep.getComparisonElement();

        // null sorts later
        if( element1 == null )
            return element2 == null ? 0 : 1;
        else if( element2 == null )
            return 1;

        // Forward declarations sort after #includes, so if we have one of each kind then
        // comparison is hard coded.  Otherwise the forward declartions are compared.
        IForwardDeclarable fwdDecl1 = element1 instanceof IForwardDeclarable ? (IForwardDeclarable)element1 : null;
        IForwardDeclarable fwdDecl2 = element2 instanceof IForwardDeclarable ? (IForwardDeclarable)element2 : null;
        if( fwdDecl1 != null )
            return fwdDecl2 == null ? 1 : fwdDecl1.compareTo( fwdDecl2 );
        if( fwdDecl2 != null )
            return -1;

        // In cases where neither element can be forward declared we fallback to comparing
        // the #include directives.
        return compareUse( dep );
    }

    @Override
    protected boolean isReferenceProvidedInUse( Dependency use )
    {
        Element element1 = getComparisonElement();
        Element element2 = use.getComparisonElement();
        if( element1 == null
         || element2 == null )
            return false;

        HeaderFile header1 = element1.getDefinedIn();
        HeaderFile header2 = element2.getDefinedIn();
        if( header1 == null
         || header2 == null )
            return false;

        return header1 == header2;
    }

    @Override
    protected boolean writeReference( CppFormatter fmt )
    {
        Element element = type.getElement();
        if( element instanceof IForwardDeclarable )
            return ( (IForwardDeclarable)element ).writeForwardDeclaration( fmt );

        return writeInclusion( fmt, element );
    }

    @Override
    protected int compareUse( Dependency dep )
    {
        // Blobs always compare after other dependencies.
        if( dep instanceof DependencyBlob )
            return -1;

        Element element1 = getComparisonElement();
        Element element2 = dep.getComparisonElement();

        // null sorts later
        if( element1 == null )
            return element2 == null ? 0 : 1;
        else if( element2 == null )
            return 1;

        HeaderFile header1 = element1.getDefinedIn();
        HeaderFile header2 = element2.getDefinedIn();
        if( header1 == null )
            return header2 == null ? 0 : -1;
        else if( header2 == null )
            return 1;

        return header1.compareTo( header2 );
    }

    @Override
    protected boolean writeUse( CppFormatter fmt )
    {
        return writeInclusion( fmt, type.getElement() );
    }
}
