/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyBlob;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class ExpressionBlob extends Expression
{
    private final DependencyBlob dependencies;
    private final String code;

    public ExpressionBlob( String code )
    {
        this( null, code );
    }

    public ExpressionBlob( DependencyBlob dependencies, String code )
    {
        this.dependencies = dependencies;
        this.code = code;
    }

    public String getText()
    {
        return code;
    }

    @Override
    protected Type createType()
    {
        return PrimitiveType.VOID;
    }

    // An expression blob should always be evaluated on it's own, which means that it
    // should always have parenthesis.
    @Override public Precedence getPrecedence() { return Precedence.Lowest; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        return dependencies == null ? true : deps.add( dependencies );
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return fmt.write( this.code );
    }
}
