/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Macro extends NamedElement implements IUserElement
{
    private List<MacroParameter> parameters = null;
    private final Expression expr;

    public Macro( String ident ) { this( ident, null ); }
    public Macro( String ident, Expression expr )
    {
        super( ident );
        this.expr = expr;
    }

    public boolean expectsArguments() { return parameters != null; }
    public Expression getReplacement() { return expr; }

    // Call as macro.addParameter( null ) to generate as "MACRO()".
    public void addParameter( MacroParameter param )
    {
        if( this.parameters == null )
            this.parameters = new ArrayList<>();

        if( param != null )
            parameters.add( param );
    }

    public void addParameters( MacroParameter... params )
    {
        if( params == null )
            addParameter( null );
        else
            for( MacroParameter param : params )
                addParameter( param );
    }

    public boolean addDependencies( DependencyList deps )
    {
        return expr == null ? true : expr.addDependencies( deps );
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return addDependencies( deps.decl() );
    }

    public boolean write( CppFormatter fmt )
    {
        if( ! fmt.write( "#define" )
         || ! fmt.space()
         || ! fmt.write( getName() ) )
            return false;

        if( parameters != null )
        {
            if( ! fmt.write( '(' ) )
                return false;

            boolean first = true;
            for( MacroParameter param : parameters )
            {
                if( first )
                    first = false;
                else if( ! fmt.write( ',' ) )
                    return false;

                if( ! fmt.space()
                 || ! param.write( fmt ) )
                    return false;
            }

            if( ! fmt.spaceUnless( '(' )
             || ! fmt.write( ')' ) )
                return false;
        }

        if( expr == null )
            return fmt.newline();

        return fmt.space()
            && fmt.write( '(' )
            && fmt.space()
            && expr.write( fmt )
            && fmt.space()
            && fmt.write( ')' )
            && fmt.newline();
    }

    @Override
    public boolean write( CppWriter out )
    {
        return write( out.decl() );
    }
}
