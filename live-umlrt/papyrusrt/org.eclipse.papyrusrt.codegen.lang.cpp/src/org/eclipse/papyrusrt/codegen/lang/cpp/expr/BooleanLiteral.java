/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;

public class BooleanLiteral extends Literal
{
    public static BooleanLiteral FALSE() { return new BooleanLiteral( "false" ); }
    public static BooleanLiteral TRUE() { return new BooleanLiteral( "true" ); }
    public static BooleanLiteral from( boolean value ) { return value ? TRUE() : FALSE(); }

    public BooleanLiteral( boolean value ) { super( PrimitiveType.BOOL, Boolean.toString( value ) ); }

    private BooleanLiteral( String value ) { super( PrimitiveType.BOOL, value ); }
}
