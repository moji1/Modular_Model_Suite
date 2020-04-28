/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependency;
import org.eclipse.papyrusrt.codegen.lang.cpp.external.StandardLibrary;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class OffsetOf extends Expression
{
    private final CppClass cls;
    private final String memberName;

    // TODO This should be the member element, not just the string name.  There isn't
    //      currently a way to get that Member, because it is a private class within
    //      CppClass.
    public OffsetOf( CppClass cls, String memberName )
    {
        this.cls = cls;
        this.memberName = memberName;
    }

    @Override protected Type createType() { return StandardLibrary.size_t; }
    @Override public Precedence getPrecedence() { return Precedence.Lowest; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return deps.add( new ElementDependency( StandardLibrary.offsetof ) )
            && deps.add( new ElementDependency( cls ) );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( "offsetof(" )
            && fmt.space()
            && fmt.write( cls.getName() )
            && fmt.write( ',' )
            && fmt.space()
            && fmt.write( memberName )
            && fmt.space()
            && fmt.write( ')' );
    }
}
