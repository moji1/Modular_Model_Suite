//============================================================================
// Name        : RealTimeLibs.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>

#include <string.h>
#include "Event.hpp"
#include "SharedMem.hpp"

#include "SROManager.hpp"
int main(){
	SROManager testSRO;
	//testSRO.addState("testState");
	Transition t1;
	t1.setTransition("t1", "targetState", "testSignal", "testProtocol");
	testSRO.addTransitionsFromState("testState",t1);
	t1.setTransition("t2", "targetState1", "testSignal1", "testProtocol1");
	testSRO.addTransitionsFromState("testState",t1);
	//testSRO.addState("testState1");
	t1.setTransition("t3", "targetState2", "testSignal2", "testProtocol2");
	testSRO.addTransitionsFromState("testState1",t1);
	t1.setTransition("t4", "targetState3", "testSignal3", "testProtocol3");
	testSRO.addTransitionsFromState("testState1",t1);
	testSRO.dumpAllTranstion();
	testSRO.addBreakPoint("t1", TransitionEffectBreakPoint, 0);
	testSRO.addBreakPoint("targetState", StateEntryBreakPoint, 0);
	testSRO.addBreakPoint("testState", StateEntryBreakPoint, 0);
	testSRO.addBreakPoint("testState", StateEntryBreakPoint, 0);
	std::cout<<testSRO.checkDebug("testState", "", "",3,StateEntryBreakPoint,0);
	testSRO.setExecMode(Running);
	return 0;
}
int main2() {
	std::cout << "!!!Sample Application for read event and send command!!!\n" ; // prints !!!Hello World!!!
	//define event class and assign the values
	debugEvents::Event e1;

    Comms::SharedMem eventShm("EventArea", "EventQ", 999999999, true);
    eventShm.setUp(client);
    Comms::SharedMem commandShm("CommandArea", "CommandQ", 999999999, true);
    eventShm.setUp(client);
    commandShm.setUp(client);
    while (true)
    {
    		std::string tempStr=eventShm.safePopBackString();
    		if (tempStr!="")
    		{
    		    std::cout<<"new event is received and deserailized to event object with these fields:"<<tempStr<<"\n";
    		}
    		else
    		{
    			std::string tempsStr;
    			std::cout<<"enter the capsule instance to receive command, currently we send default command only\n";
    			std::cin>>tempStr;
    			//std::cout<<tempStr;
    			//std::cin>>tempStr;
    			commandShm.safePushBackString(tempStr);
    		}
    }

	return 0;
}

