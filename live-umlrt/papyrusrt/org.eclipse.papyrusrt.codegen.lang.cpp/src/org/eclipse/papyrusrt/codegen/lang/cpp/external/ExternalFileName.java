/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;

/**
 * An external filename is written using the exact string provided in the constructor.
 * This differs from the FileName base class because FileName applies the current
 * header extension preference to the given name.
 */
public class ExternalFileName extends FileName
{
    public ExternalFileName( String name )
    {
        super( name );
    }

    @Override
    public String getIncludePath()
    {
        return getAbsolutePath();
    }
}
