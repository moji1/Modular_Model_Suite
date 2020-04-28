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

public class IntegralLiteral extends Literal
{
    private final String suffix;

    public IntegralLiteral( int value ) { this( Integer.toString( value ) ); }
    public IntegralLiteral( String value )
    {
        this( PrimitiveType.INT, value );
    }

    private IntegralLiteral( PrimitiveType intType, String value )
    {
        super( intType, value );

        if( ! intType.isIntegral() )
            throw new IllegalArgumentException( String.valueOf( intType ) + " is not a basic integral literal type" );

        suffix = intType.isUnsigned() ? "u" : "";
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return super.write( fmt )
            && fmt.write( suffix );
    }
}
