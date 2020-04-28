/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class Typedef extends NamedElement implements IUserElement, IGeneratable
{
    private final Type type;

    public Typedef( Type type, String ident )
    {
        super( ident );
        this.type = type;
    }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return type.addDependencies( deps );
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return addDependencies( deps.decl() );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( "typedef" )
            && fmt.space()
            && type.write( fmt, getName() )
            && fmt.terminate();
    }

    @Override
    public boolean write( CppWriter out )
    {
        return write( out.decl() );
    }
}
