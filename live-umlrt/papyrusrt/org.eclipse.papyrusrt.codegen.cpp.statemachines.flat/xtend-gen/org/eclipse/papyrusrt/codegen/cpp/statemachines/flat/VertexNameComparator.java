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
import org.eclipse.papyrusrt.xtumlrt.statemach.ChoicePoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.CompositeState;
import org.eclipse.papyrusrt.xtumlrt.statemach.DeepHistory;
import org.eclipse.papyrusrt.xtumlrt.statemach.EntryPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.ExitPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.InitialPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.JunctionPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.SimpleState;
import org.eclipse.papyrusrt.xtumlrt.statemach.Vertex;
import org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class VertexNameComparator implements Comparator<Vertex> {
  private final static List<? extends Class<? extends Vertex>> totalOrder = Collections.<Class<? extends Vertex>>unmodifiableList(CollectionLiterals.<Class<? extends Vertex>>newArrayList(SimpleState.class, CompositeState.class, InitialPoint.class, DeepHistory.class, EntryPoint.class, ExitPoint.class, JunctionPoint.class, ChoicePoint.class));
  
  @Override
  public int compare(final Vertex o1, final Vertex o2) {
    int _xblockexpression = (int) 0;
    {
      final int pos1 = VertexNameComparator.totalOrder.indexOf(o1.getClass());
      final int pos2 = VertexNameComparator.totalOrder.indexOf(o2.getClass());
      int _xifexpression = (int) 0;
      if ((pos1 == pos2)) {
        _xifexpression = QualifiedNames.cachedFullName(o1).compareTo(QualifiedNames.cachedFullName(o2));
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
