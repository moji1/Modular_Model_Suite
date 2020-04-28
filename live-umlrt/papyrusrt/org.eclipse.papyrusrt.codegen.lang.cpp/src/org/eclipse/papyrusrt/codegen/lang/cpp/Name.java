/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Name implements Comparable<Name>
{
    private NamedElement parent;
    private final String identifier;

    public Name( String ident )
    {
        this.identifier = asValidCIdentifier( ident );
    }

    private static String asValidCIdentifier( String ident )
    {
        return ident.replaceAll( "[\\s\\-]+", "_" );
    }

    public String getIdentifier() { return identifier; }
    public Name getParent() { return parent == null ? null : parent.getName(); }
    public void setParent( NamedElement parent ) { this.parent = parent; }

    public HeaderFile getDefinedIn() { return parent == null ? null : parent.getDefinedIn(); }

    @Override
    public int compareTo( Name o )
    {
        return getIdentifier().compareTo( o.getIdentifier() );
    }

    public boolean writeQualified( CppFormatter fmt, Name context )
    {
        StringBuilder nameStr = new StringBuilder();
        appendFullyQualified( nameStr );
        String nameQN = nameStr.toString();

        if( context == null )
            return fmt.write( nameQN );

        // Otherwise there is some context so we can try to reduce the fully qualified name.
        StringBuilder contextStr = new StringBuilder();
        context.appendFullyQualified( contextStr );
        String contextQN = contextStr.toString();

        // Strip the matching part of the fully qualified context from the fully
        // qualified part of this name.
        // CTX:  A::B::C::D::E
        // NAME: A::B::Y::Z
        //  =>   Y::Z
        String[] ctx = contextQN.split( "::" );
        String[] name = nameQN.split( "::" );
        int i = 0;

        // Bug 469517: The name needs at least one element, compare the shortened name to
        //             the full context.
        int limit = Math.min( ctx.length, name.length - 1 );
        for( ; i < limit && ctx[i].equals( name[i] ); ++i )
            ; // body intentionally omitted

        // If there was some matching context then replace the name's qualified string
        // with the rest of the name.
        if( i > 0 )
        {
            StringBuilder str = new StringBuilder();
            while( i < name.length )
            {
                str.append( name[i] );
                if( ++i == name.length )
                    break;
                str.append( "::" );
            }
            nameQN = str.toString();
        }

        return fmt.write( nameQN );
    }

    private void appendFullyQualified( StringBuilder str )
    {
        if( parent != null )
        {
            parent.getName().appendFullyQualified( str );
            str.append( "::" );
        }

        str.append( getIdentifier() );
    }

    @Override public String toString() { return getIdentifier(); }
}
