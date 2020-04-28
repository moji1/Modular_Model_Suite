/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type.CVQualifier;

public class MemberFunction extends AbstractFunction
{
    private boolean isVirtual = false;
    private boolean isQuery = false;
    private final CVQualifier cvQualifier;

    public MemberFunction( Type returnType, String ident )
    {
        this( returnType, ident, CVQualifier.UNQUALIFIED );
    }

    public MemberFunction( Type returnType, String ident, CVQualifier cvQualifier )
    {
        super( returnType, ident );
        this.cvQualifier = cvQualifier;
    }

    public MemberFunction( Type returnType, OverloadedOperator op )
    {
        this( returnType, op, CVQualifier.UNQUALIFIED );
    }

    public MemberFunction( Type returnType, OverloadedOperator op, CVQualifier cvQualifier  )
    {
        super( returnType, op.getName() );
        this.cvQualifier = cvQualifier;
    }

    public void setVirtual() { isVirtual = true; }

    public void setQuery() { isQuery = true; }

    @Override
    public boolean write( CppWriter out )
    {
        if( isInline && ! onlyDecl )
            if( ! out.defn().write( "inline" )
              || ! out.defn().space() )
            return false;

        if( isVirtual 
            && ( ! out.decl().write( "virtual" )
                 || ! out.decl().space() ) )
            return false;

        if( ! writeSignature( out.decl() )
         || ! out.decl().pendingSpace()
         || ! cvQualifier.write( out.decl() )){
            return false;
        }
        
        if (isQuery)
        {
            if (!out.decl().pendingSpace() 
                    || !CVQualifier.CONST.write( out.decl() )){
                return false;
            }
        }

        if(! out.decl().terminate() )
            return false;
        
        if( onlyDecl )
            return true;
        if( ! writeSignature( out.defn() )
         || ! out.defn().pendingSpace()
         || ! cvQualifier.write( out.defn() ) )
            return false;

        out.defn().enter( getName() );
        boolean ret = writeBody( out.defn() );
        out.defn().exit();
        return ret;
    }
}
