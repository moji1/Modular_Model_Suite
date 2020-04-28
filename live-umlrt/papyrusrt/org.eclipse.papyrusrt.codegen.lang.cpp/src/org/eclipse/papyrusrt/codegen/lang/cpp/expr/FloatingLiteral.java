/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class FloatingLiteral extends Literal
{
    private final boolean isFloat;

    /** Only simple (non exponential-format) values are supported. */
    public FloatingLiteral( String value )
    {
        this( value, false );
    }

    /** Only simple (non exponential-format) values are supported. */
    public FloatingLiteral( String value, boolean isFloat )
    {
        super( isFloat ? PrimitiveType.FLOAT : PrimitiveType.DOUBLE, value );
        this.isFloat = isFloat;
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( ! isFloat )
            return super.write( fmt );
        return super.write( fmt )
            && fmt.write( 'f' );
    }
}
