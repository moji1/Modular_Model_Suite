/**
 * Copyright (c) 2015-2017 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import com.google.common.base.Objects;
import java.util.Arrays;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.statemach.ActionChain;
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard;
import org.eclipse.papyrusrt.xtumlrt.statemach.StatemachFactory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.EntryAction;
import org.eclipse.papyrusrt.xtumlrt.statemachext.ExitAction;
import org.eclipse.papyrusrt.xtumlrt.statemachext.StatemachextFactory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.TransitionAction;
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants;
import org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class CppNamesUtil {
  protected static CharSequence _getFuncName(final NamedElement element) {
    StringConcatenation _builder = new StringConcatenation();
    String _prefix = CppNamesUtil.getPrefix(element);
    _builder.append(_prefix);
    CharSequence _partSep = CppNamesUtil.getPartSep();
    _builder.append(_partSep);
    String _ffqn = CppNamesUtil.getFfqn(element);
    _builder.append(_ffqn);
    return _builder;
  }
  
  protected static CharSequence _getFuncName(final EntryAction element) {
    StringConcatenation _builder = new StringConcatenation();
    String _prefix = CppNamesUtil.getPrefix(element);
    _builder.append(_prefix);
    CharSequence _partSep = CppNamesUtil.getPartSep();
    _builder.append(_partSep);
    String _ffqn = CppNamesUtil.getFfqn(XTUMLRTUtil.getOwner(element));
    _builder.append(_ffqn);
    return _builder;
  }
  
  protected static CharSequence _getFuncName(final ExitAction element) {
    StringConcatenation _builder = new StringConcatenation();
    String _prefix = CppNamesUtil.getPrefix(element);
    _builder.append(_prefix);
    CharSequence _partSep = CppNamesUtil.getPartSep();
    _builder.append(_partSep);
    String _ffqn = CppNamesUtil.getFfqn(XTUMLRTUtil.getOwner(element));
    _builder.append(_ffqn);
    return _builder;
  }
  
  protected static CharSequence _getFuncName(final TransitionAction element) {
    StringConcatenation _builder = new StringConcatenation();
    String _prefix = CppNamesUtil.getPrefix(element);
    _builder.append(_prefix);
    CharSequence _partSep = CppNamesUtil.getPartSep();
    _builder.append(_partSep);
    String _ffqn = CppNamesUtil.getFfqn(XTUMLRTUtil.getOwner(XTUMLRTUtil.getOwner(element)));
    _builder.append(_ffqn);
    return _builder;
  }
  
  protected static CharSequence _getFuncName(final ActionChain element) {
    StringConcatenation _builder = new StringConcatenation();
    String _prefix = CppNamesUtil.getPrefix(element);
    _builder.append(_prefix);
    CharSequence _partSep = CppNamesUtil.getPartSep();
    _builder.append(_partSep);
    String _ffqn = CppNamesUtil.getFfqn(XTUMLRTUtil.getOwner(element));
    _builder.append(_ffqn);
    return _builder;
  }
  
  protected static CharSequence _getFuncName(final Guard element) {
    StringConcatenation _builder = new StringConcatenation();
    String _prefix = CppNamesUtil.getPrefix(element);
    _builder.append(_prefix);
    CharSequence _partSep = CppNamesUtil.getPartSep();
    _builder.append(_partSep);
    String _ffqn = CppNamesUtil.getFfqn(XTUMLRTUtil.getOwner(element));
    _builder.append(_ffqn);
    return _builder;
  }
  
  public static CharSequence getPartSep() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(GlobalConstants.FUNC_NAME_PART_SEP);
    return _builder;
  }
  
  protected static String _getPrefix(final Object o) {
    return null;
  }
  
  protected static String _getPrefix(final NamedElement element) {
    String _switchResult = null;
    EClass _eClass = element.eClass();
    boolean _matched = false;
    EClass _simpleState = StatemachFactory.eINSTANCE.getStatemachPackage().getSimpleState();
    if (Objects.equal(_eClass, _simpleState)) {
      _matched=true;
      _switchResult = GlobalConstants.SIMPLE_STATE_FUNC_PREFIX;
    }
    if (!_matched) {
      EClass _compositeState = StatemachFactory.eINSTANCE.getStatemachPackage().getCompositeState();
      if (Objects.equal(_eClass, _compositeState)) {
        _matched=true;
        _switchResult = GlobalConstants.COMPOSITE_STATE_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _state = StatemachFactory.eINSTANCE.getStatemachPackage().getState();
      if (Objects.equal(_eClass, _state)) {
        _matched=true;
        _switchResult = GlobalConstants.STATE_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _transition = StatemachFactory.eINSTANCE.getStatemachPackage().getTransition();
      if (Objects.equal(_eClass, _transition)) {
        _matched=true;
        _switchResult = GlobalConstants.TRANS_ACTION_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _choicePoint = StatemachFactory.eINSTANCE.getStatemachPackage().getChoicePoint();
      if (Objects.equal(_eClass, _choicePoint)) {
        _matched=true;
        _switchResult = GlobalConstants.CHOICE_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _junctionPoint = StatemachFactory.eINSTANCE.getStatemachPackage().getJunctionPoint();
      if (Objects.equal(_eClass, _junctionPoint)) {
        _matched=true;
        _switchResult = GlobalConstants.JUNCTION_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _actionChain = StatemachFactory.eINSTANCE.getStatemachPackage().getActionChain();
      if (Objects.equal(_eClass, _actionChain)) {
        _matched=true;
        _switchResult = GlobalConstants.ACTION_CHAIN_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _guard = StatemachFactory.eINSTANCE.getStatemachPackage().getGuard();
      if (Objects.equal(_eClass, _guard)) {
        _matched=true;
        _switchResult = GlobalConstants.GUARD_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _entryAction = StatemachextFactory.eINSTANCE.getStatemachextPackage().getEntryAction();
      if (Objects.equal(_eClass, _entryAction)) {
        _matched=true;
        _switchResult = GlobalConstants.ENTRY_ACTION_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _exitAction = StatemachextFactory.eINSTANCE.getStatemachextPackage().getExitAction();
      if (Objects.equal(_eClass, _exitAction)) {
        _matched=true;
        _switchResult = GlobalConstants.EXIT_ACTION_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      EClass _transitionAction = StatemachextFactory.eINSTANCE.getStatemachextPackage().getTransitionAction();
      if (Objects.equal(_eClass, _transitionAction)) {
        _matched=true;
        _switchResult = GlobalConstants.TRANS_ACTION_FUNC_PREFIX;
      }
    }
    if (!_matched) {
      _switchResult = GlobalConstants.ACTION_FUNC_PREFIX;
    }
    return _switchResult;
  }
  
  protected static String _getFfqn(final Object o) {
    return null;
  }
  
  protected static String _getFfqn(final NamedElement element) {
    String _xblockexpression = null;
    {
      String txt = QualifiedNames.makeValidCName(QualifiedNames.cachedFullSMName(element));
      _xblockexpression = txt.replace(GlobalConstants.QUAL_NAME_SEP, 
        GlobalConstants.FUNC_NAME_QUAL_NAME_SEP);
    }
    return _xblockexpression;
  }
  
  public static CharSequence getFuncName(final NamedElement element) {
    if (element instanceof EntryAction) {
      return _getFuncName((EntryAction)element);
    } else if (element instanceof ExitAction) {
      return _getFuncName((ExitAction)element);
    } else if (element instanceof TransitionAction) {
      return _getFuncName((TransitionAction)element);
    } else if (element instanceof ActionChain) {
      return _getFuncName((ActionChain)element);
    } else if (element instanceof Guard) {
      return _getFuncName((Guard)element);
    } else if (element != null) {
      return _getFuncName(element);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(element).toString());
    }
  }
  
  public static String getPrefix(final Object element) {
    if (element instanceof NamedElement) {
      return _getPrefix((NamedElement)element);
    } else if (element != null) {
      return _getPrefix(element);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(element).toString());
    }
  }
  
  public static String getFfqn(final Object element) {
    if (element instanceof NamedElement) {
      return _getFfqn((NamedElement)element);
    } else if (element != null) {
      return _getFfqn(element);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(element).toString());
    }
  }
}
