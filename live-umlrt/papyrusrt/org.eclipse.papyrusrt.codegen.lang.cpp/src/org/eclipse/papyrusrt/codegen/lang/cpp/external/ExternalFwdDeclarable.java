/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import org.eclipse.papyrusrt.codegen.lang.cpp.dep.IForwardDeclarable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ExternalFwdDeclarable extends ExternalElement implements IForwardDeclarable
{
    private final String forwardDecl;

    public ExternalFwdDeclarable( ExternalHeaderFile header, String ident, String forwardDecl )
    {
        super( header, ident );
        this.forwardDecl = forwardDecl;
    }

    @Override
    public int compareTo( IForwardDeclarable o )
    {
        // TODO this use of identity will cause changes in output file
        if( ! ( o instanceof ExternalFwdDeclarable ) )
            return Integer.compare( hashCode(), o.hashCode() );

        ExternalFwdDeclarable other = (ExternalFwdDeclarable)o;

        // Sort non-forward-declarables at the start.
        if( forwardDecl == null || forwardDecl.isEmpty() )
            return other.forwardDecl == null || other.forwardDecl.isEmpty() ? 0 : -1;
        else if( other.forwardDecl == null || other.forwardDecl.isEmpty() )
            return 1;

        return forwardDecl.compareTo( other.forwardDecl );
    }

    @Override
    public boolean writeForwardDeclaration( CppFormatter fmt )
    {
        if( forwardDecl == null || forwardDecl.isEmpty() )
            return true;

        return fmt.write( forwardDecl )
            && fmt.terminate();
    }
}
