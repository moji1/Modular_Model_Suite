/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import java.util.Arrays;
import java.util.Map;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.StringLiteral;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ExpressionStatement;
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.UpdateState;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ActionInvocationGenerator {
  @Data
  public static class Context {
    private final MemberFunction func;
    
    public Context(final MemberFunction func) {
      super();
      this.func = func;
    }
    
    @Override
    @Pure
    public int hashCode() {
      return 31 * 1 + ((this.func== null) ? 0 : this.func.hashCode());
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ActionInvocationGenerator.Context other = (ActionInvocationGenerator.Context) obj;
      if (this.func == null) {
        if (other.func != null)
          return false;
      } else if (!this.func.equals(other.func))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("func", this.func);
      return b.toString();
    }
    
    @Pure
    public MemberFunction getFunc() {
      return this.func;
    }
  }
  
  @Data
  public static class UserActionContext extends ActionInvocationGenerator.Context {
    private final ElementAccess enclosingFuncParam;
    
    public UserActionContext(final MemberFunction func, final ElementAccess enclosingFuncParam) {
      super(func);
      this.enclosingFuncParam = enclosingFuncParam;
    }
    
    @Override
    @Pure
    public int hashCode() {
      return 31 * super.hashCode() + ((this.enclosingFuncParam== null) ? 0 : this.enclosingFuncParam.hashCode());
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      ActionInvocationGenerator.UserActionContext other = (ActionInvocationGenerator.UserActionContext) obj;
      if (this.enclosingFuncParam == null) {
        if (other.enclosingFuncParam != null)
          return false;
      } else if (!this.enclosingFuncParam.equals(other.enclosingFuncParam))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      return new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
    }
    
    @Pure
    public ElementAccess getEnclosingFuncParam() {
      return this.enclosingFuncParam;
    }
  }
  
  @Data
  public static class SaveHistoryActionContext extends ActionInvocationGenerator.Context {
    private final Map<State, Enumerator> stateEnumerators;
    
    public SaveHistoryActionContext(final MemberFunction func, final Map<State, Enumerator> stateEnumerators) {
      super(func);
      this.stateEnumerators = stateEnumerators;
    }
    
    @Override
    @Pure
    public int hashCode() {
      return 31 * super.hashCode() + ((this.stateEnumerators== null) ? 0 : this.stateEnumerators.hashCode());
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      ActionInvocationGenerator.SaveHistoryActionContext other = (ActionInvocationGenerator.SaveHistoryActionContext) obj;
      if (this.stateEnumerators == null) {
        if (other.stateEnumerators != null)
          return false;
      } else if (!this.stateEnumerators.equals(other.stateEnumerators))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      return new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
    }
    
    @Pure
    public Map<State, Enumerator> getStateEnumerators() {
      return this.stateEnumerators;
    }
  }
  
  @Data
  public static class UpdateStateActionContext extends ActionInvocationGenerator.Context {
    private final Map<State, Enumerator> stateEnumerators;
    
    public UpdateStateActionContext(final MemberFunction func, final Map<State, Enumerator> stateEnumerators) {
      super(func);
      this.stateEnumerators = stateEnumerators;
    }
    
    @Override
    @Pure
    public int hashCode() {
      return 31 * super.hashCode() + ((this.stateEnumerators== null) ? 0 : this.stateEnumerators.hashCode());
    }
    
    @Override
    @Pure
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      if (!super.equals(obj))
        return false;
      ActionInvocationGenerator.UpdateStateActionContext other = (ActionInvocationGenerator.UpdateStateActionContext) obj;
      if (this.stateEnumerators == null) {
        if (other.stateEnumerators != null)
          return false;
      } else if (!this.stateEnumerators.equals(other.stateEnumerators))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      return new ToStringBuilder(this)
      	.addAllFields()
      	.toString();
    }
    
    @Pure
    public Map<State, Enumerator> getStateEnumerators() {
      return this.stateEnumerators;
    }
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
  protected ExpressionStatement _visit(final UpdateState action, final ActionInvocationGenerator.Context ctx) {
    ExpressionStatement _xblockexpression = null;
    {
      final MemberFunction func = ctx.func;
      String _name = action.getNewState().getName();
      StringLiteral _stringLiteral = new StringLiteral(_name);
      FunctionCall _functionCall = new FunctionCall(func, _stringLiteral);
      _xblockexpression = new ExpressionStatement(_functionCall);
    }
    return _xblockexpression;
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
  protected ExpressionStatement _visit(final SaveHistory action, final ActionInvocationGenerator.Context ctx) {
    ExpressionStatement _xblockexpression = null;
    {
      final MemberFunction func = ctx.func;
      final Map<State, Enumerator> stateEnumerators = ((ActionInvocationGenerator.SaveHistoryActionContext) ctx).stateEnumerators;
      final String compositeState = action.getCompositeState().getName();
      final String subState = action.getSubState().getName();
      StringLiteral _stringLiteral = new StringLiteral(compositeState);
      StringLiteral _stringLiteral_1 = new StringLiteral(subState);
      FunctionCall _functionCall = new FunctionCall(func, _stringLiteral, _stringLiteral_1);
      _xblockexpression = new ExpressionStatement(_functionCall);
    }
    return _xblockexpression;
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
  protected ExpressionStatement _visit(final ActionCode action, final ActionInvocationGenerator.Context ctx) {
    ExpressionStatement _xblockexpression = null;
    {
      final MemberFunction func = ctx.func;
      final ElementAccess arg = ((ActionInvocationGenerator.UserActionContext) ctx).enclosingFuncParam;
      FunctionCall _functionCall = new FunctionCall(func, arg);
      _xblockexpression = new ExpressionStatement(_functionCall);
    }
    return _xblockexpression;
  }
  
  public ExpressionStatement visit(final ActionCode action, final ActionInvocationGenerator.Context ctx) {
    if (action instanceof SaveHistory) {
      return _visit((SaveHistory)action, ctx);
    } else if (action instanceof UpdateState) {
      return _visit((UpdateState)action, ctx);
    } else if (action != null) {
      return _visit(action, ctx);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(action, ctx).toString());
    }
  }
}
