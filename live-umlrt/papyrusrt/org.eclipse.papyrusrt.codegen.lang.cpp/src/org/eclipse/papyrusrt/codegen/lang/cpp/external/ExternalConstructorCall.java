/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AbstractFunctionCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ExternalConstructorCall extends AbstractFunctionCall
{
    private final ExternalElement element;

    public ExternalConstructorCall( ExternalElement element )
    {
        this.element = element;
    }

    @Override protected Type createType() { return element.getType(); }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( element.getName() )
            && super.write( fmt );
    }

    /**
     * Writes only the initializer part of the constructor call.
     */
    public boolean writeAsInitializer( CppFormatter fmt )
    {
        return super.write( fmt );
    }
}
