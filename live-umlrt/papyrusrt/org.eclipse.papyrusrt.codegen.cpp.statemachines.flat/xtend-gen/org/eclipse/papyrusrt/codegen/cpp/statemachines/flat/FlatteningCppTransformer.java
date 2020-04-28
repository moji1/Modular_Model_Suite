/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import java.util.Collection;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.FlatModel2Cpp;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.TransformationResult;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtModelTranslator;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtSMTranslator;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator;

@SuppressWarnings("all")
public class FlatteningCppTransformer {
  private CppCodePattern cpp;
  
  private Capsule capsuleContext;
  
  private final FlatteningTransformer flattener = new FlatteningTransformer();
  
  private final FlatModel2Cpp flat2cpp = new FlatModel2Cpp();
  
  public FlatteningCppTransformer(final CppCodePattern cpp, final Capsule capsuleContext) {
    this.cpp = cpp;
    this.capsuleContext = capsuleContext;
  }
  
  public boolean transform(final StateMachine stateMachine) {
    UML2xtumlrtTranslator _translator = this.cpp.getTranslator();
    UML2xtumlrtSMTranslator _stateMachineTranslator = ((UML2xtumlrtModelTranslator) _translator).getStateMachineTranslator();
    final FlatteningTransformer.FlatteningTransformationContext ctx1 = new FlatteningTransformer.FlatteningTransformationContext(
      this.flattener, _stateMachineTranslator);
    final TransformationResult flatteningResult = this.flattener.transform(stateMachine, ctx1);
    if (((flatteningResult == null) || (flatteningResult.isSuccess() == false))) {
      return false;
    }
    Collection<State> _discardedStates = flatteningResult.getDiscardedStates();
    final FlatModel2Cpp.CppGenerationTransformationContext ctx2 = new FlatModel2Cpp.CppGenerationTransformationContext(
      this.cpp, 
      this.capsuleContext, 
      this.flattener, _discardedStates);
    return this.flat2cpp.transformInPlace(flatteningResult.getStateMachine(), ctx2);
  }
}
