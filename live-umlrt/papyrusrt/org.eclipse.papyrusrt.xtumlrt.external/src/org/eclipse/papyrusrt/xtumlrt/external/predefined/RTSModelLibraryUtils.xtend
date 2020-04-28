/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.xtumlrt.external.predefined

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.papyrusrt.xtumlrt.common.CommonElement
import org.eclipse.papyrusrt.xtumlrt.common.CommonFactory
import org.eclipse.papyrusrt.xtumlrt.common.Model
import org.eclipse.papyrusrt.xtumlrt.common.NamedElement
import org.eclipse.papyrusrt.xtumlrt.common.Protocol
import org.eclipse.papyrusrt.xtumlrt.common.Signal
import org.eclipse.uml2.uml.Collaboration
import org.eclipse.uml2.uml.Element
import org.eclipse.uml2.uml.Package
import org.eclipse.uml2.uml.UMLPackage

import org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTLogger
import static extension org.eclipse.papyrusrt.xtumlrt.external.predefined.UMLRTProfileUtil.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTUtil.*
import static extension org.eclipse.papyrusrt.xtumlrt.util.XTUMLRTExtensions.*

class RTSModelLibraryUtils
{

	@Deprecated
    private static var ResourceSet resourceSet
	@Deprecated
    private static var reload = true
    private static var org.eclipse.uml2.uml.Model RTSModelLibrary

    public static val RTS_LIB_ANNOTATION_NAME = "RTSLibraryElement";

    /** UML RTS Model Library */
    public static val RTS_MODLIB_PATHMAP           = RTSModelLibraryMetadata.INSTANCE.pathmap;
    public static val RTS_MODLIB_ROOT_ID           = RTSModelLibraryMetadata.INSTANCE.rootId;

    /** Registered name and URI of the RTS Model Library */
    public static val RTS_LIBRARY_NAME    = "UMLRT-RTS"
    public static val RTS_LIBRARY_URI     = URI.createURI(RTS_MODLIB_PATHMAP).appendFragment(RTS_MODLIB_ROOT_ID)
    public static val RTS_LIBRARY_URI_STR = RTS_LIBRARY_URI.toPlatformString( false )
	@Deprecated
    private static var resolvedRTSLibUri = null

    /** Textual (xtumlrt) RTS Model Library */
    public static val T_RTS_MODLIB_PATHMAP           = "pathmap://UMLRTRTSLIB/TUMLRT-RTS.umlrt";

    /** Registered name and URI of the RTS Model Library */
    public static val T_RTS_LIBRARY_NAME    = "RTSLibrary"
    public static val T_RTS_LIBRARY_URI     = URI.createURI(T_RTS_MODLIB_PATHMAP)
    public static val T_RTS_LIBRARY_URI_STR = T_RTS_LIBRARY_URI.toPlatformString( false )

    /** Protocol names */
    public static val FRAME_PROTOCOL_NAME    = "Frame"
    public static val TIMING_PROTOCOL_NAME   = "Timing"
    public static val LOG_PROTOCOL_NAME      = "Log"
    public static val TCP_PROTOCOL_NAME      = "TCP"
    public static val BASECOMM_PROTOCOL_NAME = "UMLRTBaseCommProtocol"

    /** Protocol message names: Timing */
    public static val TIMEOUT_MESSAGE_NAME   = "timeout"

    /** Protocol message names: BaseComm */
    public static val RTBOUND_MESSAGE_NAME   = "rtBound"
    public static val RTUNBOUND_MESSAGE_NAME = "rtUnbound"

    /** Type names */
    public static val CAPSULE_ID_TYPE_NAME  = "UMLRTCapsuleId"
    public static val TIMER_ID_TYPE_NAME    = "UMLRTTimerId"
    public static val TIME_SPEC_TYPE_NAME   = "UMLRTTimespec"
    public static val TIME_SPEC_2_TYPE_NAME = "UMLRTTimeSpec"
    public static val MESSAGE_TYPE_NAME     = "UMLRTMessage"
    
    /** Packages */
    public static val INTERNAL_PACKAGE_NAME  = "Internal"

    /** Protocol elements */
    private static var Collaboration FRAME_PROTOCOL    = null
    private static var Collaboration TIMING_PROTOCOL   = null
    private static var Collaboration LOG_PROTOCOL      = null
    private static var Collaboration TCP_PROTOCOL      = null
    private static var Collaboration BASECOMM_PROTOCOL = null
    
    /** Textual version of base protocol */
    private static var Protocol T_BASECOMM_PROTOCOL = null
    

    /** Protocol message elements: Timing */
    private static var TIMEOUT_MESSAGE   = null

    /** Protocol message names: BaseComm */
    private static var RTBOUND_MESSAGE   = null
    private static var RTUNBOUND_MESSAGE = null

	static def setRTSModelLibrary(org.eclipse.uml2.uml.Model model) 
	{
		if (model !== null)
		{
			RTSModelLibrary = model
		}
		else
		{
			XTUMLRTLogger.error("The RTS model library was not loaded or registered.")
		}
	}

    static def addSysAnnotation( NamedElement element )
    {
        val sysAnnotation = CommonFactory.eINSTANCE.createAnnotation
        sysAnnotation.name = RTS_LIB_ANNOTATION_NAME
        element.annotations.add( sysAnnotation )
    }

	@Deprecated
    static def loadPackage( URI fullURI )
    {
        if (resourceSet === null)
        {
            XTUMLRTLogger.warning( "Unable to load package " + fullURI.toPlatformString(true) + "; Resource set is null" )
            return null
        }
        val Resource resource = resourceSet.getResource( fullURI, true )
        if (resource === null)
        {
            XTUMLRTLogger.warning( "Unable to load package " + fullURI.toPlatformString(true) + "; resource is null" )
            return null
        }
        val contents = resource.getContents()
        if (contents === null)
        {
            XTUMLRTLogger.warning( "Unable to load package " + fullURI.toPlatformString(true) + "; contents is null" )
            return null
        }
        val eobj = EcoreUtil.getObjectByType( contents, UMLPackage.Literals.PACKAGE )
        if (eobj === null || !(eobj instanceof Package))
        {
            XTUMLRTLogger.warning( "Unable to load package " + fullURI.toPlatformString(true) + "; first element is null or not a UML Package" )
            return null
        }
        val pkg = eobj as Package
        return pkg
    }

	@Deprecated
    static def loadRTSModelLibrary()
    {
        if (!reload) return
        if (resourceSet === null)
        {
            XTUMLRTLogger.warning( "Unable to load RTS Model Library; Resource set is null" )
            return null
        }
        val uriConverter = resourceSet.URIConverter
        if (uriConverter === null)
        {
            XTUMLRTLogger.warning( "Unable to load RTS Model Library; Resource set's URI converter is null" )
            return null
        }
        val normalizedRTSLibURI  = uriConverter.normalize( RTS_LIBRARY_URI )
        val pkg = loadPackage( normalizedRTSLibURI )
        if (pkg === null || !(pkg instanceof org.eclipse.uml2.uml.Model))
        {
            XTUMLRTLogger.warning( "Unable to load RTS Model Library; contained UML Package is not a UML Model" )
            return null
        }
        if (!pkg.isModelLibrary)
        {
            XTUMLRTLogger.warning( "Loaded model is not a model library" )
        }
        if (pkg.qualifiedName != RTS_LIBRARY_NAME)
        {
            XTUMLRTLogger.warning( "Loaded model library's name is not RTS" )
        }
        RTSModelLibrary = pkg as org.eclipse.uml2.uml.Model
        return RTSModelLibrary
    }

	@Deprecated
    static def reloadRTSModelLibrary()
    {
        reload = true
        loadRTSModelLibrary
    }

	@Deprecated
    static def setReload( boolean flag )
    {
        reload = flag
    }

	@Deprecated
    static def setResourceSet( ResourceSet resSet )
    {
        if (resSet !== resourceSet || resourceSet === null)
        {
            resourceSet = resSet
            reload = true
        }
        else
        {
            reload = false
        }
    }

	@Deprecated
    static def setResolvedRTSModelLibraryLocation( URI resolvedLocation )
    {
        val uriConverter = resourceSet.URIConverter
        if (uriConverter !== null)
        {
            val normalizedRTSLibURI  = uriConverter.normalize( URI.createURI(RTS_MODLIB_PATHMAP) )
            uriConverter.URIMap.put( normalizedRTSLibURI, resolvedLocation )
            resolvedRTSLibUri = resolvedLocation
        }
    }

	@Deprecated
    static def getRTSModelLibrary()
    {
        RTSModelLibrary
    }

	@Deprecated
    static def getResolvedRTSModelLibraryLocation()
    {
        resolvedRTSLibUri
    }

    static def getProtocol( Package packge, String name )
    {
        val protocolContainer = packge.getPackagedElement( name )
        if (protocolContainer !== null && protocolContainer instanceof Package)
        {
            val protocol = (protocolContainer as Package).getPackagedElement( name )
            if (protocol !== null && protocol instanceof Collaboration)
                protocol as Collaboration
        }
    }

    static def getFrameProtocol()
    {
        if (FRAME_PROTOCOL === null || reload)
        {
            FRAME_PROTOCOL = getProtocol( RTSModelLibrary, FRAME_PROTOCOL_NAME )
        }
        FRAME_PROTOCOL
    }

    static def getTimingProtocol()
    {
        if (TIMING_PROTOCOL === null || reload)
        {
            TIMING_PROTOCOL = getProtocol( RTSModelLibrary, TIMING_PROTOCOL_NAME )
        }
        TIMING_PROTOCOL
    }

    static def getLogProtocol()
    {
        if (LOG_PROTOCOL === null || reload)
        {
            LOG_PROTOCOL = getProtocol( RTSModelLibrary, LOG_PROTOCOL_NAME )
        }
        LOG_PROTOCOL
    }

    static def getTCPProtocol()
    {
        if (TCP_PROTOCOL === null || reload)
        {
            TCP_PROTOCOL = getProtocol( RTSModelLibrary, TCP_PROTOCOL_NAME )
        }
        TCP_PROTOCOL
    }
	
	
    static def getBaseCommProtocol()
    {
        if (BASECOMM_PROTOCOL === null || reload)
        {
            val internalPackage = RTSModelLibrary.getPackagedElement( INTERNAL_PACKAGE_NAME )
            if (internalPackage instanceof Package)
                BASECOMM_PROTOCOL = getProtocol( internalPackage, BASECOMM_PROTOCOL_NAME )
        }
        BASECOMM_PROTOCOL
    }
    
    /**
     * Return textual version of base protocol from textual RTS model library.
     */
    static def Protocol getTextualBaseCommProtocol(Protocol context){
    	if(T_BASECOMM_PROTOCOL === null){
		    val res = context.eResource.resourceSet.getResource(T_RTS_LIBRARY_URI, true)
	    	val root = res.contents.get(0) as Model
	    	for(p : root.protocols){
	    		if(BASECOMM_PROTOCOL_NAME == p.name){
	    			T_BASECOMM_PROTOCOL = p
	    			return T_BASECOMM_PROTOCOL
	    		}
	    	}
    	}
    	T_BASECOMM_PROTOCOL
    }

    static def getTimeout()
    {
        if (TIMEOUT_MESSAGE === null || reload)
        {
            TIMEOUT_MESSAGE = timingProtocol.getInProtocolMessages( false ).findFirst[ it.name == TIMEOUT_MESSAGE_NAME ]
        }
        TIMEOUT_MESSAGE
    }

    static def getRtBound()
    {
        if (RTBOUND_MESSAGE === null || reload)
        {
            RTBOUND_MESSAGE = baseCommProtocol.getInOutProtocolMessages.findFirst[ it.name == RTBOUND_MESSAGE_NAME ]
        }
        RTBOUND_MESSAGE
    }

    static def getRtUnbound()
    {
        if (RTUNBOUND_MESSAGE === null || reload)
        {
            RTUNBOUND_MESSAGE = baseCommProtocol.getInOutProtocolMessages.findFirst[ it.name == RTUNBOUND_MESSAGE_NAME ]
        }
        RTUNBOUND_MESSAGE
    }

    static def hasSysAnnotation( NamedElement element )
    {
        element !== null
        && element.annotations !== null
        && element.annotations.exists[ (it.name == RTS_LIB_ANNOTATION_NAME) ]
    }

    /**
     * Returns true iff the given UML model is the model library.
     */
    static def boolean isRTSModelLibrary( Package packge )
    {
        if (packge == RTSModelLibrary) return true
        val uriConverter = packge.eResource.resourceSet.URIConverter
        val packageURI = EcoreUtil.getURI( packge )
        val normalizedPackageURI = uriConverter.normalize( packageURI )
        val normalizedRTSLibURI  = uriConverter.normalize( RTS_LIBRARY_URI )

        packge instanceof org.eclipse.uml2.uml.Model
        && packge.getQualifiedName().equals( RTS_LIBRARY_NAME )
//        && packge.isModelLibrary()
        && normalizedPackageURI == normalizedRTSLibURI
    }

    /**
     * Returns true iff the given xtUMLrt model is the model library.
     * The translator attaches to such model element, an annotation named with
     * RTS_LIBRARY_ID. This method searches for such annotation.
     */
    static def boolean isRTSModelLibrary( Model model )
    {
        model !== null
        && model.name == RTS_LIBRARY_NAME
        && model.isSystemElement
    }

    /**
     * Return true iff the element belongs to the RTS Model Library
     */
    static def boolean isSystemElement( CommonElement element )
    {
        element !== null
        && element instanceof NamedElement
        && (element as NamedElement).hasSysAnnotation
    }

    static def boolean isSystemElement( Element element )
    {
        element.model !== null
        && element.model.isRTSModelLibrary
    }

    static def isCapsuleId( NamedElement type )
    {
        type !== null && type.name !== null
        && type.name == CAPSULE_ID_TYPE_NAME
        && isSystemElement( type )
    }

    static def isTextualRTSModelLibrary( Model model )
    {
        if (model === null) return false
        val uriConverter = model.eResource.resourceSet.URIConverter
        val packageURI = EcoreUtil.getURI( model )
        val normalizedPackageURI = uriConverter.normalize( packageURI )
        val normalizedRTSLibURI  = uriConverter.normalize( T_RTS_LIBRARY_URI )
		
        model.name == T_RTS_LIBRARY_NAME 
        && normalizedPackageURI.toPlatformString(true) == normalizedRTSLibURI.toPlatformString(true)
    }

    static def isTextualSystemElement( CommonElement element )
    {
        element !== null
        && element.root instanceof Model
        && isTextualRTSModelLibrary( element.root as Model )
    }

    static def isTimerId( NamedElement type )
    {
        type !== null && type.name !== null
        && type.name == TIMER_ID_TYPE_NAME
        && isSystemElement( type )
    }

    static def isTimerSpec( NamedElement type )
    {
        type !== null && type.name !== null
        && (type.name == TIME_SPEC_2_TYPE_NAME || type.name == TIME_SPEC_TYPE_NAME)  // TODO Until Bug 469218 is resolved
        && isSystemElement( type )
    }
    
    static def isMessage(NamedElement type) {
        type !== null && type.name !== null 
        && type.name == MESSAGE_TYPE_NAME && isSystemElement(type)
    }

    static def isBaseCommProtocol( Protocol protocol )
    {
        protocol !== null && protocol.name !== null
        && protocol.name == BASECOMM_PROTOCOL_NAME
        && isSystemElement( protocol )
    }

    static def isFrameProtocol( Protocol protocol )
    {
        protocol !== null && protocol.name !== null
        && protocol.name == FRAME_PROTOCOL_NAME
        && isSystemElement( protocol )
    }

    static def isTimingProtocol( Protocol protocol )
    {
        protocol !== null && protocol.name !== null
        && protocol.name == TIMING_PROTOCOL_NAME
        && isSystemElement( protocol )
    }

    static def isLogProtocol( Protocol protocol )
    {
        protocol !== null && protocol.name !== null
        && protocol.name == LOG_PROTOCOL_NAME
        && isSystemElement( protocol )
    }

    static def isTCPProtocol( Protocol protocol )
    {
        protocol !== null && protocol.name !== null
        && protocol.name == TCP_PROTOCOL_NAME
        && isSystemElement( protocol )
    }
    
    static def Iterable<Signal> getAllUserSignals( Protocol protocol )
    {
        protocol.allSignals.filter[ !(it.isSystemElement) ]
    }

    static def Iterable<Signal> getUserSignals( Protocol protocol )
    {
        protocol.signals.filter[ !(it.isSystemElement) ]
    }

}