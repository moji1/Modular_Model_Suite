package ca.jahed.papyrusrt.mm.umlrtgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.papyrusrt.codegen.cpp.AbstractElementGenerator;
import org.eclipse.papyrusrt.codegen.cpp.CppCodePattern;
import org.eclipse.papyrusrt.xtumlrt.common.Capsule;
import org.eclipse.papyrusrt.xtumlrt.common.Package;

public class CapsuleGenerator extends org.eclipse.papyrusrt.codegen.cpp.internal.CapsuleGenerator {

	private Capsule capsule;
	private List<String> targetComponents;

	public CapsuleGenerator(CppCodePattern cpp, Capsule capsule, Package context) {
		super(cpp, capsule);
		this.capsule = capsule;

		String targetComponentsStr = System.getProperty("target_components");
		if (targetComponentsStr.indexOf(',') == -1)
			targetComponents = new ArrayList<>();
		else
			targetComponents = Arrays.asList(targetComponentsStr.split(","));
	}

	public static class Factory implements AbstractElementGenerator.Factory<Capsule, Package> {

		@Override
		public AbstractElementGenerator create(CppCodePattern cpp, Capsule capsule, Package context) {
			return new CapsuleGenerator(cpp, capsule, context);
		}
	}

	@Override
	public boolean generate() {
		for (String target : targetComponents) {
			if (target.equals(capsule.getName())) {
				return super.generate();
			}
		}

		return true;
	}
}
