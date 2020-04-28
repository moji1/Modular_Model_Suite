/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import java.util.Map
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionCall
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ExpressionStatement
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.statemach.State
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory
import org.eclipse.papyrusrt.xtumlrt.statemachext.UpdateState

import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.StringLiteral

class ActionInvocationGenerator
{

    @Data static class Context
    {
        MemberFunction func
    }

    @Data static class UserActionContext extends Context
    {
        ElementAccess enclosingFuncParam
    }

    @Data static class SaveHistoryActionContext extends Context
    {
        Map<State, Enumerator> stateEnumerators
    }

    @Data static class UpdateStateActionContext extends Context
    {
        Map<State, Enumerator> stateEnumerators
    }

    /**
     * Creates a call to the update state action.
     *
     * <p> The generated call would be something like:
     *
     * <p><pre>
     * <code>
     * update_state(newState);
     * </code>
     * </pre>
     */
    def dispatch visit( UpdateState action, Context ctx )
    {
        val func = ctx.func
        new ExpressionStatement
        (
            new FunctionCall
            (
                func,
                new StringLiteral(action.newState.name)
            )
        )
    }

    /**
     * Creates a call to the history saving action.
     *
     * <p> The generated call would be something like:
     *
     * <p><pre>
     * <code>
     * save_history(compositeState, subState);
     * </code>
     * </pre>
     */
    def dispatch visit( SaveHistory action, Context ctx )
    {
        val func = ctx.func
        val stateEnumerators =
            (ctx as SaveHistoryActionContext).stateEnumerators
        val compositeState = action.compositeState.name
        val subState = action.subState.name
        new ExpressionStatement
        (
            new FunctionCall
            (
                func,
                new StringLiteral( compositeState ),
                new StringLiteral( subState )
            )
        )
    }


    /**
     * Creates a call to a user action.
     *
     * <p> The generated call would be something like:
     *
     * <p><pre>
     * <code>
     * entryaction_s1(rtdata);
     * </code>
     * </pre>
     *
     * where <code>rtdata</code> is an access to the parameter of the enclosing
     * (calling) action chain function.
     */
    def dispatch visit( ActionCode action, Context ctx )
    {
        val func = ctx.func
        val arg = (ctx as UserActionContext).enclosingFuncParam
        new ExpressionStatement( new FunctionCall( func,arg ) )
   }

}