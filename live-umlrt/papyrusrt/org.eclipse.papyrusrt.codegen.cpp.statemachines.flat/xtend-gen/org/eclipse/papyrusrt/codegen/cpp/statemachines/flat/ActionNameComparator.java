/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil;
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.statemach.ActionChain;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition;
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class ActionNameComparator implements Comparator<ActionCode> {
  private final List<? extends Class<? extends NamedElement>> totalOrder = Collections.<Class<? extends NamedElement>>unmodifiableList(CollectionLiterals.<Class<? extends NamedElement>>newArrayList(State.class, ActionChain.class, ActionCode.class, Transition.class));
  
  @Override
  public int compare(final ActionCode o1, final ActionCode o2) {
    int _xblockexpression = (int) 0;
    {
      if ((o1 instanceof CheckHistory)) {
        return (-1);
      }
      if ((o1 instanceof SaveHistory)) {
        return (-1);
      }
      final NamedElement owner1 = XTUMLRTUtil.getOwner(o1);
      final NamedElement owner2 = XTUMLRTUtil.getOwner(o2);
      final int pos1 = this.totalOrder.indexOf(owner1.getClass());
      final int pos2 = this.totalOrder.indexOf(owner2.getClass());
      int _xifexpression = (int) 0;
      if ((pos1 == pos2)) {
        _xifexpression = CppNamesUtil.getFuncName(o1).toString().compareTo(CppNamesUtil.getFuncName(o2).toString());
      } else {
        int _xifexpression_1 = (int) 0;
        if ((pos1 < pos2)) {
          _xifexpression_1 = (-1);
        } else {
          _xifexpression_1 = 1;
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
}
