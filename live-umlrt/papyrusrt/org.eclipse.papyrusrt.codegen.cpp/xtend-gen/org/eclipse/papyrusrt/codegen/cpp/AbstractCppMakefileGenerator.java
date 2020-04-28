/**
 * Copyright (c) 2016 Codics Corp and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   William Byrne - Initial API and implementation
 */
package org.eclipse.papyrusrt.codegen.cpp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTSUtil;
import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public abstract class AbstractCppMakefileGenerator {
  public abstract String formatFilename(final String component);
  
  public void generate(final String path, final List<FileName> files, final String main) {
    try {
      final File file = new File(path);
      FileWriter _fileWriter = new FileWriter(file);
      final BufferedWriter writer = new BufferedWriter(_fileWriter);
      writer.write(this.doGenerate(files, main, UMLRTSUtil.getRTSRootPath()).toString());
      writer.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected abstract String doGenerate(final List<FileName> files, final String main, final String rtsPath);
}
