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
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class SwitchClause implements IGeneratable
{
    private final List<Expression> labels = new ArrayList<Expression>();
    private final List<Statement> body = new ArrayList<Statement>();
    private boolean isFallthrough = false;

    public SwitchClause() { labels.add( null ); }
    public SwitchClause( Expression... labels )
    {
        this.labels.addAll( Arrays.asList( labels ) );
    }

    public void addLabel( Expression label ) { labels.add( label ); }
    public void addDefault()                 { labels.add( null ); }

    public boolean isFallthrough() { return isFallthrough; }
    public void setFallthrough( boolean b ) { isFallthrough = b; }

    public void add( Statement stmt )  { body.add( stmt ); }
    public void add( Expression expr ) { add( new ExpressionStatement( expr ) ); }
    public void add( Variable var )    { add( new VariableDeclarationStatement( var ) ); }

    private static boolean needsBraces( Iterable<Statement> stmts )
    {
        for( Statement stmt : stmts )
        {
            if( stmt instanceof VariableDeclarationStatement )
                return true;
            else if( stmt instanceof CodeBlock )
                return needsBraces( ( (CodeBlock)stmt ).getStatements() );
        }

        return false;
    }

    /**
     * Returns false if the last statement on all execution paths is break or return and
     * true otherwise.
     * <p>
     * <strong>NOTE</strong>: This isn't fully implemented yet, it only checks the last
     * statement, not the contents of conditionals.
     */
    public boolean clauseFallsthrough()
    {
        if( body.size() <= 0 )
            return true;

        Statement last = body.get( body.size() - 1 );
        return ! ( last instanceof ReturnStatement )
            && ! ( last instanceof BreakStatement );
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Expression expr : labels )
        {
            if( expr == null ) continue;
            if( ! expr.addDependencies( deps ) )
                return false;
        }
        for( Statement stmt : body )
            if( ! stmt.addDependencies( deps ) )
                return false;
        return true;
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        for( Expression label : labels )
        {
            if( ! ( label == null
                    ? fmt.write( "default" )
                    : ( fmt.write( "case " ) && label.write( fmt ) ) )
             || ! fmt.writeLn( ':' ) )
                return false;
        }

        boolean needsBraces = needsBraces( body );

        if( needsBraces )
        {
            if( ! fmt.openBrace() )
                return false;
        }
        else if( ! fmt.incIndent() )
            return false;

        for( Statement stmt : body )
            if( ! stmt.write( fmt ) )
                return false;

        if( needsBraces )
        {
            if( ! fmt.closeBrace() )
                return false;
        }
        else if( ! fmt.decIndent() )
            return false;

        if( ! clauseFallsthrough() )
            return true;

        return fmt.incIndent()
            && fmt.writeLn( "// break intentionally omitted" )
            && fmt.decIndent();
    }
}
