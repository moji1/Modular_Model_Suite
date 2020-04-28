/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.CodeBlock;

public abstract class AbstractFunction extends NamedElement implements IUserElement
{
    private final Type returnType;
    private final List<Parameter> parameters = new ArrayList<Parameter>();
    private final CodeBlock body;
    protected boolean isInline = false;
    protected boolean onlyDecl = false;

    public AbstractFunction( Type returnType, String ident )
    {
        super( ident );
        this.returnType = returnType;
        this.body = CodeBlock.forceBraces();
    }

    public AbstractFunction( HeaderFile header, Type returnType, Name name )
    {
        super( header, name );
        this.returnType = returnType;
        this.body = CodeBlock.forceBraces();
    }

    public AbstractFunction( Type returnType, OverloadedOperator op )
    {
        this( returnType, op.getName() );
    }

    public static enum OverloadedOperator
    {
        ASSIGNMENT("="),
        EQUALITY("=="),
        INEQUALITY("!="),
        INSERTION(">>"),
        EXTRACTION("<<");
        private OverloadedOperator( String syntax ) { this.syntax = syntax; }
        private String syntax;
        public String getName() { return "operator" + syntax; }
    }

    public void add( Statement... stmts ) { body.add( stmts ); }
    public void add( Expression expr )    { body.add( expr ); }
    public void add( Variable var )       { body.add( var ); }

    public void add( Parameter param ) { parameters.add( param ); }
    /** @param id 0-based parameter index */
    public ElementAccess param( int index )
    {
        return new ElementAccess( parameters.get( index ) );
    }

    public CodeBlock getBody() { return body; }
    public Type getReturnType() { return returnType; }

    public Type createType()
    {
        FunctionPointer funcPtr = new FunctionPointer( returnType );
        for( Parameter param : parameters )
            funcPtr.add( param.getType() );
        return funcPtr.getType();
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        return addSignatureDependencies( deps.decl() )
            && addBodyDependencies( deps.defn() );
    }

    protected boolean addSignatureDependencies( DependencyList deps )
    {
        if( ! returnType.addDependencies( deps ) )
            return false;

        for( Parameter param : parameters )
            if( ! param.addDependencies( deps ) )
                return false;

        return true;
    }

    protected boolean addBodyDependencies( DependencyList deps )
    {
        return body.addDependencies( deps );
    }

    public void setInline() { isInline = true; }
    public void setOnlyDecl() { onlyDecl = true; }

    protected boolean writeSignature( CppFormatter fmt )
    {
        if( ! returnType.write( fmt, null )
         || ! fmt.space()
         || ! fmt.write( getName() )
         || ! fmt.write( "(" ) )
            return false;

        boolean first = true;
        for( Parameter param : parameters )
        {
            if( first )
            {
                first = false;
                if( ! fmt.space() )
                    return false;
            }
            else if( ! fmt.write( ", " ) )
                return false;

            if( ! param.write( fmt ) )
                return false;
        }

        return fmt.spaceUnless( '(' )
            && fmt.write( ')' );
    }

    protected boolean writeBody( CppFormatter fmt )
    {
        return body.write( fmt );
    }
}
