/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.Element;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.IReferencable;

public class PrimitiveType extends Type {
	public static final PrimitiveType VOID = new PrimitiveType(new BuiltInElement(BuiltInType.VOID));

	public static final PrimitiveType CHAR = new PrimitiveType(new BuiltInElement(BuiltInType.CHAR));
	public static final PrimitiveType SHORT = new PrimitiveType(new BuiltInElement(BuiltInType.SHORT));
	public static final PrimitiveType INT = new PrimitiveType(new BuiltInElement(BuiltInType.INT));
	public static final PrimitiveType LONG = new PrimitiveType(new BuiltInElement(BuiltInType.LONG));
	public static final PrimitiveType LONGLONG = new PrimitiveType(new BuiltInElement(BuiltInType.LONGLONG));

	public static final PrimitiveType UCHAR = new PrimitiveType(new BuiltInElement(BuiltInType.UCHAR));
	public static final PrimitiveType USHORT = new PrimitiveType(new BuiltInElement(BuiltInType.USHORT));
	public static final PrimitiveType UINT = new PrimitiveType(new BuiltInElement(BuiltInType.UINT));
	public static final PrimitiveType ULONG = new PrimitiveType(new BuiltInElement(BuiltInType.ULONG));
	public static final PrimitiveType ULONGLONG = new PrimitiveType(new BuiltInElement(BuiltInType.ULONGLONG));

	public static final PrimitiveType BOOL = new PrimitiveType(new BuiltInElement(BuiltInType.BOOL));
	public static final PrimitiveType FLOAT = new PrimitiveType(new BuiltInElement(BuiltInType.FLOAT));
	public static final PrimitiveType DOUBLE = new PrimitiveType(new BuiltInElement(BuiltInType.DOUBLE));
	public static final PrimitiveType LONGDOUBLE = new PrimitiveType(new BuiltInElement(BuiltInType.LONGDOUBLE));

	private PrimitiveType(BuiltInElement builtIn) {
		super(builtIn);
	}

	private BuiltInType getBuiltInType() {
		Element element = getElement();
		if (!(element instanceof BuiltInElement))
			throw new RuntimeException("invalid instance of PrimitiveType");
		return ((BuiltInElement) element).type;
	}

	public boolean isIntegral() {
		return getBuiltInType().integral;
	}

	public boolean isUnsigned() {
		return getBuiltInType().unsigned;
	}

	@Override
	public String toString() {
		return getElement().toString();
	}

	private static class BuiltInElement extends Element implements IReferencable {
		public final BuiltInType type;

		public BuiltInElement(BuiltInType type) {
			// Built-in types are not declared in a header file.
			super(null);
			this.type = type;
		}

		// TODO link with PrimitiveType?
		@Override
		public Type getType() {
			return new Type(this);
		}

		@Override
		public String toString() {
			return type.literal;
		}

		public boolean write(CppFormatter fmt) {
			return type.write(fmt);
		}

		@Override
		public boolean write(CppFormatter fmt, Name name, List<Type.Pointer> pointerSpec, List<Expression> arrayBounds) {
			if (!write(fmt)
					|| !fmt.pendingSpace())
				return false;

			// TODO is this right?
			boolean first = true;
			for (Type.Pointer ptr : pointerSpec) {
				if (first)
					first = false;
				else if (!fmt.space())
					return false;

				if (!ptr.write(fmt))
					return false;
			}

			if (!fmt.pendingSpace()
					|| !fmt.write(name))
				return false;

			// Write out all provided array bounds.
			for (Expression bound : arrayBounds)
				if (!fmt.write('[')
						|| !(bound == null ? true : bound.write(fmt))
						|| !fmt.write(']'))
					return false;

			return true;
		}
	}

	private static enum BuiltInType {
		VOID("void", false, false),

		CHAR("char", false, true), SHORT("short", false, true), INT("int", false, true), LONG("long", false, true), LONGLONG("long long", false, true),

		UCHAR("unsigned char", true, true), USHORT("unsigned short", true, true), UINT("unsigned int", true, true), ULONG("unsigned long", true, true), ULONGLONG("unsigned long long", true, true),

		BOOL("bool", false, false), FLOAT("float", false, false), DOUBLE("double", false, false), LONGDOUBLE("long double", false, false);

		private BuiltInType(String literal, boolean unsigned, boolean integral) {
			this.literal = literal;
			this.unsigned = unsigned;
			this.integral = integral;
		}

		public final String literal;
		public final boolean unsigned;
		public final boolean integral;

		public boolean write(CppFormatter fmt) {
			return fmt.write(literal);
		}
	}
}
