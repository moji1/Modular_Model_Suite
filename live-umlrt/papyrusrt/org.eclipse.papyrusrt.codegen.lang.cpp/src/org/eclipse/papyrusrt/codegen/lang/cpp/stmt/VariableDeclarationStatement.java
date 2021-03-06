/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class VariableDeclarationStatement extends Statement
{
    private final Variable variable;

    public VariableDeclarationStatement( Variable variable )
    {
        this.variable = variable;
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return variable.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return variable.write( fmt );
    };
}
