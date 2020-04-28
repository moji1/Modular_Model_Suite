/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class MemberField extends NamedElement implements IGeneratable, IGeneratableElement
{
    public enum InitKind { ASSIGNMENT, CONSTANT, CONSTRUCTOR };
    private final Type type;
    private final Expression initializer;
    private boolean isMutable = false;
    private InitKind initKind = InitKind.ASSIGNMENT;

    public MemberField( Type type, String ident )
    {
        this( type, ident, null );
    }

    public MemberField( Type type, String ident, Expression initializer )
    {
        super( ident );
        this.type = type;
        this.initializer = initializer;
    }

    @Override public Type getType() { return type; }
    public Expression getInitializer() { return initializer; }
    public InitKind getInitKind() { return initKind; }

    @Override
    public boolean addDependencies( DependencyList deps )
    {
        if ( type.isArray()  && !type.isPointer() ){
            return type.getElement().getType().addDependencies( deps );
        }
        else{
            return type.addDependencies( deps );
        }        
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return addDependencies( deps.decl() );
    }

    public void setMutable() { isMutable = true; }
    public void setInitKind( InitKind initKind ) { this.initKind = initKind; }

    @Override
    public boolean write( CppFormatter fmt )
    {
        if( isMutable
            && ( ! fmt.write( "mutable" )
              || ! fmt.space() ) )
            return false;
        if( ! type.write( fmt, getName() ) )
            return false;
        if( initKind == InitKind.CONSTANT
            && ( ! fmt.space()
                || ! fmt.write( '=' )
                || ! fmt.space()
                || ! initializer.write( fmt ) ) )
           return false;
        return fmt.terminate();
    }

    @Override
    public boolean write( CppWriter out )
    {
        return write( out.decl() );
    }
}
