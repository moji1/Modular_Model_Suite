/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class CodeBlock extends Statement
{
    private boolean forceBraces;
    private final List<Statement> statements = new ArrayList<Statement>();

    public static CodeBlock defaultBraces() { return new CodeBlock( false ); }
    public static CodeBlock forceBraces() { return new CodeBlock( true ); }

    public CodeBlock( Statement... stmts )
    {
        this( false );
        statements.addAll( Arrays.asList( stmts ) );
    }

    protected CodeBlock( boolean braces ) { forceBraces = braces; }

    public void setForceBraces() { forceBraces = true; }
    public boolean isForceBraces() { return forceBraces; }
    public void add( Statement... stmts ) { add( Arrays.asList( stmts ) ); }
    public void add( List<Statement> stmts ) { statements.addAll( stmts ); }
    public void add( Expression expr )    { add( new ExpressionStatement( expr ) ); }
    public void add( Variable var ) { add( new VariableDeclarationStatement( var ) ); }
    public void add( int index, Statement stmt ) { statements.add( index, stmt ); }
    public void prepend( Statement stmt ) { add( 0, stmt ); }
    public void append( Statement stmt ) { add( stmt ); }

    public Iterable<Statement> getStatements() { return statements; }
    public Statement getLast() { return statements.size() <= 0 ? null : statements.get( statements.size() - 1 ); }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Statement stmt : statements )
            if( ! stmt.addDependencies( deps ) )
                return false;
        return true;
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        boolean useBraces = forceBraces || statements.size() != 1;

        if( (   useBraces && ! fmt.openBrace() )
         || ( ! useBraces && ! fmt.incIndent() ) )
            return false;

        for( Statement stmt : statements )
            if( ! stmt.write( fmt ) )
            {
                fmt.decIndent();
                return false;
            }

        return useBraces ? fmt.closeBrace() : fmt.decIndent();
    }
}
