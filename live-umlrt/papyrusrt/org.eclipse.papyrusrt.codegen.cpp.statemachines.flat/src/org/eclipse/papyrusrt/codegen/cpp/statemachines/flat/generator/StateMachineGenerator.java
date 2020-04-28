/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.generator;

import org.eclipse.papyrusrt.codegen.cpp.AbstractBehaviourGenerator;
import org.eclipse.papyrusrt.codegen.cpp.AbstractElementGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern.Output;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.FlatteningCppTransformer;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppClass;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;
import org.eclipse.papyrusrt.xtumlrt.statemachext.StatemachextPackage;

public class StateMachineGenerator extends AbstractBehaviourGenerator<StateMachine, Capsule>
{
    public static class Factory implements AbstractElementGenerator.Factory<StateMachine, Capsule>
    {
        @Override
        public AbstractElementGenerator create( CppCodePattern cpp, StateMachine stateMachine, Capsule capsuleContext )
        {
            return new StateMachineGenerator( cpp, stateMachine, capsuleContext );
        }
    }

    private StateMachineGenerator( CppCodePattern cpp, StateMachine stateMachine, Capsule capsuleContext )
    {
        super( cpp, stateMachine, capsuleContext );
        StatemachextPackage p = StatemachextPackage.eINSTANCE;
    }

    @Override
    protected Output getOutputKind() { return Output.BasicClass; }

    @Override
    public boolean generate()
    {
        FlatteningCppTransformer generator = new FlatteningCppTransformer( cpp, getContext() );

        return generator.transform( getElement() );
    }

    @Override
    protected void generateInitializeBody( CppClass cls, MemberFunction initializeFunc, StateMachine stateMachine, Capsule capsule )
    {
        // Do nothing. The initialize function is created by the FlatteningCppTransformer
    }

    @Override
    protected void generateInjectBody( CppClass cls, MemberFunction injectFunc, StateMachine stateMachine, Capsule capsule )
    {
        // Do nothing. The inject function is created by the FlatteningCppTransformer
    }

    @Override
    protected void generateAdditionalElements( CppClass cls, StateMachine behaviourElement, Capsule contextU )
    {
        // Do nothing. The supporting functions are created by the FlatteningCppTransformer
    }

}
