/**
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.papyrusrt.xtumlrt.external.predefined;

import com.google.common.base.Objects;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrusrt.xtumlrt.common.Annotation;
import org.eclipse.papyrusrt.xtumlrt.common.CommonElement;
import org.eclipse.papyrusrt.xtumlrt.common.CommonFactory;
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement;
import org.eclipse.papyrusrt.xtumlrt.common.Protocol;
import org.eclipse.papyrusrt.xtumlrt.common.Signal;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.RTSModelLibraryMetadata;
import org.eclipse.papyrusrt.xtumlrt.external.predefined.UMLRTProfileUtil;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTExtensions;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger;
import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class RTSModelLibraryUtils {
  @Deprecated
  private static ResourceSet resourceSet;
  
  @Deprecated
  private static boolean reload = true;
  
  private static Model RTSModelLibrary;
  
  public final static String RTS_LIB_ANNOTATION_NAME = "RTSLibraryElement";
  
  /**
   * UML RTS Model Library
   */
  public final static String RTS_MODLIB_PATHMAP = RTSModelLibraryMetadata.INSTANCE.getPathmap();
  
  public final static String RTS_MODLIB_ROOT_ID = RTSModelLibraryMetadata.INSTANCE.getRootId();
  
  /**
   * Registered name and URI of the RTS Model Library
   */
  public final static String RTS_LIBRARY_NAME = "UMLRT-RTS";
  
  public final static URI RTS_LIBRARY_URI = URI.createURI(RTSModelLibraryUtils.RTS_MODLIB_PATHMAP).appendFragment(RTSModelLibraryUtils.RTS_MODLIB_ROOT_ID);
  
  public final static String RTS_LIBRARY_URI_STR = RTSModelLibraryUtils.RTS_LIBRARY_URI.toPlatformString(false);
  
  @Deprecated
  private static Object resolvedRTSLibUri = null;
  
  /**
   * Textual (xtumlrt) RTS Model Library
   */
  public final static String T_RTS_MODLIB_PATHMAP = "pathmap://UMLRTRTSLIB/TUMLRT-RTS.umlrt";
  
  /**
   * Registered name and URI of the RTS Model Library
   */
  public final static String T_RTS_LIBRARY_NAME = "RTSLibrary";
  
  public final static URI T_RTS_LIBRARY_URI = URI.createURI(RTSModelLibraryUtils.T_RTS_MODLIB_PATHMAP);
  
  public final static String T_RTS_LIBRARY_URI_STR = RTSModelLibraryUtils.T_RTS_LIBRARY_URI.toPlatformString(false);
  
  /**
   * Protocol names
   */
  public final static String FRAME_PROTOCOL_NAME = "Frame";
  
  public final static String TIMING_PROTOCOL_NAME = "Timing";
  
  public final static String LOG_PROTOCOL_NAME = "Log";
  
  public final static String TCP_PROTOCOL_NAME = "TCP";
  
  public final static String BASECOMM_PROTOCOL_NAME = "UMLRTBaseCommProtocol";
  
  /**
   * Protocol message names: Timing
   */
  public final static String TIMEOUT_MESSAGE_NAME = "timeout";
  
  /**
   * Protocol message names: BaseComm
   */
  public final static String RTBOUND_MESSAGE_NAME = "rtBound";
  
  public final static String RTUNBOUND_MESSAGE_NAME = "rtUnbound";
  
  /**
   * Type names
   */
  public final static String CAPSULE_ID_TYPE_NAME = "UMLRTCapsuleId";
  
  public final static String TIMER_ID_TYPE_NAME = "UMLRTTimerId";
  
  public final static String TIME_SPEC_TYPE_NAME = "UMLRTTimespec";
  
  public final static String TIME_SPEC_2_TYPE_NAME = "UMLRTTimeSpec";
  
  public final static String MESSAGE_TYPE_NAME = "UMLRTMessage";
  
  /**
   * Packages
   */
  public final static String INTERNAL_PACKAGE_NAME = "Internal";
  
  /**
   * Protocol elements
   */
  private static Collaboration FRAME_PROTOCOL = null;
  
  private static Collaboration TIMING_PROTOCOL = null;
  
  private static Collaboration LOG_PROTOCOL = null;
  
  private static Collaboration TCP_PROTOCOL = null;
  
  private static Collaboration BASECOMM_PROTOCOL = null;
  
  /**
   * Textual version of base protocol
   */
  private static Protocol T_BASECOMM_PROTOCOL = null;
  
  /**
   * Protocol message elements: Timing
   */
  private static Object TIMEOUT_MESSAGE = null;
  
  /**
   * Protocol message names: BaseComm
   */
  private static Object RTBOUND_MESSAGE = null;
  
  private static Object RTUNBOUND_MESSAGE = null;
  
  public static Object setRTSModelLibrary(final Model model) {
    Object _xifexpression = null;
    if ((model != null)) {
      _xifexpression = RTSModelLibraryUtils.RTSModelLibrary = model;
    } else {
      _xifexpression = XTUMLRTLogger.error("The RTS model library was not loaded or registered.");
    }
    return _xifexpression;
  }
  
  public static boolean addSysAnnotation(final NamedElement element) {
    boolean _xblockexpression = false;
    {
      final Annotation sysAnnotation = CommonFactory.eINSTANCE.createAnnotation();
      sysAnnotation.setName(RTSModelLibraryUtils.RTS_LIB_ANNOTATION_NAME);
      _xblockexpression = element.getAnnotations().add(sysAnnotation);
    }
    return _xblockexpression;
  }
  
  @Deprecated
  public static org.eclipse.uml2.uml.Package loadPackage(final URI fullURI) {
    if ((RTSModelLibraryUtils.resourceSet == null)) {
      String _platformString = fullURI.toPlatformString(true);
      String _plus = ("Unable to load package " + _platformString);
      String _plus_1 = (_plus + "; Resource set is null");
      XTUMLRTLogger.warning(_plus_1);
      return null;
    }
    final Resource resource = RTSModelLibraryUtils.resourceSet.getResource(fullURI, true);
    if ((resource == null)) {
      String _platformString_1 = fullURI.toPlatformString(true);
      String _plus_2 = ("Unable to load package " + _platformString_1);
      String _plus_3 = (_plus_2 + "; resource is null");
      XTUMLRTLogger.warning(_plus_3);
      return null;
    }
    final EList<EObject> contents = resource.getContents();
    if ((contents == null)) {
      String _platformString_2 = fullURI.toPlatformString(true);
      String _plus_4 = ("Unable to load package " + _platformString_2);
      String _plus_5 = (_plus_4 + "; contents is null");
      XTUMLRTLogger.warning(_plus_5);
      return null;
    }
    final Object eobj = EcoreUtil.getObjectByType(contents, UMLPackage.Literals.PACKAGE);
    if (((eobj == null) || (!(eobj instanceof org.eclipse.uml2.uml.Package)))) {
      String _platformString_3 = fullURI.toPlatformString(true);
      String _plus_6 = ("Unable to load package " + _platformString_3);
      String _plus_7 = (_plus_6 + "; first element is null or not a UML Package");
      XTUMLRTLogger.warning(_plus_7);
      return null;
    }
    final org.eclipse.uml2.uml.Package pkg = ((org.eclipse.uml2.uml.Package) eobj);
    return pkg;
  }
  
  @Deprecated
  public static Model loadRTSModelLibrary() {
    if ((!RTSModelLibraryUtils.reload)) {
      Object _xifexpression = null;
      if ((RTSModelLibraryUtils.resourceSet == null)) {
        XTUMLRTLogger.warning("Unable to load RTS Model Library; Resource set is null");
        return null;
      }
      return ((Model)_xifexpression);
    }
    final URIConverter uriConverter = RTSModelLibraryUtils.resourceSet.getURIConverter();
    if ((uriConverter == null)) {
      XTUMLRTLogger.warning("Unable to load RTS Model Library; Resource set\'s URI converter is null");
      return null;
    }
    final URI normalizedRTSLibURI = uriConverter.normalize(RTSModelLibraryUtils.RTS_LIBRARY_URI);
    final org.eclipse.uml2.uml.Package pkg = RTSModelLibraryUtils.loadPackage(normalizedRTSLibURI);
    if (((pkg == null) || (!(pkg instanceof Model)))) {
      XTUMLRTLogger.warning("Unable to load RTS Model Library; contained UML Package is not a UML Model");
      return null;
    }
    boolean _isModelLibrary = pkg.isModelLibrary();
    boolean _not = (!_isModelLibrary);
    if (_not) {
      XTUMLRTLogger.warning("Loaded model is not a model library");
    }
    String _qualifiedName = pkg.getQualifiedName();
    boolean _notEquals = (!Objects.equal(_qualifiedName, RTSModelLibraryUtils.RTS_LIBRARY_NAME));
    if (_notEquals) {
      XTUMLRTLogger.warning("Loaded model library\'s name is not RTS");
    }
    RTSModelLibraryUtils.RTSModelLibrary = ((Model) pkg);
    return RTSModelLibraryUtils.RTSModelLibrary;
  }
  
  @Deprecated
  public static Model reloadRTSModelLibrary() {
    Model _xblockexpression = null;
    {
      RTSModelLibraryUtils.reload = true;
      _xblockexpression = RTSModelLibraryUtils.loadRTSModelLibrary();
    }
    return _xblockexpression;
  }
  
  @Deprecated
  public static boolean setReload(final boolean flag) {
    return RTSModelLibraryUtils.reload = flag;
  }
  
  @Deprecated
  public static boolean setResourceSet(final ResourceSet resSet) {
    boolean _xifexpression = false;
    if (((resSet != RTSModelLibraryUtils.resourceSet) || (RTSModelLibraryUtils.resourceSet == null))) {
      boolean _xblockexpression = false;
      {
        RTSModelLibraryUtils.resourceSet = resSet;
        _xblockexpression = RTSModelLibraryUtils.reload = true;
      }
      _xifexpression = _xblockexpression;
    } else {
      _xifexpression = RTSModelLibraryUtils.reload = false;
    }
    return _xifexpression;
  }
  
  @Deprecated
  public static Object setResolvedRTSModelLibraryLocation(final URI resolvedLocation) {
    Object _xblockexpression = null;
    {
      final URIConverter uriConverter = RTSModelLibraryUtils.resourceSet.getURIConverter();
      Object _xifexpression = null;
      if ((uriConverter != null)) {
        Object _xblockexpression_1 = null;
        {
          final URI normalizedRTSLibURI = uriConverter.normalize(URI.createURI(RTSModelLibraryUtils.RTS_MODLIB_PATHMAP));
          uriConverter.getURIMap().put(normalizedRTSLibURI, resolvedLocation);
          _xblockexpression_1 = RTSModelLibraryUtils.resolvedRTSLibUri = resolvedLocation;
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  @Deprecated
  public static Model getRTSModelLibrary() {
    return RTSModelLibraryUtils.RTSModelLibrary;
  }
  
  @Deprecated
  public static Object getResolvedRTSModelLibraryLocation() {
    return RTSModelLibraryUtils.resolvedRTSLibUri;
  }
  
  public static Collaboration getProtocol(final org.eclipse.uml2.uml.Package packge, final String name) {
    Collaboration _xblockexpression = null;
    {
      final PackageableElement protocolContainer = packge.getPackagedElement(name);
      Collaboration _xifexpression = null;
      if (((protocolContainer != null) && (protocolContainer instanceof org.eclipse.uml2.uml.Package))) {
        Collaboration _xblockexpression_1 = null;
        {
          final PackageableElement protocol = ((org.eclipse.uml2.uml.Package) protocolContainer).getPackagedElement(name);
          Collaboration _xifexpression_1 = null;
          if (((protocol != null) && (protocol instanceof Collaboration))) {
            _xifexpression_1 = ((Collaboration) protocol);
          }
          _xblockexpression_1 = _xifexpression_1;
        }
        _xifexpression = _xblockexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public static Collaboration getFrameProtocol() {
    Collaboration _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.FRAME_PROTOCOL == null) || RTSModelLibraryUtils.reload)) {
        RTSModelLibraryUtils.FRAME_PROTOCOL = RTSModelLibraryUtils.getProtocol(RTSModelLibraryUtils.RTSModelLibrary, RTSModelLibraryUtils.FRAME_PROTOCOL_NAME);
      }
      _xblockexpression = RTSModelLibraryUtils.FRAME_PROTOCOL;
    }
    return _xblockexpression;
  }
  
  public static Collaboration getTimingProtocol() {
    Collaboration _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.TIMING_PROTOCOL == null) || RTSModelLibraryUtils.reload)) {
        RTSModelLibraryUtils.TIMING_PROTOCOL = RTSModelLibraryUtils.getProtocol(RTSModelLibraryUtils.RTSModelLibrary, RTSModelLibraryUtils.TIMING_PROTOCOL_NAME);
      }
      _xblockexpression = RTSModelLibraryUtils.TIMING_PROTOCOL;
    }
    return _xblockexpression;
  }
  
  public static Collaboration getLogProtocol() {
    Collaboration _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.LOG_PROTOCOL == null) || RTSModelLibraryUtils.reload)) {
        RTSModelLibraryUtils.LOG_PROTOCOL = RTSModelLibraryUtils.getProtocol(RTSModelLibraryUtils.RTSModelLibrary, RTSModelLibraryUtils.LOG_PROTOCOL_NAME);
      }
      _xblockexpression = RTSModelLibraryUtils.LOG_PROTOCOL;
    }
    return _xblockexpression;
  }
  
  public static Collaboration getTCPProtocol() {
    Collaboration _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.TCP_PROTOCOL == null) || RTSModelLibraryUtils.reload)) {
        RTSModelLibraryUtils.TCP_PROTOCOL = RTSModelLibraryUtils.getProtocol(RTSModelLibraryUtils.RTSModelLibrary, RTSModelLibraryUtils.TCP_PROTOCOL_NAME);
      }
      _xblockexpression = RTSModelLibraryUtils.TCP_PROTOCOL;
    }
    return _xblockexpression;
  }
  
  public static Collaboration getBaseCommProtocol() {
    Collaboration _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.BASECOMM_PROTOCOL == null) || RTSModelLibraryUtils.reload)) {
        final PackageableElement internalPackage = RTSModelLibraryUtils.RTSModelLibrary.getPackagedElement(RTSModelLibraryUtils.INTERNAL_PACKAGE_NAME);
        if ((internalPackage instanceof org.eclipse.uml2.uml.Package)) {
          RTSModelLibraryUtils.BASECOMM_PROTOCOL = RTSModelLibraryUtils.getProtocol(((org.eclipse.uml2.uml.Package)internalPackage), RTSModelLibraryUtils.BASECOMM_PROTOCOL_NAME);
        }
      }
      _xblockexpression = RTSModelLibraryUtils.BASECOMM_PROTOCOL;
    }
    return _xblockexpression;
  }
  
  /**
   * Return textual version of base protocol from textual RTS model library.
   */
  public static Protocol getTextualBaseCommProtocol(final Protocol context) {
    Protocol _xblockexpression = null;
    {
      if ((RTSModelLibraryUtils.T_BASECOMM_PROTOCOL == null)) {
        final Resource res = context.eResource().getResourceSet().getResource(RTSModelLibraryUtils.T_RTS_LIBRARY_URI, true);
        EObject _get = res.getContents().get(0);
        final org.eclipse.papyrusrt.xtumlrt.common.Model root = ((org.eclipse.papyrusrt.xtumlrt.common.Model) _get);
        EList<Protocol> _protocols = root.getProtocols();
        for (final Protocol p : _protocols) {
          String _name = p.getName();
          boolean _equals = Objects.equal(RTSModelLibraryUtils.BASECOMM_PROTOCOL_NAME, _name);
          if (_equals) {
            RTSModelLibraryUtils.T_BASECOMM_PROTOCOL = p;
            return RTSModelLibraryUtils.T_BASECOMM_PROTOCOL;
          }
        }
      }
      _xblockexpression = RTSModelLibraryUtils.T_BASECOMM_PROTOCOL;
    }
    return _xblockexpression;
  }
  
  public static Object getTimeout() {
    Object _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.TIMEOUT_MESSAGE == null) || RTSModelLibraryUtils.reload)) {
        final Function1<Operation, Boolean> _function = (Operation it) -> {
          String _name = it.getName();
          return Boolean.valueOf(Objects.equal(_name, RTSModelLibraryUtils.TIMEOUT_MESSAGE_NAME));
        };
        RTSModelLibraryUtils.TIMEOUT_MESSAGE = IterableExtensions.<Operation>findFirst(UMLRTProfileUtil.getInProtocolMessages(RTSModelLibraryUtils.getTimingProtocol(), false), _function);
      }
      _xblockexpression = RTSModelLibraryUtils.TIMEOUT_MESSAGE;
    }
    return _xblockexpression;
  }
  
  public static Object getRtBound() {
    Object _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.RTBOUND_MESSAGE == null) || RTSModelLibraryUtils.reload)) {
        final Function1<Operation, Boolean> _function = (Operation it) -> {
          String _name = it.getName();
          return Boolean.valueOf(Objects.equal(_name, RTSModelLibraryUtils.RTBOUND_MESSAGE_NAME));
        };
        RTSModelLibraryUtils.RTBOUND_MESSAGE = IterableExtensions.<Operation>findFirst(UMLRTProfileUtil.getInOutProtocolMessages(RTSModelLibraryUtils.getBaseCommProtocol()), _function);
      }
      _xblockexpression = RTSModelLibraryUtils.RTBOUND_MESSAGE;
    }
    return _xblockexpression;
  }
  
  public static Object getRtUnbound() {
    Object _xblockexpression = null;
    {
      if (((RTSModelLibraryUtils.RTUNBOUND_MESSAGE == null) || RTSModelLibraryUtils.reload)) {
        final Function1<Operation, Boolean> _function = (Operation it) -> {
          String _name = it.getName();
          return Boolean.valueOf(Objects.equal(_name, RTSModelLibraryUtils.RTUNBOUND_MESSAGE_NAME));
        };
        RTSModelLibraryUtils.RTUNBOUND_MESSAGE = IterableExtensions.<Operation>findFirst(UMLRTProfileUtil.getInOutProtocolMessages(RTSModelLibraryUtils.getBaseCommProtocol()), _function);
      }
      _xblockexpression = RTSModelLibraryUtils.RTUNBOUND_MESSAGE;
    }
    return _xblockexpression;
  }
  
  public static boolean hasSysAnnotation(final NamedElement element) {
    return (((element != null) && (element.getAnnotations() != null)) && IterableExtensions.<Annotation>exists(element.getAnnotations(), ((Function1<Annotation, Boolean>) (Annotation it) -> {
      String _name = it.getName();
      return Boolean.valueOf(Objects.equal(_name, RTSModelLibraryUtils.RTS_LIB_ANNOTATION_NAME));
    })));
  }
  
  /**
   * Returns true iff the given UML model is the model library.
   */
  public static boolean isRTSModelLibrary(final org.eclipse.uml2.uml.Package packge) {
    boolean _xblockexpression = false;
    {
      boolean _equals = Objects.equal(packge, RTSModelLibraryUtils.RTSModelLibrary);
      if (_equals) {
        return true;
      }
      final URIConverter uriConverter = packge.eResource().getResourceSet().getURIConverter();
      final URI packageURI = EcoreUtil.getURI(packge);
      final URI normalizedPackageURI = uriConverter.normalize(packageURI);
      final URI normalizedRTSLibURI = uriConverter.normalize(RTSModelLibraryUtils.RTS_LIBRARY_URI);
      _xblockexpression = (((packge instanceof Model) && packge.getQualifiedName().equals(RTSModelLibraryUtils.RTS_LIBRARY_NAME)) && Objects.equal(normalizedPackageURI, normalizedRTSLibURI));
    }
    return _xblockexpression;
  }
  
  /**
   * Returns true iff the given xtUMLrt model is the model library.
   * The translator attaches to such model element, an annotation named with
   * RTS_LIBRARY_ID. This method searches for such annotation.
   */
  public static boolean isRTSModelLibrary(final org.eclipse.papyrusrt.xtumlrt.common.Model model) {
    return (((model != null) && Objects.equal(model.getName(), RTSModelLibraryUtils.RTS_LIBRARY_NAME)) && RTSModelLibraryUtils.isSystemElement(model));
  }
  
  /**
   * Return true iff the element belongs to the RTS Model Library
   */
  public static boolean isSystemElement(final CommonElement element) {
    return (((element != null) && (element instanceof NamedElement)) && RTSModelLibraryUtils.hasSysAnnotation(((NamedElement) element)));
  }
  
  public static boolean isSystemElement(final Element element) {
    return ((element.getModel() != null) && RTSModelLibraryUtils.isRTSModelLibrary(element.getModel()));
  }
  
  public static boolean isCapsuleId(final NamedElement type) {
    return ((((type != null) && (type.getName() != null)) && Objects.equal(type.getName(), RTSModelLibraryUtils.CAPSULE_ID_TYPE_NAME)) && RTSModelLibraryUtils.isSystemElement(type));
  }
  
  public static boolean isTextualRTSModelLibrary(final org.eclipse.papyrusrt.xtumlrt.common.Model model) {
    boolean _xblockexpression = false;
    {
      if ((model == null)) {
        return false;
      }
      final URIConverter uriConverter = model.eResource().getResourceSet().getURIConverter();
      final URI packageURI = EcoreUtil.getURI(model);
      final URI normalizedPackageURI = uriConverter.normalize(packageURI);
      final URI normalizedRTSLibURI = uriConverter.normalize(RTSModelLibraryUtils.T_RTS_LIBRARY_URI);
      _xblockexpression = (Objects.equal(model.getName(), RTSModelLibraryUtils.T_RTS_LIBRARY_NAME) && Objects.equal(normalizedPackageURI.toPlatformString(true), normalizedRTSLibURI.toPlatformString(true)));
    }
    return _xblockexpression;
  }
  
  public static boolean isTextualSystemElement(final CommonElement element) {
    return (((element != null) && (XTUMLRTUtil.getRoot(element) instanceof org.eclipse.papyrusrt.xtumlrt.common.Model)) && RTSModelLibraryUtils.isTextualRTSModelLibrary(((org.eclipse.papyrusrt.xtumlrt.common.Model) XTUMLRTUtil.getRoot(element))));
  }
  
  public static boolean isTimerId(final NamedElement type) {
    return ((((type != null) && (type.getName() != null)) && Objects.equal(type.getName(), RTSModelLibraryUtils.TIMER_ID_TYPE_NAME)) && RTSModelLibraryUtils.isSystemElement(type));
  }
  
  public static boolean isTimerSpec(final NamedElement type) {
    return ((((type != null) && (type.getName() != null)) && (Objects.equal(type.getName(), RTSModelLibraryUtils.TIME_SPEC_2_TYPE_NAME) || Objects.equal(type.getName(), RTSModelLibraryUtils.TIME_SPEC_TYPE_NAME))) && RTSModelLibraryUtils.isSystemElement(type));
  }
  
  public static boolean isMessage(final NamedElement type) {
    return ((((type != null) && (type.getName() != null)) && Objects.equal(type.getName(), RTSModelLibraryUtils.MESSAGE_TYPE_NAME)) && RTSModelLibraryUtils.isSystemElement(type));
  }
  
  public static boolean isBaseCommProtocol(final Protocol protocol) {
    return ((((protocol != null) && (protocol.getName() != null)) && Objects.equal(protocol.getName(), RTSModelLibraryUtils.BASECOMM_PROTOCOL_NAME)) && RTSModelLibraryUtils.isSystemElement(protocol));
  }
  
  public static boolean isFrameProtocol(final Protocol protocol) {
    return ((((protocol != null) && (protocol.getName() != null)) && Objects.equal(protocol.getName(), RTSModelLibraryUtils.FRAME_PROTOCOL_NAME)) && RTSModelLibraryUtils.isSystemElement(protocol));
  }
  
  public static boolean isTimingProtocol(final Protocol protocol) {
    return ((((protocol != null) && (protocol.getName() != null)) && Objects.equal(protocol.getName(), RTSModelLibraryUtils.TIMING_PROTOCOL_NAME)) && RTSModelLibraryUtils.isSystemElement(protocol));
  }
  
  public static boolean isLogProtocol(final Protocol protocol) {
    return ((((protocol != null) && (protocol.getName() != null)) && Objects.equal(protocol.getName(), RTSModelLibraryUtils.LOG_PROTOCOL_NAME)) && RTSModelLibraryUtils.isSystemElement(protocol));
  }
  
  public static boolean isTCPProtocol(final Protocol protocol) {
    return ((((protocol != null) && (protocol.getName() != null)) && Objects.equal(protocol.getName(), RTSModelLibraryUtils.TCP_PROTOCOL_NAME)) && RTSModelLibraryUtils.isSystemElement(protocol));
  }
  
  public static Iterable<Signal> getAllUserSignals(final Protocol protocol) {
    final Function1<Signal, Boolean> _function = (Signal it) -> {
      boolean _isSystemElement = RTSModelLibraryUtils.isSystemElement(it);
      return Boolean.valueOf((!_isSystemElement));
    };
    return IterableExtensions.<Signal>filter(XTUMLRTExtensions.getAllSignals(protocol), _function);
  }
  
  public static Iterable<Signal> getUserSignals(final Protocol protocol) {
    final Function1<Signal, Boolean> _function = (Signal it) -> {
      boolean _isSystemElement = RTSModelLibraryUtils.isSystemElement(it);
      return Boolean.valueOf((!_isSystemElement));
    };
    return IterableExtensions.<Signal>filter(XTUMLRTUtil.getSignals(protocol), _function);
  }
}
