/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Name;

public class ExternalTemplateName extends Name
{
    private final List<Name> args;

    public ExternalTemplateName( String ident )
    {
        this( ident, new Name[0] );
    }

    public ExternalTemplateName( String ident, Name... args )
    {
        super( ident );
        this.args = args == null ? new ArrayList<Name>() : Arrays.asList( args );
    }

    @Override
    public String getIdentifier()
    {
        StringBuilder str = new StringBuilder();
        str.append( super.getIdentifier() );
        str.append( '<' );
        boolean first = true;
        boolean lastGT = false;
        for( Name arg : args )
        {
            if( first )
                first = false;
            else
                str.append( ", " );
            str.append( arg.getIdentifier() );
            lastGT = str.charAt( str.length() - 1 ) == '>';
        }
        if( lastGT )
            str.append( ' ' );
        str.append( '>' );
        return str.toString();
    }
}
