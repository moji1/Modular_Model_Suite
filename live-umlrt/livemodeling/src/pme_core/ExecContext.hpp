//
//  ExecContext.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#ifndef ExecContext_hpp
#define ExecContext_hpp
#include <iostream>
#include "ExecRuleTypes.hpp"
#include "../util/Heap.hpp"
#include "PMDTypes.hpp"
#include <boost/circular_buffer.hpp>
#include "umlrtmessage.hh"
#include <iomanip>
namespace pmd{

class ExecContextOfCapsule{
public:
    //ExecContextOfCapsule(int execHistoryBufferSize, int userDecesionHistoryBufferSize);
    ExecContextOfCapsule();
    void setExecHistoryBufferCapacity(int n);
    void setUserDecesionHistoryBufferCapacity(int n);
    void setCapsuleInstanceID(int id);
    int getCapsuleInstance();
    void setExecLocation(ExecLocationType type, const char *  loc);
    ExecLocation getExecLocation();
    void setCapsuleType(const char * capsuleInstance);
    std::string getCapsuleType();
    void setCapsuleHeap(Heap * heap);
    Heap * getCapsuleHeap();
    void setlastMsg(const UMLRTMessage * msg);
    Message getLastMsg();
    void setLastTransition(const char * transName);
    std::string getLastTransition();
    void appendpostponedActionCodes(std::function<bool(const UMLRTMessage * msg)>);
    void clearPostponedActionCodes();
    void executePostponedActionCodes();
    void addInputMessages(std::string portName, std::string msgName, std::map<std::string,std::string> paramsWithType);
    void addOutPutMessages(std::string portName, std::string msgName, std::map<std::string,std::string> paramsWithType);
    ExecHelper* addBreakpoint(std::string transName);
    void deleteBreakpoint(std::string transName);
    void setCallBackMethodSendMessage(std::function<int(Message)> pSendMessage);
    void setCallBackMethodAddState(std::function<bool(const char *)> pAddState);
    void setCallBackMethodRemoveState(std::function<void(const char *)> pRemoveState);
    void setCallBackMethodAddTransition(std::function<bool(const char *, const char *, const char *, const char *, const char *)> pAddTransition);
    void setCallBackMethodRemoveTransition(std::function<void(const char *)> pRemoveTransition);
    void setCallBackMethodRemoveTransitions(std::function<void(const char *, const char *)> pRemoveTransitions);
    void setCallBackMethodAddBreakpoint(std::function<void(const char *)> pAddBreakpoint);
    void setCallBackMethodRemoveBreakpoint(std::function<void(const char *)> pRemoveBreakpoint);
    void setCallBackMethodReplayMessage(std::function<void()> pReplayMessage);
    void setCallBackMethodSetTimer(std::function<void(char *, int, int)> pSetTimer);

    std::function<bool(const char *)>  getCallBackMethodAddState();
    std::function<void(const char *)>  getCallBackMethodRemoveState();
    std::function<bool(const char *, const char *, const char *, const char *, const char *)> getCallBackMethodAddTransition();
    std::function<void(const char *)> getCallBackMethodRemoveTransition();
    std::function<void(const char *, const char *)> getCallBackMethodRemoveTransitions();
    std::function<void(const char *)>  getCallBackMethodAddBreakpoint();
    std::function<void(const char *)>  getCallBackMethodRemoveBreakpoint();
    std::function<int(Message)>  getCallBackMethodSendMessage();
    std::function<void()> getCallBackMethodReplayMessage();
    std::function<void(char *, int, int)> getCallBackMethodSetTimer();
    void setCallBackMethodSendInternalMessage(std::function<int(Message)> pSendInternalMessage);
    std::function<int(Message)>  getCallBackMethodSendInternalMessage();
    void addPossibleExecPath(std::string source, std::string target, std::string transtion, std::string signal);
    void clearPossibleExecPath();
    std::vector<ExecutionStep> getPossibleExecPath();
    void setPartialityPattern(PartialityPattern partialityPattern);
    PartialityPattern getPartialityPattern();
    std::map<std::string,std::string> getMessageParam(std::string portName,std::string messageName);
    std::string printMessageParamstoStr(Message msg);
    bool checkUserDecesion(ExecutionStep path);
    ExecutionStep getSelectedPath();
    void setSelectedPath(pmd::ExecutionStep executionStep);
    void setSelectedPath(int pathId);
    void saveEffect(std::string effect);
    std::string getEffect();
    void showContext();
    void showOptions();
    void showDecesions();
    void showVar();
    void showVar(std::string name);
    void showTrace();
    void showTrace(int cnt);
    void showConnections();
    void showDecesions(int cnt);
    void showInputMsgs();
    void showOutputMsgs();
    void showLastMsg();
    void showMessageMap(MessagesMap messageMap);
    //void printVar(std::string name);
    
private:
    int capsuleInstanceID;
    ExecLocation execLocation;
    std::string capsuleType;
    PartialityPattern partialityPattern;
    Heap * capsuleHeap;
    //Heap scriptHeap;
    Message lastMsg;
    std::string lastTransition;
    const UMLRTMessage * lastUMLRTMsg;
    std::function<int(Message)> pSendMessage;
    std::function<int(Message)> pSendInternalMessage;
    std::function<bool(const char *)> pAddState;
    std::function<void(const char *)> pRemoveState;
    std::function<bool(const char *, const char *, const char *, const char *, const char *)> pAddTransition;
    std::function<void(const char *)> pRemoveTransition;
    std::function<void(const char *, const char *)> pRemoveTransitions;
    std::function<void(const char *)> pAddBreakpoint;
    std::function<void(const char *)> pRemoveBreakpoint;
    std::function<void()> pReplayMessage;
    std::function<void(char *, int, int)> pSetTimer;

    std::vector<std::function<bool(const UMLRTMessage * msg)>> postponedActionCodes;
    boost::circular_buffer<ExecLocation> execHistory;
    boost::circular_buffer<ExecutionStep>    userDecesionHistory;
    MessagesMap inputMessages;
    MessagesMap outputMessages;
    EffectMap effects;
    std::vector<ExecutionStep> possibleExecPaths;
    void appendExecHistory(ExecLocation execHistory);
    void appenduserDecesionHistory(ExecutionStep userDecision);
    Message convertUMLRTMessageToMessage(const UMLRTMessage *msg);
    ExecutionStep selectedPath;
    bool runEntry;
    bool runExit;
    
};

}

#endif /* ExecContext_hpp */
