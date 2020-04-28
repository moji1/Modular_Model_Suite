/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.external;

import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Function;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.LinkageSpec;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AbstractFunctionCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionCall;

public class StandardLibrary {
	private static final ExternalHeaderFile cstddef = new ExternalHeaderFile(true, "cstddef");
	private static final ExternalHeaderFile stdint_h = new ExternalHeaderFile(true, "stdint.h");
	private static final ExternalHeaderFile string_h = new ExternalHeaderFile(true, "string.h");
	private static final ExternalHeaderFile iostream = new ExternalHeaderFile(true, "iostream");

	private static final Function memcpy_f = new Function(string_h, LinkageSpec.EXTERN, PrimitiveType.VOID.ptr(), "memcpy");
	private static final Function memset_f = new Function(string_h, LinkageSpec.EXTERN, PrimitiveType.VOID.ptr(), "memset");

	private static final Function strcmp = new Function(string_h, LinkageSpec.EXTERN, PrimitiveType.INT, "strcmp");

	public static AbstractFunctionCall memcpy(Expression dest, Expression src, Expression n) {
		return new FunctionCall(memcpy_f, dest, src, n);
	}

	public static AbstractFunctionCall memset(Expression buffer, Expression byteValue, Expression num) {
		return new FunctionCall(memset_f, buffer, byteValue, num);
	}

	public static AbstractFunctionCall strcmp(Expression right, Expression left) {
		return new FunctionCall(strcmp, right, left);
	}

	public static final NamedElement NULL = new ExternalElement(cstddef, "NULL");

	public static final Expression NULL() {
		return new ElementAccess(NULL);
	}

	public static final Type size_t = new ExternalElement(cstddef, "size_t").getType();

	public static final Function offsetof = new Function(cstddef, LinkageSpec.EXTERN, PrimitiveType.INT, "offsetof");

	public static final Type int8_t = new ExternalElement(stdint_h, "int8_t").getType();
	public static final Type int16_t = new ExternalElement(stdint_h, "int16_t").getType();
	public static final Type int32_t = new ExternalElement(stdint_h, "int32_t").getType();
	public static final Type int64_t = new ExternalElement(stdint_h, "int64_t").getType();
	public static final Type uint8_t = new ExternalElement(stdint_h, "uint8_t").getType();
	public static final Type uint16_t = new ExternalElement(stdint_h, "uint16_t").getType();
	public static final Type uint32_t = new ExternalElement(stdint_h, "uint32_t").getType();
	public static final Type uint64_t = new ExternalElement(stdint_h, "uint64_t").getType();
	public static final Type wchar_t = new ExternalElement(stdint_h, "wchar_t").getType();

	public static final Type std_istream = new ExternalElement(iostream, "istream").getType();
	public static final Type std_ostream = new ExternalElement(iostream, "ostream").getType();
	public static final NamedElement std_endl = new ExternalElement(iostream, "endl");

	public static final Expression std_endl() {
		return new ElementAccess(std_endl);
	}
}
