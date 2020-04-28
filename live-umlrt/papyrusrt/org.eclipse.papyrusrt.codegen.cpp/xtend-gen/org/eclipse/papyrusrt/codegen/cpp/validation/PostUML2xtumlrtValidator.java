/**
 * Copyright (c) 2017 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.validation;

import com.google.common.base.Objects;
import java.util.Arrays;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.validation.StatusFactory;
import org.eclipse.papyrusrt.xtumlrt.common.Attribute;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.common.CapsulePart;
import org.eclipse.papyrusrt.xtumlrt.common.CommonElement;
import org.eclipse.papyrusrt.xtumlrt.common.Connector;
import org.eclipse.papyrusrt.xtumlrt.common.Entity;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.common.Port;
import org.eclipse.papyrusrt.xtumlrt.common.Protocol;
import org.eclipse.papyrusrt.xtumlrt.common.RedefinableElement;
import org.eclipse.papyrusrt.xtumlrt.common.Signal;
import org.eclipse.papyrusrt.xtumlrt.common.StructuredType;
import org.eclipse.papyrusrt.xtumlrt.statemach.CompositeState;
import org.eclipse.papyrusrt.xtumlrt.statemach.EntryPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.ExitPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition;
import org.eclipse.papyrusrt.xtumlrt.trans.TransformValidator;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTExtensions;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil;
import org.eclipse.uml2.uml.Element;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Post UML2xtumlrt SM validation
 * @author ysroh
 */
@SuppressWarnings("all")
public class PostUML2xtumlrtValidator implements TransformValidator<EObject> {
  private UML2xtumlrtTranslator translator;
  
  public PostUML2xtumlrtValidator(final UML2xtumlrtTranslator translator) {
    this.translator = translator;
  }
  
  @Override
  public MultiStatus validate(final EObject element) {
    MultiStatus _xblockexpression = null;
    {
      final MultiStatus status = new MultiStatus(CodeGenPlugin.ID, IStatus.INFO, "UML-RT Code Generator Invoked", null);
      final Procedure1<EObject> _function = (EObject it) -> {
        this.validateGeneratedElement(it, status);
      };
      IteratorExtensions.<EObject>forEach(element.eAllContents(), _function);
      _xblockexpression = status;
    }
    return _xblockexpression;
  }
  
  protected void validateGeneratedElement(final EObject o, final MultiStatus result) {
    if ((o instanceof CommonElement)) {
      final Element source = this.translator.getSource(((CommonElement)o));
      this.validateElement(o, source, result);
    }
  }
  
  protected void _validateElement(final EObject o, final EObject source, final MultiStatus result) {
  }
  
  /**
   * Validate if the transition belongs to correct state
   * let n1 be the source node of t and n2 its target node.
   * 1) sibling transition: the owner of n1 is S, the owner of n2 is also S and the owner of t must be S as well.
   * 2) entering transition: the owner of n2 is S; then the owner of t must be S as well (and n1 is S or one of its entry points)
   * 3) exiting transition: the owner of n1 is S; then the owner of t must be S as well (and n2 is S or one of its exit points)
   * 4) through transition: then n1 = n2 is a composite state S and the owner of t is S.
   */
  protected void _validateElement(final Transition t, final EObject source, final MultiStatus result) {
    if ((source == null)) {
      return;
    }
    final EObject transitionOwner = t.eContainer();
    EObject sourceNode = t.getSourceVertex();
    if (((sourceNode instanceof EntryPoint) || (sourceNode instanceof ExitPoint))) {
      sourceNode = sourceNode.eContainer();
    }
    EObject targetNode = t.getTargetVertex();
    if (((targetNode instanceof EntryPoint) || (targetNode instanceof ExitPoint))) {
      targetNode = sourceNode.eContainer();
    }
    if (((sourceNode instanceof CompositeState) && (sourceNode == targetNode))) {
      if (((t.getSourceVertex() instanceof EntryPoint) || (t.getTargetVertex() instanceof ExitPoint))) {
        if ((targetNode != transitionOwner)) {
          StatusFactory.addErrorStatus(source, 
            "The transition\'s owner is not the composite state that owns its source and target.", 
            "A \"through\" transition must be owned by its source and target state (which are the same).", result);
        }
      } else {
        if (((targetNode != transitionOwner) && (targetNode.eContainer() != transitionOwner))) {
          StatusFactory.addErrorStatus(source, 
            "The transition does not belong to the correct state.", 
            "The transition\'s owner must be either its target state or it\'s target\'s state owner.", result);
        }
      }
    } else {
      if (((t.getSourceVertex() instanceof EntryPoint) || (sourceNode == targetNode.eContainer()))) {
        if ((sourceNode != transitionOwner)) {
          StatusFactory.addErrorStatus(source, 
            "The transition is not owned by it\'s source state.", 
            "An \"entering\" transition must be owned by its source state, which itself must own the transition\'s target.", result);
        }
      } else {
        if (((t.getTargetVertex() instanceof ExitPoint) || (targetNode == sourceNode.eContainer()))) {
          if ((targetNode != transitionOwner)) {
            StatusFactory.addErrorStatus(source, 
              "The transition is not owned by it\'s target state.", 
              "An \"exiting\" transition must be owned by its target state, which itself must own the transition\'s source.", result);
          }
        } else {
          EObject _eContainer = sourceNode.eContainer();
          EObject _eContainer_1 = targetNode.eContainer();
          boolean _tripleEquals = (_eContainer == _eContainer_1);
          if (_tripleEquals) {
            EObject _eContainer_2 = targetNode.eContainer();
            boolean _tripleNotEquals = (_eContainer_2 != transitionOwner);
            if (_tripleNotEquals) {
              StatusFactory.addErrorStatus(source, 
                "The transition\'s owner is not the composite state that owns its source and target.", 
                "A \"sibling\" transition must be owned by the same composite state that owns its source and target states.", result);
            }
          }
        }
      }
    }
  }
  
  protected void _validateElement(final Port port, final EObject source, final MultiStatus result) {
    NamedElement _owner = XTUMLRTUtil.getOwner(port);
    final Capsule capsule = ((Capsule) _owner);
    final RedefinableElement capsuleParent = capsule.getRedefines();
    if ((capsuleParent instanceof Capsule)) {
      final Iterable<Port> allParentPorts = XTUMLRTExtensions.getAllRTPorts(((Capsule)capsuleParent));
      final Function1<Port, Boolean> _function = (Port it) -> {
        return Boolean.valueOf((Objects.equal(it.getName(), port.getName()) && (port.getRedefines() != it)));
      };
      boolean _exists = IterableExtensions.<Port>exists(allParentPorts, _function);
      if (_exists) {
        StatusFactory.addErrorStatus(source, 
          "Port has the same name as a port in the parent capsule but it does not redefine it.", 
          "A named element that redefines another named element must have the same name.", result);
      }
    }
  }
  
  protected void _validateElement(final CapsulePart part, final EObject source, final MultiStatus result) {
    NamedElement _owner = XTUMLRTUtil.getOwner(part);
    final Capsule capsule = ((Capsule) _owner);
    final RedefinableElement capsuleParent = capsule.getRedefines();
    if ((capsuleParent instanceof Capsule)) {
      final Iterable<CapsulePart> allParentParts = XTUMLRTExtensions.getAllCapsuleParts(((Capsule)capsuleParent));
      final Function1<CapsulePart, Boolean> _function = (CapsulePart it) -> {
        return Boolean.valueOf((Objects.equal(it.getName(), part.getName()) && (part.getRedefines() != it)));
      };
      boolean _exists = IterableExtensions.<CapsulePart>exists(allParentParts, _function);
      if (_exists) {
        StatusFactory.addErrorStatus(source, 
          "Port has the same name as a port in the parent capsule but it does not redefine it.", 
          "A named element that redefines another named element must have the same name.", result);
      }
    }
  }
  
  protected void _validateElement(final Connector conn, final EObject source, final MultiStatus result) {
    NamedElement _owner = XTUMLRTUtil.getOwner(conn);
    final Capsule capsule = ((Capsule) _owner);
    final RedefinableElement capsuleParent = capsule.getRedefines();
    if ((capsuleParent instanceof Capsule)) {
      final Iterable<Connector> allParentParts = XTUMLRTExtensions.getAllConnectors(((Capsule)capsuleParent));
      final Function1<Connector, Boolean> _function = (Connector it) -> {
        return Boolean.valueOf((Objects.equal(it.getName(), conn.getName()) && (conn.getRedefines() != it)));
      };
      boolean _exists = IterableExtensions.<Connector>exists(allParentParts, _function);
      if (_exists) {
        StatusFactory.addErrorStatus(source, 
          "Connector has the same name as a connector in the parent capsule but it does not redefine it", 
          "A named element that redefines another named element must have the same name.", result);
      }
    }
  }
  
  protected void _validateElement(final Attribute attr, final EObject source, final MultiStatus result) {
    NamedElement _owner = XTUMLRTUtil.getOwner(attr);
    final Entity capsule = ((Entity) _owner);
    final RedefinableElement capsuleParent = capsule.getRedefines();
    if ((capsuleParent instanceof Entity)) {
      final Iterable<Attribute> allParentAttrs = XTUMLRTExtensions.getAllAttributes(((StructuredType)capsuleParent));
      final Function1<Attribute, Boolean> _function = (Attribute it) -> {
        return Boolean.valueOf((Objects.equal(it.getName(), attr.getName()) && (attr.getRedefines() != it)));
      };
      boolean _exists = IterableExtensions.<Attribute>exists(allParentAttrs, _function);
      if (_exists) {
        StatusFactory.addErrorStatus(source, 
          "Attribute has the same name as a attribute in the parent capsule but it does not redefine it", 
          "A named element that redefines another named element must have the same name.", result);
      }
    }
  }
  
  protected void _validateElement(final Signal signal, final EObject source, final MultiStatus result) {
    NamedElement _owner = XTUMLRTUtil.getOwner(signal);
    final Protocol protocol = ((Protocol) _owner);
    final RedefinableElement protocolParent = protocol.getRedefines();
    if ((protocolParent instanceof Protocol)) {
      final Iterable<Signal> allParentSignals = XTUMLRTExtensions.getAllSignals(((Protocol)protocolParent));
      final Function1<Signal, Boolean> _function = (Signal it) -> {
        return Boolean.valueOf((Objects.equal(it.getName(), signal.getName()) && (signal.getRedefines() != it)));
      };
      boolean _exists = IterableExtensions.<Signal>exists(allParentSignals, _function);
      if (_exists) {
        StatusFactory.addErrorStatus(source, 
          "Protocol message has the same name as a protocol message in the parent protocol but it does not redefine it", 
          "A named element that redefines another named element must have the same name.", result);
      }
    }
  }
  
  protected void _validateElement(final State state, final EObject source, final MultiStatus result) {
    NamedElement _owner = XTUMLRTUtil.getOwner(state);
    final RedefinableElement owner = ((RedefinableElement) _owner);
    if ((owner instanceof StateMachine)) {
      return;
    }
    final RedefinableElement redefine = owner.getRedefines();
    if ((redefine instanceof CompositeState)) {
      final EList<State> allParentStates = ((CompositeState)redefine).getSubstates();
      final Function1<State, Boolean> _function = (State it) -> {
        return Boolean.valueOf((Objects.equal(it.getName(), state.getName()) && (state.getRedefines() != it)));
      };
      boolean _exists = IterableExtensions.<State>exists(allParentStates, _function);
      if (_exists) {
        StatusFactory.addErrorStatus(source, 
          "State has the same name as a state in the parent state but it does not redefine it", 
          "A named element that redefines another named element must have the same name.", result);
      }
    }
  }
  
  protected void validateElement(final EObject attr, final EObject source, final MultiStatus result) {
    if (attr instanceof Attribute) {
      _validateElement((Attribute)attr, source, result);
      return;
    } else if (attr instanceof CapsulePart) {
      _validateElement((CapsulePart)attr, source, result);
      return;
    } else if (attr instanceof Connector) {
      _validateElement((Connector)attr, source, result);
      return;
    } else if (attr instanceof Port) {
      _validateElement((Port)attr, source, result);
      return;
    } else if (attr instanceof Signal) {
      _validateElement((Signal)attr, source, result);
      return;
    } else if (attr instanceof State) {
      _validateElement((State)attr, source, result);
      return;
    } else if (attr instanceof Transition) {
      _validateElement((Transition)attr, source, result);
      return;
    } else if (attr != null) {
      _validateElement(attr, source, result);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(attr, source, result).toString());
    }
  }
}
