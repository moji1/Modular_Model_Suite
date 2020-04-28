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
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class GuardInvocationGenerator {
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
      GuardInvocationGenerator.Context other = (GuardInvocationGenerator.Context) obj;
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
  public static class UserGuardContext extends GuardInvocationGenerator.Context {
    private final ElementAccess enclosingFuncParam;
    
    public UserGuardContext(final MemberFunction func, final ElementAccess enclosingFuncParam) {
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
      GuardInvocationGenerator.UserGuardContext other = (GuardInvocationGenerator.UserGuardContext) obj;
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
  public static class CheckHistoryGuardContext extends GuardInvocationGenerator.Context {
    private final Map<State, Enumerator> stateEnumerators;
    
    public CheckHistoryGuardContext(final MemberFunction func, final Map<State, Enumerator> stateEnumerators) {
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
      GuardInvocationGenerator.CheckHistoryGuardContext other = (GuardInvocationGenerator.CheckHistoryGuardContext) obj;
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
  protected FunctionCall _visit(final CheckHistory guard, final GuardInvocationGenerator.Context ctx) {
    FunctionCall _xblockexpression = null;
    {
      final MemberFunction func = ctx.func;
      final Map<State, Enumerator> stateEnumerators = ((GuardInvocationGenerator.CheckHistoryGuardContext) ctx).stateEnumerators;
      final String compositeState = guard.getCompositeState().getName();
      final String subState = guard.getSubState().getName();
      StringLiteral _stringLiteral = new StringLiteral(compositeState);
      StringLiteral _stringLiteral_1 = new StringLiteral(subState);
      _xblockexpression = new FunctionCall(func, _stringLiteral, _stringLiteral_1);
    }
    return _xblockexpression;
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
  protected FunctionCall _visit(final Guard guard, final GuardInvocationGenerator.Context ctx) {
    FunctionCall _xblockexpression = null;
    {
      final MemberFunction func = ctx.func;
      final ElementAccess arg = ((GuardInvocationGenerator.UserGuardContext) ctx).enclosingFuncParam;
      _xblockexpression = new FunctionCall(func, arg);
    }
    return _xblockexpression;
  }
  
  public FunctionCall visit(final NamedElement guard, final GuardInvocationGenerator.Context ctx) {
    if (guard instanceof CheckHistory) {
      return _visit((CheckHistory)guard, ctx);
    } else if (guard instanceof Guard) {
      return _visit((Guard)guard, ctx);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(guard, ctx).toString());
    }
  }
}
