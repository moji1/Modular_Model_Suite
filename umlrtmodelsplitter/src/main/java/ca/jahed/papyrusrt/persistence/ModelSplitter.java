package ca.jahed.papyrusrt.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.Capsule;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.CapsulePart;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.Protocol;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.ProtocolContainer;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTConnector;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTMessageSet;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTPort;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.UMLRealTimePackage;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTPseudostate;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTRegion;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTState;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.RTStateMachine;
import org.eclipse.papyrusrt.umlrt.profile.statemachine.UMLRTStateMachines.UMLRTStateMachinesPackage;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;


public class ModelSplitter {
    public static void main( String[] args ) {
        if( args.length != 1) {
            System.err.println("Usage: "+System.getProperty("sun.java.command")+" <model>");
            return;
        }

        File inputModelFile = new File(args[0]);
        File mainDir = new File(inputModelFile.getParent());

        ResourceSet resourceSet = new ResourceSetImpl();
        UMLResourcesUtil.init(resourceSet);

       // resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
      //  resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
	//	    .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

    //    resourceSet.createResource(URI.createURI("/Users/kjahed/Desktop/mm/papyrusrt-mm/profiles/uml-rt.profile.uml"));
        //resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		//    UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        
        //resourceSet.getPackageRegistry().put("http://www.eclipse.org/uml2/5.0.0/UML", UMLPackage.eINSTANCE);
        //resourceSet.getPackageRegistry().put("http://www.eclipse.org/papyrus/umlrt", UMLRealTimePackage.eINSTANCE);
        //resourceSet.getPackageRegistry().put("http://www.eclipse.org/papyrus/umlrt/statemachine", UMLRTStateMachinesPackage.eINSTANCE);
        //resourceSet.getPackageRegistry().put("http://www.eclipse.org/papyrus/umlrt/cppproperties", RTCppPropertiesPackage.eINSTANCE);

        XMIResource mainResource = (XMIResource) resourceSet.getResource(URI.createURI(inputModelFile.getAbsolutePath()), true);		
        EcoreUtil.resolveAll(mainResource);

        Model inputModel = (Model) EcoreUtil.getObjectByType(mainResource.getContents(), UMLPackage.Literals.MODEL);

        List<XMIResource> allResources = new LinkedList<XMIResource>();
        allResources.add(mainResource);

        EList<PackageableElement> modelElements = inputModel.getPackagedElements();	
        List<PackageableElement> movedElements = new ArrayList<PackageableElement>();
        
        int i=0;
        while(i < modelElements.size()) {
            PackageableElement element = modelElements.get(i);						
            if(element instanceof Class 
                    || element instanceof Enumeration
                    || isProtocol(element)) {
                handleElement(mainResource, allResources, element, mainDir);
                movedElements.add(element);
                }
            else if(element instanceof Package) {
                handlePackage(mainResource, allResources, element, mainDir);
                movedElements.add(element);
                //i++;
            }
            else {
                i++;
            }
        }
        
        for(PackageableElement el : movedElements) {
            PackageableElement proxyElement = null;
            if(el instanceof Package)
                proxyElement = UMLFactory.eINSTANCE.createPackage();
            else if(el instanceof Class)
                proxyElement = UMLFactory.eINSTANCE.createClass();
            else if(el instanceof Enumeration)
                proxyElement = UMLFactory.eINSTANCE.createEnumeration();
            
            if(proxyElement != null) {
                inputModel.getPackagedElements().add(proxyElement);
                ((InternalEObject) proxyElement).eSetProxyURI(EcoreUtil.getURI(el));
            }
        }
        
        try {
                for(XMIResource resource : allResources)
                    resource.save(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleElement(XMIResource originalResource, List<XMIResource> allResources, PackageableElement element, File outputDir) {
		outputDir.mkdirs();

		File outputFile = new File(outputDir, element.getName() + ".uml");
		XMIResource outputResource = new XMIResourceImpl(URI.createFileURI(
				outputFile.getAbsolutePath())); 
		
		try {
			outputResource.delete(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Model newModel = UMLFactory.eINSTANCE.createModel();
		newModel.setName(element.getName());
		
		addUmlrtElements(originalResource, outputResource, element);
		
		newModel.getPackagedElements().add(element);
		outputResource.getContents().add(newModel);
		allResources.add(outputResource);
	}
	
	private static void handlePackage(XMIResource originalResource, List<XMIResource> allResources, PackageableElement element, File outputDir) {	
		File packageDir = new File(outputDir, element.getName());
		packageDir.mkdirs();
		
		EList<Element> children = element.getOwnedElements();
		List<PackageableElement> movedElements = new ArrayList<PackageableElement>();
		
		int i=0;
		while(i < children.size())  {
			if(children.get(i) instanceof PackageableElement) {
				PackageableElement child = (PackageableElement) children.get(i);
				if(child instanceof Class 
						|| child instanceof Enumeration
						|| isProtocol(child)) {
					handleElement(originalResource, allResources, child, packageDir);
					movedElements.add(child);
	        		}
				else if(child instanceof Package) {
					handlePackage(originalResource, allResources, child, packageDir);
					movedElements.add(child);
					//i++;
				}
				else {
					i++;
				}
			} else {
				i++;
			}
		}
		
		File outputFile = new File(outputDir, element.getName() + ".uml");
		XMIResource outputResource = new XMIResourceImpl(URI.createFileURI(
				outputFile.getAbsolutePath())); 
		
		try {
			outputResource.delete(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Model newModel = UMLFactory.eINSTANCE.createModel();
		newModel.setName(element.getName());
		
		//addUmlrtElements(originalResource, outputResource, element);
		
		newModel.getPackagedElements().add(element);
		outputResource.getContents().add(newModel);
		allResources.add(outputResource);
		
		for(PackageableElement el : movedElements) {
			PackageableElement proxyElement = null;
			if(el instanceof Package)
				proxyElement = UMLFactory.eINSTANCE.createPackage();
			else if(el instanceof Class)
				proxyElement = UMLFactory.eINSTANCE.createClass();
			else if(el instanceof Enumeration)
				proxyElement = UMLFactory.eINSTANCE.createEnumeration();
			
			if(proxyElement != null) {
				newModel.getPackagedElements().add(proxyElement);
				((InternalEObject) proxyElement).eSetProxyURI(EcoreUtil.getURI(el));
			}
		}
	}

    private static boolean isProtocol(PackageableElement element) {
        if(element instanceof Package) {
            EList<PackageableElement> owned = ((Package) element).getPackagedElements();
            if(owned.size() > 0 && owned.get(0) instanceof Collaboration)
                return true;
        }
        return false;
    }

    private static void addUmlrtElements(XMIResource mainResource, XMIResource newResource, Element element) {		
		if(element instanceof StateMachine) {
			RTStateMachine statemachine = findStateMachine(mainResource, (StateMachine) element);
			if(statemachine != null)
				newResource.getContents().add(statemachine);
			
			EList<Element> subElements = element.getOwnedElements();
			for(Element sub : subElements) {
				addUmlrtElements(mainResource, newResource, sub);
			}
		}
		else if(element instanceof Region) {
			RTRegion region = findRegion(mainResource, (Region) element);
			if(region != null)
				newResource.getContents().add(region);
			
			EList<Element> subElements = element.getOwnedElements();
			for(Element sub : subElements) {
				addUmlrtElements(mainResource, newResource, sub);
			}
		}
		else if(element instanceof State) {
			RTState state = findState(mainResource, (State) element);
			if(state != null)
				newResource.getContents().add(state);
			
		}
		else if(element instanceof Pseudostate) {
			RTPseudostate state = findPsuedoState(mainResource, (Pseudostate) element);
			if(state != null)
				newResource.getContents().add(state);
			
		}
		else if(element instanceof Port) {
			RTPort port = findPort(mainResource, (Port) element);
			if(port != null)
				newResource.getContents().add(port);
		}
		else if(element instanceof Package) {
			ProtocolContainer protocolContainer = findProtocolContainer(mainResource, (Package) element);
			if(protocolContainer != null)
				newResource.getContents().add(protocolContainer);
			
			EList<Element> subElements = element.getOwnedElements();
			for(Element sub : subElements) {
				addUmlrtElements(mainResource, newResource, sub);
			}
		}
		else if(element instanceof Collaboration) {
			Protocol protocol = findProtocol(mainResource, (Collaboration) element);
			if(protocol != null)
				newResource.getContents().add(protocol);
		}
		else if(element instanceof Interface) {
			RTMessageSet messageSet = findMessageSet(mainResource, (Interface) element);
			if(messageSet != null)
				newResource.getContents().add(messageSet);
		}
		else if(element instanceof Class) {
			Class cls = (Class) element;
			if(cls.isActive()) {
				Capsule capsule = findCapsule(mainResource, (Class) element);
				if(capsule != null)
					newResource.getContents().add(capsule);
			}
			
			EList<Element> subElements = element.getOwnedElements();
			for(Element sub : subElements) {
				addUmlrtElements(mainResource, newResource, sub);
			}
		}
		else if(element instanceof Connector) {
			RTConnector connector = findConnector(mainResource, (Connector) element);
			if(connector != null)
				newResource.getContents().add(connector);
		}
		else if(element instanceof Property
				&& ((Property) element).getType() instanceof Class) {			
			CapsulePart part = findCapsulePart(mainResource, (Property) element);
			if(part != null)
				newResource.getContents().add(part);
		}
	}
    
	private static RTMessageSet findMessageSet(XMIResource resource, Interface baseInterface) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.RT_MESSAGE_SET);
		for(EObject obj : objects) {
			RTMessageSet messageSet = (RTMessageSet) obj;
			if(EcoreUtil.equals(messageSet.getBase_Interface(), baseInterface)) {
				return messageSet;
			}
		}
		return null;
	}
	
	private static RTPort findPort(XMIResource resource, Port basePort) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.RT_PORT);
		for(EObject obj : objects) {
			RTPort port = (RTPort) obj;
			if(EcoreUtil.equals(port.getBase_Port(), basePort)) {
				return port;
			}
		}
		return null;
	}
	
	private static Capsule findCapsule(XMIResource resource, Class baseClass) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.CAPSULE);
		for(EObject obj : objects) {
			Capsule capsule = (Capsule) obj;
			if(EcoreUtil.equals(capsule.getBase_Class(), baseClass)) {
				return capsule;
			}
		}
		return null;
	}
	
	private static CapsulePart findCapsulePart(XMIResource resource, Property baseProperty) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.CAPSULE_PART);
		for(EObject obj : objects) {
			CapsulePart capsulePart = (CapsulePart) obj;
			if(EcoreUtil.equals(capsulePart.getBase_Property(), baseProperty)) {
				return capsulePart;
			}
		}
		return null;
	}
	
	private static RTStateMachine findStateMachine(XMIResource resource, StateMachine sm) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRTStateMachinesPackage.Literals.RT_STATE_MACHINE);
		for(EObject obj : objects) {
			RTStateMachine rtStateMachine = (RTStateMachine) obj;
			if(EcoreUtil.equals(rtStateMachine.getBase_StateMachine(), sm)) {
				return rtStateMachine;
			}
		}
		return null;
	}
	
	private static RTState findState(XMIResource resource, State st) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRTStateMachinesPackage.Literals.RT_STATE);
		for(EObject obj : objects) {
			RTState rtState = (RTState) obj;
			if(EcoreUtil.equals(rtState.getBase_State(), st)) {
				return rtState;
			}
		}
		return null;
	}
	
	private static RTRegion findRegion(XMIResource resource, Region region) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRTStateMachinesPackage.Literals.RT_REGION);
		for(EObject obj : objects) {
			RTRegion rtRegion = (RTRegion) obj;
			if(EcoreUtil.equals(rtRegion.getBase_Region(), region)) {
				return rtRegion;
			}
		}
		return null;
	}
	
	private static RTPseudostate findPsuedoState(XMIResource resource,  Pseudostate st) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRTStateMachinesPackage.Literals.RT_PSEUDOSTATE);
		for(EObject obj : objects) {
			RTPseudostate rtState = (RTPseudostate) obj;
			if(EcoreUtil.equals(rtState.getBase_Pseudostate(), st)) {
				return rtState;
			}
		}
		return null;
	}
	
	private static ProtocolContainer findProtocolContainer(XMIResource resource, Package pkg) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.PROTOCOL_CONTAINER);
		for(EObject obj : objects) {
			ProtocolContainer container = (ProtocolContainer) obj;
			if(EcoreUtil.equals(container.getBase_Package(), pkg)) {
				return container;
			}
		}
		return null;
	}
	
	private static Protocol findProtocol(XMIResource resource, Collaboration collab) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.PROTOCOL);
		for(EObject obj : objects) {
			Protocol proto = (Protocol) obj;
			if(EcoreUtil.equals(proto.getBase_Collaboration(), collab)) {
				return proto;
			}
		}
		return null;
	}
	
	private static RTConnector findConnector(XMIResource resource, Connector connector) {
		Collection<EObject> objects = EcoreUtil.getObjectsByType(resource.getContents(), UMLRealTimePackage.Literals.RT_CONNECTOR);
		for(EObject obj : objects) {
			RTConnector conn = (RTConnector) obj;
			if(EcoreUtil.equals(conn.getBase_Connector(), connector)) {
				return conn;
			}
		}
		return null;
	}    
}
