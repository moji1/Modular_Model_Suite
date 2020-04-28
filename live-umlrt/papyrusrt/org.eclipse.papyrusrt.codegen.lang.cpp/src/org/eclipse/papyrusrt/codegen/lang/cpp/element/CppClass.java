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

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratable;
import org.eclipse.papyrusrt.codegen.lang.cpp.IGeneratableElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.IForwardDeclarable;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public class CppClass extends NamedElement implements IForwardDeclarable, IUserElement
{
    private Kind kind;
    private final List<BaseClassSpecifier> bases = new ArrayList<CppClass.BaseClassSpecifier>();
    private final List<Member> ctors = new ArrayList<Member>();
    private final List<Member> members = new ArrayList<Member>();
    private Member dtor = null;

    public CppClass( String ident ) { this( Kind.CLASS, ident ); }
    public CppClass( Kind kind, String ident ) { super( ident ); this.kind = kind; }

    public CppClass( GenerationTarget genTarget, String ident )            { this(  genTarget, Kind.CLASS, ident ); }
    public CppClass( GenerationTarget genTarget, Kind kind, String ident ) { super( genTarget, ident ); this.kind = kind; }

    /**
     * The kind can be modified up until the time that the class is written.
     */
    public void setKind( Kind kind ) { this.kind = kind; }

    public enum Kind
    {
        // The Kind is used to sort forward declarations to these elements, so the ordinals
        // must be ordered such that lower ordinals are generated first in a block of
        // forward declarations.
        CLASS( Visibility.PRIVATE, "class" ),
        STRUCT( Visibility.PUBLIC, "struct" ),
        UNION( Visibility.PUBLIC, "union" );
        // TODO POD, others?

        public final Visibility defaultVisibility;

        private Kind( Visibility v, String syntax ) { this.defaultVisibility = v; this.syntax = syntax; }
        private String syntax;

        public boolean write( CppFormatter fmt )
        {
            return fmt.write( syntax );
        }
    }

    public enum Visibility
    {
        PUBLIC( "public" ),
        PROTECTED( "protected" ),
        PRIVATE( "private" );

        private Visibility( String syntax ) { this.syntax = syntax; }
        private final String syntax;

        public boolean write( CppFormatter fmt )
        {
            if( ! fmt.decIndent() )
                return false;

            if( ! fmt.write( syntax )
             || ! fmt.write( ':' )
             || ! fmt.newline() )
                return fmt.incIndent()
                    && false;

            return fmt.incIndent();
        }
    }

    public enum Access
    {
        PUBLIC( "public" ),
        PROTECTED( "protected" ),
        PRIVATE( "private" );

        private Access( String syntax ) { this.syntax = syntax; }
        private final String syntax;

        public boolean write( CppFormatter fmt )
        {
            return fmt.write( syntax );
        }
    }

    private class BaseClassSpecifier implements IGeneratable
    {
        private final Access access;
        private final boolean virtual;
        // TODO this needs to be NamedElement because there isn't an inheritance tree
        //      to CppClass for externally defined.
        private final NamedElement element;

        public BaseClassSpecifier( Access access, NamedElement element, boolean virtual )
        {
            this.access = access;
            this.element = element;
            this.virtual = virtual;
        }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            return element.getType().addDependencies( deps );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            return access.write( fmt )
                && fmt.space()
                && ( ! virtual || fmt.write( "virtual" ) && fmt.space() )
                && fmt.write( element.getName() );
        }
    }

    private static class Member implements IGeneratable, IGeneratableElement
    {
        public final boolean isStatic;
        public final Visibility visibility;
        private final IGeneratableElement element;

        public Member( boolean isStatic, Visibility visibility, IGeneratableElement element )
        {
            this.isStatic = isStatic;
            this.visibility = visibility;
            this.element = element;
        }

        @Override
        public boolean addDependencies( ElementDependencies deps )
        {
            return element.addDependencies( deps );
        }

        @Override
        public boolean write( CppWriter out )
        {
            if( ! isStatic )
                return element.write( out );

            return out.decl().write( "static" )
                && out.decl().space()
                && element.write( out );
        }

        private IGeneratable asGeneratable()
        {
            if( element instanceof IGeneratable )
                return (IGeneratable)element;

            throw new RuntimeException( "invalid attempt to use non-default generation on inappropriate element" );
        }

        @Override
        public boolean addDependencies( DependencyList deps )
        {
            return asGeneratable().addDependencies( deps );
        }

        @Override
        public boolean write( CppFormatter fmt )
        {
            IGeneratable gen = asGeneratable();
            if( ! isStatic )
                return gen.write( fmt );

            return fmt.write( "static" )
                && fmt.space()
                && gen.write( fmt );
        }
    }

    public void addBase( Access access, NamedElement base )
    {
        bases.add( new BaseClassSpecifier( access, base, false ) );
    }

    public void addVirtualBase( Access access, NamedElement base )
    {
        bases.add( new BaseClassSpecifier( access, base, true ) );
    }

    public void addMember( Visibility visibility, Constructor ctor )
    {
        ctor.setCppClass( this );
        ctors.add( new Member( false, visibility, ctor ) );
    }

    public void addMember( Visibility visibility, Destructor dtor )
    {
        dtor.setCppClass( this );
        this.dtor = new Member( false, visibility, dtor );
    }

    public void addMember( Visibility visibility, MemberFunction function )
    {
        function.setParent( this );
        members.add( new Member( false, visibility, function ) );
    }

    public void addMember( Visibility visibility, MemberField field )
    {
        field.setParent( this );
        members.add( new Member( false, visibility, field ) );
    }

    public void addMember( Visibility visibility, CppClass cls )
    {
        cls.setParent( this );
        members.add( new Member( false, visibility, cls ) );
    }

    public void addMember( Visibility visibility, CppEnum enm )
    {
        enm.setParent( this );
        members.add( new Member( false, visibility, enm ) );
    }

    public void addMember( Visibility visibility, Typedef typedef )
    {
        typedef.setParent( this );
        members.add( new Member( false, visibility, typedef ) );
    }

    public void addStaticMember( Visibility visibility, MemberFunction function )
    {
        function.setParent( this );
        members.add( new Member( true, visibility, function ) );
    }

    public void addStaticMember( Visibility visibility, MemberField field )
    {
        field.setParent( this );
        members.add( new Member( true, visibility, field ) );
    }

    public void addFriendFunction( Function function )
    {
        function.setFriend();
        members.add( new Member( false, Visibility.PUBLIC, function ) );
    }

    public void addDeclarationBlob( Visibility visibility, DeclarationBlob declBlob )
    {
        members.add( new Member( false, visibility, declBlob ) );
    }

    private boolean addDependencies( DependencyList deps )
    {
        for( BaseClassSpecifier base : bases )
            if( ! base.addDependencies( deps ) )
                return false;
        for( Member ctor : ctors )
            if( ! ctor.addDependencies( deps ) )
                return false;
        for( Member member : members )
            if( ! member.addDependencies( deps ) )
                return false;

        return true;
    }

    @Override
    public boolean addDependencies( ElementDependencies deps )
    {
        switch( getGenerationTarget() )
        {
        case DECL_ONLY: return addDependencies( deps.decl() );
        case DEFN_ONLY: return addDependencies( deps.defn() );
        default:
            break;
        }

        for( BaseClassSpecifier base : bases )
            if( ! base.addDependencies( deps.decl() ) )
                return false;
        for( Member ctor : ctors )
            if( ! ctor.addDependencies( deps ) )
                return false;
        for( Member member : members )
            if( ! member.addDependencies( deps ) )
                return false;

        return true;
    }

    @Override
    public int compareTo( IForwardDeclarable o )
    {
        if( ! ( o instanceof CppClass ) )
            return Integer.compare( hashCode(), o.hashCode() );

        CppClass other = (CppClass)o;
        int cmp = kind.compareTo( other.kind );
        if( cmp != 0 )
            return cmp;

        return getName().compareTo( other.getName() );
    }

    @Override
    public boolean writeForwardDeclaration( CppFormatter fmt )
    {
        return kind.write( fmt )
            && fmt.space()
            && fmt.write( getName() )
            && fmt.terminate();
    }

    private boolean writeBaseClassSpecifierList( CppFormatter fmt )
    {
        if( bases.isEmpty() )
            return true;

        if( ! fmt.space() )
            return false;

        char separator = ':';
        for( BaseClassSpecifier base : bases )
        {
            if( ! fmt.write( separator )
             || ! fmt.space()
             || ! base.write( fmt ) )
                return false;
            separator = ',';
        }

        return true;
    }

    private boolean write( CppFormatter fmt )
    {
        // Initialize all static fields before writing any of the body.
        for( Member member : members )
            if( member.isStatic
             && member.element instanceof MemberField )
            {
                MemberField field = (MemberField)member.element;

                if ( ! writeStaticFieldInitialization( fmt, field ) )
                    return false;
            }

        if( ! kind.write( fmt )
         || ! fmt.space()
         || ! fmt.write( getName() )
         || ! writeBaseClassSpecifierList( fmt )
         || ! fmt.openBrace() )
           return false;

        fmt.enter( getName() );

        Visibility prevVisibility = kind.defaultVisibility;
        for( Member member : ctors )
        {
            if( prevVisibility != member.visibility )
            {
                if( ! member.visibility.write( fmt ) )
                    return false;
                prevVisibility = member.visibility;
            }

            if( ! member.write( fmt ) )
                return false;
        }
        for( Member member : members )
        {
            if( prevVisibility != member.visibility )
            {
                if( ! member.visibility.write( fmt ) )
                    return false;
                prevVisibility = member.visibility;
            }

            if( ! member.write( fmt ) )
                return false;
        }

        // TODO This is needed for early returns too.
        fmt.exit();

        return fmt.closeBrace( false )
            && fmt.terminate();
    }

    @Override
    public boolean write( CppWriter out )
    {
        switch( getGenerationTarget() )
        {
        case DECL_ONLY: return write( out.decl() );
        case DEFN_ONLY: return write( out.defn() );
        default:
            break;
        }

        // Initialize all static fields before writing any of the body.
        for( Member member : members )
            if( member.isStatic
             && member.element instanceof MemberField )
            {
                MemberField field = (MemberField)member.element;

                if ( ! writeStaticFieldInitialization( out.defn(), field ) )
                    return false;
            }

        if( ! kind.write( out.decl() )
         || ! out.decl().space()
         || ! out.decl().write( getName() )
         || ! writeBaseClassSpecifierList( out.decl() )
         || ! out.decl().openBrace() )
           return false;

        out.decl().enter( getName() );

        // Write constructors
        Visibility prevVisibility = kind.defaultVisibility;
        for( Member member : ctors )
        {
            if( prevVisibility != member.visibility )
            {
                if( ! member.visibility.write( out.decl() ) )
                    return false;
                prevVisibility = member.visibility;
            }

            if( ! member.write( out )
             || ! out.defn().newline() )
                return false;
        }
        // Write destructor
        if( dtor != null )
        {
            if( prevVisibility != dtor.visibility )
            {
                if( ! dtor.visibility.write( out.decl() ) )
                    return false;
                prevVisibility = dtor.visibility;
            }
            if( ! dtor.write( out )
                || ! out.defn().newline() )
               return false;
        }
        // Write other members
        for( Member member : members )
        {
            if( prevVisibility != member.visibility )
            {
                if( ! member.visibility.write( out.decl() ) )
                    return false;
                prevVisibility = member.visibility;
            }

            if( ! member.write( out )
             || ! out.defn().newline() )
                return false;
        }

        // TODO This is needed for early returns too.
        out.decl().exit();

        return out.decl().closeBrace( false )
            && out.decl().terminate();
    }

    private boolean writeStaticFieldInitialization( CppFormatter fmt, MemberField field )
    {
        if( ! field.getType().write( fmt, field.getName() ) )
            return false;

        if( field.getInitializer() != null )
        {
            switch( field.getInitKind() )
            {
                case ASSIGNMENT:
                    if( ! fmt.space()
                     || ! fmt.write( '=' )
                     || ! fmt.space()
                     || ! field.getInitializer().write( fmt ) )
                        return false;
                    break;
                case CONSTRUCTOR:
                    if( ! fmt.write( '(' )
                     || ! fmt.space()
                     || ! field.getInitializer().write( fmt )
                     || ! fmt.space()
                     || ! fmt.write( ')' ) )
                        return false;
                    break;
                default:
                    break;
            }
        }

        return fmt.terminate();
    }
}
