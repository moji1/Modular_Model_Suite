/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class MacroParameter extends NamedElement
{
    public MacroParameter( String ident )
    {
        super( ident );
    }

    public boolean addDependencies( DependencyList deps )
    {
        return true;
    }

    public boolean write( CppFormatter fmt )
    {
        return fmt.write( getName() );
    }
}
