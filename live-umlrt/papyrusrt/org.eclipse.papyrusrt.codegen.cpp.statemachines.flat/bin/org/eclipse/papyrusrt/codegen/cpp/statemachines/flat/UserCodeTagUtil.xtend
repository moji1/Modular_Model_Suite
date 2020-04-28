/*******************************************************************************
 * Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import java.util.ArrayList
import org.eclipse.emf.ecore.EObject
import org.eclipse.papyrusrt.codegen.CodeGenPlugin
import org.eclipse.papyrusrt.codegen.UserEditableRegion
import org.eclipse.papyrusrt.codegen.UserEditableRegion.Label
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.common.CommonElement
import org.eclipse.papyrusrt.xtumlrt.common.Entity
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement
import org.eclipse.papyrusrt.xtumlrt.statemach.State
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition
import org.eclipse.papyrusrt.xtumlrt.statemachext.GuardAction
import org.eclipse.papyrusrt.xtumlrt.statemachext.TransitionAction
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator
import org.eclipse.papyrusrt.xtumlrt.umlrt.RTTrigger
import org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.papyrusrt.codegen.UserEditableRegion.TriggerDetail
import org.eclipse.papyrusrt.codegen.UserEditableRegion.TransitionDetails
import org.eclipse.papyrusrt.xtumlrt.umlrt.AnyEvent
import org.eclipse.papyrusrt.xtumlrt.util.ContainmentUtils

/**
 * Utility class for generating user editable region tag.
 * 
 * @author Young-Soo Roh
 */
class UserCodeTagUtil {

	/**
	 * Get information required to calculate user edit region tag
	 */
	static def Label generateLabel(ActionCode context, UML2xtumlrtTranslator translator, FlatteningTransformer flattener, Entity capsuleOrClass) {
		val label = new Label
		if (capsuleOrClass.eResource != null) {
			label.uri = capsuleOrClass.eResource.URI.toString
		} else {
			var src = translator.getSource(context)
			if(src == null){
				src = translator.getSource(context.eContainer as CommonElement)
			}
			if (src !== null){
				label.uri = src.eResource.URI.toString
			}
		}
		label.qualifiedName = context.getQualifiedName(capsuleOrClass)
		label.type = context.eClass.name.replace("Action", "").toLowerCase
		var RTTrigger trigger = null;
		if(context.eContainer instanceof Guard){
			if(context.eContainer.eContainer instanceof RTTrigger){
				// this is trigger guard
				trigger = context.eContainer.eContainer as RTTrigger
				label.type = UMLPackage.Literals.TRANSITION__TRIGGER.getName();
			}
		}
		label.details = ""
		if(context instanceof TransitionAction || context instanceof GuardAction){
			label.details = context.eContainer.getTransitionDetails(flattener)
		}
		if(trigger != null){
			val ports = new ArrayList
			for (p : trigger.ports) {
				ports.add(p.name)
			}
			var signalName = trigger.signal.name
			if(trigger.signal instanceof AnyEvent){
				signalName = "*"
			}
			val triggerDetail = new TriggerDetail(signalName, ports)
			label.details = label.details + TransitionDetails.EXTRA_DETAIL_SEPARATOR + triggerDetail.tagString
		}
		return label
	}

	/**
	 * Get details for identifying transition or guard since they require more informaiton
	 * in order to distinguish among other elements.
	 */
	static def String getTransitionDetails(EObject object, FlatteningTransformer flattener) {
			var container = object
			while (container != null) {
				if (container instanceof Transition) {
					var CommonElement source = container.sourceVertex
					if(flattener.isNewElement(source)){
						source = flattener.getOriginalOwner(source)
					}else{
						source = flattener.getOriginalElement(source)
					}
					if(source == null){
						CodeGenPlugin.debug("source is null : " + (source as NamedElement).name)
					}
					var CommonElement target = container.targetVertex
					if(flattener.isNewElement(target)){
						target = flattener.getOriginalOwner(target)
					}else{
						target = flattener.getOriginalElement(target)
					}	
					if(target == null){
						CodeGenPlugin.debug("target is null : " + (target as NamedElement).name)
					}
					if(target != null && source != null){
						var sourceQname = QualifiedNames.cachedFullSMName(source as NamedElement)
						var targetQname = QualifiedNames.cachedFullSMName(target as NamedElement)
						var details = new UserEditableRegion.TransitionDetails(sourceQname, targetQname)
						for (t : container.triggers) {
							val ports = new ArrayList
							for (p : (t as RTTrigger).ports) {
								ports.add(p.name)
							}
							val signal = (t as RTTrigger).signal
							if(signal instanceof AnyEvent){
								details.addTriggerDetail("*", ports)
							}else{
								details.addTriggerDetail(signal.name, ports)
							}
						}
						return details.tagString
					}else{
						CodeGenPlugin.error("Source vertex and target vertex should not be null")
						return ""
					}
				}
				container = container.eContainer
		}
		""
	}

	/** 
	 * Calculate qualified name for object's container in the context of UMLRT codegen
	 */
	static def String getQualifiedName(NamedElement context, EObject capsule) {
		val hierarchy =	ContainmentUtils.cachedFullContainmentChain(context)
		var result = QualifiedNames.cachedFullName(capsule as NamedElement)
		
		hierarchy.reverse
		for(parent : hierarchy){
			if (parent instanceof State && !(parent.eContainer instanceof StateMachine)) {
				val smqname = QualifiedNames.cachedFullSMName(parent as NamedElement)
				return result + QualifiedNames.SEPARATOR + smqname
			}
		}

		return result
	}

}
