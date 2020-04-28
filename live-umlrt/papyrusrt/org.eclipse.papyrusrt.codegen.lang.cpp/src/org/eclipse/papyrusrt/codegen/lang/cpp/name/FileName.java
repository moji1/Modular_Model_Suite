/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.name;


public class FileName extends FolderName
{
    private boolean hasObjectCode = false;

    public FileName( String name )
    {
        super( name );
    }

    public FileName( FolderName folder, String name )
    {
        super( folder, name );
    }

    public void setHasObjectCode( boolean b ) { hasObjectCode = b; }

    public String getIncludePath()
    {
        return getAbsolutePath() + ".hh";
    }
}
