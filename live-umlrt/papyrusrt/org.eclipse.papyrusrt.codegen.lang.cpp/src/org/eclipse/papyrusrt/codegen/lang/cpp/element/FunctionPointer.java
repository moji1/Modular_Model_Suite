/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type.Pointer;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.IReferencable;

public class FunctionPointer extends UserElement implements IReferencable
{
    private final Type returnType;
    private final List<Type> parameters = new ArrayList<Type>();

    public FunctionPointer( Type returnType )
    {
        this.returnType = returnType;
    }

    public void add( Type param )
    {
        this.parameters.add( param );
    }

    public boolean write( CppFormatter fmt )
    {
        if( ! returnType.write( fmt, null )
         || ! fmt.space()
         || ! fmt.write( "(*)(" ) )
            return false;

        boolean first = true;
        for( Type param : parameters )
        {
            if( first )
                first = false;
            else if( ! fmt.write( ", " ) )
                return false;

            if( ! param.write( fmt, null ) )
                return false;
        }

        return fmt.write( ')' );
    }

    @Override
    public boolean write( CppFormatter fmt, Name name, List<Pointer> pointerSpec, List<Expression> arrayBounds )
    {
        if( ! returnType.write( fmt, null )
         || ! fmt.space()
         || ! fmt.write( '(' )
         || ! fmt.write( '*' ) )
            return false;

        for( Type.Pointer ptr : pointerSpec )
            if( ! ptr.write( fmt ) )
                return false;

        if( ! fmt.write( name ) )
            return false;

        // Write out all provided array bounds.
        for( Expression bound : arrayBounds )
            if( ! fmt.write( '[' )
             || ! ( bound == null ? true : bound.write( fmt ) )
             || ! fmt.write( ']' ) )
                return false;

        if( ! fmt.write( ')' )
         || ! fmt.write( '(' ) )
            return false;

        boolean first = true;
        for( Type param : parameters )
        {
            if( first )
                first = false;
            else if( ! fmt.write( ", " ) )
                return false;

            if( ! param.write( fmt, null ) )
                return false;
        }

        return fmt.write( ')' );
    }
}
