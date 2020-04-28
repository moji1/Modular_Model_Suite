/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type.Pointer;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.IReferencable;

public abstract class NamedElement extends UserElement implements IReferencable
{
    private final Name name;

    protected NamedElement( String ident )
    {
        this( null, new Name( ident ) );
    }

    protected NamedElement( HeaderFile header, Name name )
    {
        super( header );
        this.name = name;
    }

    protected NamedElement( GenerationTarget genTarget, String ident )
    {
        this( genTarget, null, new Name( ident ) );
    }

    protected NamedElement( GenerationTarget genTarget, HeaderFile header, Name name )
    {
        super( genTarget, header );
        this.name = name;
    }

    public Name getName() { return name; }
    public void setParent( NamedElement parent ) { name.setParent( parent ); }

    @Override
    public HeaderFile getDefinedIn()
    {
        HeaderFile header = super.getDefinedIn();
        if( header != null )
            return header;

        return getName().getDefinedIn();
    }

    @Override
    public boolean write( CppFormatter fmt, Name name, List<Pointer> pointerSpec, List<Expression> arrayBounds )
    {
        if( ! fmt.write( getName() )
         || ! fmt.pendingSpace() )
            return false;

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
         || ( name != null && ! fmt.write( name ) ) )
            return false;

        // Write out all provided array bounds.
        for( Expression bound : arrayBounds )
            if( ! fmt.write( '[' )
             || ! ( bound == null ? true : bound.write( fmt ) )
             || ! fmt.write( ']' ) )
                return false;

        return true;
    }

    @Override public String toString() { return name.toString(); }
}
