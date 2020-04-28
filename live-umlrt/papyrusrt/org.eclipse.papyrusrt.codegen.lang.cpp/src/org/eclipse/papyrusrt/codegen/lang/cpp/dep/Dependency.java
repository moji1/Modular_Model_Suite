/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.dep;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public abstract class Dependency implements Comparable<Dependency>
{
    private Kind kind;

    protected Dependency( Kind kind )
    {
        this.kind = kind;
    }

    /**
     * Overrides the kind of a dependency. This is needed if the user explicitly
     * sets this kind using the C++ Properties Set profile DependencyProperties
     * stereotype.
     *
     * @param kind
     */
    public void setKind( Kind kind )
    {
        this.kind = kind;
    }
    
    public Kind getKind()
    {
        return kind;
    }

    // TODO When dependencies are written to the formatter, there should only be one
    //      #include for each file, there should not be forward declarations for elements
    //      that have already been #include'ed and the list should be sorted, all #includes
    //      first, then all forward declarations, and each section alphabetic.

    protected boolean writeReference( CppFormatter fmt ) { return writeUse( fmt ); }
    protected abstract boolean writeUse( CppFormatter fmt );
    protected int compareReference( Dependency dep ) { return compareUse( dep ); }
    protected abstract int compareUse( Dependency dep );
    protected abstract boolean isReferenceProvidedInUse( Dependency use );
    protected abstract Element getComparisonElement();
    protected abstract HeaderFile getHeader();

    @Override
    public int compareTo( Dependency o )
    {
        // There is a coarse sort based on the kind of the dependency.
        int cmp = kind.compareTo( o.kind );
        if( cmp != 0 )
            return cmp;

        // The fine-grained sort uses details provided by the implementation.
        switch( kind )
        {
        case Use:       return o.kind == Kind.None ? -1 : compareUse( o );
        case Reference: return o.kind == Kind.None ? -1 : compareReference( o );
        case None:      return 1;
        default:
            throw new RuntimeException( "cannot compare " + toString() + " with " + o.toString() );
        }
    }

    /**
     * Returns true if the receiver is provided by the given dependency and false otherwise.
     * In the current implementation, a dependency is only considered provided if the receiver is
     * a forward declaration, the parameter an inclusion, and the first is declared in the second.
     * This is used for filtering unneeded forward declarations from dependency lists.  
     */
    public boolean isProvidedBy( Dependency other )
    {
        // Only reference dependencies can be provided by another dependency and a dependency
        // can never be provided by another reference (duplicates have already been filtered during
        // the sort).
        if( kind == Kind.Reference
         && other.kind == Kind.Use )
            return isReferenceProvidedInUse( other );

        return false;
    }

    public boolean write( CppFormatter fmt )
    {
        switch( kind )
        {
        case Use:       return writeUse( fmt );
        case Reference: return writeReference( fmt );
        case None:      return true;
        }
        return false;
    }

    public static enum Kind
    {
        // The Kind is used as a comparable to order dependencies so make sure that the
        // ordinals are sorted to match.
        Use,
        Reference,
        None        // This kind is used when the user wants to explicitly inhibit the dependency from being written
    }
}
