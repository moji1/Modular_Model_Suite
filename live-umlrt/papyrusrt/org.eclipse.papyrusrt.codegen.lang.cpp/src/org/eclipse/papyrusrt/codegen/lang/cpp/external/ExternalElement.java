/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;

/**
 * This is used to represent elements that are available in external libraries.  External
 * elements are used in two ways:
 * <ol>
 * <li>As a dependency target, e.g., #include &lt;type.h&gt;</li>
 * <li>As a container listing the available members, e.g., signal->id</li>
 * </ol>
 * This base class is for introducing unstructured external types.  Subclasses are able
 * to provide more information related to fields.
 */
public class ExternalElement extends NamedElement
{
    // External elements have a single instance of type so that an identity
    // test can be performed.
    private Type type;

    public ExternalElement( ExternalHeaderFile header, String ident )
    {
        this( header, new Name( ident ) );
    }

    public ExternalElement( ExternalHeaderFile header, Name name )
    {
        super( header, name );
        this.type = null;
    }

    public ExternalElement( ExternalHeaderFile header, Type type, String ident )
    {
        super( header, new Name( ident ) );
        this.type = type;
    }

    @Override
    public Type getType()
    {
        if( type == null )
            type = super.getType();
        return type;
    }

    @Override
    public void setDefinedIn( HeaderFile header )
    {
        throw new RuntimeException( "external types cannot be defined in user-defined element lists" );
    }
}
