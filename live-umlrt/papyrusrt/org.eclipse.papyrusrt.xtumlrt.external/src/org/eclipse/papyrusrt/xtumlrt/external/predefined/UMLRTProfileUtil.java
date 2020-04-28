/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.Capsule;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.CapsulePart;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.Protocol;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.ProtocolContainer;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTConnector;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTMessageKind;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTMessageSet;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTPort;
import org.eclipse.papyrusrt.umlrt.profile.UMLRealTime.RTRedefinedElement;
import org.eclipse.papyrusrt.umlrt.uml.util.UMLRTExtensionUtil;
import org.eclipse.papyrusrt.xtumlrt.util.UML2NamesUtil;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.EncapsulatedClassifier;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * This class provides a facade to the UML-RT profile.
 * 
 * @author epp
 */
public final class UMLRTProfileUtil {

	/** Name of the capsule stereotype. */
	private static final String UML_REAL_TIME_CAPSULE = "UMLRealTime::Capsule";

	/** Name of the capsule part stereotype. */
	private static final String UML_REAL_TIME_CAPSULE_PART = "UMLRealTime::CapsulePart";

	/** Name of the protocol stereotype. */
	private static final String UML_REAL_TIME_PROTOCOL = "UMLRealTime::Protocol";

	/** Name of the port stereotype. */
	private static final String UML_REAL_TIME_RT_PORT = "UMLRealTime::RTPort";

	/** Name of the connector stereotype. */
	private static final String UML_REAL_TIME_RT_CONNECTOR = "UMLRealTime::RTConnector";

	/** Name of the protocol container stereotype. */
	private static final String UML_REAL_TIME_PROTOCOL_CONTAINER = "UMLRealTime::ProtocolContainer";

	/** Name of the redefined element stereotype. */
	private static final String UML_REAL_TIME_RT_REDEFINED_ELEMENT = "UMLRealTime::RTRedefinedElement";

	/** Name of the RT message set stereotype. */
	private static final String UML_REAL_TIME_RT_MESSAGE_SET = "UMLRealTime::RTMessageSet";


	/**
	 * Default Constructor.
	 */
	private UMLRTProfileUtil() {
	}

	/**
	 * Determine whether el is a capsule.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link Capsule} stereotype applied.
	 */
	public static boolean isCapsule(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_CAPSULE);
		return s != null;
	}

	/**
	 * Get the Capsule stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link Capsule} stereotype.
	 */
	public static Capsule getCapsule(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_CAPSULE);
		return s == null ? null : (Capsule) el.getStereotypeApplication(s);
	}

	/**
	 * Determine whether el is a capsule part.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link CapsulePart} stereotype applied.
	 */
	public static boolean isCapsulePart(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_CAPSULE_PART);
		return s != null;
	}

	/**
	 * Get the CapsulePart stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link CapsulePart} stereotype.
	 */
	public static CapsulePart getCapsulePart(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_CAPSULE_PART);
		return s == null ? null : (CapsulePart) el.getStereotypeApplication(s);
	}

	/**
	 * Determine whether el is a protocol.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link Protocol} stereotype applied.
	 */
	public static boolean isProtocol(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_PROTOCOL);
		return s != null;
	}

	/**
	 * Get the Protocol stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link Protocol} stereotype.
	 */
	public static Protocol getProtocol(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_PROTOCOL);
		return s == null ? null : (Protocol) el.getStereotypeApplication(s);
	}

	/**
	 * Get the {@link Collaboration} representing a {@link Protocol} in a given {@link Package} if
	 * the package is a {@link ProtocolContainer}.
	 * 
	 * @param protocolPackage
	 *            - A {@link Package}.
	 * @return The {@link Collaboration} representing a {@link Protocol}.
	 */
	public static Collaboration getProtocolCollaboration(Package protocolPackage) {
		Collaboration collaboration = null;
		if (isProtocolContainer(protocolPackage)) {
			for (Element element : protocolPackage.getOwnedElements()) {
				if (element instanceof Collaboration && isProtocol(element)) {
					collaboration = (Collaboration) element;
					break;
				}
			}
		}
		return collaboration;
	}

	/**
	 * Determine whether el is a port.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link RTPort} stereotype applied.
	 */
	public static boolean isRTPort(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_PORT);
		return s != null;
	}

	/**
	 * Get the RTPort stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link RTPort} stereotype.
	 */
	public static RTPort getRTPort(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_PORT);
		return s == null ? null : (RTPort) el.getStereotypeApplication(s);
	}

	/**
	 * Determine whether el is a connector.
	 * 
	 * @param connector
	 *            - An {@link Connector}.
	 * @return True iff el has the {@link RTConnector} stereotype applied.
	 */
	public static boolean isRTConnector(Connector connector) {
		Stereotype s = connector.getAppliedStereotype(UML_REAL_TIME_RT_CONNECTOR);
		return s != null;
	}

	/**
	 * Get the RTConnector stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link RTConnector} stereotype.
	 */
	public static RTConnector getRTConnector(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_CONNECTOR);
		return s == null ? null : (RTConnector) el.getStereotypeApplication(s);
	}

	/**
	 * Determine whether el is a protocol container.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link ProtocolContainer} stereotype applied.
	 */
	public static boolean isProtocolContainer(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_PROTOCOL_CONTAINER);
		return s != null;
	}

	/**
	 * Get the ProtocolContainer stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link ProtocolContainer} stereotype.
	 */
	public static ProtocolContainer getProtocolContainer(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_PROTOCOL_CONTAINER);
		return s == null ? null : (ProtocolContainer) el.getStereotypeApplication(s);
	}

	/**
	 * Determine whether el is a redefined element.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link RTRedefinedElement} stereotype applied.
	 */
	public static boolean isRTRedefinedElement(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_REDEFINED_ELEMENT);
		return s != null;
	}

	/**
	 * Get the RTRedefinedElement stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link RTRedefinedElement} stereotype.
	 */
	public static RTRedefinedElement getRTRedefinedElement(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_REDEFINED_ELEMENT);
		return s == null ? null : (RTRedefinedElement) el.getStereotypeApplication(s);
	}

	/**
	 * Determine whether el is an RT message set.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el has the {@link RTMessageSet} stereotype applied.
	 */
	public static boolean isRTMessageSet(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_MESSAGE_SET);
		return s != null;
	}

	/**
	 * Get the RTRedefinedElement stereotype for el.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return The {@link RTRedefinedElement} stereotype.
	 */
	public static RTMessageSet getRTMessageSet(Element el) {
		Stereotype s = el.getAppliedStereotype(UML_REAL_TIME_RT_MESSAGE_SET);
		return s == null ? null : (RTMessageSet) el.getStereotypeApplication(s);
	}

	/**
	 * Get the set of all messages of a particular kind from a set of RT message sets from a protocol.
	 * 
	 * @param interfaces
	 *            - An {@link Iterable} over the {@link Interface}s in a {@link ProtocolContainer}.
	 * @param messageKind
	 *            - The {@link RTMessageKind}: IN, OUT or INOUT.
	 * @param includeInOut
	 *            - Whether to include the INOUT messages when messageKind is IN or OUT.
	 * @return An {@link Iterable} of {@link Operation}s that represent the action messages.
	 */
	private static Iterable<Operation> getProtocolMessagesFromRTMessageSets(
			Iterable<Interface> interfaces,
			RTMessageKind messageKind,
			boolean includeInOut) {
		final Set<Operation> operations = new TreeSet<>(new UML2NamesUtil.NameComparator());
		if (interfaces != null) {
			for (Interface iface : interfaces) {
				final RTMessageSet rtMessageSet = getRTMessageSet(iface);
				if (rtMessageSet != null) {
					final RTMessageKind kind = rtMessageSet.getRtMsgKind();
					if (kind == messageKind || (includeInOut && kind == RTMessageKind.IN_OUT)) {
						operations.addAll(iface.getOwnedOperations());
					}
				}
			}
		}
		return operations;
	}

	/**
	 * Get the set of all messages of a particular kind from a set of RT message sets from a protocol.
	 * 
	 * @param collaboration
	 *            - A {@link Collaboration} with the {@link Protocol} stereotype.
	 * @param kind
	 *            - The {@link RTMessageKind}: IN, OUT or INOUT.
	 * @return An {@link Iterable} of {@link Operation}s that represent the action messages.
	 */
	public static Iterable<Operation> getProtocolMessages(Collaboration collaboration, RTMessageKind kind) {
		Iterable<Operation> messages = null;
		switch (kind) {
		case IN:
			messages = getInProtocolMessages(collaboration, false);
			break;
		case OUT:
			messages = getOutProtocolMessages(collaboration, false);
			break;
		case IN_OUT:
			messages = getInOutProtocolMessages(collaboration);
			break;
		default:
			break;
		}
		return messages;
	}

	/**
	 * Get the set of IN messages of a protocol.
	 * 
	 * @param collaboration
	 *            - A {@link Collaboration} with the {@link Protocol} stereotype.
	 * @param includeInOut
	 *            - Whether to include the INOUT messages.
	 * @return An {@link Iterable} of {@link Operation}s that represent the action messages.
	 */
	public static Iterable<Operation> getInProtocolMessages(Collaboration collaboration, boolean includeInOut) {
		// Q: what's the difference between this and allImplementedInterfaces?
		return getProtocolMessagesFromRTMessageSets(collaboration.directlyRealizedInterfaces(), RTMessageKind.IN, true);
	}

	/**
	 * Get the set of OUT messages of a protocol.
	 * 
	 * @param collaboration
	 *            - A {@link Collaboration} with the {@link Protocol} stereotype.
	 * @param includeInOut
	 *            - Whether to include the INOUT messages.
	 * @return An {@link Iterable} of {@link Operation}s that represent the action messages.
	 */
	public static Iterable<Operation> getOutProtocolMessages(Collaboration collaboration, boolean includeInOut) {
		return getProtocolMessagesFromRTMessageSets(collaboration.directlyUsedInterfaces(), RTMessageKind.OUT, true);
	}

	/**
	 * Get the set of INOUT messages of a protocol.
	 * 
	 * @param collaboration
	 *            - A {@link Collaboration} with the {@link Protocol} stereotype.
	 * @return An {@link Iterable} of {@link Operation}s that represent the action messages.
	 */
	public static Iterable<Operation> getInOutProtocolMessages(Collaboration collaboration) {
		// Q: what's the difference between this and allImplementedInterfaces?
		return getProtocolMessagesFromRTMessageSets(collaboration.directlyRealizedInterfaces(), RTMessageKind.IN_OUT, false);
	}

	/**
	 * Determine whether el is an RT message.
	 * 
	 * @param el
	 *            - An {@link Element}.
	 * @return True iff el is an {@link Operation} whose interface is an {@link RTMessageSet}.
	 */
	public static boolean isSignal(Element el) {
		boolean result = false;
		if (el != null && el instanceof Operation) {
			Operation op = (Operation) el;
			Interface iface = op.getInterface();
			result = iface != null && isRTMessageSet(iface);
		}
		return result;
	}

	/**
	 * Get the protocol container of a port.
	 * 
	 * @param umlPort
	 *            - A {@link Port}.
	 * @return A {@link Package} which is a {@link ProtocolContainer}.
	 */
	public static Package getProtocol(Port umlPort) {
		Package protocolContainer = null;
		Type type = umlPort.getType();
		if (type != null) {
			Package pkg = type.getPackage();
			if (pkg != null) {
				protocolContainer = isProtocolContainer(pkg) ? pkg : null;
			}
		}
		return protocolContainer;
	}

	/**
	 * Get the protocol container of a message.
	 * 
	 * @param umlOperation
	 *            - An {@link Operation} (the message).
	 * @return A {@link Package} which is a {@link ProtocolContainer}, or {@code null}
	 *         if the operation is not a message or not in a protocol container.
	 */
	public static Package getProtocol(Operation umlOperation) {
		Package protocolContainer = null;
		if (isSignal(umlOperation)) {
			Package pkg = umlOperation.getNearestPackage();
			if (isProtocolContainer(pkg)) {
				protocolContainer = pkg;
			}
		}
		return protocolContainer;
	}

	/**
	 * A filter that produces only Properties owned by the classifier that are properly stereotyped as
	 * CapsulePart.
	 * 
	 * @param classifier
	 *            - An {@link EncapsulatedClassifier}
	 * @return An {@link Iterable} of {@link Property}(ies) that have the {@link CapsulePart} stereotype.
	 */
	public static Iterable<Property> getCapsuleParts(EncapsulatedClassifier classifier) {
		// Ports are put into a sorted list to make sure that the order is
		// stable between
		// different parts of the generator (as well as between invocations).
		final Set<Property> parts = new TreeSet<>(new UML2NamesUtil.NameComparator());
		for (Property attr : IterableExtensions.filter(classifier.getOwnedMembers(), Property.class)) {
			if (isCapsulePart(attr) && !UMLRTExtensionUtil.isExcluded(attr)) {
				parts.add(attr);
			}
		}
		return parts;
	}

	/**
	 * A filter that produces only Ports owned by the classifier that are properly stereotyped as RTPort
	 * and have a UML-RT protocol.
	 * 
	 * @param classifier
	 *            - An {@link EncapsulatedClassifier}
	 * @return An {@link Iterable} of {@link Port}(ies) that have the {@link RTPort} stereotype.
	 */
	public static Iterable<Port> getRTPorts(EncapsulatedClassifier classifier) {
		// Ports are put into a sorted list to make sure that the order is
		// stable between
		// different parts of the generator (as well as between invocations).
		final Set<Port> ports = new TreeSet<>(new UML2NamesUtil.NameComparator());
		for (Port port : IterableExtensions.filter(classifier.getOwnedMembers(), Port.class)) {
			if (isRTPort(port) && getProtocol(port) != null && !UMLRTExtensionUtil.isExcluded(port)) {
				ports.add(port);
			}
		}
		return ports;
	}

	/**
	 * Returns the set of all ports in the class, including those which have
	 * been inherited and those which are redefinitions of inherited ports.
	 * 
	 * @param klass
	 *            - A {@link Class} with the {@link Capsule} stereotype.
	 * @return An {@link Iterable} of {@link Port}(ies) that have the {@link RTPort} stereotype.
	 */
	public static Iterable<Port> getAllRTPorts(Class klass) {
		final Set<Port> allPorts = new TreeSet<>(new UML2NamesUtil.NameComparator());
		if (klass != null) {
			for (Port p : getRTPorts(klass)) {
				allPorts.add(p);
			}
			Class parent = getBaseRTCapsule(klass);
			if (parent != null) {
				for (Port p : getAllRTPorts(parent)) {
					if (!redefines(klass, p)) {
						allPorts.add(p);
					}
				}
			}
		}
		return allPorts;
	}

	/**
	 * A filter that produces only Ports that are properly stereotyped as RTPort
	 * and have a UML-RT protocol.
	 * 
	 * @param classifier
	 *            - An {@link EncapsulatedClassifier}
	 * @return An {@link Iterable} of {@link Connector}(ies) that have the {@link RTConnector} stereotype.
	 */
	public static Iterable<Connector> getRTConnectors(EncapsulatedClassifier classifier) {
		// Connectors are put into a sorted list to make sure that the order is
		// stable between
		// different parts of the generator (as well as between invocations).
		final Set<Connector> connectors = new TreeSet<>(new UML2NamesUtil.NameComparator());
		for (Connector connector : IterableExtensions.filter(classifier.getOwnedMembers(), Connector.class)) {
			if (isRTConnector(connector) && !UMLRTExtensionUtil.isExcluded(connector)) {
				connectors.add(connector);
			}
		}
		return connectors;
	}

	/**
	 * Returns the set of all connectors in the class, including those which have
	 * been inherited and those which are redefinitions of inherited connectors.
	 * 
	 * @param klass
	 *            - A {@link Class} with the {@link Capsule} stereotype.
	 * @return An {@link Iterable} of {@link Connector}(ies) that have the {@link RTConnector} stereotype.
	 */
	public static Iterable<Connector> getAllRTConnectors(Class klass) {
		final Set<Connector> allConnectors = new TreeSet<>(new UML2NamesUtil.NameComparator());
		if (klass != null) {
			for (Connector c : getRTConnectors(klass)) {
				allConnectors.add(c);
			}
			Class parent = getBaseRTCapsule(klass);
			if (parent != null) {
				for (Connector c : getAllRTConnectors(parent)) {
					if (!redefines(klass, c)) {
						allConnectors.add(c);
					}
				}
			}
		}
		return allConnectors;
	}

	/**
	 * Get the parent capsule of the given capsule.
	 * 
	 * @param klass
	 *            - A {@link Class} with the {@link Capsule} stereotype.
	 * @return The first super-class of klass which has the {@link Capsule} stereotype.
	 */
	private static Class getBaseRTCapsule(Class klass) {
		Class parent = null;
		if (klass != null && !klass.getSuperClasses().isEmpty()) {
			for (Class kls : klass.getSuperClasses()) {
				if (isCapsule(kls)) {
					parent = kls;
					break;
				}
			}
		}
		return parent;
	}

	/**
	 * Returns true iff the given port is inherited and redefined by the given
	 * class.
	 * 
	 * @param klass
	 *            - A {@link Class}.
	 * @param port
	 *            - A {@link Port}.
	 * @return True iff the given port is inherited and redefined by the given class.
	 */
	private static boolean redefines(Class klass, Port port) {
		boolean result = false;
		if (port != null) {
			for (Port p : getRedefinedPorts(klass)) {
				if (p == port || port.equals(p)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns true iff the given port is inherited and redefined by the given
	 * class.
	 * 
	 * @param klass
	 *            - A {@link Class}.
	 * @param connector
	 *            - A {@link Connector}.
	 * @return True iff the given connector is inherited and redefined by the given class.
	 */
	private static boolean redefines(Class klass, Connector connector) {
		boolean result = false;
		if (connector != null) {
			for (Connector c : getRedefinedConnectors(klass)) {
				if (c == connector || connector.equals(c)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Returns an iterable over the ports redefined by the given class.
	 * 
	 * @param klass
	 *            - A {@link Class}.
	 * @return An {@link Iterable} over {@link Port}s from the parent class which are redefined by some port in klass.
	 */
	private static Iterable<Port> getRedefinedPorts(Class klass) {
		List<Port> ports = new ArrayList<>();
		for (Port p : getPortRedefinitions(klass)) {
			// Assume a port redefines only one port.
			Port redefined = p.getRedefinedPorts().get(0);
			ports.add(redefined);
		}
		return ports;
	}

	/**
	 * Returns an iterable over the ports of the given class which redefine some
	 * inherited port.
	 * 
	 * @param klass
	 *            - A {@link Class}.
	 * @return An {@link Iterable} over {@link Port}s in klass which redefine some port in the parent class.
	 */
	private static Iterable<Port> getPortRedefinitions(Class klass) {
		List<Port> ports = new ArrayList<>();
		for (Port p : getRTPorts(klass)) {
			if (p.getRedefinedPorts() != null && !p.getRedefinedPorts().isEmpty()) {
				ports.add(p);
			}
		}
		return ports;
	}

	/**
	 * Returns an iterable over the connectors redefined by the given class.
	 * 
	 * @param klass
	 *            - A {@link Class}.
	 * @return An {@link Iterable} over {@link Connector}s from the parent class which are redefined by some connector in klass.
	 */
	private static Iterable<Connector> getRedefinedConnectors(Class klass) {
		List<Connector> connectors = new ArrayList<>();
		for (Connector c : getConnectorRedefinitions(klass)) {
			// Assume a port redefines only one port.
			Connector redefined = c.getRedefinedConnectors().get(0);
			connectors.add(redefined);
		}
		return connectors;
	}

	/**
	 * Returns an iterable over the connectors of the given class which redefine some
	 * inherited connector.
	 * 
	 * @param klass
	 *            - A {@link Class}.
	 * @return An {@link Iterable} over {@link Connector}s in klass which redefine some connector in the parent class.
	 */
	private static Iterable<Connector> getConnectorRedefinitions(Class klass) {
		List<Connector> connectors = new ArrayList<>();
		for (Connector c : getRTConnectors(klass)) {
			if (c.getRedefinedConnectors() != null && !c.getRedefinedConnectors().isEmpty()) {
				connectors.add(c);
			}
		}
		return connectors;
	}

}
