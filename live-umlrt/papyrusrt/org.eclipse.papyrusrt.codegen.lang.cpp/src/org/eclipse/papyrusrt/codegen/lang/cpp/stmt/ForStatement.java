/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ForStatement extends Statement
{
    private final List<Variable> loopVars = new ArrayList<Variable>();
    private final Expression condition;
    private final List<Expression> increments = new ArrayList<Expression>();
    private final CodeBlock body = CodeBlock.defaultBraces();

    public ForStatement( Variable loopVar, Expression condition, Expression increment )
    {
        if( loopVar != null )
        {
            validateLoopVar( loopVar );
            this.loopVars.add( loopVar );
        }
        this.condition = condition;
        if( increment != null )
            this.increments.add( increment );
    }

    public boolean addLoopVar( Variable var )
    {
        validateLoopVar( var );

        loopVars.add( var );
        return true;
    }

    public void addIncrementExpression( Expression expr ) { increments.add( expr ); }
    public void add( Statement stmt ) { body.add( stmt ); }

    private void validateLoopVar( Variable var ) throws RuntimeException
    {
        // The var cannot be added unless its type exactly matches the existing ones.
        if( ! loopVars.isEmpty()
         && ! var.getType().equals( loopVars.get( 0 ).getType() ) )
            throw new RuntimeException( "cannot create loop variables with differing types" );

        if( var.getInitializer() == null )
            throw new RuntimeException( "cannot create loop variables without an initializer" );
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Variable var : loopVars )
            if( ! var.addDependencies( deps ) )
                return false;
        if( ! condition.addDependencies( deps ) )
            return false;
        for( Expression expr : increments )
            if( ! expr.addDependencies( deps ) )
                return false;
        return body.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( ! fmt.write( "for" )
         || ! fmt.write( '(' )
         || ! fmt.space() )
            return false;

        boolean first = true;
        for( Variable loopVar : loopVars )
        {
            if( first )
            {
                if( ! loopVar.getType().write( fmt, loopVar.getName() ) )
                    return false;
                first = false;
            }
            else if( ! fmt.write( ',' )
                  || ! fmt.space()
                  || ! fmt.write( loopVar.getName() ) )
                return false;

            if( ! fmt.space()
             || ! fmt.write( '=' )
             || ! fmt.space()
             || ! loopVar.getInitializer().write( fmt ) )
                return false;
        }

        if( ! fmt.write( ';' )
         || ! fmt.space()
         || ! condition.write( fmt )
         || ! fmt.write( ';' )
         || ! fmt.space() )
            return false;

        first = true;
        for( Expression inc : increments )
        {
            if( first )
                first = false;
            else if( ! fmt.write( ',' )
                  || ! fmt.space() )
                return false;
            
            if( ! inc.write( fmt ) )
                return false;
        }

        return fmt.space()
            && fmt.write( ')' )
            && fmt.newline()
            && body.write( fmt );
    }
}
