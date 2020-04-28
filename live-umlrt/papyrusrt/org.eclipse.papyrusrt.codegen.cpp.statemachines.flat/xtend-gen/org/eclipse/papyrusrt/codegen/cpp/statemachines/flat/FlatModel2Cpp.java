/**
 * Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.cpp.SerializationManager;
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.ActionDeclarationGenerator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.ActionInvocationGenerator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamedElementComparator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.GuardDeclarationGenerator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.GuardInvocationGenerator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.VertexNameComparator;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement;
import org.eclipse.papyrusrt.codegen.lang.cpp.Type;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Constructor;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppClass;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppEnum;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.LinkageSpec;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberField;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Parameter;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AbstractFunctionCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AddressOfExpr;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BinaryOperation;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BlockInitializer;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.CastExpr;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.IndexExpr;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.IntegralLiteral;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.LogicalComparison;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.MemberAccess;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.MemberFunctionAddress;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.NewExpr;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.StringLiteral;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.UnaryOperation;
import org.eclipse.papyrusrt.codegen.lang.cpp.external.StandardLibrary;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.BreakStatement;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.CodeBlock;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ConditionalStatement;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ExpressionStatement;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ReturnStatement;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.SwitchClause;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.SwitchStatement;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.VariableDeclarationStatement;
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.WhileStatement;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.InPlaceTransformation;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.StateNestingFlattener;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.TransformationContext;
import org.eclipse.papyrusrt.codegen.statemachines.transformations.TransitionDepthComparator;
import org.eclipse.papyrusrt.xtumlrt.common.AbstractAction;
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode;
import org.eclipse.papyrusrt.xtumlrt.common.ActionReference;
import org.eclipse.papyrusrt.xtumlrt.common.Entity;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.common.Port;
import org.eclipse.papyrusrt.xtumlrt.common.RedefinableElement;
import org.eclipse.papyrusrt.xtumlrt.common.Signal;
import org.eclipse.papyrusrt.xtumlrt.statemach.ActionChain;
import org.eclipse.papyrusrt.xtumlrt.statemach.ChoicePoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.CompositeState;
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard;
import org.eclipse.papyrusrt.xtumlrt.statemach.JunctionPoint;
import org.eclipse.papyrusrt.xtumlrt.statemach.Pseudostate;
import org.eclipse.papyrusrt.xtumlrt.statemach.State;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition;
import org.eclipse.papyrusrt.xtumlrt.statemach.Trigger;
import org.eclipse.papyrusrt.xtumlrt.statemach.Vertex;
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory;
import org.eclipse.papyrusrt.xtumlrt.statemachext.UpdateState;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtModelTranslator;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtSMTranslator;
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtTranslator;
import org.eclipse.papyrusrt.xtumlrt.umlrt.AnyEvent;
import org.eclipse.papyrusrt.xtumlrt.umlrt.RTPort;
import org.eclipse.papyrusrt.xtumlrt.umlrt.RTTrigger;
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants;
import org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTStateMachineExtensions;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTStateMachineUtil;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * This class contains the transformation from flat UML-RT state machines to the
 * C/C++ language model.
 * 
 * It implements the algorithms described in the technical report
 * 
 * E. Posse. "Transforming flat UML-RT State Machines to a C/C++ language model".
 * Technical Report ZTR-2014-EP-002, Version 2, Zeligsoft, Sep 2014.
 * 
 * @author eposse
 */
@SuppressWarnings("all")
public class FlatModel2Cpp extends InPlaceTransformation {
  /**
   * The CppCodePattern that is being used for this transformation operation.
   */
  @Data
  public static class CppGenerationTransformationContext implements TransformationContext {
    private final CppCodePattern cpp;
    
    private final Entity capsuleContext;
    
    private final FlatteningTransformer flattener;
    
    private final Collection<State> discardedStates;
    
    public CppGenerationTransformationContext(final CppCodePattern cpp, final Entity capsuleContext, final FlatteningTransformer flattener, final Collection<State> discardedStates) {
      super();
      this.cpp = cpp;
      this.capsuleContext = capsuleContext;
      this.flattener = flattener;
      this.discardedStates = discardedStates;
    }
    
    @Override
    @Pure
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.cpp== null) ? 0 : this.cpp.hashCode());
      result = prime * result + ((this.capsuleContext== null) ? 0 : this.capsuleContext.hashCode());
      result = prime * result + ((this.flattener== null) ? 0 : this.flattener.hashCode());
      return prime * result + ((this.discardedStates== null) ? 0 : this.discardedStates.hashCode());
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
      FlatModel2Cpp.CppGenerationTransformationContext other = (FlatModel2Cpp.CppGenerationTransformationContext) obj;
      if (this.cpp == null) {
        if (other.cpp != null)
          return false;
      } else if (!this.cpp.equals(other.cpp))
        return false;
      if (this.capsuleContext == null) {
        if (other.capsuleContext != null)
          return false;
      } else if (!this.capsuleContext.equals(other.capsuleContext))
        return false;
      if (this.flattener == null) {
        if (other.flattener != null)
          return false;
      } else if (!this.flattener.equals(other.flattener))
        return false;
      if (this.discardedStates == null) {
        if (other.discardedStates != null)
          return false;
      } else if (!this.discardedStates.equals(other.discardedStates))
        return false;
      return true;
    }
    
    @Override
    @Pure
    public String toString() {
      ToStringBuilder b = new ToStringBuilder(this);
      b.add("cpp", this.cpp);
      b.add("capsuleContext", this.capsuleContext);
      b.add("flattener", this.flattener);
      b.add("discardedStates", this.discardedStates);
      return b.toString();
    }
    
    @Pure
    public CppCodePattern getCpp() {
      return this.cpp;
    }
    
    @Pure
    public Entity getCapsuleContext() {
      return this.capsuleContext;
    }
    
    @Pure
    public FlatteningTransformer getFlattener() {
      return this.flattener;
    }
    
    @Pure
    public Collection<State> getDiscardedStates() {
      return this.discardedStates;
    }
  }
  
  /**
   * The C++ code pattern factory.
   */
  private CppCodePattern cpp;
  
  /**
   * The class/capsule context where the state machine occurs (maybe the owner or a subclass of the SM owner.
   */
  private Entity capsuleContext;
  
  /**
   * The generic state-machine flattening transformer.
   */
  private FlatteningTransformer flattener;
  
  /**
   * (Composite) States discarded by the flattener.
   */
  private Collection<State> discardedStates;
  
  /**
   * The source state machine to transform.
   */
  private StateMachine machine;
  
  /**
   * Elements that go into the generated model.
   */
  private CppEnum statesDeclaration;
  
  private MemberField stateNamesTableDeclaration;
  
  private Map<State, Enumerator> stateEnumerators;
  
  private MemberField historyTableDeclaration;
  
  private MemberFunction saveHistoryFunction;
  
  private MemberFunction checkHistoryFunction;
  
  private MemberFunction updateStateFunction;
  
  private Map<AbstractAction, MemberFunction> userActionFunctions;
  
  private Map<Guard, MemberFunction> userGuardFunctions;
  
  private Map<ActionChain, MemberFunction> actionChainFunctions;
  
  private Map<ChoicePoint, MemberFunction> choicePointFunctions;
  
  private Map<JunctionPoint, MemberFunction> junctionPointFunctions;
  
  private Map<State, MemberFunction> stateFunctions;
  
  private Parameter injectFuncParam;
  
  private MemberFunction initializeFunc;
  
  private Parameter initializeFuncParam;
  
  private MemberFunction getCurrentStateStringFunc;
  
  private CppClass cppCapsuleClass;
  
  /**
   * Objects used for the generation
   */
  private final ActionDeclarationGenerator actionDeclarationGenerator = new ActionDeclarationGenerator();
  
  private final GuardDeclarationGenerator guardDeclarationGenerator = new GuardDeclarationGenerator();
  
  private final ActionInvocationGenerator actionInvocationGenerator = new ActionInvocationGenerator();
  
  private final GuardInvocationGenerator guardInvocationGenerator = new GuardInvocationGenerator();
  
  private Map<State, String> stateNames;
  
  private final ArrayList<State> states = new ArrayList<State>();
  
  public FlatModel2Cpp() {
    this.stateEnumerators = CollectionLiterals.<State, Enumerator>newHashMap();
    this.userActionFunctions = CollectionLiterals.<AbstractAction, MemberFunction>newHashMap();
    this.userGuardFunctions = CollectionLiterals.<Guard, MemberFunction>newHashMap();
    this.actionChainFunctions = CollectionLiterals.<ActionChain, MemberFunction>newHashMap();
    this.choicePointFunctions = CollectionLiterals.<ChoicePoint, MemberFunction>newHashMap();
    this.junctionPointFunctions = CollectionLiterals.<JunctionPoint, MemberFunction>newHashMap();
    this.stateFunctions = CollectionLiterals.<State, MemberFunction>newHashMap();
    this.stateNames = CollectionLiterals.<State, String>newHashMap();
  }
  
  /**
   * This is the main method of the transformation. It performs the
   * transformation by invoking methods that generate each part of the
   * target language model.
   */
  @Override
  public boolean transformInPlace(final StateMachine stateMachine, final TransformationContext context) {
    this.setup(stateMachine, context);
    if ((!(context instanceof FlatModel2Cpp.CppGenerationTransformationContext))) {
      return false;
    }
    CompositeState _top = stateMachine.getTop();
    boolean _tripleEquals = (_top == null);
    if (_tripleEquals) {
      return true;
    }
    this.discardedStates = ((FlatModel2Cpp.CppGenerationTransformationContext) context).discardedStates;
    this.cpp = ((FlatModel2Cpp.CppGenerationTransformationContext) context).cpp;
    this.capsuleContext = ((FlatModel2Cpp.CppGenerationTransformationContext) context).capsuleContext;
    this.flattener = ((FlatModel2Cpp.CppGenerationTransformationContext) context).flattener;
    this.machine = stateMachine;
    try {
      this.cppCapsuleClass = this.cpp.getCppClass(CppCodePattern.Output.CapsuleClass, this.capsuleContext);
      this.generateStatesDeclaration();
      this.saveHistoryFunction = this.getSaveHistoryFunction();
      this.checkHistoryFunction = this.getCheckHistoryFunction();
      this.updateStateFunction = this.getUpdateStateFunction();
      this.generateAllUserActionFunctions();
      this.generateAllUserGuardFunctions();
      this.generateAllActionChainFunctions();
      this.generateAllChoicePointFunctions();
      this.generateAllJunctionFunctions();
      this.generateInitializeFunc();
      this.generateTransitionTableCalls();
      this.generateCompilationUnit();
      return true;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception e = (Exception)_t;
        CodeGenPlugin.error("[FlatModel2Cpp] error generating C++ code from flat state machine", e);
        return false;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  protected Variable getThisRef() {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList();
    final Variable _result;
    synchronized (_createCache_getThisRef) {
      if (_createCache_getThisRef.containsKey(_cacheKey)) {
        return _createCache_getThisRef.get(_cacheKey);
      }
      Type _ptr = this.cppCapsuleClass.getType().ptr();
      Variable _variable = new Variable(_ptr, "this");
      _result = _variable;
      _createCache_getThisRef.put(_cacheKey, _result);
    }
    _init_getThisRef(_result);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, Variable> _createCache_getThisRef = CollectionLiterals.newHashMap();
  
  private void _init_getThisRef(final Variable it) {
  }
  
  /**
   * Builds an enum type for the states of the state machine.
   * 
   * <p>The generated code would be something like:
   * 
   * <p><pre>
   * <code>enum State { s0, s1, s1_s0, s1_s1, s2, ..., TOP, UNVISITED };</code>
   * </pre>
   * 
   * <p>The enumerators for former composite states go first so that they can be used as indices
   * to the history table.
   * 
   * <p> The TOP state is a special state which is active at the top-level, when
   * executing top-level transitions.
   */
  protected Enumerator generateStatesDeclaration() {
    Enumerator _xblockexpression = null;
    {
      final int numSubStates = IterableExtensions.size(XTUMLRTStateMachineExtensions.getAllSubstates(this.machine));
      CppEnum _cppEnum = new CppEnum(GlobalConstants.STATE_TYPE_NAME);
      this.statesDeclaration = _cppEnum;
      final VertexNameComparator comparator = new VertexNameComparator();
      State[] formerCompositeStates = new State[this.discardedStates.size()];
      this.discardedStates.<State>toArray(formerCompositeStates);
      Arrays.<State>sort(formerCompositeStates, comparator);
      for (final State s : formerCompositeStates) {
        {
          final String name = QualifiedNames.makeValidCName(QualifiedNames.cachedFullSMName(s));
          final Enumerator stateEnum = new Enumerator(name);
          this.statesDeclaration.add(stateEnum);
          this.stateEnumerators.put(s, stateEnum);
          this.stateNames.put(s, name);
          this.states.add(s);
        }
      }
      State[] otherStates = new State[numSubStates];
      IterableExtensions.<State>toList(XTUMLRTStateMachineExtensions.getAllSubstates(this.machine)).<State>toArray(otherStates);
      Arrays.<State>sort(otherStates, comparator);
      for (final State s_1 : otherStates) {
        {
          final String name = QualifiedNames.makeValidCName(QualifiedNames.cachedFullSMName(s_1));
          final Enumerator stateEnum = new Enumerator(name);
          this.statesDeclaration.add(stateEnum);
          this.stateEnumerators.put(s_1, stateEnum);
          this.stateNames.put(s_1, name);
          this.states.add(s_1);
        }
      }
      final Enumerator topStateEnum = new Enumerator(GlobalConstants.TOP);
      this.statesDeclaration.add(topStateEnum);
      this.stateEnumerators.put(this.machine.getTop(), topStateEnum);
      final Enumerator unvisitedStateEnum = new Enumerator(GlobalConstants.UNVISITED);
      this.statesDeclaration.add(unvisitedStateEnum);
      _xblockexpression = this.stateEnumerators.put(StateNestingFlattener.UNVISITED, unvisitedStateEnum);
    }
    return _xblockexpression;
  }
  
  protected void generateStateNamesTableDeclaration() {
    final int numStates = this.stateNames.size();
    Type _const_ = PrimitiveType.CHAR.ptr().const_();
    IntegralLiteral _integralLiteral = new IntegralLiteral((numStates + 2));
    final Type stateNamesType = _const_.arrayOf(_integralLiteral);
    final BlockInitializer stateNamesInitializer = new BlockInitializer(stateNamesType);
    Collection<String> _values = this.stateNames.values();
    for (final String name : _values) {
      StringLiteral _stringLiteral = new StringLiteral(name);
      stateNamesInitializer.addExpression(_stringLiteral);
    }
    MemberField _memberField = new MemberField(stateNamesType, 
      GlobalConstants.STATE_NAMES_TABLE_NAME, stateNamesInitializer);
    this.stateNamesTableDeclaration = _memberField;
    final Constructor ctor = this.cpp.getConstructor(
      CppCodePattern.Output.CapsuleClass, 
      this.capsuleContext);
    for (final State s : this.states) {
      {
        final String stateName = this.stateNames.get(s);
        final Enumerator stateEnum = this.stateEnumerators.get(s);
        final ExpressionStatement stateNameInitStmt = this.makeStateNamesTableEntry(stateEnum, stateName);
        ctor.add(stateNameInitStmt);
      }
    }
    this.addTopNameInitStmt(ctor);
    this.addUnvisitedNameInitStmt(ctor);
  }
  
  protected void generateTransitionTableCalls() {
    final Constructor ctor = this.cpp.getConstructor(
      CppCodePattern.Output.CapsuleClass, 
      this.capsuleContext);
    Iterable<State> _allSubstates = XTUMLRTStateMachineExtensions.getAllSubstates(this.machine);
    for (final State s : _allSubstates) {
      {
        final String stateName = QualifiedNames.cachedFullSMName(s);
        ElementAccess _transitionTable = UMLRTRuntime.UMLRTCapsule.transitionTable();
        StringLiteral _stringLiteral = new StringLiteral(stateName);
        AbstractFunctionCall _Ctor = UMLRTRuntime.UMLRTState.Ctor(_stringLiteral);
        NewExpr _newExpr = new NewExpr(_Ctor);
        ctor.add(
          UMLRTRuntime.UMLRTTransitionTable.addState(_transitionTable, _newExpr));
      }
    }
    ElementAccess _transitionTable = UMLRTRuntime.UMLRTCapsule.transitionTable();
    StringLiteral _stringLiteral = new StringLiteral("top");
    AbstractFunctionCall _Ctor = UMLRTRuntime.UMLRTState.Ctor(_stringLiteral);
    NewExpr _newExpr = new NewExpr(_Ctor);
    ctor.add(
      UMLRTRuntime.UMLRTTransitionTable.addState(_transitionTable, _newExpr));
    ElementAccess _transitionTable_1 = UMLRTRuntime.UMLRTCapsule.transitionTable();
    StringLiteral _stringLiteral_1 = new StringLiteral(GlobalConstants.UNVISITED_STATE_NAME);
    AbstractFunctionCall _Ctor_1 = UMLRTRuntime.UMLRTState.Ctor(_stringLiteral_1);
    NewExpr _newExpr_1 = new NewExpr(_Ctor_1);
    ctor.add(
      UMLRTRuntime.UMLRTTransitionTable.addState(_transitionTable_1, _newExpr_1));
    ElementAccess _transitionTable_2 = UMLRTRuntime.UMLRTCapsule.transitionTable();
    StringLiteral _stringLiteral_2 = new StringLiteral(GlobalConstants.CHOICE_POINT_LABEL);
    AbstractFunctionCall _Ctor_2 = UMLRTRuntime.UMLRTState.Ctor(_stringLiteral_2);
    NewExpr _newExpr_2 = new NewExpr(_Ctor_2);
    ctor.add(
      UMLRTRuntime.UMLRTTransitionTable.addState(_transitionTable_2, _newExpr_2));
    Iterable<State> _allSubstates_1 = XTUMLRTStateMachineExtensions.getAllSubstates(this.machine);
    for (final State s_1 : _allSubstates_1) {
      {
        final LinkedHashMap<RTPort, LinkedHashMap<Signal, LinkedHashSet<Transition>>> table = this.getPortSignalTransitionsTable(s_1);
        Set<RTPort> _keySet = table.keySet();
        for (final RTPort port : _keySet) {
          {
            final LinkedHashMap<Signal, LinkedHashSet<Transition>> portSignals = table.get(port);
            LinkedHashSet<Transition> anyEventTransitions = null;
            Set<Signal> _keySet_1 = portSignals.keySet();
            for (final Signal signal : _keySet_1) {
              {
                final LinkedHashSet<Transition> transitions = portSignals.get(signal);
                if ((signal instanceof AnyEvent)) {
                  anyEventTransitions = transitions;
                } else {
                  String _name = port.getName();
                  StringLiteral _stringLiteral_3 = new StringLiteral(_name);
                  String _name_1 = signal.getName();
                  StringLiteral _stringLiteral_4 = new StringLiteral(_name_1);
                  final AbstractFunctionCall trigger = UMLRTRuntime.UMLRTTrigger.Ctor(_stringLiteral_3, _stringLiteral_4);
                  ElementAccess _transitionTable_3 = UMLRTRuntime.UMLRTCapsule.transitionTable();
                  String _cachedFullSMName = QualifiedNames.cachedFullSMName(s_1);
                  StringLiteral _stringLiteral_5 = new StringLiteral(_cachedFullSMName);
                  final AbstractFunctionCall fromState = UMLRTRuntime.UMLRTTransitionTable.getState(_transitionTable_3, _stringLiteral_5);
                  ElementAccess _transitionTable_4 = UMLRTRuntime.UMLRTCapsule.transitionTable();
                  String _cachedFullSMName_1 = QualifiedNames.cachedFullSMName(XTUMLRTStateMachineUtil.getTargetState(((Transition[])Conversions.unwrapArray(transitions, Transition.class))[0]));
                  StringLiteral _stringLiteral_6 = new StringLiteral(_cachedFullSMName_1);
                  AbstractFunctionCall toState = UMLRTRuntime.UMLRTTransitionTable.getState(_transitionTable_4, _stringLiteral_6);
                  final MemberFunction guard = this.userGuardFunctions.getOrDefault(((Transition[])Conversions.unwrapArray(transitions, Transition.class))[0].getGuard(), null);
                  final MemberFunction action = this.actionChainFunctions.getOrDefault(((Transition[])Conversions.unwrapArray(transitions, Transition.class))[0].getActionChain(), null);
                  Expression _xifexpression = null;
                  if ((guard != null)) {
                    Type _type = UMLRTRuntime.UMLRTGuard.getType();
                    MemberFunctionAddress _memberFunctionAddress = new MemberFunctionAddress(this.cppCapsuleClass, guard);
                    _xifexpression = new CastExpr(_type, _memberFunctionAddress);
                  } else {
                    _xifexpression = StandardLibrary.NULL();
                  }
                  final Expression guardCall = _xifexpression;
                  Expression _xifexpression_1 = null;
                  if ((action != null)) {
                    Type _type_1 = UMLRTRuntime.UMLRTAction.getType();
                    MemberFunctionAddress _memberFunctionAddress_1 = new MemberFunctionAddress(this.cppCapsuleClass, action);
                    _xifexpression_1 = new CastExpr(_type_1, _memberFunctionAddress_1);
                  } else {
                    _xifexpression_1 = StandardLibrary.NULL();
                  }
                  final Expression actionCall = _xifexpression_1;
                  Expression choiceCall = StandardLibrary.NULL();
                  Vertex _targetVertex = ((Transition[])Conversions.unwrapArray(transitions, Transition.class))[0].getTargetVertex();
                  if ((_targetVertex instanceof ChoicePoint)) {
                    final MemberFunction choice = this.choicePointFunctions.getOrDefault(((Transition[])Conversions.unwrapArray(transitions, Transition.class))[0].getTargetVertex(), null);
                    Type _type_2 = UMLRTRuntime.UMLRTChoice.getType();
                    MemberFunctionAddress _memberFunctionAddress_2 = new MemberFunctionAddress(this.cppCapsuleClass, choice);
                    CastExpr _castExpr = new CastExpr(_type_2, _memberFunctionAddress_2);
                    choiceCall = _castExpr;
                    ElementAccess _transitionTable_5 = UMLRTRuntime.UMLRTCapsule.transitionTable();
                    StringLiteral _stringLiteral_7 = new StringLiteral(GlobalConstants.CHOICE_POINT_LABEL);
                    toState = UMLRTRuntime.UMLRTTransitionTable.getState(_transitionTable_5, _stringLiteral_7);
                  }
                  String _makeValidCName = QualifiedNames.makeValidCName(((Transition[])Conversions.unwrapArray(transitions, Transition.class))[0].getName());
                  StringLiteral _stringLiteral_8 = new StringLiteral(_makeValidCName);
                  final AbstractFunctionCall transition = UMLRTRuntime.UMLRTTransition.Ctor(_stringLiteral_8, fromState, toState, ((Expression) choiceCall), ((Expression) guardCall), ((Expression) actionCall));
                  ElementAccess _transitionTable_6 = UMLRTRuntime.UMLRTCapsule.transitionTable();
                  NewExpr _newExpr_3 = new NewExpr(trigger);
                  NewExpr _newExpr_4 = new NewExpr(transition);
                  ctor.add(UMLRTRuntime.UMLRTTransitionTable.addTransition(_transitionTable_6, fromState, _newExpr_3, _newExpr_4));
                }
              }
            }
          }
        }
      }
    }
    for (final State s_2 : this.discardedStates) {
      {
        final String stateName = QualifiedNames.cachedFullSMName(s_2);
        ElementAccess _historyMap = UMLRTRuntime.UMLRTCapsule.historyMap();
        StringLiteral _stringLiteral_3 = new StringLiteral(stateName);
        StringLiteral _stringLiteral_4 = new StringLiteral("SPECIAL_INTERNAL_STATE_UNVISITED");
        ctor.add(
          UMLRTRuntime.UMLRTHistoryMap.put(_historyMap, _stringLiteral_3, _stringLiteral_4));
      }
    }
  }
  
  protected void addTopNameInitStmt(final Constructor ctor) {
    final String topName = GlobalConstants.TOP_STATE_NAME;
    final Enumerator topEnum = this.stateEnumerators.get(this.machine.getTop());
    final ExpressionStatement topNameInitStmt = this.makeStateNamesTableEntry(topEnum, topName);
    ctor.add(topNameInitStmt);
  }
  
  protected void addUnvisitedNameInitStmt(final Constructor ctor) {
    final String unvisitedName = GlobalConstants.UNVISITED_STATE_NAME;
    final Enumerator unvisitedEnum = this.stateEnumerators.get(StateNestingFlattener.UNVISITED);
    final ExpressionStatement unvisitedNameInitStmt = this.makeStateNamesTableEntry(unvisitedEnum, unvisitedName);
    ctor.add(unvisitedNameInitStmt);
  }
  
  protected ExpressionStatement makeStateNamesTableEntry(final Enumerator stateEnumerator, final String stateName) {
    ElementAccess _elementAccess = new ElementAccess(this.stateNamesTableDeclaration);
    ElementAccess _elementAccess_1 = new ElementAccess(stateEnumerator);
    IndexExpr _indexExpr = new IndexExpr(_elementAccess, _elementAccess_1);
    StringLiteral _stringLiteral = new StringLiteral(stateName);
    BinaryOperation _binaryOperation = new BinaryOperation(_indexExpr, 
      BinaryOperation.Operator.ASSIGN, _stringLiteral);
    return new ExpressionStatement(_binaryOperation);
  }
  
  /**
   * Generates a declaration for the history table for the state machine.
   * 
   * The generated code would be something like:
   * 
   * <p>
   * <code>State[] history = { UNDEFINED, ..., UNDEFINED };
   */
  protected MemberField generateHistoryTableDeclaration() {
    MemberField _xblockexpression = null;
    {
      final int numStates = this.discardedStates.size();
      MemberField _xifexpression = null;
      if ((numStates > 0)) {
        MemberField _xblockexpression_1 = null;
        {
          Type _type = this.statesDeclaration.getType();
          IntegralLiteral _integralLiteral = new IntegralLiteral(numStates);
          final Type historyTableType = _type.arrayOf(_integralLiteral);
          final MemberField tableDecl = new MemberField(historyTableType, 
            GlobalConstants.HISTORY_TABLE_NAME);
          IntegralLiteral _integralLiteral_1 = new IntegralLiteral(0);
          final Variable counter = new Variable(
            LinkageSpec.UNSPECIFIED, 
            PrimitiveType.INT, 
            "i", _integralLiteral_1);
          final VariableDeclarationStatement counterDecl = new VariableDeclarationStatement(counter);
          ElementAccess _elementAccess = new ElementAccess(counter);
          IntegralLiteral _integralLiteral_2 = new IntegralLiteral(numStates);
          LogicalComparison _logicalComparison = new LogicalComparison(_elementAccess, 
            LogicalComparison.Operator.LESS_THAN, _integralLiteral_2);
          final WhileStatement historyInitLoopStmt = new WhileStatement(_logicalComparison);
          ElementAccess _elementAccess_1 = new ElementAccess(tableDecl);
          ElementAccess _elementAccess_2 = new ElementAccess(counter);
          UnaryOperation _unaryOperation = new UnaryOperation(
            UnaryOperation.Operator.POST_INCREMENT, _elementAccess_2);
          IndexExpr _indexExpr = new IndexExpr(_elementAccess_1, _unaryOperation);
          Enumerator _get = this.stateEnumerators.get(StateNestingFlattener.UNVISITED);
          ElementAccess _elementAccess_3 = new ElementAccess(_get);
          BinaryOperation _binaryOperation = new BinaryOperation(_indexExpr, 
            BinaryOperation.Operator.ASSIGN, _elementAccess_3);
          final ExpressionStatement historyEntryInitStmt = new ExpressionStatement(_binaryOperation);
          historyInitLoopStmt.add(historyEntryInitStmt);
          final Constructor ctor = this.cpp.getConstructor(
            CppCodePattern.Output.CapsuleClass, 
            this.capsuleContext);
          ctor.add(counterDecl);
          ctor.add(historyInitLoopStmt);
          _xblockexpression_1 = this.historyTableDeclaration = tableDecl;
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  /**
   * Generates a function that saves history.
   * 
   * The code generated is as follows:
   * 
   * <p><pre>
   * <code>
   * void save_history(State compositeState, State subState) {
   *     history[compositeState] = subState;
   * }
   * <code>
   * </pre>
   * 
   * where <code>State</code> is the capsule's state type (an enum) and
   * <code>history</code> is the capsule's history table.
   * 
   * <p><b>Note:</b> The current implementation generates this as a normal
   * function but it should be either a macro or an inline function.
   * However the C/C++ language model does not currently support these.
   * 
   * @see
   *  #generateStatesDeclaration
   *  #generateHistoryTableDeclaration
   */
  protected MemberFunction getSaveHistoryFunction() {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList();
    final MemberFunction _result;
    synchronized (_createCache_getSaveHistoryFunction) {
      if (_createCache_getSaveHistoryFunction.containsKey(_cacheKey)) {
        return _createCache_getSaveHistoryFunction.get(_cacheKey);
      }
      MemberFunction _memberFunction = new MemberFunction(
        PrimitiveType.VOID, 
        GlobalConstants.SAVE_HISTORY_FUNC_NAME);
      _result = _memberFunction;
      _createCache_getSaveHistoryFunction.put(_cacheKey, _result);
    }
    _init_getSaveHistoryFunction(_result);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_getSaveHistoryFunction = CollectionLiterals.newHashMap();
  
  private void _init_getSaveHistoryFunction(final MemberFunction it) {
    boolean _equals = Objects.equal(it, null);
    if (_equals) {
      return;
    }
    Type _const_ = PrimitiveType.CHAR.ptr().const_();
    final Parameter param1 = new Parameter(_const_, "compositeState");
    Type _const__1 = PrimitiveType.CHAR.ptr().const_();
    final Parameter param2 = new Parameter(_const__1, "subState");
    ElementAccess _historyMap = UMLRTRuntime.UMLRTCapsule.historyMap();
    ElementAccess _elementAccess = new ElementAccess(param1);
    ElementAccess _elementAccess_1 = new ElementAccess(param2);
    final AbstractFunctionCall body = UMLRTRuntime.UMLRTHistoryMap.put(_historyMap, _elementAccess, _elementAccess_1);
    it.add(param1);
    it.add(param2);
    it.add(body);
  }
  
  /**
   * Generates a function that checks history.
   * 
   * The code generated is as follows:
   * 
   * <p><pre>
   * <code>
   * void check_history(State compositeState, State subState) {
   *     return history[compositeState] == subState;
   * }
   * <code>
   * </pre>
   * 
   * where <code>State</code> is the capsule's state type (an enum) and
   * <code>history</code> is the capsule's history table.
   * 
   * <p><b>Note:</b> The current implementation generates this as a normal
   * function but it should be either a macro or an inline function.
   * However the C/C++ language model does not currently support these.
   * 
   * @see
   *  #generateStatesDeclaration
   *  #generateHistoryTableDeclaration
   */
  protected MemberFunction getCheckHistoryFunction() {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList();
    final MemberFunction _result;
    synchronized (_createCache_getCheckHistoryFunction) {
      if (_createCache_getCheckHistoryFunction.containsKey(_cacheKey)) {
        return _createCache_getCheckHistoryFunction.get(_cacheKey);
      }
      MemberFunction _memberFunction = new MemberFunction(
        PrimitiveType.BOOL, 
        GlobalConstants.CHECK_HISTORY_FUNC_NAME);
      _result = _memberFunction;
      _createCache_getCheckHistoryFunction.put(_cacheKey, _result);
    }
    _init_getCheckHistoryFunction(_result);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_getCheckHistoryFunction = CollectionLiterals.newHashMap();
  
  private void _init_getCheckHistoryFunction(final MemberFunction it) {
    boolean _equals = Objects.equal(it, null);
    if (_equals) {
      return;
    }
    Type _const_ = PrimitiveType.CHAR.ptr().const_();
    final Parameter param1 = new Parameter(_const_, "compositeState");
    Type _const__1 = PrimitiveType.CHAR.ptr().const_();
    final Parameter param2 = new Parameter(_const__1, "subState");
    ElementAccess _historyMap = UMLRTRuntime.UMLRTCapsule.historyMap();
    ElementAccess _elementAccess = new ElementAccess(param1);
    AbstractFunctionCall _get = UMLRTRuntime.UMLRTHistoryMap.get(_historyMap, _elementAccess);
    ElementAccess _elementAccess_1 = new ElementAccess(param2);
    AbstractFunctionCall _strcmp = StandardLibrary.strcmp(_get, _elementAccess_1);
    IntegralLiteral _integralLiteral = new IntegralLiteral(0);
    LogicalComparison _logicalComparison = new LogicalComparison(_strcmp, 
      LogicalComparison.Operator.EQUIVALENT, _integralLiteral);
    final ReturnStatement body = new ReturnStatement(_logicalComparison);
    it.add(param1);
    it.add(param2);
    it.add(body);
  }
  
  /**
   * Generates a function that updates the state variable.
   * 
   * The code generated is as follows:
   * 
   * <p><pre>
   * <code>
   * void update_state(State newState) {
   *     currentState = newState;
   * }
   * <code>
   * </pre>
   * 
   * where <code>State</code> is the capsule's state type (an enum) and
   * <code>currentState</code> is the capsule's state variable.
   * 
   * <p><b>Note:</b> The current implementation generates this as a normal
   * function but it should be either a macro or an inline function.
   * However the C/C++ language model does not currently support these.
   * 
   * @see
   *  #generateStatesDeclaration
   *  #generateHistoryTableDeclaration
   */
  protected MemberFunction getUpdateStateFunction() {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList();
    final MemberFunction _result;
    synchronized (_createCache_getUpdateStateFunction) {
      if (_createCache_getUpdateStateFunction.containsKey(_cacheKey)) {
        return _createCache_getUpdateStateFunction.get(_cacheKey);
      }
      MemberFunction _memberFunction = new MemberFunction(
        PrimitiveType.VOID, 
        GlobalConstants.UPDATE_STATE_FUNC_NAME);
      _result = _memberFunction;
      _createCache_getUpdateStateFunction.put(_cacheKey, _result);
    }
    _init_getUpdateStateFunction(_result);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_getUpdateStateFunction = CollectionLiterals.newHashMap();
  
  private void _init_getUpdateStateFunction(final MemberFunction it) {
    boolean _equals = Objects.equal(it, null);
    if (_equals) {
      return;
    }
    Type _const_ = PrimitiveType.CHAR.ptr().const_();
    final Parameter param = new Parameter(_const_, "newState");
    ElementAccess _currentState = UMLRTRuntime.UMLRTCapsule.currentState();
    ElementAccess _transitionTable = UMLRTRuntime.UMLRTCapsule.transitionTable();
    ElementAccess _elementAccess = new ElementAccess(param);
    AbstractFunctionCall _state = UMLRTRuntime.UMLRTTransitionTable.getState(_transitionTable, _elementAccess);
    BinaryOperation _binaryOperation = new BinaryOperation(_currentState, 
      BinaryOperation.Operator.ASSIGN, _state);
    final ExpressionStatement body = new ExpressionStatement(_binaryOperation);
    it.add(param);
    it.add(body);
  }
  
  /**
   * Generates declarations for action functions for actions occurring in
   * a transition's chain.
   * 
   * <p>This generation includes not only transition actions from the original
   * model, but also state entry and exit actions, since the flattening
   * transformation moves entry and exit actions to transition's action chains.
   * 
   * <p><b>Notes:</b>
   * <p>The "save history" action is not generated by this function, as
   * there is only one declaration for the save history.
   */
  protected void generateAllUserActionFunctions() {
    Iterable<Transition> _allTransitions = XTUMLRTStateMachineExtensions.getAllTransitions(this.machine);
    for (final Transition t : _allTransitions) {
      if (((t.getActionChain() != null) && (t.getActionChain().getActions() != null))) {
        EList<AbstractAction> _actions = t.getActionChain().getActions();
        for (final AbstractAction a : _actions) {
          this.generateActionFunc(a, t);
        }
      }
    }
  }
  
  /**
   * Generates the function declaration for an action occurring in
   * a transition's chain.
   */
  protected void _generateActionFunc(final SaveHistory a, final Transition t) {
    this.actionDeclarationGenerator.visit(a, null);
  }
  
  protected void _generateActionFunc(final UpdateState a, final Transition t) {
    this.actionDeclarationGenerator.visit(a, null);
  }
  
  protected void _generateActionFunc(final ActionCode a, final Transition t) {
    boolean _containsKey = this.userActionFunctions.containsKey(a);
    if (_containsKey) {
      return;
    }
    Type _rTMessageType = this.getRTMessageType();
    SerializationManager.ParameterSet _triggerParams = this.getTriggerParams(t);
    UML2xtumlrtTranslator _translator = this.cpp.getTranslator();
    UML2xtumlrtSMTranslator _stateMachineTranslator = null;
    if (((UML2xtumlrtModelTranslator) _translator)!=null) {
      _stateMachineTranslator=((UML2xtumlrtModelTranslator) _translator).getStateMachineTranslator();
    }
    ActionDeclarationGenerator.UserActionContext _userActionContext = new ActionDeclarationGenerator.UserActionContext(_rTMessageType, _triggerParams, _stateMachineTranslator, 
      this.flattener, 
      this.capsuleContext);
    final ActionDeclarationGenerator.Context ctx = ((ActionDeclarationGenerator.Context) _userActionContext);
    final MemberFunction f = this.actionDeclarationGenerator.visit(a, ctx);
    this.userActionFunctions.put(a, f);
  }
  
  protected void _generateActionFunc(final ActionReference a, final Transition t) {
    if (((a != null) && (a.getTarget() != null))) {
      this.generateActionFunc(a.getTarget(), t);
    }
  }
  
  /**
   * Generates declarations for guard functions for guards occurring in
   * a transition's chain.
   * 
   * <p><b>Notes:</b>
   * <p>The "check history" action is not generated by this function, as
   * there is only one declaration for the check history guard.
   */
  protected void generateAllUserGuardFunctions() {
    Iterable<Transition> _allTransitions = XTUMLRTStateMachineExtensions.getAllTransitions(this.machine);
    for (final Transition t : _allTransitions) {
      {
        Guard _guard = t.getGuard();
        boolean _tripleNotEquals = (_guard != null);
        if (_tripleNotEquals) {
          this.generateGuardFunc(t.getGuard(), t);
        }
        Iterable<RTTrigger> _filter = Iterables.<RTTrigger>filter(t.getTriggers(), RTTrigger.class);
        for (final RTTrigger trigger : _filter) {
          Guard _triggerGuard = trigger.getTriggerGuard();
          boolean _tripleNotEquals_1 = (_triggerGuard != null);
          if (_tripleNotEquals_1) {
            this.generateGuardFunc(trigger.getTriggerGuard(), t);
          }
        }
      }
    }
  }
  
  /**
   * Generates the function declaration for an action occurring in
   * a transition's chain.
   */
  protected MemberFunction _generateGuardFunc(final CheckHistory g, final Transition t) {
    return null;
  }
  
  protected MemberFunction _generateGuardFunc(final Guard g, final Transition t) {
    MemberFunction _xifexpression = null;
    if (((g.getBody() != null) && (!(g.getBody() instanceof CheckHistory)))) {
      MemberFunction _xblockexpression = null;
      {
        Transition sourceTransition = this.getTransitionChainUniqueSource(t);
        Transition _xifexpression_1 = null;
        if ((sourceTransition == null)) {
          _xifexpression_1 = t;
        } else {
          _xifexpression_1 = sourceTransition;
        }
        sourceTransition = _xifexpression_1;
        Type _rTMessageType = this.getRTMessageType();
        SerializationManager.ParameterSet _triggerParams = this.getTriggerParams(sourceTransition);
        UML2xtumlrtTranslator _translator = this.cpp.getTranslator();
        UML2xtumlrtSMTranslator _stateMachineTranslator = null;
        if (((UML2xtumlrtModelTranslator) _translator)!=null) {
          _stateMachineTranslator=((UML2xtumlrtModelTranslator) _translator).getStateMachineTranslator();
        }
        GuardDeclarationGenerator.UserGuardContext _userGuardContext = new GuardDeclarationGenerator.UserGuardContext(_rTMessageType, _triggerParams, _stateMachineTranslator, 
          this.flattener, 
          this.capsuleContext);
        final GuardDeclarationGenerator.Context ctx = ((GuardDeclarationGenerator.Context) _userGuardContext);
        final MemberFunction f = this.guardDeclarationGenerator.visit(g, ctx);
        _xblockexpression = this.userGuardFunctions.put(g, f);
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }
  
  /**
   * Generate function declarations for transition action chains.
   * 
   * <p>Each function generated will have a sequence of calls, invoking either
   * the action functions generated by {@link generateActionFunc} for
   * transition, state entry and state exit actions, as well as invoking
   * "save history" actions produced by the flattening transformation.
   */
  protected void generateAllActionChainFunctions() {
    Iterable<Transition> _allTransitions = XTUMLRTStateMachineExtensions.getAllTransitions(this.machine);
    for (final Transition t : _allTransitions) {
      if (((t.getActionChain() != null) && (!t.getActionChain().getActions().isEmpty()))) {
        final MemberFunction f = this.generateActionChainFunc(t);
        this.actionChainFunctions.put(t.getActionChain(), f);
      }
    }
  }
  
  /**
   * Generates the function declaration for a single action chain.
   */
  protected MemberFunction generateActionChainFunc(final Transition t) {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList(t);
    final MemberFunction _result;
    synchronized (_createCache_generateActionChainFunc) {
      if (_createCache_generateActionChainFunc.containsKey(_cacheKey)) {
        return _createCache_generateActionChainFunc.get(_cacheKey);
      }
      String _string = CppNamesUtil.getFuncName(t.getActionChain()).toString();
      MemberFunction _memberFunction = new MemberFunction(PrimitiveType.VOID, _string);
      _result = _memberFunction;
      _createCache_generateActionChainFunc.put(_cacheKey, _result);
    }
    _init_generateActionChainFunc(_result, t);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_generateActionChainFunc = CollectionLiterals.newHashMap();
  
  private void _init_generateActionChainFunc(final MemberFunction it, final Transition t) {
    Type _rTMessageType = this.getRTMessageType();
    final Parameter param = new Parameter(_rTMessageType, GlobalConstants.CHAIN_FUNC_PARAM);
    it.add(param);
    ActionChain _actionChain = t.getActionChain();
    boolean _tripleNotEquals = (_actionChain != null);
    if (_tripleNotEquals) {
      EList<AbstractAction> _actions = t.getActionChain().getActions();
      for (final AbstractAction a : _actions) {
        {
          final ExpressionStatement call = this.getActionInvocation(a, param);
          if ((call != null)) {
            it.add(((ExpressionStatement) call));
          }
        }
      }
    }
  }
  
  /**
   * Generates a call to an action, either user action or action generated by
   * the transformation.
   */
  protected ExpressionStatement _getActionInvocation(final SaveHistory action, final Parameter param) {
    ExpressionStatement _xblockexpression = null;
    {
      ActionInvocationGenerator.SaveHistoryActionContext _saveHistoryActionContext = new ActionInvocationGenerator.SaveHistoryActionContext(
        this.saveHistoryFunction, 
        this.stateEnumerators);
      final ActionInvocationGenerator.Context ctx = ((ActionInvocationGenerator.Context) _saveHistoryActionContext);
      _xblockexpression = this.actionInvocationGenerator.visit(action, ctx);
    }
    return _xblockexpression;
  }
  
  protected ExpressionStatement _getActionInvocation(final UpdateState action, final Parameter param) {
    ExpressionStatement _xblockexpression = null;
    {
      ActionInvocationGenerator.UpdateStateActionContext _updateStateActionContext = new ActionInvocationGenerator.UpdateStateActionContext(
        this.updateStateFunction, 
        this.stateEnumerators);
      final ActionInvocationGenerator.Context ctx = ((ActionInvocationGenerator.Context) _updateStateActionContext);
      _xblockexpression = this.actionInvocationGenerator.visit(action, ctx);
    }
    return _xblockexpression;
  }
  
  protected ExpressionStatement _getActionInvocation(final ActionCode action, final Parameter param) {
    ExpressionStatement _xblockexpression = null;
    {
      final MemberFunction funcDecl = this.userActionFunctions.get(action);
      ElementAccess _elementAccess = new ElementAccess(param);
      ActionInvocationGenerator.UserActionContext _userActionContext = new ActionInvocationGenerator.UserActionContext(funcDecl, _elementAccess);
      final ActionInvocationGenerator.Context ctx = ((ActionInvocationGenerator.Context) _userActionContext);
      _xblockexpression = this.actionInvocationGenerator.visit(action, ctx);
    }
    return _xblockexpression;
  }
  
  protected ExpressionStatement _getActionInvocation(final ActionReference action, final Parameter param) {
    ExpressionStatement _xifexpression = null;
    if (((action != null) && (action.getTarget() != null))) {
      _xifexpression = this.getActionInvocation(action.getTarget(), param);
    }
    return _xifexpression;
  }
  
  /**
   * Generates all functions corresponding to choice points.
   */
  protected void generateAllChoicePointFunctions() {
    Iterable<ChoicePoint> _allChoicePoints = XTUMLRTStateMachineExtensions.getAllChoicePoints(this.machine);
    for (final ChoicePoint c : _allChoicePoints) {
      {
        final MemberFunction f = this.getChoicePointFunction(c);
        this.choicePointFunctions.put(c, f);
      }
    }
  }
  
  /**
   * Generates the function corresponding to a given choice point.
   */
  protected MemberFunction getChoicePointFunction(final ChoicePoint p) {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList(p);
    final MemberFunction _result;
    synchronized (_createCache_getChoicePointFunction) {
      if (_createCache_getChoicePointFunction.containsKey(_cacheKey)) {
        return _createCache_getChoicePointFunction.get(_cacheKey);
      }
      Type _ptr = UMLRTRuntime.UMLRTState.getType().ptr();
      String _string = CppNamesUtil.getFuncName(p).toString();
      MemberFunction _memberFunction = new MemberFunction(_ptr, _string);
      _result = _memberFunction;
      _createCache_getChoicePointFunction.put(_cacheKey, _result);
    }
    _init_getChoicePointFunction(_result, p);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_getChoicePointFunction = CollectionLiterals.newHashMap();
  
  private void _init_getChoicePointFunction(final MemberFunction it, final ChoicePoint p) {
    Type _rTMessageType = this.getRTMessageType();
    final Parameter param = new Parameter(_rTMessageType, GlobalConstants.CHAIN_FUNC_PARAM);
    ElementAccess _currentState = UMLRTRuntime.UMLRTCapsule.currentState();
    final ReturnStatement defRet = new ReturnStatement(_currentState);
    final ConditionalStatement cond = new ConditionalStatement();
    boolean elseBranch = false;
    boolean ignoreBranch = false;
    Iterable<Transition> _directOutgoingTransitions = XTUMLRTStateMachineUtil.getDirectOutgoingTransitions(p);
    for (final Transition t : _directOutgoingTransitions) {
      {
        CodeBlock newBranch = null;
        ignoreBranch = false;
        Guard _guard = t.getGuard();
        boolean _tripleNotEquals = (_guard != null);
        if (_tripleNotEquals) {
          FunctionCall guard = this.getGuardInvocation(t.getGuard(), param);
          newBranch = cond.add(guard);
        } else {
          if ((!elseBranch)) {
            newBranch = cond.defaultBlock();
            elseBranch = true;
          } else {
            ignoreBranch = true;
          }
        }
        if ((!ignoreBranch)) {
          if (((t.getActionChain() != null) && (!t.getActionChain().getActions().isEmpty()))) {
            final MemberFunction actFunc = this.actionChainFunctions.get(t.getActionChain());
            final ElementAccess arg = new ElementAccess(param);
            final FunctionCall call = new FunctionCall(actFunc, arg);
            ExpressionStatement _expressionStatement = new ExpressionStatement(call);
            newBranch.add(_expressionStatement);
          }
          ElementAccess _elementAccess = new ElementAccess(param);
          ExpressionStatement destStmt = this.getDestination(t, false, _elementAccess);
          newBranch.add(destStmt);
        }
      }
    }
    it.add(param);
    it.add(cond);
    it.add(defRet);
  }
  
  protected FunctionCall getGuardInvocation(final Guard guard, final Parameter param) {
    FunctionCall _xifexpression = null;
    if (((guard.getBody() != null) && (guard.getBody() instanceof CheckHistory))) {
      AbstractAction _body = guard.getBody();
      _xifexpression = this.getCheckHistoryGuardInvocation(((CheckHistory) _body), param);
    } else {
      _xifexpression = this.getOtherGuardInvocation(guard, param);
    }
    return _xifexpression;
  }
  
  protected FunctionCall getCheckHistoryGuardInvocation(final CheckHistory guard, final Parameter param) {
    FunctionCall _xblockexpression = null;
    {
      GuardInvocationGenerator.CheckHistoryGuardContext _checkHistoryGuardContext = new GuardInvocationGenerator.CheckHistoryGuardContext(
        this.checkHistoryFunction, 
        this.stateEnumerators);
      final GuardInvocationGenerator.Context ctx = ((GuardInvocationGenerator.Context) _checkHistoryGuardContext);
      _xblockexpression = this.guardInvocationGenerator.visit(guard, ctx);
    }
    return _xblockexpression;
  }
  
  protected FunctionCall getOtherGuardInvocation(final Guard guard, final Parameter param) {
    FunctionCall _xblockexpression = null;
    {
      final MemberFunction funcDecl = this.userGuardFunctions.get(guard);
      ElementAccess _elementAccess = new ElementAccess(param);
      GuardInvocationGenerator.UserGuardContext _userGuardContext = new GuardInvocationGenerator.UserGuardContext(funcDecl, _elementAccess);
      final GuardInvocationGenerator.Context ctx = ((GuardInvocationGenerator.Context) _userGuardContext);
      _xblockexpression = this.guardInvocationGenerator.visit(guard, ctx);
    }
    return _xblockexpression;
  }
  
  /**
   * Generates all functions corresponding to junction points.
   */
  protected void generateAllJunctionFunctions() {
    Iterable<JunctionPoint> _allJunctionPoints = XTUMLRTStateMachineExtensions.getAllJunctionPoints(this.machine);
    for (final JunctionPoint j : _allJunctionPoints) {
      {
        final MemberFunction f = this.getJunctionPointFunction(j);
        this.junctionPointFunctions.put(j, f);
      }
    }
  }
  
  /**
   * Generates the function corresponding to a specific junction point.
   */
  protected MemberFunction getJunctionPointFunction(final JunctionPoint p) {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList(p);
    final MemberFunction _result;
    synchronized (_createCache_getJunctionPointFunction) {
      if (_createCache_getJunctionPointFunction.containsKey(_cacheKey)) {
        return _createCache_getJunctionPointFunction.get(_cacheKey);
      }
      Type _ptr = UMLRTRuntime.UMLRTState.getType().ptr();
      String _string = CppNamesUtil.getFuncName(p).toString();
      MemberFunction _memberFunction = new MemberFunction(_ptr, _string);
      _result = _memberFunction;
      _createCache_getJunctionPointFunction.put(_cacheKey, _result);
    }
    _init_getJunctionPointFunction(_result, p);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_getJunctionPointFunction = CollectionLiterals.newHashMap();
  
  private void _init_getJunctionPointFunction(final MemberFunction it, final JunctionPoint p) {
    Type _rTMessageType = this.getRTMessageType();
    final Parameter param = new Parameter(_rTMessageType, GlobalConstants.JUNC_FUNC_PARAM);
    final Transition t = ((Transition[])Conversions.unwrapArray(XTUMLRTStateMachineUtil.getDirectOutgoingTransitions(p), Transition.class))[0];
    final ArrayList<Statement> block = new ArrayList<Statement>();
    if (((t.getActionChain() != null) && (!t.getActionChain().getActions().isEmpty()))) {
      final MemberFunction actFunc = this.actionChainFunctions.get(t.getActionChain());
      final ElementAccess arg = new ElementAccess(param);
      final FunctionCall call = new FunctionCall(actFunc, arg);
      ExpressionStatement _expressionStatement = new ExpressionStatement(call);
      block.add(_expressionStatement);
    }
    ElementAccess _elementAccess = new ElementAccess(param);
    final ExpressionStatement d = this.getDestination(t, false, _elementAccess);
    block.add(d);
    it.add(param);
    it.add(((Statement[])Conversions.unwrapArray(block, Statement.class)));
  }
  
  /**
   * Generates all functions corresponding to states.
   */
  protected void generateAllStateFunctions() {
    Iterable<State> _allSubstates = XTUMLRTStateMachineExtensions.getAllSubstates(this.machine);
    for (final State s : _allSubstates) {
      {
        final MemberFunction f = this.generateStateFunc(s);
        this.stateFunctions.put(s, f);
      }
    }
  }
  
  /**
   * Generates the function corresponding to a given state.
   */
  protected MemberFunction generateStateFunc(final State state) {
    final ArrayList<?> _cacheKey = CollectionLiterals.newArrayList(state);
    final MemberFunction _result;
    synchronized (_createCache_generateStateFunc) {
      if (_createCache_generateStateFunc.containsKey(_cacheKey)) {
        return _createCache_generateStateFunc.get(_cacheKey);
      }
      Type _type = this.statesDeclaration.getType();
      String _string = CppNamesUtil.getFuncName(state).toString();
      MemberFunction _memberFunction = new MemberFunction(_type, _string);
      _result = _memberFunction;
      _createCache_generateStateFunc.put(_cacheKey, _result);
    }
    _init_generateStateFunc(_result, state);
    return _result;
  }
  
  private final HashMap<ArrayList<?>, MemberFunction> _createCache_generateStateFunc = CollectionLiterals.newHashMap();
  
  private void _init_generateStateFunc(final MemberFunction it, final State state) {
    Type _rTMessageType = this.getRTMessageType();
    final Parameter param = new Parameter(_rTMessageType, GlobalConstants.STATE_FUNC_PARAM);
    final LinkedHashMap<RTPort, LinkedHashMap<Signal, LinkedHashSet<Transition>>> table = this.getPortSignalTransitionsTable(state);
    ElementAccess _currentState = UMLRTRuntime.UMLRTCapsule.currentState();
    final ReturnStatement defRet = new ReturnStatement(_currentState);
    MemberAccess _portCond = this.getPortCond(param);
    final SwitchStatement portSwitch = new SwitchStatement(_portCond);
    Set<RTPort> _keySet = table.keySet();
    for (final RTPort port : _keySet) {
      {
        AbstractFunctionCall _sigCond = this.getSigCond(param);
        final SwitchStatement sigSwitch = new SwitchStatement(_sigCond);
        final LinkedHashMap<Signal, LinkedHashSet<Transition>> portSignals = table.get(port);
        LinkedHashSet<Transition> anyEventTransitions = null;
        Set<Signal> _keySet_1 = portSignals.keySet();
        for (final Signal signal : _keySet_1) {
          {
            final LinkedHashSet<Transition> transitions = portSignals.get(signal);
            if ((signal instanceof AnyEvent)) {
              anyEventTransitions = transitions;
            } else {
              final SwitchClause sigCase = this.generatePortSignalCase(transitions, signal, param, defRet);
              sigSwitch.add(sigCase);
            }
          }
        }
        final SwitchClause defaultCase = this.generateDefaultCase(anyEventTransitions, param, defRet);
        sigSwitch.add(defaultCase);
        Expression _enumeratorFor = this.enumeratorFor(port);
        final SwitchClause portCase = new SwitchClause(_enumeratorFor);
        portCase.add(sigSwitch);
        portCase.add(defRet);
        portSwitch.add(portCase);
      }
    }
    final SwitchClause defaultCase = this.generateDefaultCase(null, null, null);
    portSwitch.add(defaultCase);
    final SwitchStatement body = portSwitch;
    it.add(param);
    it.add(body);
    it.add(defRet);
  }
  
  protected SwitchClause generateDefaultCase(final LinkedHashSet<Transition> anyEventTransitions, final Parameter param, final ReturnStatement defRet) {
    SwitchClause _xblockexpression = null;
    {
      SwitchClause defaultClause = null;
      if ((anyEventTransitions == null)) {
        SwitchClause _switchClause = new SwitchClause();
        defaultClause = _switchClause;
        Variable _thisRef = this.getThisRef();
        ElementAccess _elementAccess = new ElementAccess(_thisRef);
        AbstractFunctionCall _unexpectedMessage = UMLRTRuntime.UMLRTCapsule.unexpectedMessage(_elementAccess);
        final ExpressionStatement callStmt = new ExpressionStatement(_unexpectedMessage);
        defaultClause.add(callStmt);
      } else {
        defaultClause = this.generatePortSignalCase(anyEventTransitions, null, param, defRet);
      }
      _xblockexpression = defaultClause;
    }
    return _xblockexpression;
  }
  
  /**
   * Builds a table that contains for each port and signal, all the outgoing
   * transitions of a given state whose trigger has that port and signal.
   * 
   * @param s     a {@link State}
   * @return a table T indexed by {@link Port}s such that for each port p,
   *         the entry T[p] is a table T' indexed by {@link Signal}s such that
   *         for each signal e, T'[e] contains all outgoing {@link Transition}s
   *         from s whose trigger has port p and signal e. In other words,
   *         T[p][e] contains all transitions from s whose trigger has port p
   *         and signal e.
   */
  protected LinkedHashMap<RTPort, LinkedHashMap<Signal, LinkedHashSet<Transition>>> getPortSignalTransitionsTable(final State s) {
    final LinkedHashMap<RTPort, LinkedHashMap<Signal, LinkedHashSet<Transition>>> table = CollectionLiterals.<RTPort, LinkedHashMap<Signal, LinkedHashSet<Transition>>>newLinkedHashMap();
    Iterable<Transition> _allOutgoingTransitions = XTUMLRTStateMachineExtensions.getAllOutgoingTransitions(s);
    for (final Transition transition : _allOutgoingTransitions) {
      EList<Trigger> _triggers = transition.getTriggers();
      for (final Trigger trigger : _triggers) {
        if ((trigger instanceof RTTrigger)) {
          final Signal signal = ((RTTrigger)trigger).getSignal();
          EList<RTPort> _ports = ((RTTrigger)trigger).getPorts();
          for (final RTPort port : _ports) {
            {
              boolean _containsKey = table.containsKey(port);
              boolean _not = (!_containsKey);
              if (_not) {
                LinkedHashMap<Signal, LinkedHashSet<Transition>> _linkedHashMap = new LinkedHashMap<Signal, LinkedHashSet<Transition>>();
                table.put(port, _linkedHashMap);
              }
              final LinkedHashMap<Signal, LinkedHashSet<Transition>> portSignals = table.get(port);
              boolean _containsKey_1 = portSignals.containsKey(signal);
              boolean _not_1 = (!_containsKey_1);
              if (_not_1) {
                portSignals.put(signal, CollectionLiterals.<Transition>newLinkedHashSet());
              }
              portSignals.get(signal).add(transition);
            }
          }
        }
      }
    }
    return table;
  }
  
  /**
   * Generates the body of the case block for a particular port/signal pair.
   * 
   * <p>It sorts the transitions by nesting depth, and filters out those
   * which have no guard except for the first one.
   * 
   * <p>If only one transition is left, the generated case contains the code
   * for it, possibly with a conditional if it had a guard.
   * 
   * <p>If there is more than one transition left, then they must have guards
   * except possibly for the last one. In this case we generate a conditional.
   */
  protected SwitchClause generatePortSignalCase(final LinkedHashSet<Transition> transitions, final Signal signal, final Parameter param, final ReturnStatement defRet) {
    SwitchClause _xifexpression = null;
    if (((signal == null) || (signal instanceof AnyEvent))) {
      _xifexpression = new SwitchClause();
    } else {
      Expression _enumeratorFor = this.enumeratorFor(signal);
      _xifexpression = new SwitchClause(_enumeratorFor);
    }
    final SwitchClause sigCase = _xifexpression;
    final ArrayList<Transition> sortedTransitions = this.sortTransitions(transitions);
    for (final Transition transition : sortedTransitions) {
      {
        final ArrayList<Statement> actionChainCall = this.generateActionChainAndDestinationCall(transition, param);
        final Function1<RTTrigger, Boolean> _function = (RTTrigger it) -> {
          return Boolean.valueOf(((it.getSignal() instanceof AnyEvent) || Objects.equal(it.getSignal(), signal)));
        };
        Iterable<RTTrigger> _filter = IterableExtensions.<RTTrigger>filter(Iterables.<RTTrigger>filter(transition.getTriggers(), RTTrigger.class), _function);
        for (final RTTrigger trigger : _filter) {
          {
            ArrayList<Statement> stmts = actionChainCall;
            Guard _guard = transition.getGuard();
            boolean _tripleNotEquals = (_guard != null);
            if (_tripleNotEquals) {
              stmts = this.generateGuardConditional(transition.getGuard(), param, stmts);
            }
            Guard _triggerGuard = trigger.getTriggerGuard();
            boolean _tripleNotEquals_1 = (_triggerGuard != null);
            if (_tripleNotEquals_1) {
              stmts = this.generateGuardConditional(trigger.getTriggerGuard(), param, stmts);
            }
            for (final Statement stmt : stmts) {
              sigCase.add(stmt);
            }
          }
        }
      }
    }
    boolean _isEmpty = sortedTransitions.isEmpty();
    if (_isEmpty) {
      sigCase.add(defRet);
    }
    return sigCase;
  }
  
  protected ArrayList<Statement> generateActionChainAndDestinationCall(final Transition transition, final Parameter param) {
    ArrayList<Statement> _xblockexpression = null;
    {
      final ArrayList<Statement> sequence = new ArrayList<Statement>();
      final ExpressionStatement call = this.getActionChainInvocation(transition, param);
      ElementAccess _elementAccess = new ElementAccess(param);
      final ExpressionStatement dest = this.getDestination(transition, false, _elementAccess);
      if ((call != null)) {
        sequence.add(call);
      }
      sequence.add(dest);
      _xblockexpression = sequence;
    }
    return _xblockexpression;
  }
  
  protected ExpressionStatement getActionChainInvocation(final Transition transition, final Parameter param) {
    ExpressionStatement _xifexpression = null;
    if (((transition.getActionChain() != null) && (!transition.getActionChain().getActions().isEmpty()))) {
      ExpressionStatement _xblockexpression = null;
      {
        final MemberFunction actFunc = this.actionChainFunctions.get(transition.getActionChain());
        final ElementAccess arg = new ElementAccess(param);
        final FunctionCall call = new FunctionCall(actFunc, arg);
        final ExpressionStatement callStmt = new ExpressionStatement(call);
        _xblockexpression = callStmt;
      }
      _xifexpression = _xblockexpression;
    }
    return _xifexpression;
  }
  
  protected ArrayList<Statement> generateGuardConditional(final Guard guard, final Parameter param, final List<Statement> body) {
    ArrayList<Statement> _xblockexpression = null;
    {
      final ArrayList<Statement> stmts = new ArrayList<Statement>();
      final ConditionalStatement guardCond = new ConditionalStatement();
      final FunctionCall guardCall = this.getGuardInvocation(guard, param);
      final CodeBlock guardBlock = guardCond.add(guardCall);
      guardBlock.add(body);
      stmts.add(guardCond);
      _xblockexpression = stmts;
    }
    return _xblockexpression;
  }
  
  /**
   * Sorts transitions for the same port/signal pair by order of nesting
   * depth, marking the first non-guarded transition as the default transition
   * and filters-out all other non-guarded transitions.
   */
  protected ArrayList<Transition> sortTransitions(final LinkedHashSet<Transition> transitions) {
    Transition[] transitionsArray = new Transition[transitions.size()];
    transitions.<Transition>toArray(transitionsArray);
    final Transition[] _converted_transitionsArray = (Transition[])transitionsArray;
    TransitionDepthComparator _transitionDepthComparator = new TransitionDepthComparator();
    transitionsArray = ((Transition[])Conversions.unwrapArray(IterableExtensions.<Transition>sortWith(((Iterable<Transition>)Conversions.doWrapArray(_converted_transitionsArray)), _transitionDepthComparator), Transition.class));
    final ArrayList<Transition> sortedList = CollectionLiterals.<Transition>newArrayList();
    Transition defaultTransition = null;
    int i = 0;
    while ((i < transitionsArray.length)) {
      {
        final Transition t = transitionsArray[i];
        final boolean hasNonEmptyGuard = FlatModel2Cpp.hasNonEmptyGuard(t);
        if (hasNonEmptyGuard) {
          sortedList.add(t);
        } else {
          if ((defaultTransition == null)) {
            defaultTransition = t;
          }
        }
        i++;
      }
    }
    if ((defaultTransition != null)) {
      sortedList.add(defaultTransition);
    }
    return sortedList;
  }
  
  private static boolean hasNonEmptyGuard(final Transition t) {
    Guard _guard = t.getGuard();
    boolean _tripleNotEquals = (_guard != null);
    if (_tripleNotEquals) {
      final AbstractAction r = FlatModel2Cpp.resolveActionReference(t.getGuard().getBody());
      if (((r != null) && (r instanceof ActionCode))) {
        final String src = ((ActionCode) r).getSource();
        if (((src != null) && (!src.isEmpty()))) {
          return true;
        }
      }
    }
    Iterable<RTTrigger> _filter = Iterables.<RTTrigger>filter(t.getTriggers(), RTTrigger.class);
    for (final RTTrigger trigger : _filter) {
      {
        final Guard g = trigger.getTriggerGuard();
        if ((g != null)) {
          final AbstractAction r1 = FlatModel2Cpp.resolveActionReference(g.getBody());
          if (((r1 != null) && (r1 instanceof ActionCode))) {
            final String src1 = ((ActionCode) r1).getSource();
            if (((src1 != null) && (!src1.isEmpty()))) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  private static AbstractAction resolveActionReference(final AbstractAction a) {
    AbstractAction _xblockexpression = null;
    {
      if (((a != null) && (a instanceof ActionReference))) {
        FlatModel2Cpp.resolveActionReference(((ActionReference) a).getTarget());
      }
      _xblockexpression = a;
    }
    return _xblockexpression;
  }
  
  /**
   * Obtains the function call corresponding to a transition's destination.
   * 
   * @param t - The {@link Transition}
   * @param init - Whether we are looking for the destination to be obtained
   *               in the initialize method or in the inject method.
   * @param destArg - The argument to pass to the destination if it is a
   *                  function.
   */
  protected ExpressionStatement getDestination(final Transition t, final boolean init, final Expression destArg) {
    ExpressionStatement _xblockexpression = null;
    {
      Expression retVal = null;
      Vertex _targetVertex = t.getTargetVertex();
      if ((_targetVertex instanceof ChoicePoint)) {
        Vertex _targetVertex_1 = t.getTargetVertex();
        final ChoicePoint c = ((ChoicePoint) _targetVertex_1);
        final MemberFunction func = this.getChoicePointFunction(c);
        FunctionCall _functionCall = new FunctionCall(func, destArg);
        retVal = _functionCall;
      } else {
        Vertex _targetVertex_2 = t.getTargetVertex();
        if ((_targetVertex_2 instanceof JunctionPoint)) {
          Vertex _targetVertex_3 = t.getTargetVertex();
          final JunctionPoint j = ((JunctionPoint) _targetVertex_3);
          final MemberFunction func_1 = this.getJunctionPointFunction(j);
          FunctionCall _functionCall_1 = new FunctionCall(func_1, destArg);
          retVal = _functionCall_1;
        } else {
          Vertex _targetVertex_4 = t.getTargetVertex();
          final State s = ((State) _targetVertex_4);
          ElementAccess _transitionTable = UMLRTRuntime.UMLRTCapsule.transitionTable();
          String _name = s.getName();
          StringLiteral _stringLiteral = new StringLiteral(_name);
          retVal = UMLRTRuntime.UMLRTTransitionTable.getState(_transitionTable, _stringLiteral);
        }
      }
      ExpressionStatement _xifexpression = null;
      if (init) {
        ElementAccess _currentState = UMLRTRuntime.UMLRTCapsule.currentState();
        BinaryOperation _binaryOperation = new BinaryOperation(_currentState, 
          BinaryOperation.Operator.ASSIGN, retVal);
        _xifexpression = new ExpressionStatement(_binaryOperation);
      } else {
        _xifexpression = new ReturnStatement(retVal);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  protected void generateMsgFieldInit(final Parameter param, final MemberFunction func) {
    ElementAccess _msg = UMLRTRuntime.UMLRTCapsule.msg();
    ElementAccess _elementAccess = new ElementAccess(param);
    AddressOfExpr _addressOfExpr = new AddressOfExpr(_elementAccess);
    BinaryOperation _binaryOperation = new BinaryOperation(_msg, 
      BinaryOperation.Operator.ASSIGN, _addressOfExpr);
    final ExpressionStatement msgAssn = new ExpressionStatement(_binaryOperation);
    func.add(msgAssn);
  }
  
  /**
   * Generates the parameter for the main 'inject' function
   */
  protected Parameter getInjectFuncParam() {
    if ((this.injectFuncParam == null)) {
      Type _injectRTMessageType = this.getInjectRTMessageType();
      Parameter _parameter = new Parameter(_injectRTMessageType, 
        GlobalConstants.INJECT_FUNC_PARAM);
      this.injectFuncParam = _parameter;
    }
    return this.injectFuncParam;
  }
  
  protected SwitchClause generateDefaultStateCase() {
    SwitchClause _xblockexpression = null;
    {
      final SwitchClause defaultClause = new SwitchClause();
      final BreakStatement defStmt = new BreakStatement();
      defaultClause.add(defStmt);
      _xblockexpression = defaultClause;
    }
    return _xblockexpression;
  }
  
  protected Parameter getInitializeFuncParam() {
    if ((this.initializeFuncParam == null)) {
      Type _injectRTMessageType = this.getInjectRTMessageType();
      Parameter _parameter = new Parameter(_injectRTMessageType, 
        GlobalConstants.INITIALIZE_FUNC_PARAM);
      this.initializeFuncParam = _parameter;
    }
    return this.initializeFuncParam;
  }
  
  /**
   * Build the initialize function which performs the initial transition.
   * 
   * <p>This assumes that the top level of the state machine must have an
   * initial pseudo-state, and that there is exactly one outgoing transition
   * from such initial point.
   * 
   * <p> If there is no initial point, the body of the initialize method is
   * empty.
   */
  protected void generateInitializeFunc() {
    MemberFunction _memberFunction = new MemberFunction(
      PrimitiveType.VOID, 
      GlobalConstants.INITIALIZE_FUNC_NAME);
    this.initializeFunc = _memberFunction;
    this.initializeFunc.setVirtual();
    final Parameter param = this.getInitializeFuncParam();
    this.initializeFunc.add(param);
    this.generateMsgFieldInit(param, this.initializeFunc);
    if (((this.machine.getTop().getInitial() != null) && (!IterableExtensions.isEmpty(XTUMLRTStateMachineExtensions.getAllDirectOutgoingTransitions(this.machine.getTop().getInitial()))))) {
      final Transition initialTransition = ((Transition[])Conversions.unwrapArray(XTUMLRTStateMachineUtil.getDirectOutgoingTransitions(this.machine.getTop().getInitial()), Transition.class))[0];
      if (((initialTransition.getActionChain() != null) && (!initialTransition.getActionChain().getActions().isEmpty()))) {
        final MemberFunction actFunc = this.actionChainFunctions.get(initialTransition.getActionChain());
        Parameter _initializeFuncParam = this.getInitializeFuncParam();
        ElementAccess _elementAccess = new ElementAccess(_initializeFuncParam);
        final AddressOfExpr arg = new AddressOfExpr(_elementAccess);
        final FunctionCall call = new FunctionCall(actFunc, arg);
        this.initializeFunc.add(call);
      }
      ElementAccess _elementAccess_1 = new ElementAccess(param);
      AddressOfExpr _addressOfExpr = new AddressOfExpr(_elementAccess_1);
      final ExpressionStatement d = this.getDestination(initialTransition, true, _addressOfExpr);
      this.initializeFunc.add(d);
    }
  }
  
  protected void generateGetCurrentStateStrFunc() {
    Type _const_ = PrimitiveType.CHAR.ptr().const_();
    MemberFunction _memberFunction = new MemberFunction(_const_, 
      GlobalConstants.GET_CURR_STATE_NAME_FUNC_NAME, 
      Type.CVQualifier.CONST);
    this.getCurrentStateStringFunc = _memberFunction;
    ElementAccess _currentState = UMLRTRuntime.UMLRTCapsule.currentState();
    MemberAccess _memberAccess = new MemberAccess(_currentState, UMLRTRuntime.UMLRTState.nameField);
    final ReturnStatement body = new ReturnStatement(_memberAccess);
    this.getCurrentStateStringFunc.add(body);
  }
  
  /**
   * Generates the compilation unit for the state machine (*)
   * 
   * <p><b>Notes:</b> This implementation generates only a list of elements
   * to be consumed by the capsule generator which is assumed to be
   * responsible for putting together the full compilation unit.
   */
  protected void generateCompilationUnit() {
    this.cppCapsuleClass.addMember(CppClass.Visibility.PUBLIC, this.initializeFunc);
    this.cppCapsuleClass.addMember(CppClass.Visibility.PRIVATE, this.saveHistoryFunction);
    this.cppCapsuleClass.addMember(CppClass.Visibility.PRIVATE, this.checkHistoryFunction);
    this.cppCapsuleClass.addMember(CppClass.Visibility.PRIVATE, this.updateStateFunction);
    this.addPrivateMembers(this.cppCapsuleClass, this.userActionFunctions.values());
    this.addPrivateMembers(this.cppCapsuleClass, this.userGuardFunctions.values());
    this.addPrivateMembers(this.cppCapsuleClass, this.actionChainFunctions.values());
    this.addPrivateMembers(this.cppCapsuleClass, this.junctionPointFunctions.values());
    this.addPrivateMembers(this.cppCapsuleClass, this.choicePointFunctions.values());
  }
  
  private void addPrivateMembers(final CppClass cppClass, final Collection<MemberFunction> members) {
    MemberFunction[] memArray = new MemberFunction[members.size()];
    members.<MemberFunction>toArray(memArray);
    final MemberFunction[] _converted_memArray = (MemberFunction[])memArray;
    CppNamedElementComparator _cppNamedElementComparator = new CppNamedElementComparator();
    final List<MemberFunction> sortedMembers = IterableExtensions.<MemberFunction>sortWith(((Iterable<MemberFunction>)Conversions.doWrapArray(_converted_memArray)), _cppNamedElementComparator);
    for (final MemberFunction member : sortedMembers) {
      cppClass.addMember(CppClass.Visibility.PRIVATE, member);
    }
  }
  
  /**
   * Auxiliary methods
   */
  private Expression _enumeratorFor(final State s) {
    Enumerator _get = this.stateEnumerators.get(s);
    return new ElementAccess(_get);
  }
  
  private Expression _enumeratorFor(final Port p) {
    ElementAccess _xblockexpression = null;
    {
      final Enumerator enumerator = this.cpp.getEnumerator(CppCodePattern.Output.PortId, p, this.capsuleContext);
      _xblockexpression = new ElementAccess(enumerator);
    }
    return _xblockexpression;
  }
  
  private Expression _enumeratorFor(final Signal s) {
    return this.cpp.getEnumeratorAccess(CppCodePattern.Output.SignalId, s, null);
  }
  
  private Type getStateType() {
    return this.statesDeclaration.getType();
  }
  
  private Type getRTMessageType() {
    return UMLRTRuntime.UMLRTMessage.Element.getType().ptr().const_();
  }
  
  private Type getInjectRTMessageType() {
    return UMLRTRuntime.UMLRTMessage.Element.getType().ref().const_();
  }
  
  private SerializationManager.ParameterSet getTriggerParams(final Transition t) {
    SerializationManager.ParameterSet _xblockexpression = null;
    {
      final SerializationManager.ParameterSet params = new SerializationManager.ParameterSet(this.cpp);
      EList<Trigger> _triggers = t.getTriggers();
      for (final Trigger trigger : _triggers) {
        if ((trigger instanceof RTTrigger)) {
          Signal _signal = ((RTTrigger)trigger).getSignal();
          boolean _notEquals = (!Objects.equal(_signal, null));
          if (_notEquals) {
            params.add(((RTTrigger)trigger).getSignal().getParameters());
          }
        }
      }
      _xblockexpression = params;
    }
    return _xblockexpression;
  }
  
  /**
   * Builds an expression to obtain the port enum id for the switch.
   * 
   * <p>It assumes that the message parameter to the inject function is a
   * pointer to the RTMessage type, as returned by {@link #getRTMessageType},
   * this is, the signature of the inject function must be:
   * 
   * <p><pre>
   * <code>void inject(UMLRTMessage * msg)</code>
   * </pre>
   * 
   * <p>It also assumes that the port id and signal id are accessible from
   * this type. Basically the assumption is that the relevant definitions are
   * as follows:
   * 
   * <p>
   * <pre>
   * <code>
   * class UMLRTMessage : ... {
   * public:
   *     UMLRTPort * destPort;
   *     UMLRTSignal * signal;
   * }
   * 
   * struct UMLRTPort {
   *     int id;
   *     // ...
   * }
   * 
   * class UMLRTSignal {
   * public:
   *     int id;
   *     // ...
   * }
   * </code>
   * </pre>
   * 
   * <p>... where the typed <code>UMLRTPortId</code> and
   * <code>UMLRTSignalId</code> can be cast to the <code>Port</code> and
   * <code>Signal</code> enum types generated in the state machine's class.
   * 
   * <p>Given this assumptions, the port condition generated has the form:
   * 
   * <p><pre><code>(Port)(msg->destPort)->id</code></pre>
   * 
   * <p>and the signal condition is:
   * 
   * <p><pre><code>(ProtocolX::Signal)(msg->signal)->getId()</code></pre>
   * 
   * <p>where <code>ProtocolX</code> is the name of the port's protocol
   */
  private MemberAccess getPortCond(final Parameter param) {
    MemberAccess _xblockexpression = null;
    {
      final MemberField messagePortField = UMLRTRuntime.UMLRTMessage.destPort;
      final MemberField roleIdField = UMLRTRuntime.UMLRTCommsPortRole.id;
      ElementAccess _elementAccess = new ElementAccess(param);
      MemberAccess _memberAccess = new MemberAccess(_elementAccess, messagePortField);
      AbstractFunctionCall _role = UMLRTRuntime.UMLRTCommsPort.role(_memberAccess);
      _xblockexpression = new MemberAccess(_role, roleIdField);
    }
    return _xblockexpression;
  }
  
  private AbstractFunctionCall getSigCond(final Parameter param) {
    ElementAccess _elementAccess = new ElementAccess(param);
    return UMLRTRuntime.UMLRTMessage.getSignalId(_elementAccess);
  }
  
  /**
   * Looks for the source stable state of the transition chain that contains the given
   * transition t, if it is unique, this is, if there is a path
   * 
   * <p>s --t1--> p_1 --t2--> p_2 --t3--> ... --t--> v
   * 
   * <p>where t is the given transition, s is a state, each p_i is a pseudo-state with exactly
   * one incoming transition. If any p_i has more than one incoming transition, then this
   * method returns null, otherwise it returns t1.
   */
  private Transition getTransitionChainUniqueSource(final Transition t) {
    final HashSet<Vertex> visited = new HashSet<Vertex>();
    Vertex vertex = t.getSourceVertex();
    Transition sourceTransition = t;
    while ((((vertex instanceof Pseudostate) && (!IterableExtensions.isEmpty(XTUMLRTStateMachineUtil.getDirectIncomingTransitions(((Pseudostate) vertex))))) && (!visited.contains(vertex)))) {
      {
        visited.add(vertex);
        sourceTransition = ((Transition[])Conversions.unwrapArray(XTUMLRTStateMachineUtil.getDirectIncomingTransitions(vertex), Transition.class))[0];
        vertex = sourceTransition.getSourceVertex();
      }
    }
    if ((vertex instanceof State)) {
      return sourceTransition;
    }
    return null;
  }
  
  protected void generateActionFunc(final AbstractAction a, final Transition t) {
    if (a instanceof SaveHistory) {
      _generateActionFunc((SaveHistory)a, t);
      return;
    } else if (a instanceof UpdateState) {
      _generateActionFunc((UpdateState)a, t);
      return;
    } else if (a instanceof ActionCode) {
      _generateActionFunc((ActionCode)a, t);
      return;
    } else if (a instanceof ActionReference) {
      _generateActionFunc((ActionReference)a, t);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(a, t).toString());
    }
  }
  
  protected MemberFunction generateGuardFunc(final NamedElement g, final Transition t) {
    if (g instanceof CheckHistory) {
      return _generateGuardFunc((CheckHistory)g, t);
    } else if (g instanceof Guard) {
      return _generateGuardFunc((Guard)g, t);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(g, t).toString());
    }
  }
  
  protected ExpressionStatement getActionInvocation(final AbstractAction action, final Parameter param) {
    if (action instanceof SaveHistory) {
      return _getActionInvocation((SaveHistory)action, param);
    } else if (action instanceof UpdateState) {
      return _getActionInvocation((UpdateState)action, param);
    } else if (action instanceof ActionCode) {
      return _getActionInvocation((ActionCode)action, param);
    } else if (action instanceof ActionReference) {
      return _getActionInvocation((ActionReference)action, param);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(action, param).toString());
    }
  }
  
  private Expression enumeratorFor(final RedefinableElement p) {
    if (p instanceof Port) {
      return _enumeratorFor((Port)p);
    } else if (p instanceof Signal) {
      return _enumeratorFor((Signal)p);
    } else if (p instanceof State) {
      return _enumeratorFor((State)p);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(p).toString());
    }
  }
}
