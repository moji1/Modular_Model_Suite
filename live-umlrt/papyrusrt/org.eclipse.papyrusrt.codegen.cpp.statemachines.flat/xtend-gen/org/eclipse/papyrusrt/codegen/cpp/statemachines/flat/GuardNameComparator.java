/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import java.util.Comparator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil;
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard;
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory;

@SuppressWarnings("all")
public class GuardNameComparator implements Comparator<Guard> {
  @Override
  public int compare(final Guard g1, final Guard g2) {
    int _xblockexpression = (int) 0;
    {
      if ((g1 instanceof CheckHistory)) {
        return (-1);
      }
      _xblockexpression = CppNamesUtil.getFuncName(g1).toString().compareTo(CppNamesUtil.getFuncName(g2).toString());
    }
    return _xblockexpression;
  }
}
