/**
 * Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrusrt.codegen.UserEditableRegion;
import org.eclipse.papyrusrt.codegen.cpp.SerializationManager;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.UserCodeTagUtil;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Parameter;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.UserCode;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer;
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode;
import org.eclipse.papyrusrt.xtumlrt.common.Annotation;
import org.eclipse.papyrusrt.xtumlrt.common.Entity;
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.UpdateState;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator;
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * This visitor generates function declarations for actions in the state machine.
 * 
 * @author Ernesto Posse
 */
@SuppressWarnings("all")
public class ActionDeclarationGenerator {
  public static class Context {
  }
  
  @Data
  public static class UserActionContext extends ActionDeclarationGenerator.Context {
    private final Type rtmessageType;
    
    private final SerializationManager.ParameterSet params;
    
    private final UML2xtumlrtTranslator translator;
    
    private final FlatteningTransformer flattener;
    
    private final Entity capsuleContext;
    
    public UserActionContext(final Type rtmessageType, final SerializationManager.ParameterSet params, final UML2xtumlrtTranslator translator, final FlatteningTransformer flattener, final Entity capsuleContext) {
      super();
      this.rtmessageType = rtmessageType;
      this.params = params;
      this.translator = translator;
      this.flattener = flattener;
      this.capsuleContext = capsuleContext;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.rtmessageType== null) ? 0 : this.rtmessageType.hashCode());
      result = prime * result + ((this.params== null) ? 0 : this.params.hashCode());
      result = prime * result + ((this.translator== null) ? 0 : this.translator.hashCode());
      result = prime * result + ((this.flattener== null) ? 0 : this.flattener.hashCode());
      return prime * result + ((this.capsuleContext== null) ? 0 : this.capsuleContext.hashCode());
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
      ActionDeclarationGenerator.UserActionContext other = (ActionDeclarationGenerator.UserActionContext) obj;
      if (this.rtmessageType == null) {
        if (other.rtmessageType != null)
          return false;
      } else if (!this.rtmessageType.equals(other.rtmessageType))
        return false;
      if (this.params == null) {
        if (other.params != null)
          return false;
      } else if (!this.params.equals(other.params))
        return false;
      if (this.translator == null) {
        if (other.translator != null)
          return false;
      } else if (!this.translator.equals(other.translator))
        return false;
      if (this.flattener == null) {
        if (other.flattener != null)
          return false;
      } else if (!this.flattener.equals(other.flattener))
        return false;
      if (this.capsuleContext == null) {
        if (other.capsuleContext != null)
          return false;
      } else if (!this.capsuleContext.equals(other.capsuleContext))
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
    public Type getRtmessageType() {
      return this.rtmessageType;
    }
    
    @Pure
    public SerializationManager.ParameterSet getParams() {
      return this.params;
    }
    
    @Pure
    public UML2xtumlrtTranslator getTranslator() {
      return this.translator;
    }
    
    @Pure
    public FlatteningTransformer getFlattener() {
      return this.flattener;
    }
    
    @Pure
    public Entity getCapsuleContext() {
      return this.capsuleContext;
    }
  }
  
  protected MemberFunction _visit(final SaveHistory action, final ActionDeclarationGenerator.Context ctx) {
    return null;
  }
  
  protected MemberFunction _visit(final UpdateState action, final ActionDeclarationGenerator.Context ctx) {
    return null;
  }
  
  protected MemberFunction _visit(final ActionCode action, final ActionDeclarationGenerator.Context ctx) {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList(action, ctx);
    final MemberFunction _result;
    synchronized (_createCache_visit) {
      if (_createCache_visit.containsKey(_cacheKey)) {
        return _createCache_visit.get(_cacheKey);
      }
      String _string = CppNamesUtil.getFuncName(action).toString();
      MemberFunction _memberFunction = new MemberFunction(PrimitiveType.VOID, _string);
      _result = _memberFunction;
      _createCache_visit.put(_cacheKey, _result);
    }
    _init_visit(_result, action, ctx);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_visit = CollectionLiterals.newHashMap();
  
  private void _init_visit(final MemberFunction func, final ActionCode action, final ActionDeclarationGenerator.Context ctx) {
    final Type type = ((ActionDeclarationGenerator.UserActionContext) ctx).rtmessageType;
    final Parameter param = new Parameter(type, GlobalConstants.ACTION_FUNC_PARAM);
    String _source = action.getSource();
    final UserCode body = new UserCode(_source, 0);
    func.add(param);
    final UML2xtumlrtTranslator translator = ((ActionDeclarationGenerator.UserActionContext) ctx).translator;
    final Entity capsuleContext = ((ActionDeclarationGenerator.UserActionContext) ctx).capsuleContext;
    final FlatteningTransformer flattener = ((ActionDeclarationGenerator.UserActionContext) ctx).flattener;
    String userCodeTag = UserEditableRegion.userEditBegin(UserCodeTagUtil.generateLabel(action, translator, flattener, capsuleContext));
    EList<Annotation> _annotations = action.getAnnotations();
    for (final Annotation anno : _annotations) {
      if ((Objects.equal(anno.getName(), GlobalConstants.GENERATED_ACTION) && anno.getParameters().isEmpty())) {
        userCodeTag = null;
      }
    }
    SerializationManager.getInstance().generateUserCode(func, param, ((ActionDeclarationGenerator.UserActionContext) ctx).params, body, userCodeTag);
  }
  
  public MemberFunction visit(final ActionCode action, final ActionDeclarationGenerator.Context ctx) {
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
