package ca.queensu.cs.papyrus.rt.optimization.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.uml.UmlModel;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.epsilon.eol.types.EolCollectionType;

import ca.queensu.cs.papyrus.rt.optimization.actions.RunTimeCodeGeneration;

public abstract class EpsilonClass  {

	protected static final String String = null; //needs to be private

	protected IEolExecutableModule module;
	
	protected Object result;
	
	public abstract IEolExecutableModule createModule();
	
	public abstract String getSource() throws Exception;
	
	public abstract List<IModel> getModels() throws Exception;
	
	public void postProcess() {};
	
	protected IEolExecutableModule event;
	
	public static String impactedElementsFilePath;

	public void preProcess() {};
	public void execute() throws Exception { 
		module = createModule(); 
		module.parse(getFileURI(getSource()));
		
		if (module.getParseProblems().size() > 0) {
			System.err.println("Parse errors occured...");
			for (ParseProblem problem : module.getParseProblems()) {
				System.err.println(problem.toString());
			}
			System.exit(-1);
		}
		// Create parameters
		EMFCompareAction storage = new EMFCompareAction();
        Map<String, Set<String>> ownerMap = new HashMap<String,Set<String>>(storage.ownerMap); //Changed
        Map<String, Set<String>> attributeMap = new HashMap<String,Set<String>>(storage.attributeMap); //Changed

		Variable ownerParams = new Variable("ownerData", ownerMap, EolCollectionType.OrderedSet);
		Variable attributeParams = new Variable("attributeData", attributeMap, EolCollectionType.OrderedSet);
		Variable outputVal = new Variable("outputValues", EolCollectionType.Set);

		module.getContext().getFrameStack().putGlobal(ownerParams);
		module.getContext().getFrameStack().putGlobal(attributeParams);
		module.getContext().getFrameStack().putGlobal(outputVal);

		
		for (IModel model : getModels()) {
			module.getContext().getModelRepository().addModel(model);
		}
		
		preProcess();
		result = execute(module);
		
		genrateCodeGenFile(outputVal);
		
		postProcess(); 
		module.getContext().getModelRepository().dispose();
		RunTimeCodeGeneration.main(outputVal.getValue().toString());
	}
	
	public void genrateCodeGenFile(Variable outputVal) throws IOException
	{
		//System.out.println("==============================\nFinal Impacted Elements are : " + outputVal);

		String str = outputVal.getValue().toString();
		String text = str.toString().replace("[", "").replace("]", "").replace("\n, ", "");
		System.out.println("==============================\nFinal Impacted Elements for Code Generation are : " + text);
		System.out.println("==============================");
		File tmp = File.createTempFile("codeGen",".txt"); 
		impactedElementsFilePath = tmp.getPath();
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		bw.write(text);
		bw.flush();
		bw.close();
		
		impactedData();
			
	//	tmp.deleteOnExit();
	}
	
	public void impactedData()
	{
		System.out.println("===========Calling Generator File");
		RunTimeCodeGeneration.main(impactedElementsFilePath);	
	} 
	
	protected Object execute(IEolExecutableModule module) throws EolRuntimeException {	
		return module.execute();
	} 

	protected UmlModel createUmlModelByURI(String name, String model, String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
					throws EolModelLoadingException, URISyntaxException, MalformedURLException, IOException {
		UmlModel umlModel = new UmlModel();
		StringProperties properties = new StringProperties();
		properties.put(UmlModel.PROPERTY_NAME, name);
		properties.put(UmlModel.PROPERTY_METAMODEL_URI, metamodel);
		properties.put(UmlModel.PROPERTY_MODEL_URI, getFileURI(model).toString());//.getAbsolutePath());
		properties.put(UmlModel.PROPERTY_READONLOAD, readOnLoad + "");
		properties.put(UmlModel.PROPERTY_STOREONDISPOSAL,storeOnDisposal + "");
		umlModel.load(properties, (IRelativePathResolver) null);
		return umlModel;
	}
	
	protected URI getFileURI(String fileName) throws URISyntaxException, IOException { 
		URI uri= URIUtil.fromString("file:"+fileName);
	//	System.out.println("URI is "+ uri);
		if (uri.isAbsolute())
		return uri;
		uri = EpsilonClass.class.getClassLoader().getResource("../"+fileName).toURI();
		return uri;
	}		 
}