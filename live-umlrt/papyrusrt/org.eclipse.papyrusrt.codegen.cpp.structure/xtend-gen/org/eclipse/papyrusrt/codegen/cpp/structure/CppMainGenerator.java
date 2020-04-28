/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.structure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import org.eclipse.papyrusrt.codegen.cpp.structure.model.Controller;
import org.eclipse.papyrusrt.codegen.cpp.structure.model.Deployment;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class CppMainGenerator {
  public void generate(final String filePath, final String topName, final Deployment deployment, final Variable slotsVariable) {
    try {
      final File file = new File(filePath);
      FileWriter _fileWriter = new FileWriter(file);
      final BufferedWriter writer = new BufferedWriter(_fileWriter);
      writer.write(this.doGenerate(topName, deployment, slotsVariable).toString());
      writer.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private CharSequence doGenerate(final String topName, final Deployment deployment, final Variable slotsVariable) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#include \"umlrtmain.hh\"");
    _builder.newLine();
    _builder.newLine();
    _builder.append("#include \"umlrtcontroller.hh\"");
    _builder.newLine();
    _builder.append("#include \"");
    _builder.append(topName);
    _builder.append("Controllers.hh\"");
    _builder.newLineIfNotEmpty();
    _builder.append("#include \"umlrtcapsuletocontrollermap.hh\"");
    _builder.newLine();
    _builder.append("#include \"umlrtmessagepool.hh\"");
    _builder.newLine();
    _builder.append("#include \"umlrtsignalelementpool.hh\"");
    _builder.newLine();
    _builder.append("#include \"umlrttimerpool.hh\"");
    _builder.newLine();
    _builder.append("#include \"umlrtuserconfig.hh\"");
    _builder.newLine();
    _builder.append("#include <stdio.h>");
    _builder.newLine();
    _builder.newLine();
    _builder.append("static UMLRTSignalElement signalElementBuffer[USER_CONFIG_SIGNAL_ELEMENT_POOL_SIZE];");
    _builder.newLine();
    _builder.append("static UMLRTSignalElementPool signalElementPool( signalElementBuffer, USER_CONFIG_SIGNAL_ELEMENT_POOL_SIZE );");
    _builder.newLine();
    _builder.newLine();
    _builder.append("static UMLRTMessage messageBuffer[USER_CONFIG_MESSAGE_POOL_SIZE];");
    _builder.newLine();
    _builder.append("static UMLRTMessagePool messagePool( messageBuffer, USER_CONFIG_MESSAGE_POOL_SIZE );");
    _builder.newLine();
    _builder.newLine();
    _builder.append("static UMLRTTimer timers[USER_CONFIG_TIMER_POOL_SIZE];");
    _builder.newLine();
    _builder.append("static UMLRTTimerPool timerPool( timers, USER_CONFIG_TIMER_POOL_SIZE );");
    _builder.newLine();
    _builder.newLine();
    _builder.append("int main( int argc, char * argv[] )");
    _builder.newLine();
    _builder.append("{");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("UMLRTController::initializePools( &signalElementPool, &messagePool, &timerPool );");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("UMLRTMain::setArgs( argc, argv );");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("UMLRTCapsuleToControllerMap::setDefaultSlotList( ");
    String _identifier = slotsVariable.getName().getIdentifier();
    _builder.append(_identifier, "    ");
    _builder.append(", ");
    int _numInitializedInstances = slotsVariable.getNumInitializedInstances();
    _builder.append(_numInitializedInstances, "    ");
    _builder.append(" );");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("if( ! UMLRTMain::targetStartup() )");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("return EXIT_FAILURE;");
    _builder.newLine();
    _builder.newLine();
    {
      Iterable<Controller> _controllers = deployment.getControllers();
      for(final Controller c : _controllers) {
        _builder.append("    ");
        String _name = c.getName();
        _builder.append(_name, "    ");
        _builder.append("->spawn();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("    ");
    _builder.append("if( ! UMLRTMain::mainLoop() )");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("return UMLRTMain::targetShutdown( false );");
    _builder.newLine();
    _builder.newLine();
    {
      List<Controller> _reverse = ListExtensions.<Controller>reverse(IterableExtensions.<Controller>toList(deployment.getControllers()));
      for(final Controller c_1 : _reverse) {
        _builder.append("    ");
        String _name_1 = c_1.getName();
        _builder.append(_name_1, "    ");
        _builder.append("->join();");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return UMLRTMain::targetShutdown( true );");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
}
