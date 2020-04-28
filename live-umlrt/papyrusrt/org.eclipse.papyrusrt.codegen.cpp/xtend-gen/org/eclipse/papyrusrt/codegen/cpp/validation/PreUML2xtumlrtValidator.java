/**
 * Copyright (c) 2017 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.validation;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.CppCodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.UMLPrettyPrinter;
import org.eclipse.papyrusrt.codegen.cpp.validation.StatusFactory;
import org.eclipse.papyrusrt.umlrt.uml.UMLRTGuard;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.UMLRTProfileUtil;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.UMLRTStateMachProfileUtil;
import org.eclipse.papyrusrt.xtumlrt.trans.TransformValidator;
import org.eclipse.papyrusrt.xtumlrt.util.ContainmentUtils;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Pre UML2xtumlrt validation
 * @author ysroh
 */
@SuppressWarnings("all")
public class PreUML2xtumlrtValidator implements TransformValidator<List<EObject>> {
  @Extension
  private UMLPrettyPrinter prettyPrinter = new UMLPrettyPrinter();
  
  private final static int UNLIMITED_NATURAL = LiteralUnlimitedNatural.UNLIMITED;
  
  @Override
  public MultiStatus validate(final List<EObject> context) {
    MultiStatus _xblockexpression = null;
    {
      final MultiStatus status = new MultiStatus(CodeGenPlugin.ID, IStatus.INFO, "UML-RT Code Generator - pre-generation validation", null);
      for (final EObject e : context) {
        final Procedure1<EObject> _function = (EObject it) -> {
          this.validateElement(it, status);
          this.validateDuplicateElement(it, status);
        };
        IteratorExtensions.<EObject>forEach(e.eAllContents(), _function);
      }
      _xblockexpression = status;
    }
    return _xblockexpression;
  }
  
  protected void _validateElement(final EObject e, final MultiStatus result) {
  }
  
  protected void _validateElement(final Constraint element, final MultiStatus result) {
    final UMLRTGuard guard = UMLRTGuard.getInstance(element);
    if (((guard != null) && (!guard.getBodies().containsKey(CppCodeGenPlugin.LANGUAGE)))) {
      StatusFactory.addErrorStatus(element, "Guard must have a C++ body specification", "No C++ body was found.", result);
    }
  }
  
  protected void _validateElement(final MultiplicityElement element, final MultiStatus result) {
    final ValueSpecification lower = element.getLowerValue();
    if ((lower != null)) {
      this.validateElement(lower, result);
    }
    final ValueSpecification upper = element.getUpperValue();
    if ((upper != null)) {
      this.validateElement(upper, result);
    }
  }
  
  /**
   * Validates properties that are not ports, i.e. parts or attributes.
   * 
   * <p>Parts must satisfy the following:
   * 
   * <ol>
   *   <li> A part must have the {@link CapsulePart} stereotype
   *   <li> A part must have a type
   *   <li> The type of a part must be a {@link Capsule}
   *   <li> A part must have a replication set.
   *   <li> A part's replication must not be 0..
   * </ol>
   * 
   * <p>Note: if the property doesn't have the {@link CapsulePart} stereotype we do not issue a warning or error
   * because it might be an attribute.
   * 
   * @param element - A {@link Property}.
   * @param result - A {@link MultiStatus}.
   */
  protected void _validateElement(final Property property, final MultiStatus result) {
    boolean _isCapsulePart = UMLRTProfileUtil.isCapsulePart(property);
    if (_isCapsulePart) {
      Type _type = property.getType();
      boolean _tripleEquals = (_type == null);
      if (_tripleEquals) {
        StatusFactory.addErrorStatus(property, "The part\'s type is unset.", "All parts must have their type set to be a Capsule.", result);
      } else {
        boolean _isCapsule = UMLRTProfileUtil.isCapsule(property.getType());
        boolean _not = (!_isCapsule);
        if (_not) {
          StatusFactory.addErrorStatus(property, "The part\'s type is not a Capsule.", "All parts must have their type set to be a Capsule.", result);
        }
      }
      boolean _isReplicationSet = this.isReplicationSet(property);
      boolean _not_1 = (!_isReplicationSet);
      if (_not_1) {
        StatusFactory.addWarningStatus(property, "The part has no replication set. Assuming 1.", 
          ("The replication of a part is its multiplicity. The multiplicity\'s lower and upper values are derived from the replication. " + "If no replication is given the default (1) is assumed. A part must not have replication set to \'0..*\'."), result);
      } else {
        boolean _isUnlimited = this.isUnlimited(this.getReplication(property).get());
        if (_isUnlimited) {
          StatusFactory.addErrorStatus(property, "The part has replication set to 0..*. This is not allowed.", 
            ("The part\'s replication must be set to a positive integer or an arithmetic expression " + 
              "with constants and variables defined in the namespace."), result);
        }
      }
    }
  }
  
  /**
   * Validates a port.
   * 
   * <p>Ports must satisfy the following:
   * 
   * <ol>
   *   <li> A port must have the {@link RTPort} stereotype
   *   <li> A port must have a type
   *   <li> The type of a port must be a {@link Protocol}
   *   <li> A port must have a replication set.
   *   <li> A port's replication must not be 0..
   * </ol>
   * 
   * @param element - A {@link Port}.
   * @param result - A {@link MultiStatus}.
   */
  protected void _validateElement(final Port port, final MultiStatus result) {
    boolean _isRTPort = UMLRTProfileUtil.isRTPort(port);
    boolean _not = (!_isRTPort);
    if (_not) {
      StatusFactory.addWarningStatus(port, "This port doesn\'t have the RTPort stereotype.", result);
    }
    Type _type = port.getType();
    boolean _tripleEquals = (_type == null);
    if (_tripleEquals) {
      StatusFactory.addErrorStatus(port, "The port\'s type is unset.", "All ports must have their type set to be a Protocol.", result);
    } else {
      boolean _isProtocol = UMLRTProfileUtil.isProtocol(port.getType());
      boolean _not_1 = (!_isProtocol);
      if (_not_1) {
        StatusFactory.addErrorStatus(port, "The port\'s type is not a protocol.", "All ports must have their type set to be a Protocol.", result);
      }
    }
    boolean _isReplicationSet = this.isReplicationSet(port);
    boolean _not_2 = (!_isReplicationSet);
    if (_not_2) {
      StatusFactory.addWarningStatus(port, "The port has no replication set. Assuming 1.", 
        ("The replication of a port is its multiplicity. The multiplicity\'s lower and upper values are derived from the replication. " + "If no replication is given the default (1) is assumed. A port must not have replication set to \'0..*\'."), result);
    } else {
      boolean _isUnlimited = this.isUnlimited(this.getReplication(port).get());
      if (_isUnlimited) {
        StatusFactory.addErrorStatus(port, "The port has replication set to 0..*. This is not allowed.", 
          ("The port\'s replication must be set to a positive integer or an arithmetic expression " + 
            "with constants and variables defined in the namespace."), result);
      }
    }
  }
  
  private boolean isReplicationSet(final MultiplicityElement element) {
    final boolean lowerValueIsSet = (element.eIsSet(UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER_VALUE) || (element.getLowerValue() != null));
    final boolean upperValueIsSet = (element.eIsSet(UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER_VALUE) || (element.getUpperValue() != null));
    return (lowerValueIsSet || upperValueIsSet);
  }
  
  private Optional<ValueSpecification> getReplication(final MultiplicityElement element) {
    Optional<ValueSpecification> _xblockexpression = null;
    {
      ValueSpecification lowerValue = element.getLowerValue();
      ValueSpecification upperValue = element.getUpperValue();
      final boolean lowerValueIsSet = (element.eIsSet(UMLPackage.Literals.MULTIPLICITY_ELEMENT__LOWER_VALUE) || (lowerValue != null));
      final boolean upperValueIsSet = (element.eIsSet(UMLPackage.Literals.MULTIPLICITY_ELEMENT__UPPER_VALUE) || (upperValue != null));
      Optional<ValueSpecification> _xifexpression = null;
      if (upperValueIsSet) {
        _xifexpression = Optional.<ValueSpecification>of(upperValue);
      } else {
        Optional<ValueSpecification> _xifexpression_1 = null;
        if (lowerValueIsSet) {
          _xifexpression_1 = Optional.<ValueSpecification>of(lowerValue);
        } else {
          _xifexpression_1 = Optional.<ValueSpecification>empty();
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  private boolean _isUnlimited(final ValueSpecification spec) {
    return false;
  }
  
  private boolean _isUnlimited(final LiteralUnlimitedNatural spec) {
    int _value = spec.getValue();
    return (_value == PreUML2xtumlrtValidator.UNLIMITED_NATURAL);
  }
  
  /**
   * Validates a state.
   * 
   * <p>Checks whether there are conflicting outgoing transitions from the state.
   * 
   * @param state - A {@link State}.
   * @param result - A {@link MultiStatus}.
   */
  protected void _validateElement(final State state, final MultiStatus result) {
    final Function1<Element, Boolean> _function = (Element it) -> {
      return Boolean.valueOf((it instanceof State));
    };
    final Function1<Element, State> _function_1 = (Element it) -> {
      return ((State) it);
    };
    final Iterable<State> containingStates = IterableExtensions.<Element, State>map(IterableExtensions.<Element>filter(ContainmentUtils.getAllOwningElementsUptoType(state, StateMachine.class), _function), _function_1);
    final Function1<State, Iterable<Transition>> _function_2 = (State it) -> {
      return UMLRTStateMachProfileUtil.getAllOutgoingTransitions(it);
    };
    final Iterable<Transition> allOutgoingTransitionsHierarchy = Iterables.<Transition>concat(IterableExtensions.<State, Iterable<Transition>>map(containingStates, _function_2));
    Iterable<Transition> _allOutgoingTransitions = UMLRTStateMachProfileUtil.getAllOutgoingTransitions(state);
    for (final Transition transition : _allOutgoingTransitions) {
      {
        final Function1<Transition, Boolean> _function_3 = (Transition it) -> {
          return Boolean.valueOf(this.conflict(it, transition));
        };
        final Iterable<Transition> otherEquivalentTransitions = IterableExtensions.<Transition>filter(allOutgoingTransitionsHierarchy, _function_3);
        boolean _isEmpty = IterableExtensions.isEmpty(otherEquivalentTransitions);
        boolean _not = (!_isEmpty);
        if (_not) {
          final String conflictingTransitions = this.prettyPrinter.multiLineListText(otherEquivalentTransitions);
          String _text = this.prettyPrinter.text(transition);
          String _plus = (("The state has at least two conflicting (ambiguous) outgoing transitions with the same trigger and guard. \n" + "Transition ") + _text);
          String _plus_1 = (_plus + "conflicts with the following:\n");
          String _plus_2 = (_plus_1 + conflictingTransitions);
          String _plus_3 = (_plus_2 + "\n");
          String _plus_4 = (_plus_3 + "The transition with the deepest source will be selected and the others ignored.\n");
          String _plus_5 = (_plus_4 + "If there is more than one such transitions any one of them will be selected and others will be ignored.\n");
          String _plus_6 = (_plus_5 + "Note that the transitions may have a different source, namely a composite state that contains this state.\n");
          StatusFactory.addWarningStatus(state, "State has conflicting transitions.", _plus_6, result);
          return;
        }
      }
    }
  }
  
  /**
   * @param transition1 - A {@link Transition}.
   * @param transition2 - A {@link Transition}.
   * @return {@code true} iff the two transition conflict, i.e. if they are not the same but
   * they have a common trigger (cf. {@link #commonTrigger}) and the same guard (cf. {@link #sameGuard}).
   * Note that this method already assumes that the source of one of the transitions is the same as or is
   * contained in the source of the other transition.
   */
  private boolean conflict(final Transition transition1, final Transition transition2) {
    return (((transition1 != transition2) && this.commonTrigger(transition1, transition2)) && this.sameGuard(transition1, transition2));
  }
  
  /**
   * @param transition1 - A {@link Transition}.
   * @param transition2 - A {@link Transition}.
   * @return {@code true} iff the two transition have equal guards or at least guards with equal specification.
   */
  private boolean sameGuard(final Transition transition1, final Transition transition2) {
    boolean _or = false;
    Constraint _guard = transition1.getGuard();
    Constraint _guard_1 = transition2.getGuard();
    boolean _equals = Objects.equal(_guard, _guard_1);
    if (_equals) {
      _or = true;
    } else {
      Constraint _guard_2 = transition1.getGuard();
      ValueSpecification _specification = null;
      if (_guard_2!=null) {
        _specification=_guard_2.getSpecification();
      }
      Constraint _guard_3 = transition2.getGuard();
      ValueSpecification _specification_1 = null;
      if (_guard_3!=null) {
        _specification_1=_guard_3.getSpecification();
      }
      boolean _equals_1 = Objects.equal(_specification, _specification_1);
      _or = _equals_1;
    }
    return _or;
  }
  
  /**
   * @param transition1 - A {@link Transition}.
   * @param transition2 - A {@link Transition}.
   * @return {@code true} iff the two transition have at least one equivalent trigger (cf. {@link #equivalentTrigger})
   */
  private boolean commonTrigger(final Transition transition1, final Transition transition2) {
    final Function1<Trigger, Boolean> _function = (Trigger t1) -> {
      final Function1<Trigger, Boolean> _function_1 = (Trigger t2) -> {
        return Boolean.valueOf(this.equivalentTrigger(t1, t2));
      };
      return Boolean.valueOf(IterableExtensions.<Trigger>exists(transition2.getTriggers(), _function_1));
    };
    return IterableExtensions.<Trigger>exists(transition1.getTriggers(), _function);
  }
  
  /**
   * @param trigger1 - A {@link Trigger}.
   * @param trigger2 - A {@link Trigger}.
   * @return {@code true} iff the two triggers are eequivalent, i.e., iff they are equal or
   * the have the same event and at least one port in common.
   */
  private boolean equivalentTrigger(final Trigger trigger1, final Trigger trigger2) {
    return (Objects.equal(trigger1, trigger2) || (Objects.equal(trigger1.getEvent(), trigger2.getEvent()) && IterableExtensions.<Port>exists(trigger1.getPorts(), ((Function1<Port, Boolean>) (Port it) -> {
      return Boolean.valueOf(trigger2.getPorts().contains(it));
    }))));
  }
  
  /**
   * Validates a transition.
   * 
   * <p>A transition must satisfy the following:
   * 
   * <ol>
   *   <li>If the transition is the first segment in a transition chain, it should have a trigger (but it may be added by a subclass)
   * </ol>
   * 
   * @param transition - A {@link Transition}.
   * @param result - A {@link MultiStatus}.
   */
  protected void _validateElement(final Transition transition, final MultiStatus result) {
    boolean _isFirstSegment = this.isFirstSegment(transition);
    if (_isFirstSegment) {
      boolean _isEmpty = transition.getTriggers().isEmpty();
      if (_isEmpty) {
        StatusFactory.addWarningStatus(transition, 
          "Transition has no triggers", 
          (("A transition which is the first segment in a transition chain, i.e. a transition which " + 
            "leaves a state, should have at least one trigger. Triggers may be added in state machines ") + 
            "of capsules which are subclasses of this capsule."), result);
      }
    }
  }
  
  /**
   * Determines if the given transition is the first segment in a transition chain,
   * this is, if the source of the transition is a state either directly or as an exit point.
   * 
   * @param t - A {@link Transition}
   * @return {@code true} iff t's source is either a {@link State} or an exit point with no incoming transitions.
   */
  private boolean isFirstSegment(final Transition t) {
    boolean _xblockexpression = false;
    {
      final Vertex source = t.getSource();
      _xblockexpression = ((source instanceof State) || (((source instanceof Pseudostate) && Objects.equal(((Pseudostate) source).getKind(), PseudostateKind.EXIT_POINT_LITERAL)) && source.getIncomings().isEmpty()));
    }
    return _xblockexpression;
  }
  
  /**
   * Validates a trigger.
   * 
   * <p>A trigger must satisfy the following:
   * 
   * <ol>
   *   <li>A trigger should have the {@link RTTrigger} stereotype
   *   <li>A trigger should have an event
   *   <li>A trigger should have a port
   * </ol>
   * 
   * @param trigger - A {@link Trigger}.
   * @param result - A {@link MultiStatus}.
   */
  protected void _validateElement(final Trigger trigger, final MultiStatus result) {
    boolean _isRTTrigger = UMLRTStateMachProfileUtil.isRTTrigger(trigger);
    boolean _not = (!_isRTTrigger);
    if (_not) {
      StatusFactory.addWarningStatus(trigger, 
        "Trigger doesn\'t have the \"RTTrigger\" stereotype applied.", 
        "Triggers without the \"RTTrigger\" stereotype might lead to incorrectly generated code.", result);
    }
    Event _event = trigger.getEvent();
    boolean _tripleEquals = (_event == null);
    if (_tripleEquals) {
      StatusFactory.addWarningStatus(trigger, 
        "Trigger has no event", 
        "A trigger should have an event associated. Without an event, code generation might fail or it might produce incorrect code.", result);
    }
    boolean _isEmpty = trigger.getPorts().isEmpty();
    if (_isEmpty) {
      StatusFactory.addWarningStatus(trigger, 
        "Trigger has no ports", 
        "A trigger should have at least one port associated. Without a port, code generation might fail or it might produce incorrect code.", result);
    }
  }
  
  /**
   * Validates a pseudostate.
   * 
   * <p>Pseudostates must satisfy the following:
   * 
   * <ol>
   *   <li>A choice point should have at least one outgoing transition.
   *   <li>For a choice point with only one outgoing transition, the transition should not have a guard.
   *   <li>A choice point should not have multiple unguarded outgoing transitions.
   *   <li>A junction point should have one outgoing transition.
   *   <li>A junction point must not have more than one outgoing transition.
   * </ol>
   * 
   * @param pseudostate - A {@link Pseudostate}
   * @param result - A {@link MultiStatus}
   */
  protected void _validateElement(final Pseudostate pseudostate, final MultiStatus result) {
    boolean _isChoicePoint = UMLRTStateMachProfileUtil.isChoicePoint(pseudostate);
    if (_isChoicePoint) {
      final EList<Transition> outgoingTransitions = pseudostate.getOutgoings();
      boolean _isEmpty = outgoingTransitions.isEmpty();
      if (_isEmpty) {
        StatusFactory.addWarningStatus(pseudostate, 
          "Choice point has no outgoing transitions.", 
          ("A choice point should have outgoing transitions. Outgoing transitions may be specified in " + 
            "state machines of capsules which are subclasses of this capsule."), result);
      } else {
        int _size = outgoingTransitions.size();
        boolean _equals = (_size == 1);
        if (_equals) {
          Constraint _guard = outgoingTransitions.get(0).getGuard();
          boolean _tripleNotEquals = (_guard != null);
          if (_tripleNotEquals) {
            StatusFactory.addWarningStatus(pseudostate, 
              "Choice point has exactly one guarded outgoing transition.", 
              "If a choice point has only one outgoing transition, it should be guarded. ", result);
          }
        } else {
          final Function1<Transition, Boolean> _function = (Transition it) -> {
            Constraint _guard_1 = it.getGuard();
            return Boolean.valueOf((_guard_1 == null));
          };
          final Iterable<Transition> unguardedTransitions = IterableExtensions.<Transition>filter(outgoingTransitions, _function);
          int _size_1 = IterableExtensions.size(unguardedTransitions);
          boolean _greaterThan = (_size_1 > 1);
          if (_greaterThan) {
            StatusFactory.addWarningStatus(pseudostate, 
              "Choice point has multiple unguarded transitions.", 
              "A choice point should have one unguarded transition at most.", result);
          }
        }
      }
    } else {
      boolean _isJunctionPoint = UMLRTStateMachProfileUtil.isJunctionPoint(pseudostate);
      if (_isJunctionPoint) {
        boolean _isEmpty_1 = pseudostate.getOutgoings().isEmpty();
        if (_isEmpty_1) {
          StatusFactory.addWarningStatus(pseudostate, 
            "Junction point has no outgoing transitions.", 
            ("A junction point should have outgoing transitions. Outgoing transitions may be specified in " + 
              "state machines of capsules which are subclasses of this capsule."), result);
        } else {
          int _size_2 = pseudostate.getOutgoings().size();
          boolean _greaterThan_1 = (_size_2 > 1);
          if (_greaterThan_1) {
            StatusFactory.addErrorStatus(pseudostate, 
              "Junction point has more than one outgoing transition.", 
              "A junction point can have one and only one outgoing transition.", result);
          }
        }
      }
    }
  }
  
  /**
   * Check for duplicated named elements.
   */
  protected void _validateDuplicateElement(final Object element, final MultiStatus result) {
  }
  
  protected void _validateDuplicateElement(final NamedElement element, final MultiStatus result) {
    if (((element.getNamespace() != null) && (!Strings.isNullOrEmpty(element.getName())))) {
      final Function1<NamedElement, Boolean> _function = (NamedElement e) -> {
        return Boolean.valueOf((((e != element) && (e.eClass() == element.eClass())) && Objects.equal(e.getName(), element.getName())));
      };
      final boolean unique = IterableExtensions.isEmpty(IterableExtensions.<NamedElement>filter(element.getNamespace().getOwnedMembers(), _function));
      if ((!unique)) {
        StatusFactory.addWarningStatus(element, "More than one element with the same name exist in the same namespace.", result);
      }
    }
  }
  
  protected void validateElement(final EObject port, final MultiStatus result) {
    if (port instanceof Port) {
      _validateElement((Port)port, result);
      return;
    } else if (port instanceof Property) {
      _validateElement((Property)port, result);
      return;
    } else if (port instanceof Constraint) {
      _validateElement((Constraint)port, result);
      return;
    } else if (port instanceof Pseudostate) {
      _validateElement((Pseudostate)port, result);
      return;
    } else if (port instanceof State) {
      _validateElement((State)port, result);
      return;
    } else if (port instanceof Transition) {
      _validateElement((Transition)port, result);
      return;
    } else if (port instanceof Trigger) {
      _validateElement((Trigger)port, result);
      return;
    } else if (port instanceof MultiplicityElement) {
      _validateElement((MultiplicityElement)port, result);
      return;
    } else if (port != null) {
      _validateElement(port, result);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(port, result).toString());
    }
  }
  
  private boolean isUnlimited(final ValueSpecification spec) {
    if (spec instanceof LiteralUnlimitedNatural) {
      return _isUnlimited((LiteralUnlimitedNatural)spec);
    } else if (spec != null) {
      return _isUnlimited(spec);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(spec).toString());
    }
  }
  
  protected void validateDuplicateElement(final Object element, final MultiStatus result) {
    if (element instanceof NamedElement) {
      _validateDuplicateElement((NamedElement)element, result);
      return;
    } else if (element != null) {
      _validateDuplicateElement(element, result);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(element, result).toString());
    }
  }
}
