/*******************************************************************************
 * Copyright (c) 2014-2017 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.cpp.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.AbstractElementGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern.Output;
import org.eclipse.papyrusrt.codegen.cpp.rts.UMLRTRuntime;
import org.eclipse.papyrusrt.codegen.cpp.structure.model.Controller;
import org.eclipse.papyrusrt.codegen.cpp.structure.model.ControllerAllocations;
import org.eclipse.papyrusrt.codegen.cpp.structure.model.Deployment;
import org.eclipse.papyrusrt.codegen.instance.model.ICapsuleInstance;
import org.eclipse.papyrusrt.codegen.instance.model.IPortInstance;
import org.eclipse.papyrusrt.codegen.lang.cpp.Expression;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppEnum;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.ElementList;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Enumerator;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.LinkageSpec;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.Variable;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.AddressOfExpr;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BlockInitializer;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.BooleanLiteral;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ConstructorCall;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.CppEnumOrderedInitializer;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.ElementAccess;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.IndexExpr;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.IntegralLiteral;
import org.eclipse.papyrusrt.codegen.lang.cpp.expr.StringLiteral;
import org.eclipse.papyrusrt.codegen.lang.cpp.external.StandardLibrary;
import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;
import org.eclipse.papyrusrt.xtumlrt.aexpr.uml.XTUMLRTBoundsEvaluator;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.common.CapsulePart;
import org.eclipse.papyrusrt.xtumlrt.util.QualifiedNames;
import org.eclipse.papyrusrt.xtumlrt.util.UndefinedValueException;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTExtensions;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil;

/**
 * Generated the static structure of the model.
 */
public final class CompositionGenerator extends AbstractElementGenerator {

	/** The Top {@link Capsule} model element. */
	private final org.eclipse.papyrusrt.xtumlrt.common.Capsule topCapsule;

	/** The {@link ControllerAllocations}. */
	private final ControllerAllocations controllerAllocations;

	/** The C++ {@link Variable} for the slots array. */
	private Variable slotsVar;

	/** The {@link Map} from {@link ICapsuleInstance}s to C++ {@link Enumerator}s. */
	private final Map<ICapsuleInstance, Enumerator> capsuleEnums = new HashMap<>();

	/** The {@link Map} from {@link ICapsuleInstance}s to C++ {@link Variable}s. */
	private final Map<ICapsuleInstance, Variable> capsuleTypeInstances = new HashMap<>();

	/** The {@link Map} from {@link ICapsuleInstance}s to their capsule border/internal ports array. */
	private final Map<ICapsuleInstance, Variable[]> capsulePortArrays = new HashMap<>();

	/** The {@link Map} from {@link ICapsuleInstance}s to their capsule border/internal ports array. */
	private final Map<ICapsuleInstance, VarWrapper[]> portArrays = new HashMap<>();

	/** The {@link Deployment}. */
	private Deployment deployment;

	/**
	 * Constructor.
	 *
	 * @param cpp
	 *            - The {@link CppCodePattern}.
	 * @param topCapsule
	 *            - The top {@link Capsule}.
	 * @param controllerAllocations
	 *            - The {@link ControllerAllocations}.
	 */
	private CompositionGenerator(CppCodePattern cpp, org.eclipse.papyrusrt.xtumlrt.common.Capsule topCapsule, ControllerAllocations controllerAllocations) {
		super(cpp);
		this.topCapsule = topCapsule;
		this.controllerAllocations = controllerAllocations;
	}

	@Override
	protected Output getOutputKind() {
		return Output.Deployment;
	}

	@Override
	public String getLabel() {
		return super.getLabel() + ' ' + topCapsule.getName();
	}

	@Override
	public boolean generate() {
		deployment = Deployment.build(cpp, topCapsule, controllerAllocations);
		if (deployment == null) {
			return false;
		}

		ElementList elements = cpp.getElementList(CppCodePattern.Output.Deployment, topCapsule);
		if (elements == null || !cpp.markWritable(elements)) {
			return false;
		}

		CppEnum capsuleIdEnum = generateCapsuleIdEnum(elements);

		CppEnumOrderedInitializer arrayInit = new CppEnumOrderedInitializer(capsuleIdEnum, UMLRTRuntime.UMLRTSlot.getType().arrayOf(null));
		for (Controller controller : deployment.getControllers()) {
			generateStaticStructureForController(controller, arrayInit, elements);
		}

		generateSlotsVar(arrayInit, elements);

		generateMain();

		return true;
	}

	/**
	 * Populate the full CapsuleId enum so that enumerators are assigned in a
	 * consistent order. When generating connections the far end capsule instance's
	 * enumerator is used. That access could change the generation order based
	 * on how the connectors are traversed.
	 * 
	 * @return capsuleIdEnum
	 *         The Cpp enum
	 */
	private CppEnum generateCapsuleIdEnum(ElementList elements) {
		CppEnum capsuleIdEnum = new CppEnum("CapsuleInstanceId");
		elements.addElement(capsuleIdEnum);
		for (Controller controller : deployment.getControllers()) {
			for (ICapsuleInstance capsule : controller.getCapsules()) {
				final boolean staticCapsuleInstance = !capsule.isDynamic();
				final boolean staticContainerCapsuleInstance = capsule.getContainer() != null && !capsule.getContainer().isDynamic();
				if (staticCapsuleInstance || staticContainerCapsuleInstance) {
					Enumerator enumerator = new Enumerator("InstId_" + capsule.getQualifiedName('_'));
					capsuleIdEnum.add(enumerator);
					capsuleEnums.put(capsule, enumerator);
				}
			}
		}
		return capsuleIdEnum;
	}

	/**
	 * @param controller
	 * @param elements
	 * @param arrayInit
	 */
	private void generateStaticStructureForController(Controller controller, CppEnumOrderedInitializer arrayInit, ElementList elements) {
		Variable controllerVar = generateControllerVar(controller, elements);
		generateExportedControllerVar(controller, controllerVar, elements);

		for (ICapsuleInstance capsule : controller.getCapsules()) {
			final boolean staticCapsuleInstance = !capsule.isDynamic();
			final boolean staticContainerCapsuleInstance = capsule.getContainer() != null && !capsule.getContainer().isDynamic();
			Variable borderPortArray = null;

			// Bug 515855: we generate static capsule instances only for capsules that are in a fixed-part chain from the top,
			// A fixed-part chain is a list of parts [p1, p2, ..., pn] where:
			// a) Each p_i is a fixed-part
			// b) p1 is a part in the top capsule
			// c) For each i, if p_i is of type C_i where C_i is some capsule, then p_{i+1} is a (fixed) part of C_i.
			// The capsule instance model (see {@link CapsuleInstance}) ensures that if a capsule instance is dynamic,
			// all its descendants (the transitive closure of its contained capsule instances) are also dynamic. So if a
			// capsule instance is not dynamic, it must be in a fixed part, and all its ancestors (the transitive closure
			// of its container capsule instances) are also not dynamic.
			if (staticCapsuleInstance || staticContainerCapsuleInstance) {
				borderPortArray = getPortArray(elements, capsule, true);
			}

			// The user capsule class is only created for statically allocated capsules
			if (staticCapsuleInstance) {
				generateStaticCapsuleInstance(capsule, borderPortArray, elements);
			}

			// Bug 515855: we generate the capsule part array and slot initializer only for static capsule instances
			// and for dynamic capsule instances whose container is static.
			// As per the previous comment, if the capsule instance is dynamic and its parent is not, then the parent's part
			// the last part in a fixed-part chain.
			if (staticCapsuleInstance || staticContainerCapsuleInstance) {
				Variable partArray = capsule.isDynamic() ? null : createCapsulePartArray(elements, capsule);
				if (partArray != null) {
					elements.addElement(partArray);
				}

				generateSlotInitializer(capsule, arrayInit, controllerVar, borderPortArray, partArray);
			}

		}
	}

	/**
	 * @param controller
	 * @param elements
	 * @return
	 */
	private Variable generateControllerVar(Controller controller, ElementList elements) {
		Variable controllerVar = new Variable(
				LinkageSpec.STATIC,
				UMLRTRuntime.UMLRTController.getType(),
				controller.getName() + "_",
				UMLRTRuntime.UMLRTController.Ctor(new StringLiteral(controller.getName())));
		elements.addElement(controllerVar);
		return controllerVar;
	}

	/**
	 * @param controller
	 * @param controllerVar
	 * @param elements
	 */
	private void generateExportedControllerVar(Controller controller, Variable controllerVar, ElementList elements) {
		Variable exportedControllerVar = new Variable(
				LinkageSpec.EXTERN,
				UMLRTRuntime.UMLRTController.getType().ptr(),
				controller.getName(),
				new AddressOfExpr(new ElementAccess(controllerVar)));
		elements.addElement(exportedControllerVar);
	}

	/**
	 * @param capsule
	 * @param borderPortArray
	 * @param elements
	 */
	private void generateStaticCapsuleInstance(ICapsuleInstance capsule, Variable borderPortArray, ElementList elements) {
		Variable borderPortPtrArray = generatePortPointerArray(borderPortArray, elements);
		Variable internalPortArray = getPortArray(elements, capsule, false);
		Variable internalPortPtrArray = generatePortPointerArray(internalPortArray, elements);

		ConstructorCall ctorCall = generateCapsuleConstructorCall(capsule, borderPortPtrArray, internalPortPtrArray);

		generateCapsuleInstanceVar(capsule, ctorCall, elements);
	}

	/**
	 * @param borderPortArray
	 * @param elements
	 * @return
	 */
	private Variable generatePortPointerArray(Variable borderPortArray, ElementList elements) {
		Variable borderPortPtrArray = null;
		if (borderPortArray != null) {
			borderPortPtrArray = createCapsulePortPointerArray(borderPortArray);
			if (borderPortPtrArray != null) {
				elements.addElement(borderPortPtrArray);
			}
		}
		return borderPortPtrArray;
	}

	/**
	 * @param capsule
	 * @param borderPortPtrArray
	 * @param internalPortPtrArray
	 * @return
	 */
	private ConstructorCall generateCapsuleConstructorCall(ICapsuleInstance capsule, Variable borderPortPtrArray, Variable internalPortPtrArray) {
		ConstructorCall ctorCall = new ConstructorCall(cpp.getConstructor(CppCodePattern.Output.CapsuleClass, capsule.getType()));
		ctorCall.addArgument(
				new AddressOfExpr(
						new ElementAccess(cpp.getVariable(CppCodePattern.Output.UMLRTCapsuleClass, capsule.getType()))));
		ctorCall.addArgument(new AddressOfExpr(getSlotAccess(capsule)));
		ctorCall.addArgument(borderPortPtrArray == null ? StandardLibrary.NULL() : new ElementAccess(borderPortPtrArray));
		ctorCall.addArgument(internalPortPtrArray == null ? StandardLibrary.NULL() : new ElementAccess(internalPortPtrArray));
		ctorCall.addArgument(new BooleanLiteral(!capsule.isDynamic()));
		return ctorCall;
	}

	/**
	 * @param capsule
	 * @param ctorCall
	 * @param elements
	 */
	private void generateCapsuleInstanceVar(ICapsuleInstance capsule, ConstructorCall ctorCall, ElementList elements) {
		char[] instNameChars = capsule.getQualifiedName('_').toCharArray();
		String instName = null;
		if (instNameChars.length <= 0) {
			throw new RuntimeException("invalid attempt to generate code for unnamed Capsule");
		} else if (Character.isLowerCase(instNameChars[0])) {
			instName = new String(instNameChars) + '_';
		} else {
			instNameChars[0] = Character.toLowerCase(instNameChars[0]);
			instName = new String(instNameChars);
		}

		Variable instVar = new Variable(
				LinkageSpec.STATIC,
				cpp.getCppClass(CppCodePattern.Output.CapsuleClass, capsule.getType()).getType(),
				instName,
				ctorCall);
		elements.addElement(instVar);
		capsuleTypeInstances.put(capsule, instVar);
	}

	/**
	 * @param capsule
	 * @param arrayInit
	 * @param controllerVar
	 * @param borderPortArray
	 * @param partArray
	 */
	private void generateSlotInitializer(ICapsuleInstance capsule, CppEnumOrderedInitializer arrayInit, Variable controllerVar, Variable borderPortArray, Variable partArray) {
		Variable userClassVar = capsuleTypeInstances.get(capsule);
		arrayInit.putExpression(
				capsuleEnums.get(capsule),
				new BlockInitializer(
						UMLRTRuntime.UMLRTSlot.getType(),
						new StringLiteral(capsule.getQualifiedName('.')),
						new IntegralLiteral(capsule.getIndex()),
						new AddressOfExpr(new ElementAccess(cpp.getVariable(CppCodePattern.Output.UMLRTCapsuleClass, capsule.getType()))),
						capsule.getContainer() == null
								? StandardLibrary.NULL()
								: new AddressOfExpr(
										new ElementAccess(
												cpp.getVariable(CppCodePattern.Output.UMLRTCapsuleClass,
														capsule.getContainer().getType()))),
						capsule.getCapsulePart() == null
								? new IntegralLiteral(0)
								: cpp.getEnumeratorAccess(
										CppCodePattern.Output.PartId,
										capsule.getCapsulePart(),
										capsule.getContainer() == null ? null : capsule.getContainer().getType()),
						userClassVar == null ? StandardLibrary.NULL() : new AddressOfExpr(new ElementAccess(userClassVar)),
						new AddressOfExpr(new ElementAccess(controllerVar)),
						new IntegralLiteral(partArray == null ? 0 : partArray.getNumInitializedInstances()),
						partArray == null ? StandardLibrary.NULL() : new ElementAccess(partArray),
						new IntegralLiteral(borderPortArray == null ? 0 : borderPortArray.getNumInitializedInstances()),
						borderPortArray == null ? StandardLibrary.NULL() : new ElementAccess(borderPortArray),
						StandardLibrary.NULL(),
						BooleanLiteral.TRUE(),
						BooleanLiteral.FALSE()));
	}

	/**
	 * @param arrayInit
	 * @param elements
	 */
	private void generateSlotsVar(CppEnumOrderedInitializer arrayInit, ElementList elements) {
		if (slotsVar != null) {
			slotsVar.setInitializer(arrayInit);
		} else {
			slotsVar = new Variable(
					LinkageSpec.EXTERN,
					UMLRTRuntime.UMLRTSlot.getType().arrayOf(null),
					topCapsule.getName() + "_slots",
					arrayInit);
		}
		elements.addElement(slotsVar);
	}

	/**
	 * Generates the main source file.
	 */
	private void generateMain() {
		new CppMainGenerator()
				.generate(cpp.getOutputFolder().getAbsolutePath() + "/" + topCapsule.getName() + "Main.cc",
						topCapsule.getName(), deployment, slotsVar);
	}

	/**
	 * @param capsule
	 *            - A {@link ICapsuleInstance}.
	 * @return A slot access C++ {@link Expression}, specifically an {@link IndexExpr} which referes to the
	 *         slot entry in the slots array by its index.
	 */
	private Expression getSlotAccess(ICapsuleInstance capsule) {
		if (slotsVar == null) {
			slotsVar = new Variable(
					LinkageSpec.EXTERN,
					UMLRTRuntime.UMLRTSlot.getType().arrayOf(null),
					topCapsule.getName() + "_slots");
		}

		return new IndexExpr(new ElementAccess(slotsVar), new ElementAccess(capsuleEnums.get(capsule)));
	}

	/**
	 * Creates the C++ array with the blocks for each part in a capsule, filling out each array entry
	 * with the relevant information for each capsule instance inferred from the corresponding {@link ICapsuleInstance}.
	 * 
	 * @param elements
	 *            - The {@link ElementList} where the slots array variable is to be added.
	 * @param capsule
	 *            - The {@link ICapsuleInstance} to consider.
	 * @return The C++ {@link Variable} representing the parts array.
	 */
	private Variable createCapsulePartArray(ElementList elements, ICapsuleInstance capsule) {
		BlockInitializer slotArrayInit = new BlockInitializer(UMLRTRuntime.UMLRTSlot.getType().ptr().arrayOf(null));
		Variable slotArrayVar = new Variable(
				LinkageSpec.STATIC,
				slotArrayInit.getType(),
				"slots_" + capsule.getQualifiedName('_'),
				slotArrayInit);

		BlockInitializer init = new BlockInitializer(UMLRTRuntime.UMLRTCapsulePart.getType().arrayOf(null));
		Variable var = new Variable(
				LinkageSpec.STATIC,
				UMLRTRuntime.UMLRTCapsulePart.getType().arrayOf(null),
				"parts_" + capsule.getQualifiedName('_'),
				init);

		Variable capsuleClassVar = cpp.getVariable(CppCodePattern.Output.UMLRTCapsuleClass, capsule.getType());

		for (CapsulePart part : XTUMLRTExtensions.getAllCapsuleParts(capsule.getType())) {
			// The part points to an array of slots. If the part is replicated at most one time,
			// then we avoid creating the array and instead point to a single slot.
			Expression slotAccess = null;
			List<? extends ICapsuleInstance> contained = capsule.getContained(part);
			if (contained == null
					|| contained.size() <= 0) {
				slotAccess = StandardLibrary.NULL();
			} else {
				slotAccess = new AddressOfExpr(
						new IndexExpr(
								new ElementAccess(slotArrayVar),
								new IntegralLiteral(slotArrayVar.getNumInitializedInstances())));
				for (ICapsuleInstance sub : contained) {
					slotArrayInit.addExpression(new AddressOfExpr(getSlotAccess(sub)));
				}
			}

			init.addExpression(
					new BlockInitializer(
							UMLRTRuntime.UMLRTCapsulePart.getType(),
							new AddressOfExpr(new ElementAccess(capsuleClassVar)),
							cpp.getEnumeratorAccess(CppCodePattern.Output.PartId, part, capsule.getType()),
							new IntegralLiteral(contained == null ? 0 : contained.size()),
							slotAccess));
		}

		if (slotArrayVar.getNumInitializedInstances() > 0) {
			elements.addElement(slotArrayVar);
		}

		return var.getNumInitializedInstances() <= 0 ? null : var;
	}

	/**
	 * Creates the C++ array with the blocks for each port in a capsule.
	 * 
	 * @param capsule
	 *            - The {@link ICapsuleInstance} to consider.
	 * @param border
	 *            - {@code true} for the border ports array, {@code false} for the non-border ports array.
	 * @return The C++ {@link Variable} representing the relevant ports array.
	 */
	private Variable createCapsulePortArray(ICapsuleInstance capsule, boolean border) {
		Variable[] vars = capsulePortArrays.get(capsule);
		if (vars == null) {
			vars = new Variable[2];
			capsulePortArrays.put(capsule, vars);
		}

		Variable var = vars[border ? 0 : 1];
		if (var == null) {
			var = new Variable(
					LinkageSpec.EXTERN, // extern so port farEnds can reference each other
					UMLRTRuntime.UMLRTCommsPort.getType().arrayOf(null),
					(border ? "border" : "internal") + "ports_" + capsule.getQualifiedName('_'));
			vars[border ? 0 : 1] = var;
		}

		return var;
	}

	/**
	 * Creates the border/internal ports array for a given capsule instance and adds it to the given element list.
	 * 
	 * @param elements
	 *            - The {@link ElementList} where the array is to be added.
	 * @param capsule
	 *            - The {@link ICapsuleInstance} under consideration.
	 * @param border
	 *            - {@code true} for the border ports array, {@code false} for the non-border ports array.
	 * @return The C++ {@link Variable} representing the relevant ports array.
	 */
	private Variable getPortArray(ElementList elements, ICapsuleInstance capsule, boolean border) {
		VarWrapper[] vars = portArrays.get(capsule);
		if (vars == null) {
			vars = new VarWrapper[2];
			portArrays.put(capsule, vars);
		}

		VarWrapper var = vars[border ? 0 : 1];
		if (var == null) {
			var = new VarWrapper(createPortArray(elements, capsule, border));
			vars[border ? 0 : 1] = var;

			if (var.variable != null) {
				elements.addElement(var.variable);
			}
		}

		return var.variable;
	}

	/**
	 * Creates the border/internal ports array for a given capsule instance, filling out each array entry
	 * with the relevant information for each port instance inferred from the corresponding {@link IPortInstance}.
	 * 
	 * @param elements
	 *            - The {@link ElementList} where the array is to be added.
	 * @param capsule
	 *            - The {@link ICapsuleInstance} under consideration.
	 * @param border
	 *            - {@code true} for the border ports array, {@code false} for the non-border ports array.
	 * @return The C++ {@link Variable} representing the relevant ports array.
	 */
	private Variable createPortArray(ElementList elements, ICapsuleInstance capsule, boolean border) {
		// The port array must be initialized in the same order as the corresponding PortId enum.
		CppEnumOrderedInitializer arrayInit = null;

		CppCodePattern.Output CppCodePattern_Output_portId = border ? CppCodePattern.Output.BorderPortId : CppCodePattern.Output.InternalPortId;

		// This generates two arrays. One has an element for each port, the other is the array of
		// farEnd ports instances. The farEnd array has an entry for each instance, the port array
		// has an entry for each port.
		// E.g., if there are two ports, one not replicated and the other replicated twice, then the
		// port array will have two elements and the farEnd array will have three.
		// Further, the port array is initialized using slices of the farEnd array. E.g.,
		// farEndArray[3] = { farEndA0, farEndA1, farEndB };
		// portArray[2] = { &farEndArray[0], &farEndArray[2] );

		BlockInitializer farEndInit = new BlockInitializer(UMLRTRuntime.UMLRTCommsPortFarEnd.getType().arrayOf(null));
		Variable farEndPorts = new Variable(
				LinkageSpec.STATIC,
				farEndInit.getType(),
				(border ? "border" : "internal") + "farEndList_" + capsule.getQualifiedName('_'),
				farEndInit);

		for (IPortInstance port : capsule.getPorts()) {
			// If we're processing border ports, then ignore the internal ones, and
			// vice versa.
			if (border == XTUMLRTUtil.isNonBorderPort(port.getType())) {
				continue;
			}

			// This generates an expression to access the start of the farEnd array.
			// For index 0 this is the array "farEndList_capsulename". For other indexes it
			// is the address of the corresponding element "&farEndList_capsulename[n]".
			int farEndCount = 0;
			Expression farEndAccess = farEndPorts.getNumInitializedInstances() == 0
					? new ElementAccess(farEndPorts)
					: new AddressOfExpr(
							new IndexExpr(
									new ElementAccess(farEndPorts),
									new IntegralLiteral(farEndPorts.getNumInitializedInstances())));
			for (IPortInstance.IFarEnd farEnd : port.getFarEnds()) {
				++farEndCount;
				ICapsuleInstance farEndCapsule = farEnd.getContainer();
				farEndInit.addExpression(
						new BlockInitializer(
								UMLRTRuntime.UMLRTCommsPortFarEnd.getType(),
								new IntegralLiteral(farEnd.getIndex()),
								new AddressOfExpr(
										new IndexExpr(
												new ElementAccess(createCapsulePortArray(farEndCapsule, !XTUMLRTUtil.isNonBorderPort(farEnd.getType()))),
												cpp.getEnumeratorAccess(
														XTUMLRTUtil.isNonBorderPort(farEnd.getType()) ? CppCodePattern.Output.InternalPortId : CppCodePattern.Output.BorderPortId,
														farEnd.getType(),
														farEndCapsule.getType())))));
			}

			int bound = -1;
			try {
				bound = XTUMLRTBoundsEvaluator.getBound(port.getType());
				// Bug 469882: Generate empty elements for all unconnected port instances. The RTS
				// requires this farEnd list to be the length that is declared in
				// UMLRTCommsPortRole.numFarEnd (in the UMLRTCapsuleClass).
				// This numFarEnd is initialized with the lower bound of the port type.
				for (; farEndCount < bound; ++farEndCount) {
					farEndInit.addExpression(
							new BlockInitializer(
									UMLRTRuntime.UMLRTCommsPortFarEnd.getType(),
									new IntegralLiteral(0),
									StandardLibrary.NULL()));
				}
			} catch (UndefinedValueException e) {
				String msg = "Unable to determine the replication factor of port " + QualifiedNames.cachedFullName(port.getType());
				CodeGenPlugin.error(msg);
				throw new RuntimeException(msg);
			}

			BlockInitializer portInit = new BlockInitializer(UMLRTRuntime.UMLRTCommsPort.getType());
			portInit.addExpression(
					new AddressOfExpr(
							new ElementAccess(
									cpp.getVariable(CppCodePattern.Output.UMLRTCapsuleClass, port.getContainer().getType()))));
			portInit.addExpression(
					cpp.getEnumeratorAccess(CppCodePattern_Output_portId, port.getType(), capsule.getType()));
			portInit.addExpression(new AddressOfExpr(getSlotAccess(port.getContainer())));
			portInit.addExpression(new IntegralLiteral(farEndCount));
			portInit.addExpression(farEndCount <= 0 ? StandardLibrary.NULL() : farEndAccess);

			// TODO some fields need the actual values
			portInit.addExpression(StandardLibrary.NULL()); // mutable UMLRTMessageQueue * deferQueue; // Deferred messages on this port.
			portInit.addExpression(StandardLibrary.NULL()); // mutable char * registeredName;
			portInit.addExpression(new StringLiteral(XTUMLRTUtil.getRegistrationOverride(port.getType()))); // mutable char * registrationOverride;
			portInit.addExpression(BooleanLiteral.from(XTUMLRTUtil.isAutomatic(port.getType())));
			portInit.addExpression(BooleanLiteral.from(border));
			portInit.addExpression(BooleanLiteral.TRUE()); // unsigned generated : 1; // True for code-generated ports (registeredName is not from heap).
			portInit.addExpression(BooleanLiteral.from(XTUMLRTUtil.isAutomaticLocked(port.getType())));
			portInit.addExpression(BooleanLiteral.from(XTUMLRTUtil.isNotification(port.getType())));
			portInit.addExpression(BooleanLiteral.FALSE()); // unsigned proxy : 1; // True for proxy border ports created if the slot port replication is less than the capsule border port replication.
			portInit.addExpression(BooleanLiteral.from(port.isRelay()));

			portInit.addExpression(BooleanLiteral.from(XTUMLRTUtil.isSAP(port.getType()))); // unsigned sap : 1; // True if the port is an SAP.
			portInit.addExpression(BooleanLiteral.from(XTUMLRTUtil.isSPP(port.getType()))); // unsigned spp : 1; // True if the port is an SPP.

			portInit.addExpression(BooleanLiteral.FALSE()); // unsigned unbound : 1; // True to represent the unbound port. Has no far-end instances and is replaced when binding.
			portInit.addExpression(BooleanLiteral.from(XTUMLRTUtil.isWired(port.getType())));

			if (arrayInit == null) {
				arrayInit = new CppEnumOrderedInitializer(
						cpp.getIdEnum(CppCodePattern_Output_portId, port.getContainer().getType()),
						UMLRTRuntime.UMLRTCommsPortFarEnd.getType().arrayOf(null));
			}
			arrayInit.putExpression(cpp.getEnumerator(CppCodePattern_Output_portId, port.getType(), capsule.getType()), portInit);
		}

		if (arrayInit == null
				|| arrayInit.getNumInitializers() <= 0) {
			return null;
		}

		if (farEndPorts.getNumInitializedInstances() > 0) {
			elements.addElement(farEndPorts);
		}

		Variable portArray = createCapsulePortArray(capsule, border);
		portArray.setInitializer(arrayInit);
		return portArray;
	}

	/**
	 * Creates the C++ array with pointers to capsule ports.
	 * 
	 * @param var
	 *            - The C++ {@link Variable} with the ports array.
	 * @return The new C++ {@link Variable} with the port pointers array.
	 */
	private Variable createCapsulePortPointerArray(Variable var) {
		if (var == null) {
			return null;
		}

		BlockInitializer init = new BlockInitializer(UMLRTRuntime.UMLRTCommsPort.getType().const_().ptr().arrayOf(null));
		for (int i = 0; i < var.getNumInitializedInstances(); ++i) {
			init.addExpression(
					new AddressOfExpr(
							new IndexExpr(
									new ElementAccess(var),
									new IntegralLiteral(i))));
		}
		return new Variable(LinkageSpec.STATIC, init.getType(), var.getName().getIdentifier() + "_ptrs", init);
	}

	@Override
	public List<FileName> getGeneratedFilenames() {
		List<FileName> result = new ArrayList<>();
		ElementList el = cpp.getElementList(CppCodePattern.Output.Deployment, topCapsule);
		result.add(el.getName());
		return result;
	}

	/**
	 * A wrapper for C++ {@link Variable} instances.
	 */
	private static class VarWrapper {

		/** The C++ {@link Variable} this wraps. */
		public final Variable variable;

		/**
		 * Constructor.
		 *
		 * @param variable
		 *            - The C++ {@link Variable} this wraps.
		 */
		VarWrapper(Variable variable) {
			this.variable = variable;
		}
	}

	/**
	 * The {@link AbstractElementGenerator.Factory} used to create instances of this generator.
	 */
	public static class Factory implements AbstractElementGenerator.Factory<org.eclipse.papyrusrt.xtumlrt.common.Capsule, org.eclipse.papyrusrt.xtumlrt.common.Package> {

		/**
		 * Constructor.
		 */
		public Factory() {

		}

		@Override
		public AbstractElementGenerator create(CppCodePattern cpp, org.eclipse.papyrusrt.xtumlrt.common.Capsule topCapsule, org.eclipse.papyrusrt.xtumlrt.common.Package context) {
			ControllerAllocations controllerAllocations = null;
			File file = cpp.getControllerAllocations(topCapsule.getName());
			if (file != null) {
				controllerAllocations = ControllerAllocations.load(file);
			}

			// If there isn't an allocations file, then create one that will put everything onto
			// the same controller.
			if (controllerAllocations == null) {
				controllerAllocations = ControllerAllocations.DEFAULT;
			}

			return new CompositionGenerator(cpp, topCapsule, controllerAllocations);
		}
	}

}
