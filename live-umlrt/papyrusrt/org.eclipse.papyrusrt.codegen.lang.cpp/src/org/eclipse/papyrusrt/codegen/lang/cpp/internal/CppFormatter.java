/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.internal;

import java.io.ByteArrayOutputStream;
import java.util.Stack;

import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.io.CodeFormatter;
import org.eclipse.papyrusrt.codegen.lang.io.ComparisonStream;

public class CppFormatter extends CodeFormatter
{
    private Stack<Name> context = new Stack<Name>();

    public static CppFormatter create( String pathname )
    {
        ComparisonStream stm = ComparisonStream.create( pathname );
        return stm == null ? null : new CppFormatter( stm );
    }

    public static CppFormatter createProvisional( String pathname )
    {
        ComparisonStream stm = ComparisonStream.createProvisional( pathname );
        return stm == null ? null : new CppFormatter( stm );
    }

    protected CppFormatter( ComparisonStream stm )
    {
        super( stm );
    }

    public void enter( Name name ) { context.push( name ); }
    public void exit() { context.pop(); }

    public boolean write( Name name )
    {
        if( name == null )
        {
            clearPending();
            return true;
        }

        return name.writeQualified( this, context.isEmpty() ? null : context.peek() );
    }

    public boolean write( IGeneratable gen, boolean withParen )
    {
        return ( ! withParen || ( write( '(' ) && space() ) )
            && gen.write( this )
            && ( ! withParen || ( space() && write( ')' ) ) );
    }

    /**
     * Create and return a formatter that will write to the given memory-based output
     * stream.  Mostly for the test suite.
     */
    public static CppFormatter createMemoryBased( ByteArrayOutputStream out )
    {
        return new CppFormatter( new ComparisonStream( out ) );
    }
}
