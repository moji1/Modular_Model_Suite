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
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ConditionalStatement extends Statement
{
    private final List<Branch> branches = new ArrayList<ConditionalStatement.Branch>();
    private Branch defaultBranch = null;

    public ConditionalStatement() { }

    /**
     * Creates a new if or "else if" with the given condition and returns
     * a CodeBlock for adding statements to the body.
     */
    public CodeBlock add( Expression condition )
    {
        Branch branch = new Branch( condition );
        branches.add( branch );
        return branch;
    }

    /** Returns the conditional's single else block. */
    public CodeBlock defaultBlock()
    {
        if( defaultBranch == null )
            defaultBranch = new Branch( null );
        return defaultBranch;
    }

    private static class Branch extends CodeBlock
    {
        public final Expression condition;
        public Branch( Expression condition ) { this.condition = condition; }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            return ( condition == null ? true : condition.addDependencies( deps ) )
                && super.addDependencies( deps );
        }
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Branch branch : branches )
            if( ! branch.addDependencies( deps ) )
                return false;

        return defaultBranch == null ? true : defaultBranch.addDependencies( deps );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        boolean first = true;
        for( Branch branch : branches )
        {
            // If the block's first statement is a condition, then forceBraces
            // on to avoid confusion with dangling-else.  The code would generate properly
            // (because we're holding a graph of properly structured elements), but it
            // is less confusing to read when there are explicit braces.
            if( branch.getLast() instanceof ConditionalStatement )
                branch.setForceBraces();

            if( first )
                first = false;
            else if( ! fmt.write( "else " ) )
                return false;

            if( ! fmt.write( "if" )
             || ! fmt.write( '(' )
             || ! fmt.space()
             || ! branch.condition.write( fmt )
             || ! fmt.space()
             || ! fmt.write( ')' )
             || ! fmt.newline()
             || ! branch.write( fmt ) )
                return false;
        }

        if( defaultBranch == null )
            return true;

        return fmt.write( "else" )
            && fmt.newline()
            && defaultBranch.write( fmt );
    }
}
