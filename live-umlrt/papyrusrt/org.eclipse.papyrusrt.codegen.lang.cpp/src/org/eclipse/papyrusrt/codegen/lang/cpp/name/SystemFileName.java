/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.name;

/**
 * A marker to generate the right type of #include directive.
 */
public class SystemFileName extends FileName
{
    public SystemFileName( String name )
    {
        super( null, name );
    }

    public SystemFileName( FolderName parent, String name )
    {
        super( parent, name );
    }
}
