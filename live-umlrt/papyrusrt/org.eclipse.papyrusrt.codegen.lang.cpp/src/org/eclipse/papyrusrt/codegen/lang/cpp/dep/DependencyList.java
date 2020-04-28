/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.dep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.Dependency.Kind;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.ElementList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * This list manages a set of dependencies.  The following must be true when
 * the list is written to a file:
 * <li>each dependency should be written only once</li>
 * <li>there should not be a forward declaration for something that was
 *     explicitly #include'ed</li>
 * <li>all #inlcude's should be written before the forward declarations</li>
 * <li>each section (#include and forward declaration) should be sorted
 *     alphabetically</li>
 */
public class DependencyList implements Iterable<Dependency>
{
    private final Set<Dependency> deps = new TreeSet<Dependency>();

    @Override public Iterator<Dependency> iterator() { return deps.iterator(); }

    // TODO Problems like conflicts or circular dependencies should be rejected when they
    //      happen.
    public boolean add( Dependency dep )
    {
        if( dep != null )
            deps.add( dep );

        return true;
    }

    public boolean add( DependencyList depList )
    {
        // Add each dependency separately to filter and sort.
        for( Dependency dep : depList.deps )
            if( ! add( dep ) )
                return false;

        return true;
    }

    public boolean isEmpty() { return deps.isEmpty(); }

    public boolean write( CppFormatter fmt ) { return write( null, fmt ); }

    public boolean write( ElementList container, CppFormatter fmt )
    {
        // Unneeded dependencies are filtered when they are written.  An example of an unneeded
        // dependency is a forward declaration to an element who's definition has already been
        // included.  The dependency list is already sorted by importance, so we only need to
        // examine dependencies that have already been written.
        List<Dependency> writtenDeps = new ArrayList<Dependency>();
        
        // In order to remove duplicate includes we examine ones that already printed includes
        // bug#483051
        Set<String> writtenInclude = new HashSet<String>();

        // The filtering algorithm performs poorly in the general case.  However, lists are
        // generally only a dozen or so entries.
        outer: for( Dependency dep : deps )
        {
            for( Dependency d : writtenDeps )
                if( dep.isProvidedBy( d ) )
                    continue outer;
            
            
            HeaderFile header = dep.getHeader();
            if (header != null && !(dep.getKind() == Kind.Reference && dep instanceof TypeDependency
                    && dep.getComparisonElement() instanceof IForwardDeclarable))
            {
                String include = header.getName().getIncludePath();
                if (writtenInclude.contains( include ))
                {
                    continue;
                }
                if (!(dep instanceof DependencyBlob))
                    writtenInclude.add( include );
            }
            
            if( ( container == null || ! container.equals( dep.getHeader() ) )
                    && ! dep.write( fmt ) )
                return false;

            // Blobs are never used to block other dependencies, so don't add them to
            // to the list.
            if (!(dep instanceof DependencyBlob))
                writtenDeps.add( dep );
        
        }

        return true;
    }
}
