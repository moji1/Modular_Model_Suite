/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp;

import org.eclipse.papyrusrt.codegen.lang.cpp.element.AbstractFunction;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.CodeBlock;

/**
 * Statements are used as holders of expressions.
 *
 * <p>The C++ grammar defines statements that contain expressions.  Statements are contained
 * within code blocks (which are also Statements).  Code blocks are contained within functions
 * or variable declaration initializers.
 *
 * @see Expression
 * @see Statement
 * @see Type
 *
 * @see CodeBlock
 * @see AbstractFunction
 * @see Variable
 */
public abstract class Statement implements IGeneratable
{
}
