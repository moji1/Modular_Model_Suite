/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Enumerator extends NamedElement
{
    private final Expression literal;

    public Enumerator( String ident ) { this( ident, null ); }
    public Enumerator( String ident, Expression literal )
    {
        super( ident );
        this.literal = literal;
    }

    @Override public Type getType() { return PrimitiveType.INT; }

    public boolean addDependencies( DependencyList deps )
    {
        return literal == null
            || literal.addDependencies( deps );
    }

    public boolean write( CppFormatter fmt )
    {
        if( ! fmt.write( getName() ) )
            return false;

        if( literal == null )
            return true;

        return fmt.space()
            && fmt.write( '=' )
            && fmt.space()
            && literal.write( fmt );
    }
}
