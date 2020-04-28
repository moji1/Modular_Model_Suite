/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.TypeDependency;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * Types are references to elements of the language model.
 *
 * <p>Type is a decorated reference to an element.  The decorators are pointers and
 * const-volatile qualifiers.
 *
 * @see Element
 */
public class Type
{
    private final Element element;
    private final CVQualifier cvQualifier;

    private final List<Pointer> pointerSpec = new ArrayList<Pointer>();
    private final List<Expression> arrayBounds = new ArrayList<Expression>();

    /**
     * This should not be called by clients, use {@link Element#getType()} instead.
     */
    public Type( Element element )
    {
        this( CVQualifier.UNQUALIFIED, element );
    }

    private Type( CVQualifier cvQualifier, Element element )
    {
        this.element = element;
        this.cvQualifier = cvQualifier;
    }

    public Type ref()              { Type copy = deepCopy(); copy.pointerSpec.add( Pointer.REFERENCE );              return copy; }
    public Type ptr()              { Type copy = deepCopy(); copy.pointerSpec.add( Pointer.POINTER );                return copy; }
    public Type constPtr()         { Type copy = deepCopy(); copy.pointerSpec.add( Pointer.CONST_POINTER );          return copy; }
    public Type volatilePtr()      { Type copy = deepCopy(); copy.pointerSpec.add( Pointer.VOLATILE_POINTER );       return copy; }
    public Type constVolatilePtr() { Type copy = deepCopy(); copy.pointerSpec.add( Pointer.CONST_VOLATILE_POINTER ); return copy; }
    public Type arrayOf( Expression bound ) { Type copy = deepCopy(); copy.arrayBounds.add( bound );                 return copy; }

    public Type const_()        { return deepCopy( CVQualifier.CONST ); }
    public Type volatile_()     { return deepCopy( CVQualifier.VOLATILE ); }
    public Type constVolatile() { return deepCopy( CVQualifier.CONST_VOLATILE ); }

    public Type dereference()
    {
        Type copy = new Type( cvQualifier, element );

        // If there are array bounds then keep all pointer specs and strip one layer.
        if( arrayBounds.size() > 0 )
        {
            copy.pointerSpec.addAll( pointerSpec );
            for( int i = 0; i < arrayBounds.size() - 1; ++i )
                copy.arrayBounds.add( arrayBounds.get( i ) );
        }
        // Otherwise strip one level of indirection.
        else
            for( int i = 0; i < pointerSpec.size() - 1; ++i )
                copy.pointerSpec.add( pointerSpec.get( i ) );

        return copy;
    }

    public Element getElement() { return element; }
    public boolean isArray()    { return ! arrayBounds.isEmpty(); }
    public boolean isIndirect()
    {
        if( arrayBounds.size() > 0 )
            return true;

        switch( pointerSpec.size() )
        {
        case 0:
            return false;
        case 1:
            return pointerSpec.get( 0 ) != Pointer.REFERENCE;
        default:
            return true;
        }
    }
    
    public boolean isPointer()
    {
        switch( pointerSpec.size() )
        {
        case 0:
            return false;
        case 1:
            return pointerSpec.get( 0 ) != Pointer.REFERENCE;
        default:
            return true;
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if( ! ( obj instanceof Type ) )
            return false;

        Type other = (Type)obj;
        if( ! element.equals( other.element )
         || ! cvQualifier.equals( other.cvQualifier ) )
            return false;

        if( pointerSpec.size() != other.pointerSpec.size()
         || arrayBounds.size() != other.arrayBounds.size() )
            return false;

        for( int i = 0; i < pointerSpec.size(); ++i )
            if( ! pointerSpec.get( i ).equals( other.pointerSpec.get( i ) ) )
                return false;

        for( int i = 0; i < arrayBounds.size(); ++i )
            if( ! arrayBounds.get( i ).equals( other.arrayBounds.get( i ) ) )
                return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return element.hashCode()
             ^ cvQualifier.ordinal()
             ^ pointerSpec.size()
             ^ arrayBounds.size();
    }

    private Type deepCopy() { return deepCopy( cvQualifier ); }
    private Type deepCopy( CVQualifier cvQualifier )
    {
        Type copy = new Type( cvQualifier, element );
        copy.pointerSpec.addAll( pointerSpec );
        copy.arrayBounds.addAll( arrayBounds );
        return copy;
    }

    public static enum CVQualifier
    {
        CONST,
        VOLATILE,
        CONST_VOLATILE,
        UNQUALIFIED;

        public boolean write( CppFormatter fmt )
        {
            switch( this )
            {
            case CONST:          return fmt.write( "const" );
            case VOLATILE:       return fmt.write( "volatile" );
            case CONST_VOLATILE: return fmt.write( "const volatile" );
            default:             return fmt.markNoText();
            }
        }
    }

    public static enum Pointer
    {
        // TODO ref shouldn't be a pointer because it can only be used after the pointer-spec
        REFERENCE( "&" ),
        POINTER( "*" ),
        CONST_POINTER( "* const" ),
        VOLATILE_POINTER( "* volatile" ),
        CONST_VOLATILE_POINTER( "* const volatile" );

        private Pointer( String syntax ) { this.syntax = syntax; }
        private final String syntax;

        public boolean write( CppFormatter fmt )
        {
            return fmt.write( syntax );
        }

        @Override public String toString() { return syntax; }
    }

    public boolean addDependencies( DependencyList deps ) { return deps.add( new TypeDependency( this ) ); }

    /** @param name may be null */
    public boolean write( CppFormatter fmt, Name name )
    {
        return cvQualifier.write( fmt )
            && fmt.space()
            && element.write( fmt, name, pointerSpec, arrayBounds );
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        if( cvQualifier != CVQualifier.UNQUALIFIED )
        {
            str.append( cvQualifier.toString() );
            str.append( ' ' );
        }

        str.append( element.toString() );
        str.append( ' ' );

        for( Pointer ptr : pointerSpec )
            str.append( ptr.toString() );

        for( int i = arrayBounds.size(); i > 0; --i )
            str.append( "[]" );

        return str.toString();
    }
}
