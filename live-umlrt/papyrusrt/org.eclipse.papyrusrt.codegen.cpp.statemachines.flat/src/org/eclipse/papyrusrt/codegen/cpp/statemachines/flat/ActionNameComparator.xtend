/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import java.util.Comparator
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.statemach.ActionChain
import org.eclipse.papyrusrt.xtumlrt.statemach.State
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory
import static extension org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil.*
import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil.*

class ActionNameComparator implements Comparator<ActionCode>
{

    val totalOrder =
    #[
        State,
        ActionChain,
        ActionCode,
        Transition
    ]

    override compare( ActionCode o1, ActionCode o2 )
    {
        if (o1 instanceof CheckHistory) return -1
        if (o1 instanceof SaveHistory) return -1
        val owner1 = o1.owner
        val owner2 = o2.owner
        val pos1 = totalOrder.indexOf(owner1.class)
        val pos2 = totalOrder.indexOf(owner2.class)
        if (pos1 == pos2)
            o1.funcName.toString.compareTo( o2.funcName.toString )
        else if (pos1 < pos2) -1
        else 1
    }

}