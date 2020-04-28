/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import org.eclipse.papyrusrt.codegen.lang.cpp.internal.IReferencable;

/**
 * Elements are things like functions, variables, and classes.  This is different from
 * a Type which is a reference to the thing defined by the Element.  E.g., in
 * <pre>
 *     int * ptr;
 * </pre>
 * The Element is <strong>int</strong> and the Type is <strong>int *</strong>.
 *
 * @see Type
 */
public abstract class Element implements IReferencable
{
    private HeaderFile header;

    protected Element( HeaderFile header ) { this.header = header; }

    public abstract Type getType();

    // TODO what if it is already in a different list?
    public void setDefinedIn( HeaderFile header ) { this.header = header; }
    public HeaderFile getDefinedIn() { return header; }
}
