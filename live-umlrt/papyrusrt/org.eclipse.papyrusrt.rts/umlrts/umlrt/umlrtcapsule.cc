// umlrtcapsule.cc

/*******************************************************************************
* Copyright (c) 2015 Zeligsoft (2009) Limited  and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

#include "umlrtcapsuleclass.hh"
#include "umlrtcapsule.hh"
#include "umlrtframeservice.hh"
#include "umlrtmessage.hh"
#include "basedebug.hh"
#include "basedebugtype.hh"

#include <chrono>
#include <iostream>

UMLRTCapsule::~UMLRTCapsule ( )
{
    BDEBUG(BD_INSTANTIATE, "%s destructor\n", slot->name);
    UMLRTCapsuleToControllerMap::removeCapsule(slot->name, this);
}

UMLRTCapsule::UMLRTCapsule ( const UMLRTRtsInterface * rtsif_, const UMLRTCapsuleClass * capsuleClass_, UMLRTSlot * slot, const UMLRTCommsPort * * borderPorts_, const UMLRTCommsPort * * internalPorts_, bool isStatic_ ) : msg(NULL), rtsif(rtsif_), capsuleClass(capsuleClass_), slot(slot), borderPorts(borderPorts_), internalPorts(internalPorts_), isStatic(isStatic_), currentState( NULL ), lastTransition( "" )
{
    BDEBUG(BD_INSTANTIATE, "slot %s constructor\n", slot->name);
    UMLRTCapsuleToControllerMap::addCapsule(slot->name, this);
}

void UMLRTCapsule::bindPort ( bool isBorder, int portIndex, int farEndIndex )
{
}

void UMLRTCapsule::unbindPort ( bool isBorder, int portIndex, int farEndIndex )
{
}

void UMLRTCapsule::inject( const UMLRTMessage & message )
{
	static long durationCount = 0;
	static long durationSum = 0;

	msg = &message;
	if(currentState != NULL)
	{
		//transitionTable.print(currentState);

		auto start = std::chrono::high_resolution_clock::now();

		UMLRTTrigger * trig = new UMLRTTrigger(message.destPort->role()->name, message.getSignalName());
		UMLRTTransition * transition = transitionTable.getTransition(currentState, trig);
		delete trig;

		auto stop = std::chrono::high_resolution_clock::now();
		auto duration = std::chrono::duration_cast<std::chrono::nanoseconds>(stop - start);

		durationSum += duration.count();
		durationCount++;

//		std::cout << "Avg. search time: " << (durationSum/durationCount) << "\n";

		if(strcmp(message.getSignalName(), "dbg") != 0 && transition != NULL)
			execTransition(transition->name);
		else
			this->defaultPartialityFunction(false);
	}
    else
    		unexpectedMessage();

}

void UMLRTCapsule::logMsg ( )
{
    if (base::debugTypeEnabled(BD_LOGMSG))
    {
        const UMLRTMessage * msg = getMsg();
        fprintf(stdout, "LOGMSG: capsule %s received signal %s", name(), msg->signal.getName());
        if (msg->signal.getType() != NULL)
        {
            fprintf(stdout, " ");
            UMLRTObject_fprintf(stdout, msg->signal.getType(), msg->signal.getPayload(), 0/*nest*/, 1/*arraysize*/);
        }
        fprintf(stdout, "\n");
    }
}

void UMLRTCapsule::unexpectedMessage ( ) const
{
    fprintf( stderr, "Unexpected message to capsule instance %s role %s on port %s protocol %s signal %s\n",
            name(),
            getName(),
            getMsg()->sap() ? getMsg()->sap()->getName() : "(no sap)",
            getMsg()->sap() ? (getMsg()->sap()->role() ? getMsg()->sap()->role()->protocol : "(sap no role)") : "(no sap)",
            getMsg()->getSignalName());
}

void UMLRTCapsule::defaultPartialityFunction ( bool shouldBreak )
{
}

UMLRTCapsule::UMLRTTransitionTrable::UMLRTTransitionTrable ( )
{
	stateTable = new UMLRTHashMap("stateTable", UMLRTHashMap::compareString, false);
	transitionsTable = new UMLRTHashMap("transitionsTable", UMLRTHashMap::compareString, false);
	breakpoints = new UMLRTHashMap("breakpoints", UMLRTHashMap::compareString, false);
}

UMLRTCapsule::UMLRTTransitionTrable::~UMLRTTransitionTrable ( )
{
	//delete stateTable;
	//delete transitionsTable;
	//delete breakpoints;
}

void UMLRTCapsule::UMLRTTransitionTrable::addBreakpoint( UMLRTState * state )
{
	UMLRTState * enabled = (UMLRTState*) breakpoints->getObject(state->name);
	if(enabled == NULL)
		breakpoints->insert(state->name, (void*) state);
	//this->print();
}

void UMLRTCapsule::UMLRTTransitionTrable::removeBreakpoint( UMLRTState * state )
{
	UMLRTState * enabled = (UMLRTState*) breakpoints->getObject(state->name);
	if(enabled != NULL)
		breakpoints->remove(enabled->name);
	//this->print();
}

bool UMLRTCapsule::UMLRTTransitionTrable::shouldBreak( UMLRTState * state )
{
	UMLRTState * enabled = (UMLRTState*) breakpoints->getObject(state->name);
	return enabled != NULL;
}


UMLRTCapsule::UMLRTState::UMLRTState( const char * _name, UMLRTAction _entry, UMLRTAction _exit ) :
		name(strdup(_name)), entry(_entry), exit(_exit)
{
	triggerTable = new UMLRTHashMap("triggerTable", UMLRTHashMap::compareString, false);
}

UMLRTCapsule::UMLRTState::~UMLRTState ( )
{
	//free(name);
	//delete triggerTable;
}

UMLRTCapsule::UMLRTTransition::UMLRTTransition ( const char * _name, UMLRTState * _from, UMLRTState * _to, UMLRTChoice _choice, UMLRTGuard _guard, UMLRTAction _effect ) :
		name(strdup(_name)), from(_from), to(_to), choice(_choice), guard(_guard), effect(_effect), added(false)
{
}

UMLRTCapsule::UMLRTTransition::~UMLRTTransition ( )
{
	//free(name);
}

UMLRTCapsule::UMLRTTrigger::UMLRTTrigger ( const char * _portName, const char * _signalName ) :
		portName(strdup(_portName)), signalName(strdup(_signalName))
{
	sprintf(name, "%s:%s", portName, signalName);
}

UMLRTCapsule::UMLRTTrigger::~UMLRTTrigger ( )
{
//	free((void*)portName);
//	free((void*)signalName);
}

bool UMLRTCapsule::addState ( const char * name )
{
	return transitionTable.addState(new UMLRTCapsule::UMLRTState(name));
}

void UMLRTCapsule::removeState ( const char * name )
{
	UMLRTCapsule::UMLRTState * state = transitionTable.getState(name);
	if(state != NULL)
		transitionTable.removeState(state);
}

void UMLRTCapsule::addBreakpoint( const char * stateName )
{
	UMLRTCapsule::UMLRTState * state = transitionTable.getState(stateName);
	if(state != NULL)
		transitionTable.addBreakpoint(state);
}

void UMLRTCapsule::removeBreakpoint( const char * stateName )
{
	UMLRTCapsule::UMLRTState * state = transitionTable.getState(stateName);
	if(state != NULL)
		transitionTable.removeBreakpoint(state);
}

bool UMLRTCapsule::addTransition ( const char * name, const char * from, const char * to, const char * portName, const char * signalName )
{
	UMLRTCapsule::UMLRTState * fromState = transitionTable.getState(from);
	if(fromState == NULL)
		return false;

	UMLRTCapsule::UMLRTState * toState = transitionTable.getState(to);
	if(fromState == NULL || toState == NULL)
		return false;

	UMLRTCapsule::UMLRTTransition * trans = new UMLRTCapsule::UMLRTTransition(name, fromState, toState, NULL, NULL, NULL);
	trans->added = true;

	return transitionTable.addTransition(
    		fromState,
		new UMLRTCapsule::UMLRTTrigger( portName, signalName ),
		trans);
}

void UMLRTCapsule::removeTransition ( const char * name )
{
	UMLRTCapsule::UMLRTTransition * transition = transitionTable.getTransition(name);
	if(transition != NULL)
		transitionTable.removeTransition(transition);
}

void UMLRTCapsule::removeTransitions ( const char * from,  const char * to )
{
	UMLRTCapsule::UMLRTState * fromState = transitionTable.getState(from);
	UMLRTCapsule::UMLRTState * toState = transitionTable.getState(to);

	if(fromState == NULL || toState == NULL)
		return;

	//[FIXME] support multiple transitions
	UMLRTCapsule::UMLRTTransition * toRemove = NULL;
	UMLRTCapsule::UMLRTTrigger * toRemoveTrigger = NULL;

	fromState->triggerTable->lock();
	UMLRTHashMap::Iterator iter = fromState->triggerTable->getIterator();

	while (iter != iter.end())
	{
		UMLRTCapsule::UMLRTTransition * transition = (UMLRTCapsule::UMLRTTransition *)iter.getObject();
		if(transition->to == toState) {
			toRemove = transition;
			toRemoveTrigger = (UMLRTCapsule::UMLRTTrigger *)iter.getKey();
			break;
		}
		iter = iter.next();
	}
	fromState->triggerTable->unlock();

	if(toRemove != NULL) {
		fromState->triggerTable->remove(toRemoveTrigger);
		transitionTable.removeTransition(toRemove);
	}
}

bool UMLRTCapsule::UMLRTTransitionTrable::addState ( UMLRTState * state )
{
	if(stateTable->getObject(state->name) != NULL)
		return false;
	stateTable->insert(state->name, (void*) state);
	//this->print();
	return true;
}

UMLRTCapsule::UMLRTState * UMLRTCapsule::UMLRTTransitionTrable::getState ( const char * name )
{
	return (UMLRTState*)stateTable->getObject(name);
}

bool UMLRTCapsule::UMLRTTransitionTrable::addTransition ( UMLRTState * state, UMLRTTrigger * trigger, UMLRTTransition * transition )
{
	if(state->triggerTable->getObject(trigger->name) != NULL)
		return false;

	state->triggerTable->insert(trigger->name, (void*) transition);
	transitionsTable->insert(transition->name, (void*) transition);
	//this->print();
	return true;
}

UMLRTCapsule::UMLRTTransition * UMLRTCapsule::UMLRTTransitionTrable::getTransition ( const char * name )
{
	return (UMLRTTransition*)transitionsTable->getObject(name);
}

UMLRTCapsule::UMLRTTransition * UMLRTCapsule::UMLRTTransitionTrable::getTransition ( UMLRTState * state, UMLRTTrigger * trigger )
{
	return (UMLRTTransition*)state->triggerTable->getObject(trigger->name);
}

void UMLRTCapsule::UMLRTTransitionTrable::removeState ( UMLRTState * state )
{
    state->triggerTable->lock();
    UMLRTHashMap::Iterator iter = state->triggerTable->getIterator();

    while (iter != iter.end())
    {
        UMLRTTransition * transition = (UMLRTTransition *)iter.getObject();
        removeTransition(transition);
        iter = iter.next();
    }
    state->triggerTable->unlock();

    stateTable->remove(state);
    //delete state;
    //this->print();
}

void UMLRTCapsule::UMLRTTransitionTrable::removeTransition ( UMLRTTransition * transition )
{
	transitionsTable->remove(transition);
	delete transition;
	//this->print();
}

void UMLRTCapsule::UMLRTTransitionTrable::print ( FILE * stream )
{
	stateTable->lock();
	UMLRTHashMap::Iterator stateIter = stateTable->getIterator();

	while(stateIter != stateIter.end())
	{
		UMLRTState * state = (UMLRTState *)stateIter.getObject();
		print(state, stream);
		stateIter = stateIter.next();
	}

    stateTable->unlock();
}

void UMLRTCapsule::UMLRTTransitionTrable::print ( UMLRTState * state, FILE * stream )
{
	if(shouldBreak(state))
		fprintf(stream, "*");
	fprintf(stream, "%s:\n", state->name);

	state->triggerTable->lock();
	UMLRTHashMap::Iterator iter = state->triggerTable->getIterator();

	while (iter != iter.end())
	{
		const char  * triggerName = (const char *)iter.getKey();
		UMLRTTransition * transition = (UMLRTTransition *)iter.getObject();
		fprintf(stream, "\t%s: =>%s on %s\n", transition->name, transition->to->name, triggerName);
		iter = iter.next();
	}
	state->triggerTable->unlock();
}

void UMLRTCapsule::execTransition( const char * transitionName )
{
	UMLRTTransition * transition = transitionTable.getTransition(transitionName);
	if(transition != NULL)
	{
		lastTransition = transition->name;
		UMLRTState * nextState = transition->choice == NULL ? transition->to : (*this.*(transition->choice))(this->msg);
		UMLRTAction entry = nextState->entry;
		UMLRTAction effect = transition->effect;
		UMLRTGuard guard = transition->guard;
		UMLRTAction exit = nextState->exit;

		if(guard == NULL || (*this.*guard)(this->msg)) {
			if(entry != NULL)
				(*this.*entry)(this->msg);
			if(effect != NULL)
				(*this.*effect)(this->msg);
			if(exit != NULL)
				(*this.*exit)(this->msg);

			currentState = nextState;
			if(transitionTable.shouldBreak(currentState)) {
				this->defaultPartialityFunction(true);
			}

			else if(transition->added) {
				this->defaultPartialityFunction(false);
			}
		}

	}
}

UMLRTCapsule::UMLRTHistoryMap::UMLRTHistoryMap ( )
{
	historyMap = new UMLRTHashMap("historyMap", UMLRTHashMap::compareString, false);
}

UMLRTCapsule::UMLRTHistoryMap::~UMLRTHistoryMap ( )
{

}

void UMLRTCapsule::UMLRTHistoryMap::put ( const char * compositeState, const char * subState )
{
	historyMap->insert(strdup(compositeState), strdup(subState));
}

const char * UMLRTCapsule::UMLRTHistoryMap::get ( const char * compositeState )
{
	return (const char *)historyMap->getObject(compositeState);
}
