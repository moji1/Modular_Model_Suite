/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.dep;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class DependencyBlob extends Dependency
{
    private final String text;

    public DependencyBlob( String deps )
    {
        super( Dependency.Kind.Use );
        this.text = deps;
    }

    @Override
    protected int compareUse( Dependency dep )
    {
        // Blobs always compare after other types of dependency, otherwise sort based on
        // their text.
        return dep instanceof DependencyBlob
                    ? text.compareTo( ( (DependencyBlob)dep ).text )
                    : 1;
    }

    @Override
    protected boolean isReferenceProvidedInUse( Dependency use )
    {
        // Blobs are not examined.
        return false;
    }

    @Override protected Element getComparisonElement() { return null; }
    @Override protected HeaderFile getHeader() { return null; }

    @Override
    public boolean writeUse( CppFormatter fmt )
    {
        return fmt.writeLn( text );
    }
}
