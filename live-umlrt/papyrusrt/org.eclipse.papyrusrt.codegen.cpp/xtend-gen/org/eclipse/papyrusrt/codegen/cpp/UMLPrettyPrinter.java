/**
 * Copyright (c) 2017 Zeligsoft and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ernesto Posse - Initial API and implementation
 */
package org.eclipse.papyrusrt.codegen.cpp;

import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.UMLRTStateMachProfileUtil;
import org.eclipse.papyrusrt.xtumlrt.util.ContainmentUtils;
import org.eclipse.papyrusrt.xtumlrt.util.NamesUtil;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.xtend2.lib.StringConcatenation;

/**
 * @author epp
 */
@SuppressWarnings("all")
public class UMLPrettyPrinter {
  protected String _text(final NamedElement element) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = element.getName();
    _builder.append(_name);
    return _builder.toString();
  }
  
  protected String _text(final StateMachine stateMachine) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("state machine ");
    String _effectiveQualifiedName = NamesUtil.getEffectiveQualifiedName(stateMachine);
    _builder.append(_effectiveQualifiedName);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  protected String _text(final Region region) {
    Element _owner = region.getOwner();
    return this.text(((NamedElement) _owner));
  }
  
  protected String _text(final State state) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("state ");
    String _effectiveQualifiedName = NamesUtil.getEffectiveQualifiedName(state);
    _builder.append(_effectiveQualifiedName);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  protected String _text(final Pseudostate pseudostate) {
    StringConcatenation _builder = new StringConcatenation();
    String _pseudoStateKindText = this.pseudoStateKindText(pseudostate);
    _builder.append(_pseudoStateKindText);
    _builder.append(" ");
    String _effectiveQualifiedName = NamesUtil.getEffectiveQualifiedName(pseudostate);
    _builder.append(_effectiveQualifiedName);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  private String pseudoStateKindText(final Pseudostate pseudostate) {
    String _switchResult = null;
    PseudostateKind _kind = pseudostate.getKind();
    if (_kind != null) {
      switch (_kind) {
        case CHOICE_LITERAL:
          _switchResult = "choice point";
          break;
        case JUNCTION_LITERAL:
          _switchResult = "junction point";
          break;
        case ENTRY_POINT_LITERAL:
          _switchResult = "entry point";
          break;
        case EXIT_POINT_LITERAL:
          _switchResult = "exit point";
          break;
        case INITIAL_LITERAL:
          _switchResult = "initial point";
          break;
        case DEEP_HISTORY_LITERAL:
          _switchResult = "deep history point";
          break;
        case TERMINATE_LITERAL:
          _switchResult = "terminate point";
          break;
        default:
          _switchResult = "pseudostate";
          break;
      }
    } else {
      _switchResult = "pseudostate";
    }
    return _switchResult;
  }
  
  protected String _text(final Transition transition) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("transition ");
    String _name = transition.getName();
    _builder.append(_name);
    _builder.append(" ");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("with triggers ");
    {
      Iterable<Trigger> _triggers = UMLRTStateMachProfileUtil.getTriggers(transition);
      boolean _hasElements = false;
      for(final Trigger t : _triggers) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append("[", "\t");
        } else {
          _builder.appendImmediate(";", "\t");
        }
        String _text = this.text(t);
        _builder.append(_text, "\t");
      }
      if (_hasElements) {
        _builder.append("]", "\t");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("from ");
    String _text_1 = this.text(transition.getSource());
    _builder.append(_text_1, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("to ");
    String _text_2 = this.text(transition.getTarget());
    _builder.append(_text_2, "\t");
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  protected String _text(final Trigger trigger) {
    StringConcatenation _builder = new StringConcatenation();
    String _text = this.text(trigger.getEvent());
    _builder.append(_text);
    _builder.append(" on ");
    {
      EList<Port> _ports = trigger.getPorts();
      boolean _hasElements = false;
      for(final Port p : _ports) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append("[");
        } else {
          _builder.appendImmediate(";", "");
        }
        String _name = p.getName();
        _builder.append(_name);
      }
      if (_hasElements) {
        _builder.append("]");
      }
    }
    return _builder.toString();
  }
  
  protected String _text(final CallEvent event) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = event.getOperation().getName();
    _builder.append(_name);
    return _builder.toString();
  }
  
  public String oneLineListText(final Iterable<? extends NamedElement> list) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _hasElements = false;
      for(final NamedElement element : list) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append("[");
        } else {
          _builder.appendImmediate(";", "");
        }
        String _text = this.text(element);
        _builder.append(_text);
      }
      if (_hasElements) {
        _builder.append("]");
      }
    }
    return _builder.toString();
  }
  
  public String multiLineListText(final Iterable<? extends NamedElement> list) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _hasElements = false;
      for(final NamedElement element : list) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append("[\n");
        }
        String _text = this.text(element);
        _builder.append(_text);
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
      if (_hasElements) {
        _builder.append("]");
      }
    }
    return _builder.toString();
  }
  
  public String groupText(final Iterable<NamedElement> elements) {
    String _xblockexpression = null;
    {
      EObject _lowestCommonAncestor = ContainmentUtils.lowestCommonAncestor(elements);
      final NamedElement context = ((NamedElement) _lowestCommonAncestor);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("context: ");
      String _text = this.text(context);
      _builder.append(_text);
      _builder.newLineIfNotEmpty();
      _builder.append("elements:");
      _builder.newLine();
      {
        for(final NamedElement element : elements) {
          _builder.append("\t");
          _builder.append("* ");
          String _effectiveRelativeQualifiedName = NamesUtil.getEffectiveRelativeQualifiedName(element, context);
          _builder.append(_effectiveRelativeQualifiedName, "\t");
          _builder.newLineIfNotEmpty();
        }
      }
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String text(final NamedElement stateMachine) {
    if (stateMachine instanceof StateMachine) {
      return _text((StateMachine)stateMachine);
    } else if (stateMachine instanceof CallEvent) {
      return _text((CallEvent)stateMachine);
    } else if (stateMachine instanceof Pseudostate) {
      return _text((Pseudostate)stateMachine);
    } else if (stateMachine instanceof Region) {
      return _text((Region)stateMachine);
    } else if (stateMachine instanceof State) {
      return _text((State)stateMachine);
    } else if (stateMachine instanceof Transition) {
      return _text((Transition)stateMachine);
    } else if (stateMachine instanceof Trigger) {
      return _text((Trigger)stateMachine);
    } else if (stateMachine != null) {
      return _text(stateMachine);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(stateMachine).toString());
    }
  }
}
