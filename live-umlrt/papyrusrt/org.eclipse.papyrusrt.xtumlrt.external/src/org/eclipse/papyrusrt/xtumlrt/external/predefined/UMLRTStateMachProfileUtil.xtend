/*******************************************************************************
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external.predefined

import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTGuard
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTTrigger
import org.eclipse.uml2.uml.Behavior
import org.eclipse.uml2.uml.Constraint
import org.eclipse.uml2.uml.Element
import org.eclipse.uml2.uml.Namespace
import org.eclipse.uml2.uml.Pseudostate
import org.eclipse.uml2.uml.PseudostateKind
import org.eclipse.uml2.uml.Region
import org.eclipse.uml2.uml.State
import org.eclipse.uml2.uml.StateMachine
import org.eclipse.uml2.uml.Transition
import org.eclipse.uml2.uml.Trigger
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTPseudostate
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTStateMachine
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTState
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTRegion

import static extension org.eclipse.papyrusrt.umlrt.uml.util.UMLRTExtensionUtil.*
import org.eclipse.uml2.uml.Vertex

class UMLRTStateMachProfileUtil
{

	static val UML_REAL_TIME_RT_STATE_MACHINE = "UMLRTStateMachines::RTStateMachine";

	static val UML_REAL_TIME_RT_STATE = "UMLRTStateMachines::RTState";

	static val UML_REAL_TIME_RT_PSEUDOSTATE = "UMLRTStateMachines::RTPseudostate";

	static val UML_REAL_TIME_RT_REGION = "UMLRTStateMachines::RTRegion";

	static val UML_REAL_TIME_RT_GUARD = "UMLRTStateMachines::RTGuard";
	
	static val UML_REAL_TIME_RT_TRIGGER = "UMLRTStateMachines::RTTrigger";

    static def Iterable<Pseudostate> getConnectionPoints( State state )
    {
        state?.ownedMembers?.filter( Pseudostate ).filter[!excluded]
    }

    static def Iterable<Pseudostate> getInternalPseudostates( Region region )
    {
        region?.ownedMembers?.filter( Pseudostate ).filter[!excluded]
    }

    static def Iterable<Pseudostate> getInternalPseudostates( State state )
    {
        state?.ownedRegion?.internalPseudostates
    }

    static def Iterable<Pseudostate> getInternalPseudostates( StateMachine stateMachine )
    {
        stateMachine?.ownedRegion?.internalPseudostates
    }

    static def Iterable<Pseudostate> getChoicePoints( Region region )
    {
        region?.internalPseudostates?.filter[ it.kind == PseudostateKind.CHOICE_LITERAL && !excluded ]
    }

    static def Iterable<Pseudostate> getChoicePoints( State state )
    {
        state?.ownedRegion?.choicePoints
    }

    static def Iterable<Pseudostate> getChoicePoints( StateMachine stateMachine )
    {
        stateMachine?.ownedRegion?.choicePoints
    }

    static def Pseudostate getDeepHistoryPoint( Region region )
    {
        region?.internalPseudostates?.findFirst[ it.kind == PseudostateKind.DEEP_HISTORY_LITERAL && !excluded ]
    }

    static def Pseudostate getDeepHistoryPoint( State state )
    {
        state?.ownedRegion?.deepHistoryPoint
    }

    static def Pseudostate getDeepHistoryPoint( StateMachine stateMachine )
    {
        stateMachine?.ownedRegion?.deepHistoryPoint
    }

    static def Iterable<Pseudostate> getEntryPoints( State state )
    {
        getConnectionPoints( state )?.filter [ it.kind == PseudostateKind.ENTRY_POINT_LITERAL && !excluded ]
    }

    static def Iterable<Pseudostate> getExitPoints( State state )
    {
        getConnectionPoints( state )?.filter [ it.kind == PseudostateKind.EXIT_POINT_LITERAL && !excluded ]
    }

    static def Pseudostate getInitialPoint( Region region )
    {
        region?.internalPseudostates?.findFirst[ it.kind == PseudostateKind.INITIAL_LITERAL && !excluded ]
    }

    static def Pseudostate getInitialPoint( State state )
    {
        state?.ownedRegion?.initialPoint
    }

    static def Pseudostate getInitialPoint( StateMachine stateMachine )
    {
        stateMachine?.ownedRegion?.initialPoint
    }

    static def Iterable<Pseudostate> getJunctionPoints( Region region )
    {
        region?.internalPseudostates?.filter[ it.kind == PseudostateKind.JUNCTION_LITERAL && !excluded ]
    }

    static def Iterable<Pseudostate> getJunctionPoints( State state )
    {
        state?.ownedRegion?.junctionPoints
    }

    static def Iterable<Pseudostate> getJunctionPoints( StateMachine stateMachine )
    {
        stateMachine?.ownedRegion?.junctionPoints
    }

    static def Region getOwnedRegion( State state )
    {
        val regions = state?.ownedMembers?.filter( Region )
        if (regions !== null && !regions.empty)
            regions.get(0)
    }

    static def Region getOwnedRegion( StateMachine stateMachine )
    {
        val regions = stateMachine?.ownedMembers?.filter( Region )
        if (regions !== null && !regions.empty)
            regions.get(0)
    }

    static def Iterable<State> getSubstates( Region region )
    {
        region?.ownedMembers?.filter( State ).filter[!excluded]
    }

    static def Iterable<State> getSubstates( State state )
    {
        getSubstates( state?.ownedRegion )
    }

    static def Iterable<State> getSubstates( StateMachine stateMachine )
    {
        getSubstates( stateMachine?.ownedRegion )
    }

    static def Iterable<Transition> getTransitions( Region region )
    {
        region?.ownedMembers?.filter( Transition ).filter[!excluded]
    }

    static def Iterable<Transition> getTransitions( State state )
    {
        getTransitions( state?.ownedRegion )
    }

    static def Iterable<Transition> getTransitions( StateMachine stateMachine )
    {
        getTransitions( stateMachine?.ownedRegion )
    }

    static def Iterable<Transition> getAllIncomingTransitions( State state )
    {
        state.incomings.filter[!excluded] + getIndirectIncomingTransitions( state )
    }

    static def Iterable<Transition> getAllOutgoingTransitions( State state )
    {
        state.outgoings.filter[!excluded] + getIndirectOutgoingTransitions( state )
    }

    static def Iterable<Transition> getIndirectIncomingTransitions( State state )
    {
        getEntryPoints( state ).map[incomings].flatten.filter[!excluded]
    }

    static def Iterable<Transition> getIndirectOutgoingTransitions( State state )
    {
        getExitPoints( state ).map[outgoings].flatten.filter[!excluded]
    }

    static def Behavior getEffect( Transition transition )
    {
        val behaviours = transition?.getUMLRTContents(UMLPackage.Literals.TRANSITION__EFFECT).filter( Behavior ).filter[!excluded]
        if (behaviours !== null && !behaviours.empty)
        {
            behaviours.get(0)
        }
    }

    static def Iterable<Trigger> getTriggers( Transition transition )
    {
        transition?.getUMLRTContents(UMLPackage.Literals.TRANSITION__TRIGGER).filter( Trigger ).filter[!excluded]
    }

    static def Iterable<Constraint> getConstraints( Element element )
    {
        if (element instanceof Namespace)
        {
            element?.ownedMembers?.filter( Constraint ).filter[!excluded]
        }
    }

    static def Constraint getGuard( Transition transition )
    {
        val constraints = transition?.constraints
            .filter[constrainedElements.contains(transition) && !constrainedElements.exists[it instanceof Trigger]]
            .filter[!excluded]
        if (constraints !== null && !constraints.empty) constraints.get(0)
    }

    static def Constraint getTriggerGuard( Trigger trigger )
    {
        val constraints = trigger?.constraints
            .filter[constrainedElements.exists[it instanceof Trigger]]
            .filter[isRTGuard]
            .map[getRTGuard]
            .map[base_Constraint]
            .filter[!excluded]
        if (constraints !== null && !constraints.empty) constraints.get(0)
    }


    static def boolean isRTStateMachine( Element el )
    {
        val s = el.getApplicableStereotype(UML_REAL_TIME_RT_STATE_MACHINE)
        s !== null && el.isStereotypeApplied(s)
    }

    static def boolean isRTState( Element el )
    {
        val s = el.getApplicableStereotype(UML_REAL_TIME_RT_STATE)
        s !== null && el.isStereotypeApplied(s)
    }

    static def boolean isRTRegion( Element el )
    {
        val s = el.getApplicableStereotype(UML_REAL_TIME_RT_REGION)
        s !== null && el.isStereotypeApplied(s)
    }

    static def boolean isRTPseudostate( Element el )
    {
        val s = el.getApplicableStereotype(UML_REAL_TIME_RT_PSEUDOSTATE)
        s !== null && el.isStereotypeApplied(s)
    }

    static def boolean isInitialPoint( Element el )
    {
        el.isRTPseudostate
        && (el as Pseudostate).kind == PseudostateKind.INITIAL_LITERAL
    }

    static def boolean isDeepHistoryPoint( Element el )
    {
        el.isRTPseudostate
        && (el as Pseudostate).kind == PseudostateKind.DEEP_HISTORY_LITERAL
    }

    static def boolean isEntryPoint( Element el )
    {
        el.isRTPseudostate
        && (el as Pseudostate).kind == PseudostateKind.ENTRY_POINT_LITERAL
    }

    static def boolean isExitPoint( Element el )
    {
        el.isRTPseudostate
        && (el as Pseudostate).kind == PseudostateKind.EXIT_POINT_LITERAL
    }

    static def boolean isChoicePoint( Element el )
    {
        el.isRTPseudostate
        && (el as Pseudostate).kind == PseudostateKind.CHOICE_LITERAL
    }

    static def boolean isJunctionPoint( Element el )
    {
        el.isRTPseudostate
        && (el as Pseudostate).kind == PseudostateKind.JUNCTION_LITERAL
    }

    static def boolean isRTGuard( Element el )
    {
        val s = el.getApplicableStereotype(UML_REAL_TIME_RT_GUARD)
        s !== null && el.isStereotypeApplied(s)
    }

    static def boolean isRTTrigger( Element el )
    {
        val s = el.getApplicableStereotype(UML_REAL_TIME_RT_TRIGGER)
        if (s !== null)
        {
            return el.isStereotypeApplied(s)
        }
        return false;
    }

    static def RTStateMachine getRTStateMachine( Element el )
    {
        val s = el.getAppliedStereotype(UML_REAL_TIME_RT_STATE_MACHINE)
        if (s === null) null else el.getStereotypeApplication(s) as RTStateMachine
    }

    static def RTState getRTState( Element el )
    {
        val s = el.getAppliedStereotype(UML_REAL_TIME_RT_STATE)
        if (s === null) null else el.getStereotypeApplication(s) as RTState
    }

    static def RTRegion getRTRegion( Element el )
    {
        val s = el.getAppliedStereotype(UML_REAL_TIME_RT_REGION)
        if (s === null) null else el.getStereotypeApplication(s) as RTRegion
    }

    static def RTPseudostate getRTPseudostate( Element el )
    {
        val s = el.getAppliedStereotype(UML_REAL_TIME_RT_PSEUDOSTATE)
        if (s === null) null else el.getStereotypeApplication(s) as RTPseudostate
    }

    static def RTGuard getRTGuard( Element el )
    {
        val s = el.getAppliedStereotype(UML_REAL_TIME_RT_GUARD)
        if (s === null) null else el.getStereotypeApplication(s) as RTGuard
    }

    static def RTTrigger getRTTrigger(Element el) 
    {
        val s = el.getAppliedStereotype(UML_REAL_TIME_RT_TRIGGER)
        if (s === null)
        {
            return null
        }
        return el.getStereotypeApplication(s) as RTTrigger
    }

    static def isSimpleState( State state )
    {
        state.ownedRegion === null || state.ownedRegion.isEmpty
    }

    static def isCompositeState( State state )
    {
        !state.isSimpleState
    }

    static def boolean isEmpty( Region region )
    {
        if (region.extendedRegion === null)
            region.ownedElements === null || region.ownedElements.empty
        else
            (region.ownedElements === null && region.ownedElements.empty) && region.extendedRegion.isEmpty
    }


}