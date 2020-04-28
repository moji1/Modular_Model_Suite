/**
 * Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.UserEditableRegion;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer;
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode;
import org.eclipse.papyrusrt.xtumlrt.common.CommonElement;
import org.eclipse.papyrusrt.xtumlrt.common.Entity;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.common.Signal;
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition;
import org.eclipse.papyrusrt.xtumlrt.statemach.Trigger;
import org.eclipse.papyrusrt.xtumlrt.statemachext.GuardAction;
import org.eclipse.papyrusrt.xtumlrt.statemachext.TransitionAction;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator;
import org.eclipse.papyrusrt.xtumlrt.umlrt.AnyEvent;
import org.eclipse.papyrusrt.xtumlrt.umlrt.RTPort;
import org.eclipse.papyrusrt.xtumlrt.umlrt.RTTrigger;
import org.eclipse.papyrusrt.xtumlrt.util.ContainmentUtils;
import org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.xtext.xbase.lib.ListExtensions;

/**
 * Utility class for generating user editable region tag.
 * 
 * @author Young-Soo Roh
 */
@SuppressWarnings("all")
public class UserCodeTagUtil {
  /**
   * Get information required to calculate user edit region tag
   */
  public static UserEditableRegion.Label generateLabel(final ActionCode context, final UML2xtumlrtTranslator translator, final FlatteningTransformer flattener, final Entity capsuleOrClass) {
    final UserEditableRegion.Label label = new UserEditableRegion.Label();
    Resource _eResource = capsuleOrClass.eResource();
    boolean _notEquals = (!Objects.equal(_eResource, null));
    if (_notEquals) {
      label.setUri(capsuleOrClass.eResource().getURI().toString());
    } else {
      Element src = translator.getSource(context);
      boolean _equals = Objects.equal(src, null);
      if (_equals) {
        EObject _eContainer = context.eContainer();
        src = translator.getSource(((CommonElement) _eContainer));
      }
      if ((src != null)) {
        label.setUri(src.eResource().getURI().toString());
      }
    }
    label.setQualifiedName(UserCodeTagUtil.getQualifiedName(context, capsuleOrClass));
    label.setType(context.eClass().getName().replace("Action", "").toLowerCase());
    RTTrigger trigger = null;
    EObject _eContainer_1 = context.eContainer();
    if ((_eContainer_1 instanceof Guard)) {
      EObject _eContainer_2 = context.eContainer().eContainer();
      if ((_eContainer_2 instanceof RTTrigger)) {
        EObject _eContainer_3 = context.eContainer().eContainer();
        trigger = ((RTTrigger) _eContainer_3);
        label.setType(UMLPackage.Literals.TRANSITION__TRIGGER.getName());
      }
    }
    label.setDetails("");
    if (((context instanceof TransitionAction) || (context instanceof GuardAction))) {
      label.setDetails(UserCodeTagUtil.getTransitionDetails(context.eContainer(), flattener));
    }
    boolean _notEquals_1 = (!Objects.equal(trigger, null));
    if (_notEquals_1) {
      final ArrayList<String> ports = new ArrayList<String>();
      EList<RTPort> _ports = trigger.getPorts();
      for (final RTPort p : _ports) {
        ports.add(p.getName());
      }
      String signalName = trigger.getSignal().getName();
      Signal _signal = trigger.getSignal();
      if ((_signal instanceof AnyEvent)) {
        signalName = "*";
      }
      final UserEditableRegion.TriggerDetail triggerDetail = new UserEditableRegion.TriggerDetail(signalName, ports);
      String _details = label.getDetails();
      String _plus = (_details + UserEditableRegion.TransitionDetails.EXTRA_DETAIL_SEPARATOR);
      String _tagString = triggerDetail.getTagString();
      String _plus_1 = (_plus + _tagString);
      label.setDetails(_plus_1);
    }
    return label;
  }
  
  /**
   * Get details for identifying transition or guard since they require more informaiton
   * in order to distinguish among other elements.
   */
  public static String getTransitionDetails(final EObject object, final FlatteningTransformer flattener) {
    String _xblockexpression = null;
    {
      EObject container = object;
      while ((!Objects.equal(container, null))) {
        {
          if ((container instanceof Transition)) {
            CommonElement source = ((Transition)container).getSourceVertex();
            boolean _isNewElement = flattener.isNewElement(source);
            if (_isNewElement) {
              source = flattener.getOriginalOwner(source);
            } else {
              source = flattener.getOriginalElement(source);
            }
            boolean _equals = Objects.equal(source, null);
            if (_equals) {
              String _name = ((NamedElement) source).getName();
              String _plus = ("source is null : " + _name);
              CodeGenPlugin.debug(_plus);
            }
            CommonElement target = ((Transition)container).getTargetVertex();
            boolean _isNewElement_1 = flattener.isNewElement(target);
            if (_isNewElement_1) {
              target = flattener.getOriginalOwner(target);
            } else {
              target = flattener.getOriginalElement(target);
            }
            boolean _equals_1 = Objects.equal(target, null);
            if (_equals_1) {
              String _name_1 = ((NamedElement) target).getName();
              String _plus_1 = ("target is null : " + _name_1);
              CodeGenPlugin.debug(_plus_1);
            }
            if (((!Objects.equal(target, null)) && (!Objects.equal(source, null)))) {
              String sourceQname = QualifiedNames.cachedFullSMName(((NamedElement) source));
              String targetQname = QualifiedNames.cachedFullSMName(((NamedElement) target));
              UserEditableRegion.TransitionDetails details = new UserEditableRegion.TransitionDetails(sourceQname, targetQname);
              EList<Trigger> _triggers = ((Transition)container).getTriggers();
              for (final Trigger t : _triggers) {
                {
                  final ArrayList<String> ports = new ArrayList<String>();
                  EList<RTPort> _ports = ((RTTrigger) t).getPorts();
                  for (final RTPort p : _ports) {
                    ports.add(p.getName());
                  }
                  final Signal signal = ((RTTrigger) t).getSignal();
                  if ((signal instanceof AnyEvent)) {
                    details.addTriggerDetail("*", ports);
                  } else {
                    details.addTriggerDetail(signal.getName(), ports);
                  }
                }
              }
              return details.getTagString();
            } else {
              CodeGenPlugin.error("Source vertex and target vertex should not be null");
              return "";
            }
          }
          container = container.eContainer();
        }
      }
      _xblockexpression = "";
    }
    return _xblockexpression;
  }
  
  /**
   * Calculate qualified name for object's container in the context of UMLRT codegen
   */
  public static String getQualifiedName(final NamedElement context, final EObject capsule) {
    final List<NamedElement> hierarchy = ContainmentUtils.cachedFullContainmentChain(context);
    String result = QualifiedNames.cachedFullName(((NamedElement) capsule));
    ListExtensions.<NamedElement>reverse(hierarchy);
    for (final NamedElement parent : hierarchy) {
      if (((parent instanceof State) && (!(parent.eContainer() instanceof StateMachine)))) {
        final String smqname = QualifiedNames.cachedFullSMName(((NamedElement) parent));
        return ((result + QualifiedNames.SEPARATOR) + smqname);
      }
    }
    return result;
  }
}
