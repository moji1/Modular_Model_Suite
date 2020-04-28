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

public class CharacterLiteral extends Literal
{
    public CharacterLiteral( String value )
    {
        // TODO validate input as character
        super( PrimitiveType.CHAR, value );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( '\'' )
            && super.write( fmt )
            && fmt.write( '\'' );
    }
}
