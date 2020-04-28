/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Constructor;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ConstructorCall extends AbstractFunctionCall
{
    private final Constructor ctor;

    public ConstructorCall( Constructor ctor )
    {
        this.ctor = ctor;
    }

    public ConstructorCall( Constructor ctor, Expression...args )
    {
        super( args );
        this.ctor = ctor;
    }

    @Override protected Type createType() { return ctor.getCppClass().getType(); }

    // A constructor call introduces no dependencies of its own.  By the time it is invoked
    // the dependencies have already been introduced by the type that is being initialized.

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( ctor.getCppClass().getName() )
            && ( ! super.hasArguments() || super.write( fmt ) );
    }

    /**
     * Writes only the initializer part of the constructor call.
     */
    public boolean writeAsInitializer( CppFormatter fmt )
    {
        return super.write( fmt );
    }
}
