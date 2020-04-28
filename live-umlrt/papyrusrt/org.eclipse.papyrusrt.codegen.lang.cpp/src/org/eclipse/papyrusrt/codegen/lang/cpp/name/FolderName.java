/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.name;


public class FolderName
{
    private final FolderName parent;
    private final String name;

    public FolderName( String name ) { this( null, name ); }
    public FolderName( FolderName parent, String name )
    {
        this.parent = parent;
        this.name = name;
    }

    public String getFolderName() { return name; }

    public String getAbsolutePath()
    {
        if( parent == null )
            return name;

        return parent.getAbsolutePath()
             + '/'
             + name;
    }
}
