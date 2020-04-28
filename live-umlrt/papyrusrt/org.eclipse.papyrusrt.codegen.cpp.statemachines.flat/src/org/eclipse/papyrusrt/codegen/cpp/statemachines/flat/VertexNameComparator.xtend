/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import java.util.Comparator
import org.eclipse.papyrusrt.xtumlrt.statemach.Vertex
import org.eclipse.papyrusrt.xtumlrt.statemach.SimpleState
import org.eclipse.papyrusrt.xtumlrt.statemach.CompositeState
import org.eclipse.papyrusrt.xtumlrt.statemach.InitialPoint
import org.eclipse.papyrusrt.xtumlrt.statemach.DeepHistory
import org.eclipse.papyrusrt.xtumlrt.statemach.EntryPoint
import org.eclipse.papyrusrt.xtumlrt.statemach.ExitPoint
import org.eclipse.papyrusrt.xtumlrt.statemach.JunctionPoint
import org.eclipse.papyrusrt.xtumlrt.statemach.ChoicePoint
import static extension org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames.*

class VertexNameComparator implements Comparator<Vertex>
{

    static val totalOrder =
    #[
        SimpleState,
        CompositeState,
        InitialPoint,
        DeepHistory,
        EntryPoint,
        ExitPoint,
        JunctionPoint,
        ChoicePoint
    ]

    override compare( Vertex o1, Vertex o2 )
    {
        val pos1 = totalOrder.indexOf( o1.class )
        val pos2 = totalOrder.indexOf( o2.class )
        if (pos1 == pos2) 
            o1.cachedFullName.compareTo( o2.cachedFullName )
        else
            if (pos1 < pos2) -1
            else 1
    }

}