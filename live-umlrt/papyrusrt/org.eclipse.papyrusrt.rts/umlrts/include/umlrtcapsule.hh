// umlrtcapsule.hh

/*******************************************************************************
* Copyright (c) 2014-2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

#ifndef UMLRTCAPSULE_HH
#define UMLRTCAPSULE_HH

#include "umlrtcommsport.hh"
#include "umlrtcontroller.hh"
#include "umlrtcapsulerole.hh"
#include "umlrtcapsuletocontrollermap.hh"
#include "umlrtslot.hh"

// This is the base class for each generated capsule that is defined in the model.

// It will have the implementation of the state machine and will include data
// like the current state.

// Sub-classes define the model's capsules and contain user-defined and methods.

struct UMLRTSlot;
class UMLRTMessage;
class UMLRTController;
struct UMLRTCapsuleClass;
class UMLRTRtsInterface;

class UMLRTCapsule
{
public:
    typedef void (UMLRTCapsule::*UMLRTAction)(const UMLRTMessage*);
    typedef bool (UMLRTCapsule::*UMLRTGuard)(const UMLRTMessage*);

protected:
    class UMLRTState
    	{
    	public:
    		UMLRTState ( const char * _name, UMLRTAction _entry = NULL, UMLRTAction _exit = NULL );
    		~UMLRTState ( );

    		const char * name;
    		UMLRTAction entry;
    		UMLRTAction exit;

    		UMLRTHashMap * triggerTable; // HashMap of trigger names to transition objects
    	};

public:
    typedef UMLRTCapsule::UMLRTState * (UMLRTCapsule::*UMLRTChoice)(const UMLRTMessage*);

protected:
    	class UMLRTTransition
    	{
    	public:
    		UMLRTTransition ( const char * _name, UMLRTState * _from, UMLRTState * _to, UMLRTChoice _choice, UMLRTGuard _guard = NULL, UMLRTAction _effect = NULL );
    		~UMLRTTransition ( );

    		const char * name;
    		UMLRTState * from;
    		UMLRTState * to;
    		UMLRTChoice choice;
    		UMLRTGuard guard;
    		UMLRTAction effect;
    		bool added;
    	};

    	class UMLRTTrigger
    	{
    	public:
    		UMLRTTrigger ( const char * _portName, const char * _signalName );
    		~UMLRTTrigger ( );

    		const char * portName;
    		const char * signalName;
    		char name[64];
    	};

    	class UMLRTTransitionTrable
    	{
    	public:
    		UMLRTTransitionTrable ( );
    		~UMLRTTransitionTrable ( );

    		bool addState ( UMLRTState * state );
    		UMLRTState * getState ( const char * name );
    		UMLRTTransition * getTransition ( const char * name );
    		UMLRTTransition * getTransition ( UMLRTState * state, UMLRTTrigger * trigger );
    		bool addTransition ( UMLRTState * state, UMLRTTrigger * trigger, UMLRTTransition * transition );
    		void removeState ( UMLRTState * state );
    		void removeTransition ( UMLRTTransition * transition );
    		void addBreakpoint( UMLRTState * state );
    		void removeBreakpoint( UMLRTState * state );
    		bool shouldBreak( UMLRTState * state );
    		void print( FILE * stream = stdout );
    		void print ( UMLRTState * state, FILE * stream = stdout );

    		UMLRTHashMap * stateTable; // HashMap of state names to state objects
    		UMLRTHashMap * transitionsTable; // HashMap of transitions names to transitions objects
    		UMLRTHashMap * breakpoints; // break points map
    	};

    	class UMLRTHistoryMap
    	{
    	public:
    		UMLRTHistoryMap ( );
    		~UMLRTHistoryMap ( );

    		void put ( const char * compositeState, const char * subState );
    		const char * get ( const char * compositeState );
    		UMLRTHashMap * historyMap;
    	};

    // User can get the controller e.g. for incarnation or getting the last error.
    UMLRTController * context ( ) const { return slot->controller; }

    UMLRTCapsule ( const UMLRTRtsInterface * rtsif, const UMLRTCapsuleClass * capsuleClass, UMLRTSlot * slot, const UMLRTCommsPort * * borderPorts_, const UMLRTCommsPort * * internalPorts_, bool isStatic_ );

    const UMLRTRtsInterface * rtsif;
    const UMLRTCapsuleClass * capsuleClass;
    UMLRTSlot * const slot;
    const UMLRTCommsPort * * borderPorts; // Capsule's border ports. May point to slot port or is a proxy or unbound port.
    const UMLRTCommsPort * * internalPorts; // Capsule's internal ports. Each of these may be replicated.
    bool isStatic; // Keep this until we've soak-tested incarnate/destroy + import/deport.

    UMLRTState * currentState;
    UMLRTTransitionTrable transitionTable;
    UMLRTHistoryMap historyMap;

public:

    virtual ~UMLRTCapsule ( );
    virtual void bindPort ( bool isBorder, int portIndex, int farEndIndex );
    virtual const char * getCurrentStateString ( ) const { return ""; }
    virtual void initialize ( const UMLRTMessage & msg ) = 0;
    virtual void logMsg ( );
    virtual void unexpectedMessage ( ) const;
    virtual void unbindPort ( bool isBorder, int portIndex, int farEndIndex );

    virtual void inject ( const UMLRTMessage & msg );

    const UMLRTCommsPort * * getBorderPorts ( ) const { return borderPorts; }
    size_t getIndex ( ) const { return slot->capsuleIndex; }
    const UMLRTCommsPort * * getInternalPorts ( ) const { return internalPorts; }
    const char * getName ( ) const { return slot->role() ? slot->role()->name : "(no role)"; }
    UMLRTSlot * getSlot ( ) const { return slot; }
    const char * getTypeName ( ) const { return getClass()->name; }
    UMLRTController::Error getError ( ) const { return context()->getError(); }
    const UMLRTCapsuleClass * getClass ( ) const { return capsuleClass; }
    const UMLRTMessage * getMsg ( ) const { return msg; }
    bool isType ( const char * classname ) const { return !strcmp(classname, getTypeName()); }
    const char * name ( ) const { return slot->name; }

    bool addState ( const char * name );
    void removeState ( const char * name );
    bool addTransition ( const char * name, const char * from, const char * to, const char * portName, const char * signalName );
    void removeTransition ( const char * name );
    void removeTransitions ( const char * from,  const char * to );
	void addBreakpoint ( const char * stateName );
	void removeBreakpoint ( const char * stateName );
	void execTransition( const char * transitionName );
	virtual void defaultPartialityFunction ( bool shouldBreak );

    const UMLRTMessage * msg;
    const char * lastTransition;
};

#endif // UMLRTCAPSULE_HH
