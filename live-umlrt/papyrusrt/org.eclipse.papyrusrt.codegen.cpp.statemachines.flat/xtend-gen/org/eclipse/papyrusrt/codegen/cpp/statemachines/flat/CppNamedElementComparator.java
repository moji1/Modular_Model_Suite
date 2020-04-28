/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import java.util.Comparator;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement;

@SuppressWarnings("all")
public class CppNamedElementComparator implements Comparator<NamedElement> {
  @Override
  public int compare(final NamedElement o1, final NamedElement o2) {
    return o1.getName().compareTo(o2.getName());
  }
}
