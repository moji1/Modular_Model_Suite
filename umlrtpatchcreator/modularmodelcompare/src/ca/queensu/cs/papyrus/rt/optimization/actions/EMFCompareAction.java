package ca.queensu.cs.papyrus.rt.optimization.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.uml2.uml.resource.UMLResource;

import ca.queensu.cs.papyrus.rt.optimization.actions.EOLCallingClass;

public class EMFCompareAction {
	
	public static HashMap<String,Set<String>> ownerMap = new HashMap<String,Set<String>>();
	public static HashMap<String,Set<String>> attributeMap = new HashMap<String,Set<String>>();
	protected IEolModule module;
	public String getSource() throws Exception {
		return null;
	}

public void compare(String expected, String actual) {

	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml",UMLResource.Factory.INSTANCE);
		
	ResourceSet expectedResSet = new ResourceSetImpl();
	ResourceSet actualResSet = new ResourceSetImpl();

	load(expected, expectedResSet);
	load(actual, actualResSet);
		
	// Configure EMF Compare
	IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
	IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
	
	IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
	matchEngineFactory.setRanking(20);
	IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	matchEngineRegistry.add(matchEngineFactory);
	    
	IDiffProcessor diffProcessor = new DiffBuilder();
	Builder builder = EMFCompare.builder();
	builder.setDiffEngine(new DefaultDiffEngine(diffProcessor));
	EMFCompare comparator = EMFCompare.builder().build();

	// Compare the two models
	IComparisonScope scope =  new DefaultComparisonScope (expectedResSet, actualResSet, null);
	Comparison comparison = comparator.compare(scope);
		
	String ownerKey = null;String attributeKey = null;

	String getOwnerIdValue = null; String getAttributeIdValue = null; EObject eObj = null;EObject ownerKy = null ;
	EObject actualEObj = null;
	
	//To find the difference after comparison
	for (Diff diff : comparison.getDifferences()) {
		{	
		//To get values if Reference is Changed
		if (diff instanceof ReferenceChange) {
			
			//This function mainly finds the Deleted Object because .getRight() is not able to find deleted Objects
			if(diff.getMatch().getLeft()!=null) {				
				actualEObj = ((ReferenceChange)diff).getValue().eClass();
    				String strClass = ((ReferenceChange)diff).getValue().eClass().getName();
    				if(strClass.equals("Class")) {
				eObj=actualEObj;
				getOwnerIdValue = eObj.eResource().getURIFragment(eObj);
    				}
    				else {

			eObj = diff.getMatch().getLeft().eContainer();
    				while ((eObj != null) && (!("Model".equals(eObj.eClass().getName())))){
    					ownerKy = eObj.eContainer();
    	    				if((!("Model".equals(ownerKy.eClass().getName()))) && (ownerKy!=null)){
    	    					if(ownerKy!=null){
    	    						eObj=ownerKy;
    	    					}
    	    				}
    	    				else {break;}
    	    			}
    				if(eObj != null) {
    	    				getOwnerIdValue = diff.getMatch().getLeft().eResource().getURIFragment(eObj);
    	    				attributeKey = 	diff.getMatch().getLeft().eClass().getName();
    	    				getAttributeIdValue = diff.getMatch().getLeft().eResource().getURIFragment(diff.getMatch().getLeft());
    				}
    				else{break;}
    				}}
    			
			//This function mainly finds the Added Object because Attribute Changes is not able to find Added Objects
			else if(diff.getMatch().getRight()!=null) {
            			eObj = diff.getMatch().getRight().eContainer();
    	    				while( (eObj != null) && (!("Model".equals(eObj.eClass().getName())))){
    	    				ownerKy = eObj.eContainer();
    	    					if(!("Model".equals(ownerKy.eClass().getName()))) {
    	    						if(ownerKy!=null){
    	    							eObj=ownerKy;
    	    						}
    	    					}
    	    					else {break;}
    	    			}}
			else {break;}
            }
	
		//To get values if Attribute is Changed
		else if (diff instanceof AttributeChange) {
    			
    			//If the changes has happend directly below Model
    			actualEObj = diff.getMatch().getRight();
        		String strClass = actualEObj.eClass().getName();
        		String strModel = actualEObj.eContainer().eClass().getName();
    			
        		if((strClass.equals("Class")) || (strClass.equals("Package"))  && (strModel.equals("Model"))){
				eObj=actualEObj;
        		}
        		//If changes has happend inside any class or protocol
        		else{ 
    		    	eObj = diff.getMatch().getRight().eContainer();
		    		while ((eObj != null) && (!("Model".equals(eObj.eClass().getName())))){
		    			ownerKy = eObj.eContainer();
		    				if(!("Model".equals(ownerKy.eClass().getName()))) {
		    					if(ownerKy!=null){
		    						eObj=ownerKy;
		    					}
		    				}
		    				else {break;}
		    		}
		    	}
		 }
		else if (diff instanceof ResourceAttachmentChange) {
        		System.out.println("ResourceAttachmentChange");
        		}
            
		else {break;}
		//End of Change type if condition
		
		//Condition to store owner key and value to Hash Map
		if(eObj!=null) {
    		    ownerKey = eObj.eClass().getName();
    		    if(diff.getMatch().getRight()!=null) {
    		    		getOwnerIdValue = diff.getMatch().getRight().eResource().getURIFragment(eObj);
    		    		attributeKey = 	diff.getMatch().getRight().eClass().getName();
    		    		getAttributeIdValue = diff.getMatch().getRight().eResource().getURIFragment(diff.getMatch().getRight());
    		    }}

			//Store values in hash map
            if (ownerMap.containsKey(ownerKey)){
               Set values = (Set)ownerMap.get(ownerKey);
               values.add(getOwnerIdValue);}
            else{
               Set<String> values = new HashSet<String>();
               values.add(getOwnerIdValue);
               ownerMap.put(ownerKey, values);}
            
            if (attributeMap.containsKey(attributeKey)){
               Set values = (Set)attributeMap.get(attributeKey);
               values.add(getAttributeIdValue);}
            else{
               Set<String> values = new HashSet<String>();
               values.add(getAttributeIdValue);
               attributeMap.put(attributeKey, values);}		
        }}
	callEOLClass();
}

	//Calls The EOL Class
	private void callEOLClass() {
		EOLCallingClass exp = new EOLCallingClass();
		try {
			exp.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Resource load(String absolutePath, ResourceSet resourceSet) {
		  URI uri = URI.createFileURI(absolutePath);
		  resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml",UMLResource.Factory.INSTANCE);
		  // Resource will be loaded within the resource set
		   return resourceSet.getResource(uri, true);
		}
}
