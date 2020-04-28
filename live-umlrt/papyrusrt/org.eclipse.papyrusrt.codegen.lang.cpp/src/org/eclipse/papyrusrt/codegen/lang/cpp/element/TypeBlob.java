/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class TypeBlob extends Type
{

    private String typeStr;

    public TypeBlob( String typeStr )
    {
        super( new TypeBlobElement( typeStr ) );
        this.typeStr = typeStr;
    }

    @Override
    public String toString()
    {
        return getElement().toString();
    }

    public boolean write( CppFormatter fmt )
    {
        return fmt.write( typeStr );
    }

    private static class TypeBlobElement extends Element
    {
        String typeStr;

        public TypeBlobElement( String typeStr )
        {
            // TypeBlobs are managed by the user, the generator does not know what (if
            // any) header they are defined in.
            super( null );
            this.typeStr = typeStr;
        }

        @Override
        public Type getType()
        {
            return new Type( this );
        }

        public boolean write( CppFormatter fmt )
        {
            return fmt.write( typeStr );
        }

        @Override
        public boolean write( CppFormatter fmt, Name name, List<Type.Pointer> pointerSpec, List<Expression> arrayBounds )
        {
            if( ! write( fmt )
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

            // Write out all provided array bounds.
            for( Expression bound : arrayBounds )
                if( ! fmt.write( '[' )
                 || ! ( bound == null ? true : bound.write( fmt ) )
                 || ! fmt.write( ']' ) )
                    return false;

            return true;
        }

    }

}
