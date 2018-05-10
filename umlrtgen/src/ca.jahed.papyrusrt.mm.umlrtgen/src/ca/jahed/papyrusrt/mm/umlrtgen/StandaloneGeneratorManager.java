package ca.jahed.papyrusrt.mm.umlrtgen;

import org.eclipse.papyrusrt.codegen.cpp.AbstractElementGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.codegen.cpp.GeneratorManager;
import org.eclipse.papyrusrt.codegen.cpp.XTUMLRT2CppCodeGenerator;
import org.eclipse.papyrusrt.codegen.cpp.statemachines.flat.generator.StateMachineGenerator;
import org.eclipse.papyrusrt.codegen.cpp.structure.CompositionGenerator;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.common.Package;
import org.eclipse.papyrusrt.xtumlrt.common.Protocol;
import org.eclipse.papyrusrt.xtumlrt.statemach.StateMachine;


public class StandaloneGeneratorManager extends GeneratorManager {

	public StandaloneGeneratorManager() {
	}

	@Override
	public AbstractElementGenerator getGenerator(
			XTUMLRT2CppCodeGenerator.Kind kind,
			CppCodePattern cpp,
			NamedElement element,
			NamedElement context) {
		AbstractElementGenerator generator = null;
		if (element != null) {
			switch (kind) {
			case Structural:
				if (element instanceof Capsule) {
					generator = new CompositionGenerator.Factory()
							.create(cpp, (Capsule) element, null);
				}
				break;
			case StateMachine:
				if (element instanceof StateMachine || context instanceof Capsule) {
					generator = new StateMachineGenerator.Factory()
							.create(cpp, (StateMachine) element, (Capsule) context);
				}
				break;
			case Capsule:
				if (element instanceof Capsule || context instanceof Package) {
					generator = new CapsuleGenerator.Factory()
							.create(cpp, (Capsule) element, (Package) context);
				}
				break;
			case Protocol:
				if (element instanceof Protocol || context instanceof Package) {
					generator = new ProtocolGenerator.Factory()
							.create(cpp, (Protocol) element, (Package) context);
				}
				break;
			default:
				generator = super.getGenerator(kind, cpp, element, context);
				break;
			}
		}
		return generator;
	}

}
