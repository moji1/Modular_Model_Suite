/*******************************************************************************
 * Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import org.eclipse.papyrusrt.codegen.UserEditableRegion
import org.eclipse.papyrusrt.codegen.cpp.SerializationManager
import org.eclipse.papyrusrt.codegen.lang.cpp.Type
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Parameter
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.UserCode
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.common.Entity
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory
import org.eclipse.papyrusrt.xtumlrt.statemachext.UpdateState
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants
import org.eclipse.xtend.lib.annotations.Data

import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil.*
import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.UserCodeTagUtil.*

/**
 * This visitor generates function declarations for actions in the state machine.
 * 
 * @author Ernesto Posse
 */
class ActionDeclarationGenerator {

	static class Context {
	}

	@Data static class UserActionContext extends Context {
		Type rtmessageType
		SerializationManager.ParameterSet params
		UML2xtumlrtTranslator translator
		FlatteningTransformer flattener		
		Entity capsuleContext
	}

	def dispatch visit(SaveHistory action, Context ctx) {
		// ... only one needs to be generated... better in the transformer class
	}

	def dispatch visit(UpdateState action, Context ctx) {
		// ... only one needs to be generated... better in the transformer class
	}

	def dispatch create
		func: new MemberFunction( PrimitiveType.VOID, action.funcName.toString )
	visit(ActionCode action, Context ctx) {
		val type = (ctx as UserActionContext).rtmessageType
		val param = new Parameter(type, GlobalConstants.ACTION_FUNC_PARAM)
		val body = new UserCode(action.source, 0)
		func.add(param)

		val translator = (ctx as UserActionContext).translator
		val capsuleContext = (ctx as UserActionContext).capsuleContext
		val flattener = (ctx as UserActionContext).flattener
		
		var userCodeTag = UserEditableRegion.userEditBegin(action.generateLabel(translator, flattener, capsuleContext))
		
		for(anno : action.annotations){
			if(anno.name == GlobalConstants.GENERATED_ACTION && anno.parameters.empty){
				userCodeTag = null
			}
		}
		
		SerializationManager.getInstance().generateUserCode(func, param, (ctx as UserActionContext).params, body, userCodeTag)
	}
}
