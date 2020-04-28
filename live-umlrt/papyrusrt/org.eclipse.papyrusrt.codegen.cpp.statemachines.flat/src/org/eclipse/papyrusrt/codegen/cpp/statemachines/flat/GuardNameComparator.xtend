/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import java.util.Comparator
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory
import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil.*

class GuardNameComparator implements Comparator<Guard>
{

    override compare( Guard g1, Guard g2 )
    {
        if (g1 instanceof CheckHistory) return -1
        g1.funcName.toString.compareTo( g2.funcName.toString )
    }

}