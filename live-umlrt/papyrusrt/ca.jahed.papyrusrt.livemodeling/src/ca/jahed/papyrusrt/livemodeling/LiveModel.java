package ca.jahed.papyrusrt.livemodeling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.infra.core.resource.NotFoundException;
import org.eclipse.papyrus.uml.tools.model.ExtendedUmlModel;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.UMLRealTimePackage;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

public class LiveModel extends ExtendedUmlModel {
	
	private DebuggerListener debuggerListener;
	
	@Override
	public void loadModel(URI uriWithoutExtension) {
		// TODO Auto-generated method stub
		super.loadModel(uriWithoutExtension);
		
		debuggerListener = new DebuggerListener();
		debuggerListener.start();
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		super.unload();
		
		if(debuggerListener != null)
			debuggerListener.abort();
	}

	@Override
	public void saveModel() throws IOException {	
		try {
			ResourceSet newSet = new ResourceSetImpl();
			newSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
			newSet.getPackageRegistry().put(UMLRealTimePackage.eNS_URI, UMLRealTimePackage.eINSTANCE);

			newSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
			   .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
			   .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

			Resource res = newSet.getResource(getResource().getURI(), true);
			
			Model oldModel = (Model) res.getContents().get(0);
			Model newModel = (Model) lookupRoot();
			
			TreeIterator<Object> oldModelContent = EcoreUtil.getAllContents(oldModel, true);
			TreeIterator<Object> newModelContent = EcoreUtil.getAllContents(newModel, true);
			
			Collection<Transition> oldTransitions = new LinkedList<>();
			Collection<Transition> newTransitions = new LinkedList<>();
			
			Collection<State> oldStates = new LinkedList<>();
			Collection<State> newStates = new LinkedList<>();
			
			while(oldModelContent.hasNext()) {
				Object o = oldModelContent.next();
				if(o instanceof State)
					oldStates.add((State) o);
				else if(o instanceof Transition)
					oldTransitions.add((Transition) o);
					
			}
			
			while(newModelContent.hasNext()) {
				Object o = newModelContent.next();
				if(o instanceof State)
					newStates.add((State) o);
				else if(o instanceof Transition)
					newTransitions.add((Transition) o);
					
			}
			
			deleteTransitions(findDeletedTransitions(oldTransitions, newTransitions));
			deleteStates(findDeletedStates(oldStates, newStates));
			addStates(findAddedStates(oldStates, newStates));
			addTransitions(findAddedTransitions(oldTransitions, newTransitions));
			
			super.saveModel();
		} catch(NotFoundException e) {
			System.err.println("Root object not found in "+getResourceURI().toString());
			e.printStackTrace();
		}
	}
	
	public void deleteTransitions(Collection<Transition> transitions) {
		for(Transition t : transitions) {
			if(t.getName() != null && !t.getName().isEmpty())
				debuggerListener.sendCmd("delete transition "+t.getName());
			else
				debuggerListener.sendCmd("delete transition "+getNestedStateName((State)t.getSource())+"->"+getNestedStateName((State)t.getTarget()));
		}
	}
	
	public void addTransitions(Collection<Transition> transitions) {
		for(Transition t : transitions) {
			//[FIXME] should handle more than one port / more than one signal
			//[FIXME] should support adding transitions with no triggers
			if(t.getTriggers().size() > 0 && t.getTriggers().get(0).getPorts().size() > 0) {
				Trigger trigger = t.getTriggers().get(0);
				if(trigger.getPorts().size() > 0) {
					Event event = trigger.getEvent();

					if(event != null) {
						Operation op = ((CallEvent) event).getOperation();
						if(op != null) {
							String signalName = op.getName();
							String portName = trigger.getPorts().get(0).getName();
							debuggerListener.sendCmd("add transition "+getNestedStateName((State)t.getSource())+"->"+getNestedStateName((State)t.getTarget())+" when "+signalName+" on "+portName);
						}
					}
				}
			}
		}
	}
	
	public void addState(String name) {
		
	}
	
	public void deleteState(String name) {
		
	}
	
	public void addTransition(String from, String to, String signalName, String port, int portIdx) {
		
	}
	
	public void deleteTransition(String name) {
		
	}
	
	//[FIXME] support more than int
	public void setVariable(String name, int value) {
		
	}
	
	public void deleteStates(Collection<State> states) {
		for(State s : states) {
			debuggerListener.sendCmd("delete state "+getNestedStateName(s));
		}
	}
	
	public void addStates(Collection<State> states) {
		for(State s : states) {
			debuggerListener.sendCmd("add state "+getNestedStateName(s));
		}
	}
	
	public String getNestedStateName(State state) {
		String name = state.getName();
		EObject container = state.eContainer();
		
		while(!(container instanceof StateMachine)) {
			if(container instanceof State)
				name = ((State)container).getName() + "::" + name;
			container = container.eContainer();
		}
		
		return name;
	}
	
	public Collection<Transition> findAddedTransitions(Collection<Transition> oldTransitions, Collection<Transition> newTransitions) {
		Collection<Transition> addedTransitions = new LinkedList<>();
		
		HashSet<String> oldTransitionsIds = new HashSet<>();
		for(Transition t :oldTransitions) {
			oldTransitionsIds.add(generateID(t));
		}
		
		for(Transition t :newTransitions) {
			if(!oldTransitionsIds.contains(generateID(t)))
				addedTransitions.add(t);
		}
		
		return addedTransitions;
	}
	
	public Collection<Transition> findDeletedTransitions(Collection<Transition> oldTransitions, Collection<Transition> newTransitions) {
		Collection<Transition> deletedTransitions = new LinkedList<>();
		
		HashSet<String> newTransitionsIds = new HashSet<>();
		for(Transition t :newTransitions) {
			newTransitionsIds.add(generateID(t));
		}
		
		for(Transition t :oldTransitions) {
			if(!newTransitionsIds.contains(generateID(t)))
				deletedTransitions.add(t);
		}
		
		return deletedTransitions;
	}
	
	public Collection<State> findAddedStates(Collection<State> oldStates, Collection<State> newStates) {
		Collection<State> addedStates = new LinkedList<>();
		
		HashSet<String> oldStateIds = new HashSet<>();
		for(State s :oldStates) {
			oldStateIds.add(generateID(s));
		}

		for(State s :newStates) {
			if(!oldStateIds.contains(generateID(s)))
				addedStates.add(s);
		}
		
		return addedStates;
	}
	
	public Collection<State> findDeletedStates(Collection<State> oldStates, Collection<State> newStates) {
		Collection<State> deletedStates = new LinkedList<>();
		
		HashSet<String> newStateIds = new HashSet<>();
		for(State s :newStates) {
			newStateIds.add(generateID(s));
		}
		
		for(State s :oldStates) {
			if(!newStateIds.contains(generateID(s)))
				deletedStates.add(s);
		}
		
		return deletedStates;
	}
	
	public String generateID(Transition t) {
		return EcoreUtil.getURI(t).toString();
	}
	
	public String generateID(State s) {
		return EcoreUtil.getURI(s).toString();
	}
	
	public class DebuggerListener extends Thread {
		private ServerSocket serverSocket;
		private Socket socket;
		private BufferedInputStream in;
		private BufferedOutputStream out;
		private boolean abort;
		
		public void run() {
			while(!abort) {
				try {
					execute();
				} catch(Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
					}
				}
			}
		}
		
		public void execute() throws Exception {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(6969));
			socket = serverSocket.accept();
			serverSocket.close();
			
			in = new BufferedInputStream(socket.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());
			byte[] data = new byte[128];
			
			int read;
			while(!abort && socket.isConnected() && (read = in.read(data)) != -1) {
				String msg = new String(data, 0, read).trim();
				
				// incoming msg's from cmd;
			}
		}
		
		public synchronized void sendCmd(String cmd) {
			System.out.println("sent: "+cmd);
			if(out != null)
				try {
					out.write((cmd+"\n").getBytes());
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
					abort();
				}
		}
		
		public void abort() {
			abort=true;
			try {
				if(serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
			}
			try {
				if(socket != null)
					socket.close();
			} catch (IOException e) {
			}
		}
	}

}
