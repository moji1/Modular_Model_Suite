/*******************************************************************************
* Copyright (c) 2014-2016 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.eclipse.papyrusrt.codegen.cpp.statemachines.flat

import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.List
import java.util.Map
import org.eclipse.papyrusrt.codegen.CodeGenPlugin
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern
import org.eclipse.papyrusrt.codegen.cpp.SerializationManager
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTCommsPort
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTCommsPortRole
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTMessage
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression
import org.eclipse.papyrusrt.codegen.lang.cpp.Statement
import org.eclipse.papyrusrt.codegen.lang.cpp.Type.CVQualifier
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppClass
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppEnum
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Constructor
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator
import org.eclipse.papyrusrt.codegen.lang.cpp.element.LinkageSpec
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberField
import org.eclipse.papyrusrt.codegen.lang.cpp.element.MemberFunction
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Parameter
import org.eclipse.papyrusrt.codegen.lang.cpp.element.PrimitiveType
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AddressOfExpr
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BinaryOperation
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BlockInitializer
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionCall
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.IndexExpr
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.IntegralLiteral
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.LogicalComparison
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.MemberAccess
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.StringLiteral
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.UnaryOperation
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.BreakStatement
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.CodeBlock
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ConditionalStatement
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ExpressionStatement
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.ReturnStatement
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.SwitchClause
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.SwitchStatement
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.VariableDeclarationStatement
import org.eclipse.papyrusrt.codegen.lang.cpp.stmt.WhileStatement
import org.eclipse.papyrusrt.codegen.statemachines.transformations.FlatteningTransformer
import org.eclipse.papyrusrt.codegen.statemachines.transformations.InPlaceTransformation
import org.eclipse.papyrusrt.codegen.statemachines.transformations.StateNestingFlattener
import org.eclipse.papyrusrt.codegen.statemachines.transformations.TransformationContext
import org.eclipse.papyrusrt.codegen.statemachines.transformations.TransitionDepthComparator
import org.eclipse.papyrusrt.xtumlrt.util.GlobalConstants
import org.eclipse.papyrusrt.xtumlrt.common.AbstractAction
import org.eclipse.papyrusrt.xtumlrt.common.ActionCode
import org.eclipse.papyrusrt.xtumlrt.common.ActionReference
import org.eclipse.papyrusrt.xtumlrt.common.Entity
import org.eclipse.papyrusrt.xtumlrt.common.Port
import org.eclipse.papyrusrt.xtumlrt.common.Signal
import org.eclipse.papyrusrt.xtumlrt.statemach.ActionChain
import org.eclipse.papyrusrt.xtumlrt.statemach.ChoicePoint
import org.eclipse.papyrusrt.xtumlrt.statemach.Guard
import org.eclipse.papyrusrt.xtumlrt.statemach.JunctionPoint
import org.eclipse.papyrusrt.xtumlrt.statemach.Pseudostate
import org.eclipse.papyrusrt.xtumlrt.statemach.State
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine
import org.eclipse.papyrusrt.xtumlrt.statemach.Transition
import org.eclipse.papyrusrt.xtumlrt.statemach.Vertex
import org.eclipse.papyrusrt.xtumlrt.statemachext.CheckHistory
import org.eclipse.papyrusrt.xtumlrt.statemachext.SaveHistory
import org.eclipse.papyrusrt.xtumlrt.statemachext.UpdateState
import org.eclipse.papyrusrt.xtumlrt.trans.from.uml.UML2xtumlrtModelTranslator
import org.eclipse.papyrusrt.xtumlrt.umlrt.AnyEvent
import org.eclipse.papyrusrt.xtumlrt.umlrt.RTTrigger
import org.eclipse.xtend.lib.annotations.Data

import static extension org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.CppNamesUtil.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTStateMachineExtensions.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTStateMachineUtil.*
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.NewExpr
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTTransitionTable
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTCapsule
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTState
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTTrigger
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.CastExpr
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTTransition
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTAction
import org.eclipse.papyrusrt.codegen.lang.cpp.element.NamedElement
import org.eclipse.papyrusrt.codegen.lang.cpp.element.FunctionPointer
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.FunctionAddress
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.MemberFunctionAddress
import org.eclipse.papyrusrt.codegen.lang.cpp.external.StandardLibrary
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTGuard
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTChoice
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BooleanLiteral
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime.UMLRTHistoryMap

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
class FlatModel2Cpp extends InPlaceTransformation
{

    /**
     * The CppCodePattern that is being used for this transformation operation.
     */
    @Data static class CppGenerationTransformationContext implements TransformationContext
    {
        CppCodePattern          cpp
        Entity                  capsuleContext
        FlatteningTransformer   flattener
        Collection<State>       discardedStates
    }

    /** The C++ code pattern factory. */
    CppCodePattern cpp
    /** The class/capsule context where the state machine occurs (maybe the owner or a subclass of the SM owner. */
    Entity capsuleContext
    /** The generic state-machine flattening transformer. */
    FlatteningTransformer flattener
    /** (Composite) States discarded by the flattener. */
    Collection<State> discardedStates
    /** The source state machine to transform. */
    StateMachine machine

    /** Elements that go into the generated model. */
    CppEnum                             statesDeclaration
    MemberField                         stateNamesTableDeclaration
    Map<State, Enumerator>              stateEnumerators
    MemberField                         historyTableDeclaration
    MemberFunction                      saveHistoryFunction
    MemberFunction                      checkHistoryFunction
    MemberFunction                      updateStateFunction
    Map<AbstractAction, MemberFunction> userActionFunctions
    Map<Guard, MemberFunction>          userGuardFunctions
    Map<ActionChain, MemberFunction>    actionChainFunctions
    Map<ChoicePoint, MemberFunction>    choicePointFunctions
    Map<JunctionPoint, MemberFunction>  junctionPointFunctions
    Map<State, MemberFunction>          stateFunctions
    Parameter                           injectFuncParam
    MemberFunction                      initializeFunc
    Parameter                           initializeFuncParam
    MemberFunction                      getCurrentStateStringFunc
    CppClass                            cppCapsuleClass

    /** Objects used for the generation */
    val actionDeclarationGenerator = new ActionDeclarationGenerator
    val guardDeclarationGenerator = new GuardDeclarationGenerator
    val actionInvocationGenerator  = new ActionInvocationGenerator
    val guardInvocationGenerator   = new GuardInvocationGenerator

    Map<State, String> stateNames
    val states = new ArrayList<State>

    new ()
    {
        stateEnumerators = newHashMap
        userActionFunctions = newHashMap
        userGuardFunctions = newHashMap
        actionChainFunctions = newHashMap
        choicePointFunctions = newHashMap
        junctionPointFunctions = newHashMap
        stateFunctions = newHashMap
        stateNames = newHashMap
    }

    /**
     * This is the main method of the transformation. It performs the
     * transformation by invoking methods that generate each part of the
     * target language model.
     */
    override boolean transformInPlace
    (
        StateMachine stateMachine,
        TransformationContext context
    )
    {
        setup( stateMachine, context )
        if ( !(context instanceof CppGenerationTransformationContext) )
            return false
        if (stateMachine.top === null) return true
        this.discardedStates = (context as CppGenerationTransformationContext).discardedStates
        this.cpp = (context as CppGenerationTransformationContext).cpp
        this.capsuleContext = (context as CppGenerationTransformationContext).capsuleContext
        this.flattener = (context as CppGenerationTransformationContext).flattener
        machine = stateMachine
        try
        {
            cppCapsuleClass =
                cpp.getCppClass( CppCodePattern.Output.CapsuleClass, capsuleContext )   
          generateStatesDeclaration    
//          generateHistoryTableDeclaration
            saveHistoryFunction = getSaveHistoryFunction
            checkHistoryFunction = getCheckHistoryFunction
            updateStateFunction = getUpdateStateFunction
            generateAllUserActionFunctions
            generateAllUserGuardFunctions
            generateAllActionChainFunctions
            generateAllChoicePointFunctions
            generateAllJunctionFunctions
            generateInitializeFunc
            generateTransitionTableCalls
            generateCompilationUnit
            return true
        }
        catch (Exception e)
        {
            CodeGenPlugin.error("[FlatModel2Cpp] error generating C++ code from flat state machine", e)
            return false
        }
    }

    protected def
    create new Variable( cppCapsuleClass.getType().ptr(), "this" )
    getThisRef()
    {
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
    protected def generateStatesDeclaration()
    {
        val numSubStates = machine.allSubstates.size
        statesDeclaration = new CppEnum( GlobalConstants.STATE_TYPE_NAME )

        val comparator = new VertexNameComparator
        // Add enumerators for each of the former composite states
        var State[] formerCompositeStates = newArrayOfSize( discardedStates.size )
        discardedStates.toArray( formerCompositeStates )
        Arrays.sort( formerCompositeStates, comparator )
        for (s : formerCompositeStates)
        {
            val name = s.cachedFullSMName.makeValidCName
            val stateEnum = new Enumerator( name )
            statesDeclaration.add( stateEnum )
            stateEnumerators.put( s, stateEnum )
            stateNames.put( s, name )
            states.add( s )
        }
        // Add enumerators for the remaining states
        var State[] otherStates = newArrayOfSize( numSubStates )
        machine.allSubstates.toList.toArray( otherStates )
        Arrays.sort( otherStates, comparator )
        for (s : otherStates)
        {
            val name = s.cachedFullSMName.makeValidCName
            val stateEnum = new Enumerator( name )
            statesDeclaration.add( stateEnum )
            stateEnumerators.put( s, stateEnum )
            stateNames.put( s, name )
            states.add( s )
        }
        // Add an enumerator for the TOP state next to last
        val topStateEnum = new Enumerator( GlobalConstants.TOP )
        statesDeclaration.add( topStateEnum )
        stateEnumerators.put( machine.top, topStateEnum )
        // Add an enumerator for the dummy UNVISITED state last
        val unvisitedStateEnum = new Enumerator( GlobalConstants.UNVISITED )
        statesDeclaration.add( unvisitedStateEnum )
        stateEnumerators.put( StateNestingFlattener.UNVISITED, unvisitedStateEnum )
    }

    protected def generateStateNamesTableDeclaration()
    {
        val numStates = stateNames.size

        val stateNamesType = PrimitiveType.CHAR.ptr().const_().arrayOf( new IntegralLiteral( numStates + 2 ))
        val stateNamesInitializer = new BlockInitializer( stateNamesType )

        for (name : stateNames.values)
        {
            stateNamesInitializer.addExpression( new StringLiteral( name ) )
        }
        stateNamesTableDeclaration =
            new MemberField
            (
                stateNamesType,
                GlobalConstants.STATE_NAMES_TABLE_NAME,
                stateNamesInitializer
            )
        val ctor =
            cpp.getConstructor
            (
                CppCodePattern.Output.CapsuleClass,
                capsuleContext
            )
        for (s : states)
        {
            val stateName = stateNames.get( s )
            val stateEnum = stateEnumerators.get( s )
            val stateNameInitStmt = makeStateNamesTableEntry( stateEnum, stateName )
            ctor.add( stateNameInitStmt )
        }
        addTopNameInitStmt( ctor )
        addUnvisitedNameInitStmt( ctor )
    }

    protected def generateTransitionTableCalls()
    {
        val ctor =
            cpp.getConstructor
            (
                CppCodePattern.Output.CapsuleClass,
                capsuleContext
            )
            
        for(s : machine.allSubstates)
        {
            val stateName = s.cachedFullSMName
            ctor.add(UMLRTTransitionTable.addState(UMLRTCapsule.transitionTable(), 
            		new NewExpr(UMLRTState.Ctor(new StringLiteral(stateName)))
            ))
        }
       
        ctor.add(UMLRTTransitionTable.addState(UMLRTCapsule.transitionTable(), 
        		new NewExpr(UMLRTState.Ctor(new StringLiteral("top"))) //[FIXME] should be GlobalConstants.TOP_STATE_NAME
        ));
        ctor.add(UMLRTTransitionTable.addState(UMLRTCapsule.transitionTable(), 
        		new NewExpr(UMLRTState.Ctor(new StringLiteral(GlobalConstants.UNVISITED_STATE_NAME)))
        ));
        ctor.add(UMLRTTransitionTable.addState(UMLRTCapsule.transitionTable(), 
        		new NewExpr(UMLRTState.Ctor(new StringLiteral(GlobalConstants.CHOICE_POINT_LABEL)))
        ));
        
         for(s : machine.allSubstates)
         {
	        val table = getPortSignalTransitionsTable( s )
	        for (port : table.keySet)
	        {
	            val portSignals = table.get( port )
	            var LinkedHashSet<Transition> anyEventTransitions = null
	            for (signal : portSignals.keySet)
	            {
	                val transitions = portSignals.get( signal )
	                if (signal instanceof AnyEvent)
	                {
	                    anyEventTransitions = transitions
	                }
	                else
	                {
	               		val trigger = UMLRTTrigger.Ctor(new StringLiteral(port.name), new StringLiteral(signal.name))
	               		val fromState = UMLRTTransitionTable.getState(UMLRTCapsule.transitionTable, new StringLiteral(s.cachedFullSMName))
	               		var toState = UMLRTTransitionTable.getState(UMLRTCapsule.transitionTable, new StringLiteral(transitions.get(0).targetState.cachedFullSMName))
	               		
	               		val guard = userGuardFunctions.getOrDefault(transitions.get(0).guard, null)
	               		val action = actionChainFunctions.getOrDefault(transitions.get(0).actionChain, null)
	               		
	               		val guardCall = if (guard !== null) new CastExpr(UMLRTGuard.type, new MemberFunctionAddress(cppCapsuleClass, guard)) else StandardLibrary.NULL()
	               		val actionCall = if (action !== null) new CastExpr(UMLRTAction.type, new MemberFunctionAddress(cppCapsuleClass, action)) else StandardLibrary.NULL()
	               		
	               		var choiceCall =  StandardLibrary.NULL();
	               		if(transitions.get(0).targetVertex instanceof ChoicePoint)
               			{
               				val choice = choicePointFunctions.getOrDefault(transitions.get(0).targetVertex, null)
               				choiceCall = new CastExpr(UMLRTChoice.type, new MemberFunctionAddress(cppCapsuleClass, choice))
               				toState = UMLRTTransitionTable.getState(UMLRTCapsule.transitionTable, new StringLiteral(GlobalConstants.CHOICE_POINT_LABEL));
               			}
	               		
	               		val transition = UMLRTTransition.Ctor(new StringLiteral(transitions.get(0).name.makeValidCName), fromState, toState, choiceCall as Expression, guardCall as Expression, actionCall as Expression)
               			ctor.add(UMLRTTransitionTable.addTransition(UMLRTCapsule.transitionTable(), fromState, new NewExpr(trigger), new NewExpr(transition)))
	                }
	            }
	        }
         }
         
     	for(s : discardedStates)
        {
            val stateName = s.cachedFullSMName
            ctor.add(UMLRTHistoryMap.put(
                		UMLRTCapsule.historyMap(), 
                		new StringLiteral(stateName), 
                		new StringLiteral("SPECIAL_INTERNAL_STATE_UNVISITED")))
        }
    }
    
    protected def addTopNameInitStmt( Constructor ctor )
    {
        val topName = GlobalConstants.TOP_STATE_NAME
        val topEnum = stateEnumerators.get( machine.top )
        val topNameInitStmt = makeStateNamesTableEntry( topEnum, topName )
        ctor.add( topNameInitStmt )
    }

    protected def addUnvisitedNameInitStmt( Constructor ctor )
    {
        val unvisitedName = GlobalConstants.UNVISITED_STATE_NAME
        val unvisitedEnum = stateEnumerators.get( StateNestingFlattener.UNVISITED )
        val unvisitedNameInitStmt = makeStateNamesTableEntry( unvisitedEnum, unvisitedName )
        ctor.add( unvisitedNameInitStmt )
    }

    protected def makeStateNamesTableEntry( Enumerator stateEnumerator, String stateName )
    {
        new ExpressionStatement
        (
            new BinaryOperation
            (
                new IndexExpr
                (
                    new ElementAccess( stateNamesTableDeclaration ),
                    new ElementAccess( stateEnumerator )
                ),
                BinaryOperation.Operator.ASSIGN,
                new StringLiteral( stateName )
            )
        )
    }

    /**
     * Generates a declaration for the history table for the state machine.
     *
     * The generated code would be something like:
     *
     * <p>
     * <code>State[] history = { UNDEFINED, ..., UNDEFINED };
     */
    protected def generateHistoryTableDeclaration()
    {
        val numStates = discardedStates.size
        if (numStates > 0)
        {
            val historyTableType =
                statesDeclaration.getType().arrayOf( new IntegralLiteral(numStates) )
            val tableDecl =
                new MemberField
                (
                    historyTableType,
                    GlobalConstants.HISTORY_TABLE_NAME// , initializer
                )
            val counter =
                new Variable
                (
                    LinkageSpec.UNSPECIFIED,
                    PrimitiveType.INT,
                    "i",
                    new IntegralLiteral(0)
                )
            val counterDecl = new VariableDeclarationStatement( counter )
            val historyInitLoopStmt =
                new WhileStatement
                (
                    new LogicalComparison(
                        new ElementAccess( counter ),
                        LogicalComparison.Operator.LESS_THAN,
                        new IntegralLiteral( numStates )
                    )
                )
            val historyEntryInitStmt =
                new ExpressionStatement
                (
                    new BinaryOperation
                    (
                        new IndexExpr
                        (
                            new ElementAccess( tableDecl ),
                            new UnaryOperation
                            (
                                UnaryOperation.Operator.POST_INCREMENT,
                                new ElementAccess( counter )
                            )
                        ),
                        BinaryOperation.Operator.ASSIGN,
                        new ElementAccess
                        (
                            stateEnumerators.get( StateNestingFlattener.UNVISITED )
                        )
                    )
                )
            historyInitLoopStmt.add( historyEntryInitStmt )
            val ctor =
                cpp.getConstructor
                (
                    CppCodePattern.Output.CapsuleClass,
                    capsuleContext
                )
            ctor.add( counterDecl )
            ctor.add( historyInitLoopStmt )
            historyTableDeclaration = tableDecl
        }
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
    protected def create
            new MemberFunction
            (
                PrimitiveType.VOID,
                GlobalConstants.SAVE_HISTORY_FUNC_NAME
            )
    getSaveHistoryFunction()
    {
        if (it == null) return
        val param1 = new Parameter( PrimitiveType.CHAR.ptr().const_ , "compositeState" )
        val param2 = new Parameter( PrimitiveType.CHAR.ptr().const_ , "subState" )
        // Body is "history[compositeState] = subState;"
        val body = UMLRTHistoryMap.put(
                		UMLRTCapsule.historyMap(), new ElementAccess(param1), new ElementAccess(param2))
        add( param1 )
        add( param2 )
        add( body )
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
    protected def create
            new MemberFunction
            (
                PrimitiveType.BOOL,
                GlobalConstants.CHECK_HISTORY_FUNC_NAME
            )
    getCheckHistoryFunction()
    {
        if (it == null) return
        val param1 = new Parameter( PrimitiveType.CHAR.ptr().const_, "compositeState" )
        val param2 = new Parameter( PrimitiveType.CHAR.ptr().const_, "subState" )
        // Body is "history[compositeState] = subState;"
        val body =
            new ReturnStatement
            (
                new LogicalComparison
                (	
                	StandardLibrary.strcmp(UMLRTHistoryMap.get(
                		UMLRTCapsule.historyMap(), new ElementAccess(param1)), new ElementAccess(param2)),
                	LogicalComparison.Operator.EQUIVALENT,
                	new IntegralLiteral(0)
                )
            )
        add( param1 )
        add( param2 )
        add( body )
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
    protected def create
        new MemberFunction
        (
            PrimitiveType.VOID,
            GlobalConstants.UPDATE_STATE_FUNC_NAME
        )
    getUpdateStateFunction()
    {
        if (it == null) return
        val param = new Parameter( PrimitiveType.CHAR.ptr().const_ , "newState" )

        val body =
            new ExpressionStatement
            (
                new BinaryOperation
                (
                    UMLRTCapsule.currentState(),
                    BinaryOperation.Operator.ASSIGN,
                    UMLRTTransitionTable.getState(
                    		UMLRTCapsule.transitionTable(), new ElementAccess(param))
                )
            )
        add( param )
        add( body )
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
     protected def generateAllUserActionFunctions()
     {
        for (t : machine.allTransitions)
        {
            if (t.actionChain !== null && t.actionChain.actions !== null)
            {
                for (a : t.actionChain.actions)
                {
                    // The same action may occur in several transition chains but
                    // the function declaration is generated only once, as the
                    // visitor that performs the generation memoizes its results,
                    // so that subsequent invocations of the following method will
                    // yield the same function declaration object.
                    generateActionFunc( a, t )
                }
            }
        }
     }

    /**
     * Generates the function declaration for an action occurring in
     * a transition's chain.
     */
    protected dispatch def void generateActionFunc( SaveHistory a, Transition t )
    {
        // This is superfluous since this function is generated by
        // {@link generateSaveHistoryFunction}, but we need to have a function
        // that accepts {@link SaveHistory} actions when processing chains.
        actionDeclarationGenerator.visit( a, null )
    }

    protected dispatch def void generateActionFunc( UpdateState a, Transition t )
    {
        // This is superfluous since this function is generated by
        // {@link generateUpsateStateFunction}, but we need to have a function
        // that accepts {@link Update} actions when processing chains.
        actionDeclarationGenerator.visit( a, null )
    }

    protected dispatch def void generateActionFunc( ActionCode a, Transition t )
    {
        if (userActionFunctions.containsKey( a ))
            return
        val ctx =
            new ActionDeclarationGenerator.UserActionContext
            (
                RTMessageType,
                getTriggerParams( t ),
                (cpp.translator as UML2xtumlrtModelTranslator)?.stateMachineTranslator,
                flattener,
                capsuleContext
            )
            as ActionDeclarationGenerator.Context
        val f = actionDeclarationGenerator.visit( a, ctx )
        userActionFunctions.put( a, f )
    }

    protected dispatch def void generateActionFunc( ActionReference a, Transition t )
    {
        if (a !== null && a.target !== null)
            generateActionFunc( a.target, t )
    }

    /**
     * Generates declarations for guard functions for guards occurring in
     * a transition's chain.
     *
     * <p><b>Notes:</b>
     * <p>The "check history" action is not generated by this function, as
     * there is only one declaration for the check history guard.
     */
     protected def generateAllUserGuardFunctions()
     {
        for (t : machine.allTransitions)
        {
            if (t.guard !== null)
            {
                generateGuardFunc( t.guard, t )
            }
            for (trigger : t.triggers.filter(RTTrigger))
            {
                if (trigger.triggerGuard !== null)
                {
                    generateGuardFunc( trigger.triggerGuard, t )
                }
            }
        }
     }

    /**
     * Generates the function declaration for an action occurring in
     * a transition's chain.
     */
    protected def dispatch generateGuardFunc( CheckHistory g, Transition t )
    {
        // This is superfluous since this function is generated by
        // {@link generateSaveHistoryFunction}, but we need to have a function
        // that accepts {@link CheckHistory} actions when processing chains.
        // guardDeclarationGenerator.visit( g, null )
    }

	protected def dispatch generateGuardFunc( Guard g, Transition t )
    {
        if (g.body !== null && !(g.body instanceof CheckHistory))
        {
            var sourceTransition = getTransitionChainUniqueSource( t )
            sourceTransition = if (sourceTransition === null) t else sourceTransition
        	val ctx =
	            new GuardDeclarationGenerator.UserGuardContext
	            (
	                RTMessageType,
	                getTriggerParams( sourceTransition ),
	                (cpp.translator as UML2xtumlrtModelTranslator)?.stateMachineTranslator,
	                flattener,
	                capsuleContext
	            )
	            as GuardDeclarationGenerator.Context
            val f = guardDeclarationGenerator.visit( g, ctx )
            userGuardFunctions.put( g, f )
        }
    }

    /**
     * Generate function declarations for transition action chains.
     *
     * <p>Each function generated will have a sequence of calls, invoking either
     * the action functions generated by {@link generateActionFunc} for
     * transition, state entry and state exit actions, as well as invoking
     * "save history" actions produced by the flattening transformation.
     */
    protected def generateAllActionChainFunctions()
    {
        for (t : machine.allTransitions)
        {
            if (t.actionChain !== null && !t.actionChain.actions.empty)
            {
                val f = generateActionChainFunc( t )
                actionChainFunctions.put( t.actionChain, f )
            }
        }
    }

    /**
     * Generates the function declaration for a single action chain.
     */
    protected def
    create
        new MemberFunction( PrimitiveType.VOID, t.actionChain.funcName.toString )
    generateActionChainFunc( Transition t )
    {
        val param = new Parameter( RTMessageType, GlobalConstants.CHAIN_FUNC_PARAM )
        add( param )
        if (t.actionChain !== null)
        {
            for (AbstractAction a : t.actionChain.actions)
            {
                val call = getActionInvocation( a , param )
                if (call !== null) add( call as ExpressionStatement )
            }
        }
    }

    /**
     * Generates a call to an action, either user action or action generated by
     * the transformation.
     */
    protected dispatch def ExpressionStatement getActionInvocation( SaveHistory action, Parameter param )
    {
        val ctx =
            new ActionInvocationGenerator.SaveHistoryActionContext
            (
                saveHistoryFunction,
                stateEnumerators
            )
            as ActionInvocationGenerator.Context
        actionInvocationGenerator.visit( action, ctx )
    }

    protected dispatch def ExpressionStatement getActionInvocation( UpdateState action, Parameter param )
    {
        val ctx =
            new ActionInvocationGenerator.UpdateStateActionContext
            (
                updateStateFunction,
                stateEnumerators
            )
            as ActionInvocationGenerator.Context
        actionInvocationGenerator.visit( action, ctx )
    }

    protected dispatch def ExpressionStatement getActionInvocation( ActionCode action, Parameter param )
    {
        val funcDecl = userActionFunctions.get( action )
        val ctx =
            new ActionInvocationGenerator.UserActionContext
            (
                funcDecl,
                new ElementAccess( param )
            )
            as ActionInvocationGenerator.Context
        actionInvocationGenerator.visit( action, ctx )
    }

    protected dispatch def ExpressionStatement getActionInvocation( ActionReference action, Parameter param )
    {
        if (action !== null && action.target !== null)
            getActionInvocation( action.target, param )
    }

    /**
     * Generates all functions corresponding to choice points.
     */
    protected def generateAllChoicePointFunctions()
    {
        for (c : machine.allChoicePoints)
        {
            val f = getChoicePointFunction( c )
            choicePointFunctions.put( c, f )
        }
    }

    /**
     * Generates the function corresponding to a given choice point.
     */
    protected def
    create
        new MemberFunction( UMLRTState.getType().ptr(), p.funcName.toString )
    getChoicePointFunction( ChoicePoint p )
    {
        val param = new Parameter( RTMessageType, GlobalConstants.CHAIN_FUNC_PARAM )
        val defRet = new ReturnStatement( UMLRTCapsule.currentState() )
        val cond = new ConditionalStatement()
        var elseBranch = false
        var ignoreBranch = false
        for (t : p.directOutgoingTransitions)
        {
            var CodeBlock newBranch = null
            ignoreBranch = false
            if (t.guard !== null)
            {
                var guard = getGuardInvocation( t.guard, param )
                newBranch = cond.add( guard )
            }
            else if (!elseBranch)
            {
                // If there is a transition without a guard, we chose it as
                // the default branch
                newBranch = cond.defaultBlock
                elseBranch = true
            }
            else
                // If there is more than one transition without a branch, we
                // chose the first and ignore the rest
                ignoreBranch = true
            if (!ignoreBranch)
            {
                if (t.actionChain !== null && !t.actionChain.actions.empty)
                {
                    val actFunc = actionChainFunctions.get( t.actionChain )
                    val arg = new ElementAccess( param )
                    val call = new FunctionCall( actFunc, arg )
                    newBranch.add( new ExpressionStatement( call ) )
                }
                var destStmt = getDestination( t, false, new ElementAccess(param) )
                newBranch.add(destStmt)
            }
        }
        add( param )
        add( cond )
        add( defRet )   // If no guard is chosen, we remain the current state
    }

    protected def getGuardInvocation( Guard guard, Parameter param )
    {
        if (guard.body !== null && guard.body instanceof CheckHistory)
            getCheckHistoryGuardInvocation( guard.body as CheckHistory, param )
        else
            getOtherGuardInvocation( guard, param )
    }

    protected def getCheckHistoryGuardInvocation( CheckHistory guard, Parameter param )
    {
        val ctx =
            new GuardInvocationGenerator.CheckHistoryGuardContext
            (
                checkHistoryFunction,
                stateEnumerators
            )
            as GuardInvocationGenerator.Context
        guardInvocationGenerator.visit( guard, ctx )
    }

    protected def getOtherGuardInvocation( Guard guard, Parameter param )
    {
        val funcDecl = userGuardFunctions.get( guard )
        val ctx =
            new GuardInvocationGenerator.UserGuardContext
            (
                funcDecl,
                new ElementAccess(param)
            )
            as GuardInvocationGenerator.Context
        guardInvocationGenerator.visit( guard, ctx )
    }

    /**
     * Generates all functions corresponding to junction points.
     */
    protected def generateAllJunctionFunctions()
    {
        for (j : machine.allJunctionPoints)
        {
            val f = getJunctionPointFunction( j )
            junctionPointFunctions.put( j, f )
        }
    }

    /**
     * Generates the function corresponding to a specific junction point.
     */
    protected def
    create
        new MemberFunction( UMLRTState.getType().ptr(), p.funcName.toString )
    getJunctionPointFunction( JunctionPoint p )
    {
        val param = new Parameter( RTMessageType, GlobalConstants.JUNC_FUNC_PARAM )
        val t = p.directOutgoingTransitions.get(0) // There should be only one. // TODO: add warning if not
        val block = new ArrayList<Statement>
        if (t.actionChain !== null && !t.actionChain.actions.empty)
        {
            val actFunc = actionChainFunctions.get( t.actionChain )
            val arg = new ElementAccess( param )
            val call = new FunctionCall( actFunc, arg )
            block.add( new ExpressionStatement(call) )
        }
        val d = getDestination( t, false, new ElementAccess(param) )
        block.add( d )
        add( param )
        add( block )
    }

    /**
     * Generates all functions corresponding to states.
     */
    protected def generateAllStateFunctions()
    {
        for (s : machine.allSubstates)
        {
            val f = generateStateFunc( s )
            stateFunctions.put( s, f )
        }
    }

    /**
     * Generates the function corresponding to a given state.
     */
    protected def
    create
        new MemberFunction( statesDeclaration.type, state.funcName.toString )
    generateStateFunc( State state )
    {
        val param = new Parameter( RTMessageType, GlobalConstants.STATE_FUNC_PARAM )
        val table = getPortSignalTransitionsTable( state )
        val defRet = new ReturnStatement( UMLRTCapsule.currentState() )
        val portSwitch = new SwitchStatement( getPortCond(param) )
        for (port : table.keySet)
        {
            val sigSwitch = new SwitchStatement( getSigCond( param ) )
            val portSignals = table.get( port )
            var LinkedHashSet<Transition> anyEventTransitions = null
            for (signal : portSignals.keySet)
            {
                val transitions = portSignals.get( signal )
                if (signal instanceof AnyEvent)
                {
                    anyEventTransitions = transitions
                }
                else
                {
                    val sigCase = generatePortSignalCase( transitions, signal, param, defRet )
                    sigSwitch.add( sigCase )
                }
            }
            val defaultCase = generateDefaultCase( anyEventTransitions, param, defRet  )
            sigSwitch.add( defaultCase )
            val portCase = new SwitchClause( enumeratorFor( port ) )
            portCase.add( sigSwitch )
            portCase.add( defRet )
            portSwitch.add( portCase )
        }
        val defaultCase = generateDefaultCase( null, null, null )
        portSwitch.add( defaultCase )
        val body = portSwitch
        add( param )
        add( body )
        add( defRet )
    }

    protected def generateDefaultCase
    (
        LinkedHashSet<Transition> anyEventTransitions,
        Parameter param,
        ReturnStatement defRet
    )
    {
        var SwitchClause defaultClause = null
        if (anyEventTransitions === null)
        {
            defaultClause = new SwitchClause()
            val callStmt =
                new ExpressionStatement
                (
                    UMLRTCapsule.unexpectedMessage( new ElementAccess(thisRef) )
                )
            defaultClause.add( callStmt )
        }
        else
        {
            defaultClause = generatePortSignalCase( anyEventTransitions, null, param, defRet )
        }
        defaultClause
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
    protected def getPortSignalTransitionsTable( State s )
    {
        val table = newLinkedHashMap
        for (transition : s.allOutgoingTransitions)
        {
            for (trigger : transition.triggers)
            {
                if (trigger instanceof RTTrigger)
                {
                    val signal = trigger.signal
                    for (port : trigger.ports)
                    {
                        if (!table.containsKey(port))
                        {
                            table.put( port, new LinkedHashMap<Signal, LinkedHashSet<Transition>> )
                        }
                        val portSignals = table.get( port )
                        if (!portSignals.containsKey( signal ))
                        {
                            portSignals.put( signal, newLinkedHashSet )
                        }
                        portSignals.get( signal ).add( transition )
                    }
                }
            }
        }
        return table
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
    protected def generatePortSignalCase
    (
        LinkedHashSet<Transition> transitions,
        Signal signal,
        Parameter param,
        ReturnStatement defRet
    )
    {
        val sigCase =
            if (signal === null || signal instanceof AnyEvent) new SwitchClause()
            else new SwitchClause( enumeratorFor(signal) )
        val sortedTransitions = sortTransitions( transitions )
        for (transition : sortedTransitions)
        {
            val actionChainCall = generateActionChainAndDestinationCall( transition, param )
            for (trigger : transition.triggers.filter(RTTrigger).filter[it.signal instanceof AnyEvent || it.signal == signal])
            {
                var ArrayList<Statement> stmts = actionChainCall
                if (transition.guard !== null)
                {
                    stmts = generateGuardConditional( transition.guard, param, stmts )
                }
                if (trigger.triggerGuard !== null)
                {
                    stmts = generateGuardConditional( trigger.triggerGuard, param, stmts )
                }
                for (stmt : stmts)
                {
                    sigCase.add( stmt )
                }
            }
        }
        if (sortedTransitions.empty)
        {
            sigCase.add( defRet )
        }
        return sigCase
    }

    protected def generateActionChainAndDestinationCall( Transition transition, Parameter param )
    {
        val sequence = new ArrayList<Statement>
        val call = getActionChainInvocation( transition, param )
        val dest = getDestination( transition, false, new ElementAccess(param) )
        if (call !== null) sequence.add( call )
        sequence.add( dest )
        sequence
    }

    protected def getActionChainInvocation( Transition transition, Parameter param )
    {
        if (transition.actionChain !== null && !transition.actionChain.actions.empty)
        {
            val actFunc = actionChainFunctions.get( transition.actionChain )
            val arg = new ElementAccess( param )
            val call = new FunctionCall( actFunc, arg )
            val callStmt = new ExpressionStatement( call )
            callStmt
        }
    }

    protected def generateGuardConditional( Guard guard, Parameter param, List<Statement> body )
    {
        val stmts = new ArrayList<Statement>
        val guardCond = new ConditionalStatement
        val guardCall = getGuardInvocation( guard, param )
        val guardBlock = guardCond.add( guardCall )
        guardBlock.add( body )
        stmts.add( guardCond )
        stmts
    }

    /**
     * Sorts transitions for the same port/signal pair by order of nesting
     * depth, marking the first non-guarded transition as the default transition
     * and filters-out all other non-guarded transitions.
     */
    protected def sortTransitions( LinkedHashSet<Transition> transitions )
    {
        var Transition[] transitionsArray = newArrayOfSize( transitions.size )
        transitions.toArray( transitionsArray )
        transitionsArray = transitionsArray.sortWith( new TransitionDepthComparator )
        val sortedList = newArrayList
        var Transition defaultTransition = null
        var i = 0
        while (i < transitionsArray.length)
        {
            val t = transitionsArray.get( i )
            val hasNonEmptyGuard = t.hasNonEmptyGuard
            if (hasNonEmptyGuard)
            {
                sortedList.add( t )
            }
            else if (defaultTransition === null)
            {
                defaultTransition = t
            }
            i++
        }
        if (defaultTransition !== null)
            sortedList.add( defaultTransition )
        return sortedList
    }

    private static def boolean hasNonEmptyGuard( Transition t )
    {
        if (t.guard !== null)
        {
            val r = resolveActionReference( t.guard.body )
            if (r !== null && r instanceof ActionCode)
            {
                val src = (r as ActionCode).source
                if (src !== null && !src.empty)
                {
                    return true
                }
            }
        }
        for (trigger : t.triggers.filter(RTTrigger))
        {
            val g = trigger.triggerGuard
            if (g !== null)
            {
                val r1 = resolveActionReference( g.body )
                if (r1 !== null && r1 instanceof ActionCode)
                {
                    val src1 = (r1 as ActionCode).source
                    if (src1 !== null && !src1.empty)
                    {
                        return true
                    }
                }
            }
        }
        return false
    }

    private static def AbstractAction resolveActionReference( AbstractAction a )
    {
        if (a !== null && a instanceof ActionReference)
            resolveActionReference( (a as ActionReference).target )
        a
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
    protected def getDestination( Transition t, boolean init, Expression destArg )
    {
        var Expression retVal
        if (t.targetVertex instanceof ChoicePoint)
        {
            val c = t.targetVertex as ChoicePoint
            val func = getChoicePointFunction( c )
            retVal = new FunctionCall( func, destArg )
        }
        else if (t.targetVertex instanceof JunctionPoint)
        {
            val j = t.targetVertex as JunctionPoint
            val func = getJunctionPointFunction( j )
            retVal = new FunctionCall(func, destArg)
        }
        else
        {
            val s = t.targetVertex as State
            retVal = UMLRTTransitionTable.getState(UMLRTCapsule.transitionTable(), new StringLiteral(s.name))
        }
        if (init)
        {
            new ExpressionStatement
            (
                new BinaryOperation
                (
                    UMLRTCapsule.currentState(),
                    BinaryOperation.Operator.ASSIGN,
                    retVal
                )
            )
        }
        else
        {
            new ReturnStatement( retVal )
        }
    }

    protected def generateMsgFieldInit( Parameter param, MemberFunction func )
    {
        val msgAssn =
            new ExpressionStatement
            (
                new BinaryOperation
                (
                    UMLRTCapsule.msg,
                    BinaryOperation.Operator.ASSIGN,
                    new AddressOfExpr( new ElementAccess( param ) )
                )
            )
        func.add( msgAssn )
    }

    /**
     * Generates the parameter for the main 'inject' function
     */
    protected  def getInjectFuncParam()
    {
        if (injectFuncParam === null)
            injectFuncParam =
                new Parameter
                (
                    getInjectRTMessageType,
                    GlobalConstants.INJECT_FUNC_PARAM
                )
        return injectFuncParam
    }

    protected def generateDefaultStateCase()
    {
        val defaultClause = new SwitchClause()
        val defStmt = new BreakStatement()
        defaultClause.add( defStmt )
        defaultClause
    }

    protected def getInitializeFuncParam()
    {
        if (initializeFuncParam === null)
            initializeFuncParam =
                new Parameter
                (
                    getInjectRTMessageType,
                    GlobalConstants.INITIALIZE_FUNC_PARAM
                )
        return initializeFuncParam
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
    protected def generateInitializeFunc()
    {
        initializeFunc =
            new MemberFunction
            (
                PrimitiveType.VOID,
                GlobalConstants.INITIALIZE_FUNC_NAME
            )
        initializeFunc.setVirtual()
        val param = getInitializeFuncParam
        initializeFunc.add( param )
        generateMsgFieldInit( param, initializeFunc )
        if (machine.top.initial !== null
            && !machine.top.initial.allDirectOutgoingTransitions.empty)
        {
            val initialTransition = machine.top.initial.directOutgoingTransitions.get(0)
            if (initialTransition.actionChain !== null
                && !initialTransition.actionChain.actions.empty)
            {
                val actFunc = actionChainFunctions.get( initialTransition.actionChain )
                val arg = new AddressOfExpr( new ElementAccess( getInitializeFuncParam ) )
                val call = new FunctionCall( actFunc, arg )
                initializeFunc.add( call )
            }
            val d = getDestination( initialTransition, true, new AddressOfExpr( new ElementAccess(param) ) )
            initializeFunc.add( d )
        }
    }

    protected def generateGetCurrentStateStrFunc()
    {
        getCurrentStateStringFunc =
            new MemberFunction
            (
                PrimitiveType.CHAR.ptr().const_(),
                GlobalConstants.GET_CURR_STATE_NAME_FUNC_NAME,
                CVQualifier.CONST
            )
        val body =
            new ReturnStatement
            (
            		new MemberAccess(UMLRTCapsule.currentState(), UMLRTState.nameField)
            )
        getCurrentStateStringFunc.add( body )
    }

    /**
     * Generates the compilation unit for the state machine (*)
     *
     * <p><b>Notes:</b> This implementation generates only a list of elements
     * to be consumed by the capsule generator which is assumed to be
     * responsible for putting together the full compilation unit.
     */
    protected def generateCompilationUnit()
    {
        cppCapsuleClass.addMember( CppClass.Visibility.PUBLIC, initializeFunc )
        cppCapsuleClass.addMember( CppClass.Visibility.PRIVATE, saveHistoryFunction )
        cppCapsuleClass.addMember( CppClass.Visibility.PRIVATE, checkHistoryFunction )
        
        cppCapsuleClass.addMember( CppClass.Visibility.PRIVATE, updateStateFunction )
        cppCapsuleClass.addPrivateMembers( userActionFunctions.values )
        cppCapsuleClass.addPrivateMembers( userGuardFunctions.values )
        cppCapsuleClass.addPrivateMembers( actionChainFunctions.values )
        cppCapsuleClass.addPrivateMembers( junctionPointFunctions.values )
        cppCapsuleClass.addPrivateMembers( choicePointFunctions.values )
    }

    private def addPrivateMembers( CppClass cppClass, Collection<MemberFunction> members )
    {
		var MemberFunction[] memArray = newArrayOfSize( members.size )
		members.toArray( memArray )
		val sortedMembers = memArray.sortWith( new CppNamedElementComparator )
		for (member : sortedMembers)
        {
			cppClass.addMember( CppClass.Visibility.PRIVATE, member )
		}
	}

    /**
     * Auxiliary methods
     */

    private dispatch def enumeratorFor( State s )
    {
        new ElementAccess( stateEnumerators.get(s) )
    }

    private dispatch def enumeratorFor( Port p )
    {
        val enumerator =
            cpp.getEnumerator( CppCodePattern.Output.PortId, p, capsuleContext )
        new ElementAccess( enumerator )
    }

    private dispatch def enumeratorFor( Signal s )
    {
        cpp.getEnumeratorAccess( CppCodePattern.Output.SignalId, s, null )
    }

    private def getStateType()
    {
        statesDeclaration.getType()
    }

    private def getRTMessageType()
    {
        UMLRTMessage.Element.getType().ptr().const_()
    }

    private def getInjectRTMessageType()
    {
        UMLRTMessage.Element.getType().ref().const_()
    }

    private def getTriggerParams( Transition t )
    {
        val SerializationManager.ParameterSet params = new SerializationManager.ParameterSet( cpp );
        for (trigger : t.triggers)
        {
            if (trigger instanceof RTTrigger)
                if(trigger.signal != null){
                    params.add( trigger.signal.parameters )
                }
        }
        params
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
    private def getPortCond( Parameter param )
    {
        val messagePortField = UMLRTMessage.destPort
        val roleIdField = UMLRTCommsPortRole.id
        new MemberAccess
        (
            UMLRTCommsPort.role
            (
                new MemberAccess
                (
                    new ElementAccess( param ),
                    messagePortField
                )
            ),
            roleIdField
        )
    }

    private def getSigCond( Parameter param )
    {
        UMLRTMessage.getSignalId(
            new ElementAccess( param )
        )
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
    private def getTransitionChainUniqueSource( Transition t )
    {
        val visited = new HashSet<Vertex>
        var vertex = t.sourceVertex
        var sourceTransition = t
        while (vertex instanceof Pseudostate
                && !(vertex as Pseudostate).directIncomingTransitions.isEmpty
                && !visited.contains( vertex ))
        {
            visited.add( vertex )
            sourceTransition = vertex.directIncomingTransitions.get(0)
            vertex = sourceTransition.sourceVertex
        }
        if (vertex instanceof State)
            return sourceTransition
        return null
    }

}
