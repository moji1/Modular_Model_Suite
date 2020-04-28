/*******************************************************************************
* Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import org.eclipse.papyrusrt.codegen.CodeGenPlugin
import org.eclipse.papyrusrt.codegen.cpp.SerializationManager
import org.eclipse.papyrusrt.codegen.lang.cpp.Type
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Parameter
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.UserCode
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.common.ActionReference
import org.eclipse.papyrusrt.xtumlrt.common.Entity
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator
import org.eclipse.xtend.lib.annotations.Data

import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames.*
import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.UserCodeTagUtil.*
import org.eclipse.papyrusrt.codegen.UserEditableRegion

/**
 * This visitor generates function declarations for guards in the state machine.
 *
 * @author Ernesto Posse
 */
class GuardDeclarationGenerator
{

    static class Context
    {
    }

    @Data static class UserGuardContext extends Context
    {
		Type rtmessageType
		SerializationManager.ParameterSet params
		UML2xtumlrtTranslator translator
		FlatteningTransformer flattener		
		Entity capsuleContext
    }

    def dispatch
    create func: new MemberFunction( PrimitiveType.BOOL, guard.funcName.toString )
    visit( Guard guard, Context ctx )
    {
        val type = (ctx as UserGuardContext).rtmessageType
        val param = new Parameter( type, GlobalConstants.ACTION_FUNC_PARAM )
        val body = new UserCode( getGuardBody( guard ) )
		val action = guard.body as ActionCode
		
		val translator = (ctx as UserGuardContext).translator
		val capsuleContext = (ctx as UserGuardContext).capsuleContext
		val flattener = (ctx as UserGuardContext).flattener
		
        func.add( param )
        
        var userCodeTag = UserEditableRegion.userEditBegin(action.generateLabel(translator, flattener, capsuleContext))
		
		for(anno : action.annotations){
			if(anno.name == GlobalConstants.GENERATED_ACTION && anno.parameters.empty){
				userCodeTag = null
			}
		}
		
		SerializationManager.getInstance().generateUserCode(func, param, (ctx as UserGuardContext).params, body, userCodeTag)
    }

    def getGuardBody( Guard guard )
    {
        var src = ""
        if (guard === null)
            CodeGenPlugin.error( "Null guard" )
        else
            if (guard.body === null)
                CodeGenPlugin.error( "Guard '" + guard.cachedFullName + "' has null body")
            else
            {
                var ref = guard.body
                while (ref instanceof ActionReference)
                    ref = ref.target
                if (! (ref instanceof ActionCode) )
                    CodeGenPlugin.error( "The body of guard '" + guard.cachedFullName + "' is not an instance of ActionCode" )
                else
                {
                    src = (ref as ActionCode).source
                    if (src === null || src.empty)
                        CodeGenPlugin.error( "The body of guard '" + guard.cachedFullName + "' is empty" )
                    return src
                }
            }
        src
    }

}
