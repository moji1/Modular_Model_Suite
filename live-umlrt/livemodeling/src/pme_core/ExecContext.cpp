//
//  ExecContext.cpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#include "ExecContext.hpp"
#include "ExecHelper.hpp"

using namespace pmd;

/*ExecContextOfCapsule::ExecContextOfCapsule( int execHistoryBufferSize, int userDecesionHistoryBufferSize) {
    this->execHistory= boost::circular_buffer<ExecLocation>(execHistoryBufferSize);
    this->userDecesionHistory=boost::circular_buffer<ExecutionStep>(userDecesionHistoryBufferSize);
}*/
ExecContextOfCapsule::ExecContextOfCapsule() {
    this->execHistory.set_capacity(100);
    this->userDecesionHistory.set_capacity(100);
 }
void ExecContextOfCapsule::setCapsuleInstanceID(int id) {
    this->capsuleInstanceID=id;
}

int ExecContextOfCapsule::getCapsuleInstance() {
    return this->capsuleInstanceID;
}

void ExecContextOfCapsule::setExecLocation(ExecLocationType type, const char *  loc) {
    this->execLocation.type=type;
    this->execLocation.locName=std::string(loc);
}

pmd::ExecLocation ExecContextOfCapsule::getExecLocation() { 
    return this->execLocation;
}

void ExecContextOfCapsule::setCapsuleType(const char *capsuleInstance) { 
    this->capsuleType=std::string(capsuleInstance);
}

std::string ExecContextOfCapsule::getCapsuleType() { 
    return this->capsuleType;
}

void ExecContextOfCapsule::setCapsuleHeap(pmd::Heap *heap) { 
    this->capsuleHeap=heap;
}

pmd::Heap *ExecContextOfCapsule::getCapsuleHeap() {
    return this->capsuleHeap;
}
std::map<std::string,std::string> ExecContextOfCapsule::getMessageParam(std::string portName,std::string messageName){
    for (MessagesMap::iterator portMessageIt=this->inputMessages.begin(); portMessageIt!= inputMessages.end();portMessageIt++)
        if (portMessageIt->first==portName)
            for (MessageParams::iterator messageParamIt=portMessageIt->second.begin();messageParamIt!=portMessageIt->second.end();messageParamIt++)
                if (messageParamIt->first==messageName)
                    return messageParamIt->second;
    return {};
}
Message ExecContextOfCapsule::convertUMLRTMessageToMessage(const UMLRTMessage *msg){
    Message nonUMLRTMsg;
    if (msg){
        if (msg->destPort)
            nonUMLRTMsg.portName=std::string(msg->destPort->getName());
        nonUMLRTMsg.signal=std::string(msg->getSignalName());
        nonUMLRTMsg.index=msg->sapIndex0();
        std::map<std::string,std::string> msgParams=getMessageParam(nonUMLRTMsg.portName,nonUMLRTMsg.signal);
        if (msgParams.begin()!=msgParams.end()){
            int cnt=0;
            for (std::map<std::string,std::string>::iterator it1=msgParams.begin();it1!=msgParams.end();it1++){
                if (it1->second=="Integer"){
                    this->capsuleHeap->setVarValue(it1->first, *static_cast<int *>(msg->signal.getParam(cnt)));
                    nonUMLRTMsg.payload[it1->first]=*static_cast<int *>(msg->signal.getParam(cnt++));
                }
                else if (it1->second=="String"){
                    this->capsuleHeap->setVarValue(it1->first, std::string(*static_cast<char **>(msg->signal.getParam(cnt))));
                    nonUMLRTMsg.payload[it1->first]=std::string(*static_cast<char **>(msg->signal.getParam(cnt++)));
                }
                else if (it1->second=="Real"){
                    this->capsuleHeap->setVarValue(it1->first, *static_cast<double *>(msg->signal.getParam(cnt)));
                    nonUMLRTMsg.payload[it1->first]=*static_cast<double *>(msg->signal.getParam(cnt++));
                }
                else if (it1->second=="Char"){
                    this->capsuleHeap->setVarValue(it1->first, *static_cast<char *>(msg->signal.getParam(cnt)));
                    nonUMLRTMsg.payload[it1->first]=*static_cast<char *>(msg->signal.getParam(cnt++));
                }
                else if (it1->second=="Boolean"){
                    this->capsuleHeap->setVarValue(it1->first, *static_cast<bool *>(msg->signal.getParam(cnt)));
                    nonUMLRTMsg.payload[it1->first]=*static_cast<bool *>(msg->signal.getParam(cnt++));
                }
            }
        }
    }
    return nonUMLRTMsg;
}
void ExecContextOfCapsule::setlastMsg(const UMLRTMessage *msg) { 
    this->lastUMLRTMsg=msg;
    this->lastMsg=convertUMLRTMessageToMessage(msg);    /*this->lastMsg.portName=std::string(msg->destPort->getName());
    this->lastMsg.signal=std::string(msg->getSignalName());
    std::map<std::string,std::string> msgParams=getMessageParam(lastMsg.portName,lastMsg.signal);
    if (msgParams.size()>0){
        int cnt=0;
        for (std::map<std::string,std::string>::iterator it1=msgParams.begin();it1!=msgParams.end();it1++){
           if (it1->second=="Integer")
               this->lastMsg.payload[it1->first]=*static_cast<int *>(msg->signal.getParam(cnt++));
            else if (it1->second=="String")
                this->lastMsg.payload[it1->first]=*static_cast<std::string *>(msg->signal.getParam(cnt++));
            else if (it1->second=="Real")
                this->lastMsg.payload[it1->first]=*static_cast<double *>(msg->signal.getParam(cnt++));
            else if (it1->second=="Char")
                this->lastMsg.payload[it1->first]=*static_cast<char *>(msg->signal.getParam(cnt++));
            else if (it1->second=="Boolean")
                this->lastMsg.payload[it1->first]=*static_cast<bool *>(msg->signal.getParam(cnt++));
        }*/
    
    //}
    //msg->signal.getType()->
    //for(size_t i=0; i<payloadObj->numFields; i++) {
    //    Value paramObj(kObjectType);
    // serialize message basedo n the information from this->inputmessage
}

std::string ExecContextOfCapsule::printMessageParamstoStr(Message msg) {
    std::map<std::string,std::string> msgParams=getMessageParam(msg.portName,lastMsg.signal);
    std::stringstream ss;
    if (msgParams.size()>0){
        bool  firstParam=true;
        for (std::map<std::string,std::string>::iterator it1=msgParams.begin();it1!=msgParams.end();it1++){
            if (not firstParam)
                ss<<", ";
            firstParam=false;
            if (it1->second=="Integer")
                ss<<it1->first<<":"<<boost::any_cast<int>(msg.payload[it1->first]);
            else if (it1->second=="String")
                ss<<it1->first<<":"<<boost::any_cast<std::string>(msg.payload[it1->first]);
            else if (it1->second=="Real")
                ss<<it1->first<<":"<<boost::any_cast<double>(msg.payload[it1->first]);
            else if (it1->second=="Char")
                ss<<it1->first<<":"<<boost::any_cast<char>(msg.payload[it1->first]);
            else if (it1->second=="Boolean")
                ss<<it1->first<<":"<<boost::any_cast<bool>(msg.payload[it1->first]);
        }
    }
    return ss.str();
}
pmd::Message ExecContextOfCapsule::getLastMsg() { 
    return this->lastMsg;
}


void ExecContextOfCapsule::appendpostponedActionCodes(std::function<bool (const UMLRTMessage * )> func) {
    this->postponedActionCodes.push_back(func);
}

void ExecContextOfCapsule::clearPostponedActionCodes() { 
    this->postponedActionCodes.clear();
}

void ExecContextOfCapsule::executePostponedActionCodes() { 
    // execute the funtion in based on current umlrt message;
}

void ExecContextOfCapsule::addInputMessages(std::string portName, std::string msgName, std::map<std::string, std::string> paramsWithType) {
    this->inputMessages[portName][msgName]=paramsWithType;
}

void ExecContextOfCapsule::addOutPutMessages(std::string portName, std::string msgName, std::map<std::string, std::string> paramsWithType) {
    this->outputMessages[portName][msgName]=paramsWithType;
}

void ExecContextOfCapsule::setCallBackMethodSendMessage(std::function<int (Message)> pSendMessage) { 
    this->pSendMessage=pSendMessage;
}

std::function<int (Message)> ExecContextOfCapsule::getCallBackMethodSendMessage() { 
    return this->pSendMessage;
}

void ExecContextOfCapsule::setCallBackMethodSendInternalMessage(std::function<int (Message)> pSendInternalMessage) { 
    this->pSendInternalMessage=pSendInternalMessage;
}

std::function<int (Message)> ExecContextOfCapsule::getCallBackMethodSendInternalMessage() { 
    return this->pSendInternalMessage;
}

void ExecContextOfCapsule::setCallBackMethodAddState(std::function<bool(const char *)> pAddState) {
    this->pAddState=pAddState;
}

void ExecContextOfCapsule::setCallBackMethodRemoveState(std::function<void(const char *)> pRemoveState) {
    this->pRemoveState=pRemoveState;
}

void ExecContextOfCapsule::setCallBackMethodAddTransition(std::function<bool(const char *, const char *, const char *, const char *, const char *)> pAddTransition) {
    this->pAddTransition=pAddTransition;
}

void ExecContextOfCapsule::setCallBackMethodRemoveTransition(std::function<void(const char *)> pRemoveTransition) {
    this->pRemoveTransition=pRemoveTransition;
}

void ExecContextOfCapsule::setCallBackMethodRemoveTransitions(std::function<void(const char *, const char *)> pRemoveTransitions) {
    this->pRemoveTransitions=pRemoveTransitions;
}

void ExecContextOfCapsule::setCallBackMethodAddBreakpoint(std::function<void(const char *)> pAddBreakpoint) {
    this->pAddBreakpoint=pAddBreakpoint;
}

void ExecContextOfCapsule::setCallBackMethodRemoveBreakpoint(std::function<void(const char *)> pRemoveBreakpoint) {
    this->pRemoveBreakpoint=pRemoveBreakpoint;
}

void ExecContextOfCapsule::setCallBackMethodReplayMessage(std::function<void()> pReplayMessage) {
    this->pReplayMessage=pReplayMessage;
}

void ExecContextOfCapsule::setCallBackMethodSetTimer(std::function<void(char *, int, int)> pSetTimer) {
    this->pSetTimer=pSetTimer;
}


std::function<bool(const char *)>  ExecContextOfCapsule::getCallBackMethodAddState() {
    return this->pAddState;
}

std::function<void(const char *)>  ExecContextOfCapsule::getCallBackMethodRemoveState() {
    return this->pRemoveState;
}

std::function<bool(const char *, const char *, const char *, const char *, const char *)> ExecContextOfCapsule::getCallBackMethodAddTransition() {
    return this->pAddTransition;
}

std::function<void(const char *)> ExecContextOfCapsule::getCallBackMethodRemoveTransition() {
    return this->pRemoveTransition;
}

std::function<void(const char *, const char *)> ExecContextOfCapsule::getCallBackMethodRemoveTransitions() {
    return this->pRemoveTransitions;
}

std::function<void(const char *)>  ExecContextOfCapsule::getCallBackMethodAddBreakpoint() {
    return this->pAddBreakpoint;
}

std::function<void(const char *)>  ExecContextOfCapsule::getCallBackMethodRemoveBreakpoint() {
    return this->pRemoveBreakpoint;
}

std::function<void()> ExecContextOfCapsule::getCallBackMethodReplayMessage() {
    return this->pReplayMessage;
}

std::function<void(char *, int, int)> ExecContextOfCapsule::getCallBackMethodSetTimer() {
    return this->pSetTimer;
}

void ExecContextOfCapsule::appendExecHistory(pmd::ExecLocation execHistory) {
    this->execHistory.push_back(execHistory);
}

void ExecContextOfCapsule::appenduserDecesionHistory(pmd::ExecutionStep userDecision) {
    this->userDecesionHistory.push_back(userDecision);
}

void ExecContextOfCapsule::addPossibleExecPath(std::string source, std::string target, std::string transtion, std::string signal) {
    ExecutionStep s1;
    s1.signal=signal;
    s1.sourceState=source;
    s1.targetState=target;
    s1.transName=transtion;
    this->possibleExecPaths.push_back(s1);
}

void ExecContextOfCapsule::clearPossibleExecPath() {
    possibleExecPaths.clear();
}

void ExecContextOfCapsule::showDecesions(int cnt) {
    if (this->userDecesionHistory.size()>cnt)
        cnt=this->userDecesionHistory.size();
    int numFields=4;
    int totalWidth=nameWidth*3+sep.size()*numFields+5;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    <<std::setw(5)<<"#No"<< sep<<std::setw(nameWidth)<<"Start From State " << sep<<std::setw(nameWidth)<<"By Transition" <<sep <<std::setw(nameWidth)<<"Ends At State"<<sep<<std::endl;
    std::cout<<line<<std::endl;
    for (int i=0; i< cnt; i++)
        std::cout<<sep <<std::setw(5)<<i<< sep<< std::setw(nameWidth)<<this->userDecesionHistory[i].sourceState << sep<<std::setw(nameWidth)<<this->userDecesionHistory[i].transName<<sep <<std::setw(nameWidth)<<this->userDecesionHistory[i].targetState<<sep<<std::endl;
    std::cout<<line;
}

std::vector<ExecutionStep> ExecContextOfCapsule::getPossibleExecPath() {
    return possibleExecPaths;
}

void ExecContextOfCapsule::setExecHistoryBufferCapacity(int n) {
    this->execHistory.set_capacity(n);
}

void ExecContextOfCapsule::setUserDecesionHistoryBufferCapacity(int n) {
    this->userDecesionHistory.set_capacity(n);
}

void ExecContextOfCapsule::setPartialityPattern(pmd::PartialityPattern partialityPattern) {
    this->partialityPattern=partialityPattern;
}

pmd::PartialityPattern ExecContextOfCapsule::getPartialityPattern() {
    return partialityPattern;
}

bool ExecContextOfCapsule::checkUserDecesion(ExecutionStep path) {
    if (this->selectedPath.transName.compare(path.transName)==0 or this->selectedPath.targetState.compare(path.targetState)==0 )
        return true;
    /*else if (path.targetState==selectedPath. and this->selectedPath.transName.compare(path.transName)==0)
        return true;
    else if (path.transName=="" and this->selectedPath.targetState.compare(path.targetState)==0)
        return true;*/
    else
        return false;
}


pmd::ExecutionStep ExecContextOfCapsule::getSelectedPath() {
    return this->selectedPath;
    
}

void ExecContextOfCapsule::setSelectedPath(pmd::ExecutionStep executionStep) {
    this->selectedPath=executionStep;
}

void ExecContextOfCapsule::setSelectedPath(int pathId) {
    if (pathId>0 and pathId<=this->possibleExecPaths.size())
        this->setSelectedPath(this->possibleExecPaths[pathId-1]);
    else if (pathId==this->possibleExecPaths.size()+1){
        ExecutionStep step={"",this->execLocation.locName,this->execLocation.locName};
        this->setSelectedPath(step);
    }
    else{
        std::cout<<"Error: Option id is invalid, please select one of the following options:" <<std::endl;
        this->showOptions();
    }
}

void ExecContextOfCapsule::showContext() { 
    int numFields=2;
    int totalWidth=strWidth+nameWidth+sep.size()*numFields;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    << std::setw(nameWidth)<<"Capsule Type" << sep << std::setw(strWidth) << this->getCapsuleType()<< sep <<std::endl <<sep
    << std::setw(nameWidth)<<"Capsule ID" << sep << std::setw(strWidth) << this->getCapsuleInstance() <<sep <<std::endl <<sep
    << std::setw(nameWidth)<<"Current State" << sep << std::setw(strWidth) << this->getExecLocation().locName <<sep <<std::endl <<sep
    << std::setw(nameWidth)<<"Current Message" << sep << std::setw(strWidth) << this->getLastMsg().portName+"::"+this->getLastMsg().signal+"("+printMessageParamstoStr(this->getLastMsg())+")"<<sep <<std::endl ;
    std::cout<<line<<std::endl;
    //<< std::setw(nameWidth)<<"Transtion " << sep << std::setw(strWidth) << ToString(this->getExecLocation().)<< sep <<std::endl;
    
}

/*void ExecContextOfCapsule::showOptions() {
    int numFields=4;
    int totalWidth=nameWidth*3+sep.size()*numFields+5;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    <<std::setw(5)<<"#No"<< sep<<std::setw(nameWidth)<<"Start From State " << sep<<std::setw(nameWidth)<<"By Transition" <<sep <<std::setw(nameWidth)<<"Ends At State"<<sep<<std::endl;
    std::cout<<line<<std::endl;
    int i=1;
    for (std::vector<ExecutionStep>::iterator it1=this->possibleExecPaths.begin();this->possibleExecPaths.end()!=it1;it1++ ){
        std::cout<<sep <<std::setw(5)<<i++<< sep<< std::setw(nameWidth)<<it1->sourceState << sep<<std::setw(nameWidth)<<it1->transName<<sep <<std::setw(nameWidth)<<it1->targetState<<sep<<std::endl;
    }
    std::cout<<sep <<std::setw(5)<<i++<< sep<< std::setw(nameWidth*3+4)<<"Ignore the  message and let system to stay at the same state"<<sep<<std::endl;
    std::cout<<line<<std::endl;
}*/

void ExecContextOfCapsule::showOptions() {
    int numFields=4;
    int totalWidth=strWidth*3+sep.size()*numFields+5;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    <<std::setw(5)<<"#No"<< sep<<std::setw(strWidth)<<"Start From State " << sep <<std::setw(strWidth)<<"Via Transition" << sep <<std::setw(strWidth)<<"Ends At State"<<sep<<std::endl;
    std::cout<<line<<std::endl;
    int i=1;
    for (std::vector<ExecutionStep>::iterator it1=this->possibleExecPaths.begin();this->possibleExecPaths.end()!=it1;it1++ ){
        std::cout<<sep <<std::setw(5)<<i++<< sep<< std::setw(strWidth)<<it1->sourceState<<sep <<std::setw(strWidth)<<it1->transName<<sep <<std::setw(strWidth)<<it1->targetState<<sep<<std::endl;
    }
    //std::cout<<sep <<std::setw(5)<<i++<< sep<< std::setw(strWidth*2+2)<<"Ignore the  message and let system to stay at the same state"<<sep<<std::endl;
    std::cout<<line<<std::endl;
}


void ExecContextOfCapsule::showDecesions() {
    int numFields=4;
    int totalWidth=nameWidth*3+sep.size()*numFields+5;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    <<std::setw(5)<<"#No"<< sep<<std::setw(nameWidth)<<"Start From State " << sep<<std::setw(nameWidth)<<"By Transition" <<sep <<std::setw(nameWidth)<<"Ends At State"<<sep<<std::endl;
    std::cout<<line<<std::endl;
    for (int i=0; i< this->userDecesionHistory.size(); i++)
         std::cout<<sep <<std::setw(5)<<i<< sep<< std::setw(nameWidth)<<this->userDecesionHistory[i].sourceState << sep<<std::setw(nameWidth)<<this->userDecesionHistory[i].transName<<sep <<std::setw(nameWidth)<<this->userDecesionHistory[i].targetState<<sep<<std::endl;
     std::cout<<line<<std::endl;
}

void ExecContextOfCapsule::showVar() { 
    this->getCapsuleHeap()->showVar();
}

void ExecContextOfCapsule::showVar(std::string name) { 
    this->getCapsuleHeap()->showVar(name);
}

void ExecContextOfCapsule::showTrace() {
    for (int i=0; i< this->execHistory.size(); i++)
        if (this->execHistory[i].type==TRANSITIONLOC)
            std::cout<<"--[TRANSITION]--> " <<execHistory[i].locName<<std::endl;
        else if (this->execHistory[i].type==STATELOC)
            std::cout<<"--[STATE]-- " <<execHistory[i].locName<<std::endl;
    /*for (boost::circular_buffer<ExecLocation>::iterator it1=this->execHistory.begin();it1!=this->execHistory.end();it1++)
        if (it1->type==TRANSITIONLOC)
            std::cout<<"--[TRANSITION]--> " <<it1->locName<<std::endl;
        else if (it1->type==STATELOC)
            std::cout<<"--=[STATE]--- " <<it1->locName<<std::endl;*/
            
}

void ExecContextOfCapsule::showTrace(int cnt) {
    if (cnt>this->execHistory.size())
        cnt=this->execHistory.size();
    for (int i=0; i< cnt; i++)
        if (this->execHistory[i].type==TRANSITIONLOC)
            std::cout<<"--[TRANSITION]--> " <<execHistory[i].locName<<std::endl;
        else if (this->execHistory[i].type==STATELOC)
            std::cout<<"--[STATE]-- " <<execHistory[i].locName<<std::endl;
}

void ExecContextOfCapsule::showConnections() { 
    //;
}

void ExecContextOfCapsule::showMessageMap(MessagesMap messageMap){
    int numFields=3;
    int totalWidth=nameWidth*2+sep.size()*numFields+strWidth;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    <<std::setw(nameWidth)<<"Port"<<sep<<std::setw(nameWidth)<<"Message" << sep<<std::setw(strWidth)<<"Parameters" <<sep<<std::endl;
    std::cout<<line<<std::endl;
    for (MessagesMap::iterator it1=messageMap.begin();it1!=messageMap.end();it1++){
        std::string portName=it1->first;
        for (MessageParams::iterator it2=it1->second.begin();it2!=it1->second.end();it2++){
            std::string messageName=it2->first;
            std::string tempComma="";
            std::string params="";
            for (std::map<std::string,std::string>::iterator it3=it2->second.begin();it3!=it2->second.end();it3++){
                params+=tempComma+it3->first+":"+it3->second;
                tempComma=", ";
            }
            std::cout<<sep<<std::setw(nameWidth)<<portName<<sep<<std::setw(nameWidth)<<messageName<< sep<<std::setw(strWidth)<<params<<sep<<std::endl;
        }
    }
    std::cout<<line<<std::endl;
}
void ExecContextOfCapsule::showInputMsgs() {
    this->showMessageMap(this->inputMessages);
}

void ExecContextOfCapsule::showOutputMsgs() {
    this->showMessageMap(this->outputMessages);
}

void ExecContextOfCapsule::showLastMsg() {
    int numFields=3;
    int totalWidth=nameWidth*2+sep.size()*numFields+strWidth;
    std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
    std::cout<<std::endl<<line<<std::endl<<sep
    <<std::setw(nameWidth)<<"Port"<<sep<<std::setw(nameWidth)<<"Message" << sep<<std::setw(strWidth)<<"Parameters" <<sep<<std::endl;
    std::cout<<line<<std::endl;
    std::cout<<sep<<std::setw(nameWidth)<<this->getLastMsg().portName << sep << std::setw(nameWidth)<< this->getLastMsg().signal<<sep<<std::setw(strWidth)<< printMessageParamstoStr(this->getLastMsg())<<sep <<std::endl;
    std::cout<<line<<std::endl;
}

void ExecContextOfCapsule::setLastTransition(const char * transName)
{
    lastTransition = std::string(transName);
}

std::string ExecContextOfCapsule::getLastTransition()
{
    return lastTransition;
}

void ExecContextOfCapsule::saveEffect(std::string effect) {
    this->effects[lastTransition] = effect;
}

std::string ExecContextOfCapsule::getEffect() {
    if(effects.find(lastTransition) != effects.end() )
        return this->effects[lastTransition];
    return std::string("");
}


