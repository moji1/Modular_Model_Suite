/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppEnum;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator;

/**
 * A specialized BlockInitializer that is able to order the initializing expressions based on the
 * order of enumerators in a given enum.
 * TODO Add support for a default value for indices that are not initialized.
 */
public class CppEnumOrderedInitializer extends BlockInitializer
{
    private final CppEnum cppEnum;

    public CppEnumOrderedInitializer( CppEnum cppEnum, Type type )
    {
        super( type );
        this.cppEnum = cppEnum;
    }

    /**
     * Some blocks need to be initialized in a specific order.  For example some arrays
     * are initialized such that they can be indexed with an enumerator.  This function
     * allow the initialization index to be specified.
     */
    public void putExpression( Enumerator enumerator, Expression expr )
    {
        int index = cppEnum.getOrderKey( enumerator );
        if( index < 0 )
            throw new RuntimeException( "invalid initialization of non-member " + enumerator );

        // Grow the list if needed.
        while( exprs.size() < index )
            exprs.add( null );

        // If the new element is going on the end of the list, then it can be
        // added (which will grow the list).
        if( exprs.size() == index )
        {
            exprs.add( expr );
            return;
        }

        // Otherwise it will be set the existing element must be null or the same
        // element.
        if( exprs.set( index, expr ) != null )
            throw new RuntimeException( "invalid replacement of initialized index " + index );
    }
}
