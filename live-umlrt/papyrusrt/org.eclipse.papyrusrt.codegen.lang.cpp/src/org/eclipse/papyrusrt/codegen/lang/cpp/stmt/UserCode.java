/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.stmt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyBlob;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.DependencyList;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;

/**
 * UserCode should be used to generate the opaque string provided for various actions
 * by the user. This should only be used in contexts where the user code will be
 * generated into the body of a function.
 */
public class UserCode extends Statement {
	private final DependencyBlob dependencies;
	private final String code;
	private final int indentation;
	private final boolean indentFirstLine;

	public UserCode(String code) {
		this(null, code);
	}

	public UserCode(String code, int indentation) {
		this(null, code, indentation);
	}

	public UserCode(DependencyBlob dependencies, String code) {
		this(dependencies, code, 0);
	}

	public UserCode(DependencyBlob dependencies, String code, int indentation) {
		this(dependencies, code, indentation, true);
	}

	public UserCode(DependencyBlob dependencies, String code, int indentation, boolean indentFirstLine) {
		this.dependencies = dependencies;
		this.code = code;
		this.indentation = indentation;
		this.indentFirstLine = indentFirstLine;
	}

	@Override
	public boolean addDependencies(DependencyList deps) {
		return dependencies == null ? true : deps.add(dependencies);
	}

	@Override
	public boolean write(CppFormatter fmt) {

		if (code == null) {
			return true;
		}
		// Write the lines separately to apply the appropriate indent.
		BufferedReader reader = new BufferedReader(new StringReader(code));
		String line = null;
		StringBuilder builder = null;
		try {
			while ((line = reader.readLine()) != null) {
				String str = formatLine(line);
				if (str == null)
					continue;
				if (!fmt.writeLn(str))
					return false;
			}
		} catch (IOException e) {
			CodeGenPlugin.error(e);
			return false;
		}

		return true;
	}

	private String formatLine(String line) {
		StringBuilder builder = new StringBuilder();
		int l = line.length();
		if (l == 0)
			return null;
		int i = 0;
		// Add indentation
		for (i = 0; i < this.indentation; i++)
			builder.append(' ');
		char[] array = line.toCharArray();
		// Skip leading spaces
		for (i = 0; i < l && (array[i] == ' ' || array[i] == '\t'); i++)
			;
		// Add the rest of the line
		while (i < l && array[i] != '\n') {
			builder.append(array[i]);
			i++;
		}
		return builder.toString();
	}
}
