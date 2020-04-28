/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class CppDefaultMakefileGenerator {
  public void generate(final String path, final String targetMakefile) {
    try {
      final File file = new File(path);
      FileWriter _fileWriter = new FileWriter(file);
      final BufferedWriter writer = new BufferedWriter(_fileWriter);
      writer.write(this.doGenerate(targetMakefile).toString());
      writer.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private CharSequence doGenerate(final String targetMakefile) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("##################################################");
    _builder.newLine();
    _builder.append("# Default makefile");
    _builder.newLine();
    _builder.append("# Redirect make to target makefile");
    _builder.newLine();
    _builder.append("##################################################");
    _builder.newLine();
    _builder.newLine();
    _builder.append("all:");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("make -f ");
    _builder.append(targetMakefile, "\t");
    _builder.append(" all");
    _builder.newLineIfNotEmpty();
    _builder.append("clean:");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("make -f ");
    _builder.append(targetMakefile, "\t");
    _builder.append(" clean");
    _builder.newLineIfNotEmpty();
    _builder.append(".PHONY: ");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("make -f ");
    _builder.append(targetMakefile, "\t");
    _builder.append(" all clean");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}
