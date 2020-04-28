/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.dep;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.ElementList;

/**
 * A small container for holding the set of declaration and definition dependencies.
 */
public class ElementDependencies
{
    private final ElementList container;
    private final DependencyList decl = new DependencyList();
    private final DependencyList defn = new DependencyList();

    public ElementDependencies( ElementList container )
    {
        this.container = container;
    }

    public DependencyList decl() { return decl; }
    public DependencyList defn() { return defn; }

    public boolean write( CppWriter out )
    {
        if( ! decl.isEmpty()
         && ( ! decl.write( container, out.decl() )
           || ! out.decl().newline() ) )
            return false;

        if( ! defn.isEmpty()
         && ( ! defn.write( container, out.defn() )
           || ! out.defn().newline() ) )
            return false;

        return true;
    }
}
