/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;

public abstract class UserElement extends Element
{
    private final GenerationTarget genTarget;

    /**
     * The way in which {@link UserElement}'s generate themselves is controlled by the
     * value of this enum.  The decision on how (or even if) to support this value is
     * defined by the various elements.  Generally it means that some sort of declaration
     * is generated to the .hh and the implementation is generated to the .cc.
     */
    public static enum GenerationTarget
    {
        /** This is the default generation target.  E.g., for a class this would produce
         *  an set of declarations in the header and definitions in the impl. */
        DEFAULT,

        /** The element will be fully generated in the header file.  E.g., for a class
         *  this would produce an implementation that is completely inlined. */
        DECL_ONLY,

        /** The element will be fully generated in the impl file.  E.g., for a class
         *  this would produce an implementation that is completely hidden in one
         *  implementation file. */
        DEFN_ONLY;
    }

    protected UserElement()                    { this( GenerationTarget.DEFAULT, null ); }
    protected UserElement( HeaderFile header ) { this( GenerationTarget.DEFAULT, header ); }
    protected UserElement( GenerationTarget kind, HeaderFile header )
    {
        super( header );
        genTarget = kind;
    }

    public GenerationTarget getGenerationTarget() { return genTarget; }

    @Override public Type getType() { return new Type( this ); }
}
