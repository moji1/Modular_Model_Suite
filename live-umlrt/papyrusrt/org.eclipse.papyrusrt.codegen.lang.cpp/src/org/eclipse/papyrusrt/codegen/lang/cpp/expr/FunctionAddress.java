/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.element.AbstractFunction;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * Getting the address of a function is a special type of element access because the
 * way in which the element name is written differs. 
 */
public class FunctionAddress extends ElementAccess
{
    public FunctionAddress( AbstractFunction function )
    {
        super( function );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( element.getName() );
    }
}
