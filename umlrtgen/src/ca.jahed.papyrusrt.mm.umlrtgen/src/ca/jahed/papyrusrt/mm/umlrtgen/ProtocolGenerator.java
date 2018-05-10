package ca.jahed.papyrusrt.mm.umlrtgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.papyrusrt.codegen.cpp.AbstractElementGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.xtumlrt.common.Package;
import org.eclipse.papyrusrt.xtumlrt.common.Protocol;

public class ProtocolGenerator extends org.eclipse.papyrusrt.codegen.cpp.internal.ProtocolGenerator {

	private Protocol protocol;
	private List<String> targetComponents;

	public ProtocolGenerator(CppCodePattern cpp, Protocol protocol, Package context) {
		super(cpp, protocol);
		this.protocol = protocol;

		String targetComponentsStr = System.getProperty("target_components");
		if (targetComponentsStr.indexOf(',') == -1)
			targetComponents = new ArrayList<>();
		else
			targetComponents = Arrays.asList(targetComponentsStr.split(","));
	}

	public static class Factory implements AbstractElementGenerator.Factory<Protocol, Package> {

		@Override
		public AbstractElementGenerator create(CppCodePattern cpp, Protocol protocol, Package context) {
			return new ProtocolGenerator(cpp, protocol, context);
		}
	}

	@Override
	public boolean generate() {
		for (String target : targetComponents) {
			if (target.equals(protocol.getName())) {
				return super.generate();
			}
		}

		return true;
	}
}
