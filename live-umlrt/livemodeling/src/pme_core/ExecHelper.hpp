//
//  exec_helper.hpp
//  PartailModelLib
//
//  Created by Mojtaba Bagherzadeh on 2018-03-15.
//  Copyright © 2018 Mojtaba Bagherzadeh. All rights reserved.
//
// the fillowing class provide helper funtions to execute the partial models. Its main purpose is to read, parse, and process users' decesions

#ifndef exec_helper_hpp
#define exec_helper_hpp

#include <stdio.h>
#include <string>
#include <iostream>
#include <map>
#include <vector>
#include "Config.hpp"
#include "PMEExecRulesAnalyzer.hpp"
#include "PMDTypes.hpp"
#include "PMEScriptExecutor.hpp"


namespace pmd{
    
using namespace std;

    
class ExecHelper {
public:
    ExecHelper ();
    ExecHelper (ExecMode execMode);
    //ExecutionStep getUserDecesion(string currentstate, vector<ExecutionStep> possiblepaths,Message incomingmessage);
    void getUserDecesion(ExecContextOfCapsule * execContext);
    void record(ExecContextOfCapsule * execContext);
    void replay(ExecContextOfCapsule * execContext, std::string effect);
    //bool checkUserDecesion(ExecutionStep path);
    void setExecMode(ExecMode execMode);
    ExecMode getExecMode();
    void readExecConfig();
    void setExecScriptPath(std::string scriptPath);
    void setSelectedPath(ExecutionStep executionStep);
    void loadExecRules();
    void execSendMessage(std::function<bool(std::string portName,std::string messageName)> pSendMessage); // it is ony writeen here for test
    void processUserCommands(ExecContextOfCapsule * execContext);
    std::string getExecScriptPath();
    /*void setHeap(pmd::Heap * heap);
    pmd::Heap * getHeap();
    /*void initCallBackMethodSendMessage(std::function<int(Message)> pSendMessage);
    std::function<int(Message)>  getCallBackMethodSendMessage();
    void initCallBackMethodSendInternalMessage(std::function<int(Message)> pSendInternalMessage);
    std::function<int(Message)>  getCallBackMethodSendInternalMessage();*/
private:
    //ExecutionStep selectedPath;
    //Message lastIncomingMessage;
    ExecMode execMode;
    string genRandomDecesion(ExecContextOfCapsule * execContext);
    string loadUserDecesionFromScript(ExecContextOfCapsule * execContext);
    void getUserDecesionFromConsole(ExecContextOfCapsule * execContext);
    Config config;
    std::string execScriptPath;
    std::string ledger;
    pmd::PMEExecRulesAnalyzer execRulesAnalyzer;
    pmd::Heap * heap=nullptr;
    PMEScriptExecutor scriptExecutor;
    /*std::function<int(Message)> pSendMessage;
    std::function<int(Message)> pSendInternalMessage;*/
};
}
#endif /* exec_helper_hpp */
