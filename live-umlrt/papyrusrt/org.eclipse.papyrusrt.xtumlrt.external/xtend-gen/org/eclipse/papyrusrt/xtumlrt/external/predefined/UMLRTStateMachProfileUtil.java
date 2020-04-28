/**
 * Copyright (c) 2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.xtumlrt.external.predefined;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTGuard;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTPseudostate;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTRegion;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTState;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTStateMachine;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTTrigger;
import org.eclipse.papyrusrt.umlrt.uml.util.UMLRTExtensionUtil;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class UMLRTStateMachProfileUtil {
  private final static String UML_REAL_TIME_RT_STATE_MACHINE = "UMLRTStateMachines::RTStateMachine";
  
  private final static String UML_REAL_TIME_RT_STATE = "UMLRTStateMachines::RTState";
  
  private final static String UML_REAL_TIME_RT_PSEUDOSTATE = "UMLRTStateMachines::RTPseudostate";
  
  private final static String UML_REAL_TIME_RT_REGION = "UMLRTStateMachines::RTRegion";
  
  private final static String UML_REAL_TIME_RT_GUARD = "UMLRTStateMachines::RTGuard";
  
  private final static String UML_REAL_TIME_RT_TRIGGER = "UMLRTStateMachines::RTTrigger";
  
  public static Iterable<Pseudostate> getConnectionPoints(final State state) {
    EList<NamedElement> _ownedMembers = null;
    if (state!=null) {
      _ownedMembers=state.getOwnedMembers();
    }
    Iterable<Pseudostate> _filter = null;
    if (_ownedMembers!=null) {
      _filter=Iterables.<Pseudostate>filter(_ownedMembers, Pseudostate.class);
    }
    final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<Pseudostate>filter(_filter, _function);
  }
  
  public static Iterable<Pseudostate> getInternalPseudostates(final Region region) {
    EList<NamedElement> _ownedMembers = null;
    if (region!=null) {
      _ownedMembers=region.getOwnedMembers();
    }
    Iterable<Pseudostate> _filter = null;
    if (_ownedMembers!=null) {
      _filter=Iterables.<Pseudostate>filter(_ownedMembers, Pseudostate.class);
    }
    final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<Pseudostate>filter(_filter, _function);
  }
  
  public static Iterable<Pseudostate> getInternalPseudostates(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    Iterable<Pseudostate> _internalPseudostates = null;
    if (_ownedRegion!=null) {
      _internalPseudostates=UMLRTStateMachProfileUtil.getInternalPseudostates(_ownedRegion);
    }
    return _internalPseudostates;
  }
  
  public static Iterable<Pseudostate> getInternalPseudostates(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    Iterable<Pseudostate> _internalPseudostates = null;
    if (_ownedRegion!=null) {
      _internalPseudostates=UMLRTStateMachProfileUtil.getInternalPseudostates(_ownedRegion);
    }
    return _internalPseudostates;
  }
  
  public static Iterable<Pseudostate> getChoicePoints(final Region region) {
    Iterable<Pseudostate> _internalPseudostates = null;
    if (region!=null) {
      _internalPseudostates=UMLRTStateMachProfileUtil.getInternalPseudostates(region);
    }
    Iterable<Pseudostate> _filter = null;
    if (_internalPseudostates!=null) {
      final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
        return Boolean.valueOf((Objects.equal(it.getKind(), PseudostateKind.CHOICE_LITERAL) && (!UMLRTExtensionUtil.isExcluded(it))));
      };
      _filter=IterableExtensions.<Pseudostate>filter(_internalPseudostates, _function);
    }
    return _filter;
  }
  
  public static Iterable<Pseudostate> getChoicePoints(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    Iterable<Pseudostate> _choicePoints = null;
    if (_ownedRegion!=null) {
      _choicePoints=UMLRTStateMachProfileUtil.getChoicePoints(_ownedRegion);
    }
    return _choicePoints;
  }
  
  public static Iterable<Pseudostate> getChoicePoints(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    Iterable<Pseudostate> _choicePoints = null;
    if (_ownedRegion!=null) {
      _choicePoints=UMLRTStateMachProfileUtil.getChoicePoints(_ownedRegion);
    }
    return _choicePoints;
  }
  
  public static Pseudostate getDeepHistoryPoint(final Region region) {
    Iterable<Pseudostate> _internalPseudostates = null;
    if (region!=null) {
      _internalPseudostates=UMLRTStateMachProfileUtil.getInternalPseudostates(region);
    }
    Pseudostate _findFirst = null;
    if (_internalPseudostates!=null) {
      final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
        return Boolean.valueOf((Objects.equal(it.getKind(), PseudostateKind.DEEP_HISTORY_LITERAL) && (!UMLRTExtensionUtil.isExcluded(it))));
      };
      _findFirst=IterableExtensions.<Pseudostate>findFirst(_internalPseudostates, _function);
    }
    return _findFirst;
  }
  
  public static Pseudostate getDeepHistoryPoint(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    Pseudostate _deepHistoryPoint = null;
    if (_ownedRegion!=null) {
      _deepHistoryPoint=UMLRTStateMachProfileUtil.getDeepHistoryPoint(_ownedRegion);
    }
    return _deepHistoryPoint;
  }
  
  public static Pseudostate getDeepHistoryPoint(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    Pseudostate _deepHistoryPoint = null;
    if (_ownedRegion!=null) {
      _deepHistoryPoint=UMLRTStateMachProfileUtil.getDeepHistoryPoint(_ownedRegion);
    }
    return _deepHistoryPoint;
  }
  
  public static Iterable<Pseudostate> getEntryPoints(final State state) {
    Iterable<Pseudostate> _connectionPoints = UMLRTStateMachProfileUtil.getConnectionPoints(state);
    Iterable<Pseudostate> _filter = null;
    if (_connectionPoints!=null) {
      final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
        return Boolean.valueOf((Objects.equal(it.getKind(), PseudostateKind.ENTRY_POINT_LITERAL) && (!UMLRTExtensionUtil.isExcluded(it))));
      };
      _filter=IterableExtensions.<Pseudostate>filter(_connectionPoints, _function);
    }
    return _filter;
  }
  
  public static Iterable<Pseudostate> getExitPoints(final State state) {
    Iterable<Pseudostate> _connectionPoints = UMLRTStateMachProfileUtil.getConnectionPoints(state);
    Iterable<Pseudostate> _filter = null;
    if (_connectionPoints!=null) {
      final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
        return Boolean.valueOf((Objects.equal(it.getKind(), PseudostateKind.EXIT_POINT_LITERAL) && (!UMLRTExtensionUtil.isExcluded(it))));
      };
      _filter=IterableExtensions.<Pseudostate>filter(_connectionPoints, _function);
    }
    return _filter;
  }
  
  public static Pseudostate getInitialPoint(final Region region) {
    Iterable<Pseudostate> _internalPseudostates = null;
    if (region!=null) {
      _internalPseudostates=UMLRTStateMachProfileUtil.getInternalPseudostates(region);
    }
    Pseudostate _findFirst = null;
    if (_internalPseudostates!=null) {
      final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
        return Boolean.valueOf((Objects.equal(it.getKind(), PseudostateKind.INITIAL_LITERAL) && (!UMLRTExtensionUtil.isExcluded(it))));
      };
      _findFirst=IterableExtensions.<Pseudostate>findFirst(_internalPseudostates, _function);
    }
    return _findFirst;
  }
  
  public static Pseudostate getInitialPoint(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    Pseudostate _initialPoint = null;
    if (_ownedRegion!=null) {
      _initialPoint=UMLRTStateMachProfileUtil.getInitialPoint(_ownedRegion);
    }
    return _initialPoint;
  }
  
  public static Pseudostate getInitialPoint(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    Pseudostate _initialPoint = null;
    if (_ownedRegion!=null) {
      _initialPoint=UMLRTStateMachProfileUtil.getInitialPoint(_ownedRegion);
    }
    return _initialPoint;
  }
  
  public static Iterable<Pseudostate> getJunctionPoints(final Region region) {
    Iterable<Pseudostate> _internalPseudostates = null;
    if (region!=null) {
      _internalPseudostates=UMLRTStateMachProfileUtil.getInternalPseudostates(region);
    }
    Iterable<Pseudostate> _filter = null;
    if (_internalPseudostates!=null) {
      final Function1<Pseudostate, Boolean> _function = (Pseudostate it) -> {
        return Boolean.valueOf((Objects.equal(it.getKind(), PseudostateKind.JUNCTION_LITERAL) && (!UMLRTExtensionUtil.isExcluded(it))));
      };
      _filter=IterableExtensions.<Pseudostate>filter(_internalPseudostates, _function);
    }
    return _filter;
  }
  
  public static Iterable<Pseudostate> getJunctionPoints(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    Iterable<Pseudostate> _junctionPoints = null;
    if (_ownedRegion!=null) {
      _junctionPoints=UMLRTStateMachProfileUtil.getJunctionPoints(_ownedRegion);
    }
    return _junctionPoints;
  }
  
  public static Iterable<Pseudostate> getJunctionPoints(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    Iterable<Pseudostate> _junctionPoints = null;
    if (_ownedRegion!=null) {
      _junctionPoints=UMLRTStateMachProfileUtil.getJunctionPoints(_ownedRegion);
    }
    return _junctionPoints;
  }
  
  public static Region getOwnedRegion(final State state) {
    Region _xblockexpression = null;
    {
      EList<NamedElement> _ownedMembers = null;
      if (state!=null) {
        _ownedMembers=state.getOwnedMembers();
      }
      Iterable<Region> _filter = null;
      if (_ownedMembers!=null) {
        _filter=Iterables.<Region>filter(_ownedMembers, Region.class);
      }
      final Iterable<Region> regions = _filter;
      Region _xifexpression = null;
      if (((regions != null) && (!IterableExtensions.isEmpty(regions)))) {
        _xifexpression = ((Region[])Conversions.unwrapArray(regions, Region.class))[0];
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Region getOwnedRegion(final StateMachine stateMachine) {
    Region _xblockexpression = null;
    {
      EList<NamedElement> _ownedMembers = null;
      if (stateMachine!=null) {
        _ownedMembers=stateMachine.getOwnedMembers();
      }
      Iterable<Region> _filter = null;
      if (_ownedMembers!=null) {
        _filter=Iterables.<Region>filter(_ownedMembers, Region.class);
      }
      final Iterable<Region> regions = _filter;
      Region _xifexpression = null;
      if (((regions != null) && (!IterableExtensions.isEmpty(regions)))) {
        _xifexpression = ((Region[])Conversions.unwrapArray(regions, Region.class))[0];
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Iterable<State> getSubstates(final Region region) {
    EList<NamedElement> _ownedMembers = null;
    if (region!=null) {
      _ownedMembers=region.getOwnedMembers();
    }
    Iterable<State> _filter = null;
    if (_ownedMembers!=null) {
      _filter=Iterables.<State>filter(_ownedMembers, State.class);
    }
    final Function1<State, Boolean> _function = (State it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<State>filter(_filter, _function);
  }
  
  public static Iterable<State> getSubstates(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    return UMLRTStateMachProfileUtil.getSubstates(_ownedRegion);
  }
  
  public static Iterable<State> getSubstates(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    return UMLRTStateMachProfileUtil.getSubstates(_ownedRegion);
  }
  
  public static Iterable<Transition> getTransitions(final Region region) {
    EList<NamedElement> _ownedMembers = null;
    if (region!=null) {
      _ownedMembers=region.getOwnedMembers();
    }
    Iterable<Transition> _filter = null;
    if (_ownedMembers!=null) {
      _filter=Iterables.<Transition>filter(_ownedMembers, Transition.class);
    }
    final Function1<Transition, Boolean> _function = (Transition it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<Transition>filter(_filter, _function);
  }
  
  public static Iterable<Transition> getTransitions(final State state) {
    Region _ownedRegion = null;
    if (state!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(state);
    }
    return UMLRTStateMachProfileUtil.getTransitions(_ownedRegion);
  }
  
  public static Iterable<Transition> getTransitions(final StateMachine stateMachine) {
    Region _ownedRegion = null;
    if (stateMachine!=null) {
      _ownedRegion=UMLRTStateMachProfileUtil.getOwnedRegion(stateMachine);
    }
    return UMLRTStateMachProfileUtil.getTransitions(_ownedRegion);
  }
  
  public static Iterable<Transition> getAllIncomingTransitions(final State state) {
    final Function1<Transition, Boolean> _function = (Transition it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    Iterable<Transition> _filter = IterableExtensions.<Transition>filter(state.getIncomings(), _function);
    Iterable<Transition> _indirectIncomingTransitions = UMLRTStateMachProfileUtil.getIndirectIncomingTransitions(state);
    return Iterables.<Transition>concat(_filter, _indirectIncomingTransitions);
  }
  
  public static Iterable<Transition> getAllOutgoingTransitions(final State state) {
    final Function1<Transition, Boolean> _function = (Transition it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    Iterable<Transition> _filter = IterableExtensions.<Transition>filter(state.getOutgoings(), _function);
    Iterable<Transition> _indirectOutgoingTransitions = UMLRTStateMachProfileUtil.getIndirectOutgoingTransitions(state);
    return Iterables.<Transition>concat(_filter, _indirectOutgoingTransitions);
  }
  
  public static Iterable<Transition> getIndirectIncomingTransitions(final State state) {
    final Function1<Pseudostate, EList<Transition>> _function = (Pseudostate it) -> {
      return it.getIncomings();
    };
    final Function1<Transition, Boolean> _function_1 = (Transition it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<Transition>filter(Iterables.<Transition>concat(IterableExtensions.<Pseudostate, EList<Transition>>map(UMLRTStateMachProfileUtil.getEntryPoints(state), _function)), _function_1);
  }
  
  public static Iterable<Transition> getIndirectOutgoingTransitions(final State state) {
    final Function1<Pseudostate, EList<Transition>> _function = (Pseudostate it) -> {
      return it.getOutgoings();
    };
    final Function1<Transition, Boolean> _function_1 = (Transition it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<Transition>filter(Iterables.<Transition>concat(IterableExtensions.<Pseudostate, EList<Transition>>map(UMLRTStateMachProfileUtil.getExitPoints(state), _function)), _function_1);
  }
  
  public static Behavior getEffect(final Transition transition) {
    Behavior _xblockexpression = null;
    {
      EContentsEList<Object> _uMLRTContents = null;
      if (transition!=null) {
        _uMLRTContents=UMLRTExtensionUtil.<Object>getUMLRTContents(transition, UMLPackage.Literals.TRANSITION__EFFECT);
      }
      final Function1<Behavior, Boolean> _function = (Behavior it) -> {
        boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
        return Boolean.valueOf((!_isExcluded));
      };
      final Iterable<Behavior> behaviours = IterableExtensions.<Behavior>filter(Iterables.<Behavior>filter(_uMLRTContents, Behavior.class), _function);
      Behavior _xifexpression = null;
      if (((behaviours != null) && (!IterableExtensions.isEmpty(behaviours)))) {
        _xifexpression = ((Behavior[])Conversions.unwrapArray(behaviours, Behavior.class))[0];
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Iterable<Trigger> getTriggers(final Transition transition) {
    EContentsEList<Object> _uMLRTContents = null;
    if (transition!=null) {
      _uMLRTContents=UMLRTExtensionUtil.<Object>getUMLRTContents(transition, UMLPackage.Literals.TRANSITION__TRIGGER);
    }
    final Function1<Trigger, Boolean> _function = (Trigger it) -> {
      boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
      return Boolean.valueOf((!_isExcluded));
    };
    return IterableExtensions.<Trigger>filter(Iterables.<Trigger>filter(_uMLRTContents, Trigger.class), _function);
  }
  
  public static Iterable<Constraint> getConstraints(final Element element) {
    Iterable<Constraint> _xifexpression = null;
    if ((element instanceof Namespace)) {
      EList<NamedElement> _ownedMembers = null;
      if (((Namespace)element)!=null) {
        _ownedMembers=((Namespace)element).getOwnedMembers();
      }
      Iterable<Constraint> _filter = null;
      if (_ownedMembers!=null) {
        _filter=Iterables.<Constraint>filter(_ownedMembers, Constraint.class);
      }
      final Function1<Constraint, Boolean> _function = (Constraint it) -> {
        boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
        return Boolean.valueOf((!_isExcluded));
      };
      _xifexpression = IterableExtensions.<Constraint>filter(_filter, _function);
    }
    return _xifexpression;
  }
  
  public static Constraint getGuard(final Transition transition) {
    Constraint _xblockexpression = null;
    {
      Iterable<Constraint> _constraints = null;
      if (transition!=null) {
        _constraints=UMLRTStateMachProfileUtil.getConstraints(transition);
      }
      final Function1<Constraint, Boolean> _function = (Constraint it) -> {
        return Boolean.valueOf((it.getConstrainedElements().contains(transition) && (!IterableExtensions.<Element>exists(it.getConstrainedElements(), ((Function1<Element, Boolean>) (Element it_1) -> {
          return Boolean.valueOf((it_1 instanceof Trigger));
        })))));
      };
      final Function1<Constraint, Boolean> _function_1 = (Constraint it) -> {
        boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
        return Boolean.valueOf((!_isExcluded));
      };
      final Iterable<Constraint> constraints = IterableExtensions.<Constraint>filter(IterableExtensions.<Constraint>filter(_constraints, _function), _function_1);
      Constraint _xifexpression = null;
      if (((constraints != null) && (!IterableExtensions.isEmpty(constraints)))) {
        _xifexpression = ((Constraint[])Conversions.unwrapArray(constraints, Constraint.class))[0];
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Constraint getTriggerGuard(final Trigger trigger) {
    Constraint _xblockexpression = null;
    {
      Iterable<Constraint> _constraints = null;
      if (trigger!=null) {
        _constraints=UMLRTStateMachProfileUtil.getConstraints(trigger);
      }
      final Function1<Constraint, Boolean> _function = (Constraint it) -> {
        final Function1<Element, Boolean> _function_1 = (Element it_1) -> {
          return Boolean.valueOf((it_1 instanceof Trigger));
        };
        return Boolean.valueOf(IterableExtensions.<Element>exists(it.getConstrainedElements(), _function_1));
      };
      final Function1<Constraint, Boolean> _function_1 = (Constraint it) -> {
        return Boolean.valueOf(UMLRTStateMachProfileUtil.isRTGuard(it));
      };
      final Function1<Constraint, RTGuard> _function_2 = (Constraint it) -> {
        return UMLRTStateMachProfileUtil.getRTGuard(it);
      };
      final Function1<RTGuard, Constraint> _function_3 = (RTGuard it) -> {
        return it.getBase_Constraint();
      };
      final Function1<Constraint, Boolean> _function_4 = (Constraint it) -> {
        boolean _isExcluded = UMLRTExtensionUtil.isExcluded(it);
        return Boolean.valueOf((!_isExcluded));
      };
      final Iterable<Constraint> constraints = IterableExtensions.<Constraint>filter(IterableExtensions.<RTGuard, Constraint>map(IterableExtensions.<Constraint, RTGuard>map(IterableExtensions.<Constraint>filter(IterableExtensions.<Constraint>filter(_constraints, _function), _function_1), _function_2), _function_3), _function_4);
      Constraint _xifexpression = null;
      if (((constraints != null) && (!IterableExtensions.isEmpty(constraints)))) {
        _xifexpression = ((Constraint[])Conversions.unwrapArray(constraints, Constraint.class))[0];
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static boolean isRTStateMachine(final Element el) {
    boolean _xblockexpression = false;
    {
      final Stereotype s = el.getApplicableStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_STATE_MACHINE);
      _xblockexpression = ((s != null) && el.isStereotypeApplied(s));
    }
    return _xblockexpression;
  }
  
  public static boolean isRTState(final Element el) {
    boolean _xblockexpression = false;
    {
      final Stereotype s = el.getApplicableStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_STATE);
      _xblockexpression = ((s != null) && el.isStereotypeApplied(s));
    }
    return _xblockexpression;
  }
  
  public static boolean isRTRegion(final Element el) {
    boolean _xblockexpression = false;
    {
      final Stereotype s = el.getApplicableStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_REGION);
      _xblockexpression = ((s != null) && el.isStereotypeApplied(s));
    }
    return _xblockexpression;
  }
  
  public static boolean isRTPseudostate(final Element el) {
    boolean _xblockexpression = false;
    {
      final Stereotype s = el.getApplicableStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_PSEUDOSTATE);
      _xblockexpression = ((s != null) && el.isStereotypeApplied(s));
    }
    return _xblockexpression;
  }
  
  public static boolean isInitialPoint(final Element el) {
    return (UMLRTStateMachProfileUtil.isRTPseudostate(el) && Objects.equal(((Pseudostate) el).getKind(), PseudostateKind.INITIAL_LITERAL));
  }
  
  public static boolean isDeepHistoryPoint(final Element el) {
    return (UMLRTStateMachProfileUtil.isRTPseudostate(el) && Objects.equal(((Pseudostate) el).getKind(), PseudostateKind.DEEP_HISTORY_LITERAL));
  }
  
  public static boolean isEntryPoint(final Element el) {
    return (UMLRTStateMachProfileUtil.isRTPseudostate(el) && Objects.equal(((Pseudostate) el).getKind(), PseudostateKind.ENTRY_POINT_LITERAL));
  }
  
  public static boolean isExitPoint(final Element el) {
    return (UMLRTStateMachProfileUtil.isRTPseudostate(el) && Objects.equal(((Pseudostate) el).getKind(), PseudostateKind.EXIT_POINT_LITERAL));
  }
  
  public static boolean isChoicePoint(final Element el) {
    return (UMLRTStateMachProfileUtil.isRTPseudostate(el) && Objects.equal(((Pseudostate) el).getKind(), PseudostateKind.CHOICE_LITERAL));
  }
  
  public static boolean isJunctionPoint(final Element el) {
    return (UMLRTStateMachProfileUtil.isRTPseudostate(el) && Objects.equal(((Pseudostate) el).getKind(), PseudostateKind.JUNCTION_LITERAL));
  }
  
  public static boolean isRTGuard(final Element el) {
    boolean _xblockexpression = false;
    {
      final Stereotype s = el.getApplicableStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_GUARD);
      _xblockexpression = ((s != null) && el.isStereotypeApplied(s));
    }
    return _xblockexpression;
  }
  
  public static boolean isRTTrigger(final Element el) {
    final Stereotype s = el.getApplicableStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_TRIGGER);
    if ((s != null)) {
      return el.isStereotypeApplied(s);
    }
    return false;
  }
  
  public static RTStateMachine getRTStateMachine(final Element el) {
    RTStateMachine _xblockexpression = null;
    {
      final Stereotype s = el.getAppliedStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_STATE_MACHINE);
      RTStateMachine _xifexpression = null;
      if ((s == null)) {
        _xifexpression = null;
      } else {
        EObject _stereotypeApplication = el.getStereotypeApplication(s);
        _xifexpression = ((RTStateMachine) _stereotypeApplication);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static RTState getRTState(final Element el) {
    RTState _xblockexpression = null;
    {
      final Stereotype s = el.getAppliedStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_STATE);
      RTState _xifexpression = null;
      if ((s == null)) {
        _xifexpression = null;
      } else {
        EObject _stereotypeApplication = el.getStereotypeApplication(s);
        _xifexpression = ((RTState) _stereotypeApplication);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static RTRegion getRTRegion(final Element el) {
    RTRegion _xblockexpression = null;
    {
      final Stereotype s = el.getAppliedStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_REGION);
      RTRegion _xifexpression = null;
      if ((s == null)) {
        _xifexpression = null;
      } else {
        EObject _stereotypeApplication = el.getStereotypeApplication(s);
        _xifexpression = ((RTRegion) _stereotypeApplication);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static RTPseudostate getRTPseudostate(final Element el) {
    RTPseudostate _xblockexpression = null;
    {
      final Stereotype s = el.getAppliedStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_PSEUDOSTATE);
      RTPseudostate _xifexpression = null;
      if ((s == null)) {
        _xifexpression = null;
      } else {
        EObject _stereotypeApplication = el.getStereotypeApplication(s);
        _xifexpression = ((RTPseudostate) _stereotypeApplication);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static RTGuard getRTGuard(final Element el) {
    RTGuard _xblockexpression = null;
    {
      final Stereotype s = el.getAppliedStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_GUARD);
      RTGuard _xifexpression = null;
      if ((s == null)) {
        _xifexpression = null;
      } else {
        EObject _stereotypeApplication = el.getStereotypeApplication(s);
        _xifexpression = ((RTGuard) _stereotypeApplication);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static RTTrigger getRTTrigger(final Element el) {
    final Stereotype s = el.getAppliedStereotype(UMLRTStateMachProfileUtil.UML_REAL_TIME_RT_TRIGGER);
    if ((s == null)) {
      return null;
    }
    EObject _stereotypeApplication = el.getStereotypeApplication(s);
    return ((RTTrigger) _stereotypeApplication);
  }
  
  public static boolean isSimpleState(final State state) {
    return ((UMLRTStateMachProfileUtil.getOwnedRegion(state) == null) || UMLRTStateMachProfileUtil.isEmpty(UMLRTStateMachProfileUtil.getOwnedRegion(state)));
  }
  
  public static boolean isCompositeState(final State state) {
    boolean _isSimpleState = UMLRTStateMachProfileUtil.isSimpleState(state);
    return (!_isSimpleState);
  }
  
  public static boolean isEmpty(final Region region) {
    boolean _xifexpression = false;
    Region _extendedRegion = region.getExtendedRegion();
    boolean _tripleEquals = (_extendedRegion == null);
    if (_tripleEquals) {
      _xifexpression = ((region.getOwnedElements() == null) || region.getOwnedElements().isEmpty());
    } else {
      _xifexpression = (((region.getOwnedElements() == null) && region.getOwnedElements().isEmpty()) && UMLRTStateMachProfileUtil.isEmpty(region.getExtendedRegion()));
    }
    return _xifexpression;
  }
}
