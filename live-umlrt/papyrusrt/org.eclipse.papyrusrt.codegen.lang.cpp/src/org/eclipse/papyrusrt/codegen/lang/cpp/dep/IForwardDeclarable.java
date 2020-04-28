/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.dep;

import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

public interface IForwardDeclarable extends Comparable<IForwardDeclarable>
{
    public boolean writeForwardDeclaration( CppFormatter fmt );
}
