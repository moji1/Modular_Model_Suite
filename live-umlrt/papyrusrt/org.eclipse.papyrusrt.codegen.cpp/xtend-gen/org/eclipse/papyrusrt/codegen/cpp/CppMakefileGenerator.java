/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp;

import com.google.common.base.Objects;
import java.util.List;
import org.eclipse.papyrusrt.codegen.cpp.AbstractCppMakefileGenerator;
import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class CppMakefileGenerator extends AbstractCppMakefileGenerator {
  @Override
  protected String doGenerate(final List<FileName> files, final String main, final String rtsPath) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("# set default value for TARGETOS if is it not defined");
    _builder.newLine();
    _builder.append("ifeq ($(TARGETOS), )");
    _builder.newLine();
    _builder.append("$(warning warning: TARGETOS not defined. Choosing linux)");
    _builder.newLine();
    _builder.append("TARGETOS=linux");
    _builder.newLine();
    _builder.append("endif");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# set default value for BUILDTOOLS if is it not defined");
    _builder.newLine();
    _builder.append("ifeq ($(BUILDTOOLS), )");
    _builder.newLine();
    _builder.append("$(warning warning: BUILDTOOLS not defined. Choosing x86-gcc-4.6.3)");
    _builder.newLine();
    _builder.append("BUILDTOOLS=x86-gcc-4.6.3");
    _builder.newLine();
    _builder.append("endif");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# Location of RTS root.");
    _builder.newLine();
    _builder.append("UMLRTS_ROOT ?= ");
    {
      boolean _equals = Objects.equal(rtsPath, "");
      if (_equals) {
        _builder.append("./umlrt.rts");
      } else {
        _builder.append(rtsPath);
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("CONFIG=$(TARGETOS).$(BUILDTOOLS)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("# Destination directory for the RTS services library.");
    _builder.newLine();
    _builder.append("LIBDEST=$(UMLRTS_ROOT)/lib/$(CONFIG)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("include $(UMLRTS_ROOT)/build/host/host.mk");
    _builder.newLine();
    _builder.append("include $(UMLRTS_ROOT)/build/buildtools/$(BUILDTOOLS)/buildtools.mk");
    _builder.newLine();
    _builder.newLine();
    _builder.append("LD_PATHS=$(LIBDEST)");
    _builder.newLine();
    _builder.append("CC_INCLUDES+=$(UMLRTS_ROOT)/include");
    _builder.newLine();
    _builder.newLine();
    _builder.append("CC_DEFINES:=$(foreach d, $(CC_DEFINES), $(CC_DEF)$d)");
    _builder.newLine();
    _builder.append("CC_INCLUDES:=$(foreach i, $(CC_INCLUDES), $(CC_INC)$i)");
    _builder.newLine();
    _builder.append("LD_LIBS:=$(foreach i, $(LD_LIBS), $(LD_LIB)$i)");
    _builder.newLine();
    _builder.append("LD_PATHS:=$(foreach i, $(LD_PATHS), $(LD_LIBPATH)$i)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("SRCS = ");
    _builder.append(main);
    _builder.append(".cc ");
    {
      for(final FileName f : files) {
        String _absolutePath = f.getAbsolutePath();
        _builder.append(_absolutePath);
        _builder.append(".cc ");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("OBJS = $(subst $(CC_EXT),$(OBJ_EXT),$(SRCS))");
    _builder.newLine();
    _builder.newLine();
    _builder.append("MAIN = ");
    _builder.append(main);
    _builder.append("$(EXE_EXT)");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("all: $(MAIN)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("$(MAIN): $(OBJS) $(UMLRTS_ROOT)/lib/$(CONFIG)/$(LIB_PRFX)rts$(LIB_EXT)");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("$(LD) $(LD_FLAGS) $(OBJS) $(LD_PATHS) $(LD_LIBS) $(LD_OUT)$@");
    _builder.newLine();
    _builder.newLine();
    _builder.append("%$(OBJ_EXT) : %$(CC_EXT)");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("$(CC) $< $(CC_FLAGS) $(CC_DEFINES) $(CC_INCLUDES) $(CC_OUT)$@");
    _builder.newLine();
    _builder.newLine();
    _builder.append("clean :");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@echo $(RM) main$(EXE_EXT) *$(OBJ_EXT) *$(DEP_EXT) $(DBG_FILES)");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@$(RM) main$(EXE_EXT) *$(OBJ_EXT) *$(DEP_EXT) $(DBG_FILES)");
    _builder.newLine();
    _builder.newLine();
    _builder.append(".PHONY: all clean");
    _builder.newLine();
    return _builder.toString();
  }
  
  @Override
  public String formatFilename(final String component) {
    return (("Makefile" + component) + ".mk");
  }
}
