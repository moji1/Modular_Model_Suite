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
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Comment extends Statement
{
    private final boolean singleLine;
    private final String text;

    public Comment( boolean singleLine, String text )
    {
        this.singleLine = singleLine;
        this.text = text;
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return true;
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( text == null || text.trim().isEmpty() ) return true;
        if( singleLine )
            return fmt.write( "// ")
                && fmt.writeLn( text.replace( '\n', ' ' ) );
        return fmt.write( "/* " )
            && fmt.write( text )
            && fmt.writeLn( " */" );
    }

}
