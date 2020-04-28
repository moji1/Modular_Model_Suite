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

import com.google.common.base.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.eclipse.papyrusrt.codegen.cpp.AbstractCppMakefileGenerator;
import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class CppCMakeListsGenerator extends AbstractCppMakefileGenerator {
  @Override
  protected String doGenerate(final List<FileName> files, final String main, final String rtsPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("# Generated ");
    SimpleDateFormat _simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date _date = new Date();
    String _format = _simpleDateFormat.format(_date);
    _builder.append(_format);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("cmake_minimum_required(VERSION 2.8.7)");
    _builder.newLine();
    _builder.append("set(TARGET ");
    _builder.append(main);
    _builder.append(")");
    _builder.newLineIfNotEmpty();
    _builder.append("project(${TARGET})");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# require location of supporting RTS");
    _builder.newLine();
    _builder.append("if (NOT UMLRTS_ROOT)");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (DEFINED ENV{UMLRTS_ROOT})");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("set(UMLRTS_ROOT $ENV{UMLRTS_ROOT})");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("else ()");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("set(UMLRTS_ROOT ");
    {
      boolean _equals = Objects.equal(rtsPath, "");
      if (_equals) {
        _builder.append("${CMAKE_CURRENT_SOURCE_DIR}/umlrt.rts");
      } else {
        _builder.append(rtsPath, "    ");
      }
    }
    _builder.append(")");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("endif ()");
    _builder.newLine();
    _builder.append("endif ()");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# setup primary envars - provides tooling config");
    _builder.newLine();
    _builder.append("include(${UMLRTS_ROOT}/build/buildenv.cmake)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# model sources");
    _builder.newLine();
    _builder.append("set(SRCS ");
    _builder.append(main);
    _builder.append(".cc ");
    {
      for(final FileName f : files) {
        String _absolutePath = f.getAbsolutePath();
        _builder.append(_absolutePath);
        _builder.append(".cc ");
      }
    }
    _builder.append(")");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("# specify target");
    _builder.newLine();
    _builder.append("add_executable(${TARGET} ${SRCS})");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# setup lib dependency support after defining TARGET");
    _builder.newLine();
    _builder.append("include(${UMLRTS_ROOT}/build/rtslib.cmake)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# compiler parameters");
    _builder.newLine();
    _builder.append("set_target_properties(${TARGET} PROPERTIES COMPILE_OPTIONS \"${COPTS}\")");
    _builder.newLine();
    _builder.append("set_target_properties(${TARGET} PROPERTIES COMPILE_DEFINITIONS \"${CDEFS}\")");
    _builder.newLine();
    _builder.append("include_directories(${INCS})");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# linker parameters");
    _builder.newLine();
    _builder.append("set_target_properties(${TARGET} PROPERTIES CMAKE_EXE_LINKER_FLAGS \"${LOPTS}\")");
    _builder.newLine();
    _builder.append("target_link_libraries(${TARGET} ${LIBS})");
    _builder.newLine();
    _builder.newLine();
    return _builder.toString();
  }
  
  @Override
  public String formatFilename(final String component) {
    return "CMakeLists.txt";
  }
}
