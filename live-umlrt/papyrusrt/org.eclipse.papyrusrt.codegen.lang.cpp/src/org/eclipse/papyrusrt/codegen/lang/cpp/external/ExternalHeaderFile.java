/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;

public class ExternalHeaderFile extends HeaderFile
{
    private final boolean isSystem;
    
    public ExternalHeaderFile( String filename ) { this( false, filename ); }
    public ExternalHeaderFile( ExternalFileName filename )
    {
        super( filename ); this.isSystem = false;
    }
    public ExternalHeaderFile( boolean isSystem, String filename )
    {
        super( new ExternalFileName( filename ) );
        this.isSystem = isSystem;
    }

    @Override
    protected String getIncludeTarget( String filename )
    {
        return isSystem
                ? "<" + getName().getIncludePath() + '>'
                : super.getIncludeTarget( filename );
    }
}
