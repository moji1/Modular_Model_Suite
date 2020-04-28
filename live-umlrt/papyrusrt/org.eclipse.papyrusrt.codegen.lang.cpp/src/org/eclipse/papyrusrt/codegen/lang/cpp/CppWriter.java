/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import org.eclipse.papyrusrt.codegen.lang.cpp.element.ElementList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class CppWriter
{
    private final CppFormatter decl;
    private final CppFormatter defn;

    public CppWriter( CppFormatter decl, CppFormatter defn )
    {
        this.decl = decl;
        this.defn = defn;
    }

    public static CppWriter create( String baseFolder, ElementList elements )
    {
        return new CppWriter(
                    CppFormatter.createProvisional( baseFolder + '/' + elements.getName().getAbsolutePath() + ".hh" ),
                    CppFormatter.createProvisional( baseFolder + '/' + elements.getName().getAbsolutePath() + ".cc" ) );
    }

    public CppFormatter decl() { return decl; }
    public CppFormatter defn() { return defn; }

    public void close()
    {
        try     { decl.close(); }
        finally { defn.close(); }
    }
}
