/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer
import org.eclipse.papyrusrt.xtumlrt.common.Capsule
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtModelTranslator

class FlatteningCppTransformer
{
    var CppCodePattern cpp
    var Capsule capsuleContext
    val flattener = new FlatteningTransformer
    val flat2cpp = new FlatModel2Cpp

    new ( CppCodePattern cpp, Capsule capsuleContext )
    {
        this.cpp = cpp
        this.capsuleContext = capsuleContext
    }

    def boolean transform( StateMachine stateMachine )
    {
        val ctx1 =
            new FlatteningTransformer.FlatteningTransformationContext
                (
                    flattener,
                    (this.cpp.translator as UML2xtumlrtModelTranslator).stateMachineTranslator
                )
        // First we flatten the state machine (inheritance and nesting)
        val flatteningResult = flattener.transform( stateMachine, ctx1 )
        if (flatteningResult === null || flatteningResult.success == false)
            return false
        // Then we transform to C++
        // FlatModel2Cpp uses the discarded state list to construct the state enum
        val ctx2 =
            new FlatModel2Cpp.CppGenerationTransformationContext
                (
                    this.cpp,
                    this.capsuleContext,
                    this.flattener,
                    flatteningResult.discardedStates
                )
        return flat2cpp.transformInPlace( flatteningResult.stateMachine, ctx2 )
    }

}