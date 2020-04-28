/*******************************************************************************
 * Copyright (c) 2015-2017 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement
import org.eclipse.papyrusrt.xtumlrt.statemach.ActionChain
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard
import org.eclipse.papyrusrt.xtumlrt.statemach.StatemachFactory
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition
import org.eclipse.papyrusrt.xtumlrt.statemach.Trigger
import org.eclipse.papyrusrt.xtumlrt.statemachext.StatemachextFactory
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants

import static extension org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames.*
import org.eclipse.papyrusrt.xtumlrt.statemachext.EntryAction
import org.eclipse.papyrusrt.xtumlrt.statemachext.ExitAction
import org.eclipse.papyrusrt.xtumlrt.statemachext.TransitionAction

class CppNamesUtil
{

	static dispatch def getFuncName(NamedElement element)
	'''«element.prefix»«partSep»«element.ffqn»'''
	
	static dispatch def getFuncName(EntryAction element)
	'''«element.prefix»«partSep»«element.owner.ffqn»'''
	
	static dispatch def getFuncName(ExitAction element)
	'''«element.prefix»«partSep»«element.owner.ffqn»'''
	
	static dispatch def getFuncName(TransitionAction element)
	'''«element.prefix»«partSep»«element.owner.owner.ffqn»'''
	
	static dispatch def getFuncName(ActionChain element)
	'''«element.prefix»«partSep»«element.owner.ffqn»'''

	static dispatch def getFuncName(Guard element)
	'''«element.prefix»«partSep»«element.owner.ffqn»'''

	static def getPartSep()
	'''«GlobalConstants.FUNC_NAME_PART_SEP»'''

	static def dispatch getPrefix(Object o)
	{
	}

	static def dispatch getPrefix(NamedElement element)
	{
		switch (element.eClass)
		{
			case StatemachFactory.eINSTANCE.statemachPackage.simpleState:
				GlobalConstants.SIMPLE_STATE_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.compositeState:
				GlobalConstants.COMPOSITE_STATE_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.state:
				GlobalConstants.STATE_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.transition:
				GlobalConstants.TRANS_ACTION_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.choicePoint:
				GlobalConstants.CHOICE_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.junctionPoint:
				GlobalConstants.JUNCTION_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.actionChain:
				GlobalConstants.ACTION_CHAIN_FUNC_PREFIX
			case StatemachFactory.eINSTANCE.statemachPackage.guard:
				GlobalConstants.GUARD_FUNC_PREFIX
			case StatemachextFactory.eINSTANCE.statemachextPackage.entryAction:
				GlobalConstants.ENTRY_ACTION_FUNC_PREFIX
			case StatemachextFactory.eINSTANCE.statemachextPackage.exitAction:
				GlobalConstants.EXIT_ACTION_FUNC_PREFIX
			case StatemachextFactory.eINSTANCE.statemachextPackage.
				transitionAction:
				GlobalConstants.TRANS_ACTION_FUNC_PREFIX
			default:
				GlobalConstants.ACTION_FUNC_PREFIX
		}
	}

	static def dispatch String getFfqn(Object o)
	{
	}

	static def dispatch String getFfqn(NamedElement element)
	{
		var txt = element.cachedFullSMName.makeValidCName
		txt.replace(GlobalConstants.QUAL_NAME_SEP,
			GlobalConstants.FUNC_NAME_QUAL_NAME_SEP)
	}

}
