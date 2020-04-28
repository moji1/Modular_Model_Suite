/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class BitField extends MemberField implements IGeneratableElement, IGeneratable
{

    private final Expression bitSize;

    public BitField( Type type, String ident, Expression bisSize )
    {
        super( type, ident );
        this.bitSize = bisSize;
    }

    public Expression getBitSize()
    {
        return bitSize;
    }

    @Override
    public boolean write( CppFormatter fmt )
    {
        return getType().write( fmt, getName() ) && fmt.space() && fmt.write( ':' ) && fmt.space()
                && bitSize.write( fmt ) && fmt.terminate();
    }

}
