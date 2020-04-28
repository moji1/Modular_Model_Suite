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
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ElementDependency extends Dependency
{
    private final Element element;

    public ElementDependency( Element element )
    {
        super( Dependency.Kind.Use );
        this.element = element;
    }

    @Override protected Element getComparisonElement() { return element; }
    @Override protected HeaderFile getHeader() { return element == null ? null : element.getDefinedIn(); }

    @Override
    protected boolean writeUse( CppFormatter fmt )
    {
        return element.getDefinedIn() == null ? true : element.getDefinedIn().writeInclude( fmt );
    }

    @Override
    protected int compareUse( Dependency dep )
    {
        if( dep instanceof ElementDependency )
        {
            if( element.getDefinedIn() == null )
                return ( (ElementDependency)dep ).element.getDefinedIn() == null ? 0 : 1;
            if( ( (ElementDependency)dep ).element.getDefinedIn() == null )
                return -1;
            return element.getDefinedIn().compareTo( ( (ElementDependency)dep ).element.getDefinedIn() );
        }

        if( dep instanceof DependencyBlob )
            return 1;

        // The type could be reference or use, so get it to do the comparison and then invert
        // the result.
        if( dep instanceof TypeDependency )
            return ( (TypeDependency)dep ).compareTo( this ) * -1;

        throw new RuntimeException( "cannot compare " + toString() + " with " + dep.toString() );
    }

    @Override
    protected boolean isReferenceProvidedInUse( Dependency use )
    {
        return false;
    }
}
