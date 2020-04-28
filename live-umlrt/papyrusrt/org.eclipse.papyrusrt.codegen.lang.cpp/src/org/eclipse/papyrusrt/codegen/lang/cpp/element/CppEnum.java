/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
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
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class CppEnum extends NamedElement implements IUserElement, IGeneratable
{
    private final NamedElement context;
    private final Expression firstLiteral;
    private final List<Enumerator> enumerators = new ArrayList<Enumerator>();

    public CppEnum( String ident ) { this( null, ident, null ); }
    public CppEnum( NamedElement context, String ident, Expression firstLiteral )
    {
        super( ident );
        this.context = context;
        this.firstLiteral = firstLiteral;
    }

    public void add( Enumerator enumerator )
    {
        if( context != null )
            enumerator.setParent( context );

        enumerators.add( enumerator );
    }

    public Enumerator add( String ident )
    {
        Enumerator enumerator
            = ( firstLiteral == null || ! enumerators.isEmpty() )
                ? new Enumerator( ident )
                : new Enumerator( ident, firstLiteral );

        if( context != null )
            enumerator.setParent( context );

        enumerators.add( enumerator );
        return enumerator;
    }

    /**
     * Return the index of the given enumerator in the receiving enum.  This is used when initializing
     * arrays that are indexed by an enumerator.
     * TODO This should be the enumerator's ordinal.
     */
    public int getOrderKey( Enumerator enumerator )
    {
        return enumerators.indexOf( enumerator );
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        for( Enumerator enumerator : enumerators )
            if( ! enumerator.addDependencies( deps ) )
                return false;
        return true;
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return addDependencies( deps.decl() );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( ! fmt.write( "enum" )
         || ! fmt.space()
         || ! fmt.write( getName() )
         || ! fmt.openBrace() )
            return false;

        boolean first = true;
        for( Enumerator enumerator : enumerators )
        {
            if( first )
                first = false;
            else if( ! fmt.write( ',' )
                  || ! fmt.newline() )
                return false;

            if( ! enumerator.write( fmt ) )
                return false;
        }

        return fmt.closeBrace( false )
            && fmt.terminate();
    }

    @Override
    public boolean write( CppWriter out )
    {
        return write( out.decl() );
    }
}
