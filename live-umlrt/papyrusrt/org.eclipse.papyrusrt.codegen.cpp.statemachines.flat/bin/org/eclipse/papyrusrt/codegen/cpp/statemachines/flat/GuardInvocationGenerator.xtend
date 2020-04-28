/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionCall
import org.eclipse.papyrusrt.xtumlrt.statemach.State
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory
import java.util.Map
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.StringLiteral

class GuardInvocationGenerator
{

    @Data static class Context
    {
        MemberFunction func
    }

    @Data static class UserGuardContext extends Context
    {
        ElementAccess enclosingFuncParam
    }

    @Data static class CheckHistoryGuardContext extends Context
    {
        Map<State, Enumerator> stateEnumerators
    }

    /**
     * Creates a call to the history checking guard.
     *
     * <p> The generated call would be something like:
     *
     * <p><pre>
     * <code>
     * check_history(compositeState, subState);
     * </code>
     * </pre>
     */
    def dispatch visit( CheckHistory guard, Context ctx )
    {
        val func = ctx.func
        val stateEnumerators =
            (ctx as CheckHistoryGuardContext).stateEnumerators
        val compositeState = guard.compositeState.name
        val subState = guard.subState.name
        new FunctionCall
        (
            func,
            new StringLiteral( compositeState ),
            new StringLiteral( subState )
        )
    }

    /**
     * Creates a call to a user guard.
     *
     * <p> The generated call would be something like:
     *
     * <p><pre>
     * <code>
     * guard_g1(rtdata);
     * </code>
     * </pre>
     *
     * where <code>rtdata</code> is an access to the parameter of the enclosing
     * (calling) action chain function.
     */
    def dispatch visit( Guard guard, Context ctx )
    {
        val func = ctx.func
        val arg = (ctx as UserGuardContext).enclosingFuncParam
        new FunctionCall(func,arg)
   }

}