/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;
import org.eclipse.papyrusrt.codegen.lang.io.CodeFormatter;

public class HeaderFile implements Comparable<HeaderFile>
{
    private final FileName name;

    public HeaderFile( String filename ) { this.name = new FileName( filename ); }
    public HeaderFile( FileName name ) { this.name = name; }

    public FileName getName() { return name; }

    protected String getIncludeTarget( String filename )
    {
        return "\"" + filename + '"';
    }

    @Override
    public int compareTo( HeaderFile o )
    {
        String incPath1 = getIncludeTarget( name.getIncludePath() );
        String incPath2 = o.getIncludeTarget( o.name.getIncludePath() );
        return incPath1.compareTo( incPath2 );
    }

    public boolean writeInclude( CodeFormatter fmt )
    {
        // TODO This should be integrated with the build environment to generate
        //      file paths that are relative to the include paths.
        return fmt.write( "#include " )
                && fmt.write( getIncludeTarget( name.getIncludePath() ) )
                && fmt.newline();
    }
}
