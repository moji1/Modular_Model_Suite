package ca.queensu.cs.papyrus.rt.optimization.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import ca.queensu.cs.papyrus.rt.optimization.actions.EpsilonClass;

public class EOLCallingClass extends EpsilonClass {
	
		File file;
		public static void main(String[] args) throws Exception {
		    new EOLCallingClass().execute();
		  }
	
	 @Override
	public IEolModule createModule() {
	    return new EolModule();
	  }
	 
	public List<IModel> getModels() throws Exception {
		GitAction action = new GitAction();
		file = action.filePath ;
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml",UMLResource.Factory.INSTANCE);
 		List<IModel> models = new ArrayList<IModel>();
		
		ResourceSet set = new ResourceSetImpl();
		UMLResourcesUtil.init(set);
		set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		Resource res = set.getResource(URI.createFileURI(file.getAbsolutePath()), true);
 		
		models.add(createUmlModelByURI("M", res.getURI().toString(), "http://www.eclipse.org/papyrus/umlrt", true, true));
		return models;
		}

	@Override
	  public String getSource() throws Exception {
		//  System.out.println("EOL File Processed");
	      return "EOLScripts/call.eol";
	  }

	  @Override
	  public void postProcess() {
	  }
}

