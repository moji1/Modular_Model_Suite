/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class WhileStatement extends Statement
{
    private final Expression condition;
    private final CodeBlock body = CodeBlock.defaultBraces();

    public WhileStatement( Expression condition )
    {
        this.condition = condition;
    }

    public void add( Statement stmt ) { body.add( stmt ); }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return condition.addDependencies( deps )
            && body.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( "while" )
            && fmt.write( '(' )
            && fmt.space()
            && condition.write( fmt )
            && fmt.space()
            && fmt.write( ')' )
            && fmt.newline()
            && body.write( fmt );
    }
}
