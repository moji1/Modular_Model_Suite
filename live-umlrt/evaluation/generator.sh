#!/bin/bash

for i in `seq 1 9999`;
do
echo 'transitionTable.addTransition( transitionTable.getState( "Playing" ), new UMLRTCapsule::UMLRTTrigger( "pongPort", "ping'$i'" ), new UMLRTCapsule::UMLRTTransition( "transition1", transitionTable.getState( "Playing" ), transitionTable.getState( "Playing" ), NULL, NULL, (UMLRTCapsule::UMLRTAction )&Capsule_Ponger::actionchain_____transition1 ) );'
done
