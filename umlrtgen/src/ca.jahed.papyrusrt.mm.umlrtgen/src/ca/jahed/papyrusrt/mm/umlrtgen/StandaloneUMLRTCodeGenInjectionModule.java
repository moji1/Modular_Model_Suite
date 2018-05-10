package ca.jahed.papyrusrt.mm.umlrtgen;

import org.eclipse.papyrusrt.codegen.UMLRTCodeGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodeGenInjectionModule;

public class StandaloneUMLRTCodeGenInjectionModule extends CppCodeGenInjectionModule {

	@Override
	protected void configure() {
		super.configure();
		bind(UMLRTCodeGenerator.class).to(StandaloneUMLRT2CppCodeGenerator.class);
	}

}
