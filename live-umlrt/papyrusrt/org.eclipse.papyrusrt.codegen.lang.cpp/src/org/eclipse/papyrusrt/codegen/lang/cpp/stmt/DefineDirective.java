/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Macro;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * Generates a #define directive.  The directive is treated as a statement so that
 * it can be generated within a function body.
 */
public class DefineDirective extends Statement
{
    private final Macro macro;

    public DefineDirective( Macro macro ) { this.macro = macro; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return macro.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return macro.write( fmt );
    }
}
