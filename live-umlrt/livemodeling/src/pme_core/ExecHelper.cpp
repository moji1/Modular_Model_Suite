//
//  exec_helper.cpp
//  PartailModelLib
//
//  Created by Mojtaba Bagherzadeh on 2018-03-15.
//  Copyright © 2018 Mojtaba Bagherzadeh. All rights reserved.
//

#include "ExecHelper.hpp"
#include <chrono> 

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

namespace pmd{
void ExecHelper::getUserDecesionFromConsole(ExecContextOfCapsule * execContext){
    /*cout<<"Missing specification found at the state "<<execContext->getExecLocation().locName<< " from port "
        << execContext->getLastMsg().portName<<" for handling of a message " <<execContext->getLastMsg().signal
        <<"("<<execContext->printMessageParamstoStr(execContext->getLastMsg())<<")";
    cout<<"Please choose one of the following options"<<endl;*/
    /*int choiceid=1;
    std::vector<ExecutionStep> possiblepaths=execContext->getPossibleExecPath();
    for (std::vector<ExecutionStep>::iterator pathiterator = possiblepaths.begin() ; pathiterator != possiblepaths.end(); ++pathiterator){
        cout<<choiceid++<<"- "<<"transition "<<pathiterator->transName<< " that ends at state " << pathiterator->targetState<<endl;
    }
    
    cout<<choiceid<<"- reject the message"<<endl;
    cout<<"1.."<<possiblepaths.size()+1<<"? ";*/
    /*
    std::cerr << "Saved: " << execContext->getEffect() << std::endl;
    */
    if(execContext->getPartialityPattern() != BREAKPOINT && !execContext->getEffect().empty()) {
        //std::cerr << "I'm gona replay!" << std::endl;
        replay(execContext, execContext->getEffect());
    }
    else {

        std::cout<<std::endl<<std::string(80, '-' )<<std::endl;
        std::cout<<"A missing specification is found with the following context "<< std::endl;
        execContext->showContext();
        std::cout<<std::endl<<"You can select one of the following transition to continue the execution"<< std::endl;
        execContext->showOptions();
        processUserCommands(execContext);
    }

    /*int sockfd, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;
    char buffer[256];

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    server = gethostbyname("127.0.0.1");

    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
    serv_addr.sin_port = htons(55553);

    while (connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0)
       usleep(100);
    bzero(buffer,256);

    auto start = std::chrono::high_resolution_clock::now(); 
    while((n = read(sockfd,buffer,16)) > 0) {
        if(n == 0)
            continue;

        buffer[n] = '\0';
        std::string userInput(buffer);
        int result = scriptExecutor.ExecuteDBGCommand(execContext, userInput);
`
        //todo test send message and go next
        //test send message and go next
        switch (result){
            case SYNTAXERROR:
                break;
            case GONEXT:
                break;
            case NOERROR:
                continue;
            case LOGICALERROR:
                break;
            default:
                continue;
        }
    }

    auto stop = std::chrono::high_resolution_clock::now(); 
    auto duration = std::chrono::duration_cast<std::chrono::nanoseconds>(stop - start);
    //std::cout << "Exec time: " << duration.count() << "\n";
*/
    /*int user_choice;
    cin>>user_choice;
    if (user_choice<possiblepaths.size()+1)
        this->setSelectedPath(possiblepaths.at(user_choice-1));
    else
        this->setSelectedPath(this->selectedPath={});
    return this->selectedPath;*/

}

void ExecHelper::processUserCommands(ExecContextOfCapsule * execContext){
    std::string userInput="";
    std::string tmp;
    bool recording = false;

    while (true){
        std::cout<<std::endl<<"PME";
        if(recording)
            std::cout<<"~action#";
        else
            std::cout<<"#";

        std::getline(std::cin, userInput);
        if(userInput == std::string("save")) {
            tmp.append("next\n");
            execContext->saveEffect(tmp);
            recording = false;
            replay(execContext, execContext->getEffect());
            break;
            // save it
        } else if(userInput == std::string("record")) {
            recording = true;
            tmp.erase();
            continue;
        } else if(recording) {
            tmp.append(userInput);
            tmp.append("\n");
            continue;
        }

        int result=scriptExecutor.ExecuteDBGCommand(execContext, userInput);

        //todo test send message and go next
        //test send message and go next
        switch (result){
            case SYNTAXERROR:
                continue;
            case GONEXT:
                break;
            case NOERROR:
                continue;
            case LOGICALERROR:
                continue;
            default:
                continue;
        }
        break;
    }
        
}


void ExecHelper::record(ExecContextOfCapsule * execContext){
    std::string userInput="";
    std::string tmp;

    while (true){
        std::getline(std::cin, userInput);
        tmp.append(userInput);
        tmp.append("\n");
        
        if(userInput == "save") {
            ledger = tmp;
        } else if(userInput == "exit") {
            break;
        }
    }
        
}

void ExecHelper::replay(ExecContextOfCapsule * execContext, std::string effect){
    std::string userInput="";
    std::istringstream ifs(effect);

    while (std::getline(ifs, userInput)) {
        int result=scriptExecutor.ExecuteDBGCommand(execContext, userInput);
        //todo test send message and go next
        //test send message and go next
        switch (result){
            case SYNTAXERROR:
                continue;
            case GONEXT:
                break;
            case NOERROR:
                continue;
            case LOGICALERROR:
                continue;
            default:
                continue;
        }
        break;
    }
        
}

string genRandomDecesion(ExecContextOfCapsule * execContext){
    return "End";
}



pmd::ExecMode ExecHelper::getExecMode() {
    return this->execMode;
}



ExecHelper::ExecHelper() {
}





void ExecHelper::setExecMode(pmd::ExecMode execMode) {
    this->execMode=execMode;
    
}

void ExecHelper::readExecConfig() {
    this->setExecMode(INVALID);
    if (this->config.load()){
        if (this->config.getConfig("PME.ExecMode")=="Interactive")
            this->setExecMode(INTERACTIVE);
        else if (this->config.getConfig("PME.ExecMode")=="Script" and this->config.getConfig("PME.ExecRulesScript").length()){
            this->setExecMode(SCRIPT);
            this->setExecScriptPath(this->config.getConfig("PME.ExecRulesScript"));
            auto start = std::chrono::high_resolution_clock::now(); 
            this->loadExecRules();
            auto stop = std::chrono::high_resolution_clock::now(); 
            auto duration = std::chrono::duration_cast<std::chrono::nanoseconds>(stop - start);
     //       std::cout << "Loading time: " << duration.count() << "\n";
            
        }
        else if (this->config.getConfig("PME.ExecMode")=="Random")
            this->setExecMode(RANDOM);
    }
    if (this->getExecMode()==INVALID){
        std::cout<<"Please provide exec configuation using a config file, a config file tamplate can be generated using \"make configure_files\""<<std::endl;
        exit(-1);
    }
}
void ExecHelper::setExecScriptPath(std::string scriptPath) {
    this->execScriptPath=scriptPath;
}

std::string ExecHelper::getExecScriptPath() {
    return this->execScriptPath;
}


//ExecutionStep ExecHelper::getUserDecesion(string currentstate, vector<ExecutionStep> possiblepaths,Message incomingmessage){
void ExecHelper::getUserDecesion(ExecContextOfCapsule * execContext){
    switch (execMode) {
        case INTERACTIVE:
            this->getUserDecesionFromConsole(execContext);
            break;
        case RANDOM :
            break;
        case SCRIPT:

            //[TODO] when should I set loc to CAPSULE?
            ExecContextForRule execCtxRule = {
                execContext->getCapsuleType() + std::to_string(execContext->getCapsuleInstance()),
                execContext->getExecLocation().locName,
                execContext->getCapsuleType(),
                STATE,
                execContext->getPartialityPattern(),
                execContext->getCapsuleHeap(),
                ""
            };

            auto start = std::chrono::high_resolution_clock::now(); 
            ExecRule* rule = this->execRulesAnalyzer.getApplicableRule(execCtxRule);
            auto stop = std::chrono::high_resolution_clock::now(); 

            auto duration = std::chrono::duration_cast<std::chrono::nanoseconds>(stop - start);

            //std::cout << "Query time: " << duration.count() << "\n";

            if(rule == NULL) {
                cout << "[WARNING] No applicable rule for [TODO]";
            } else {
                int result=scriptExecutor.ExecuteScript(execContext, rule->body);

                switch (result){
                    case SYNTAXERROR:
                        "[ERROR] Syntax error in [TODO]";
                        break;
                    default:
                        break;
                }
            }

            break;
        // default:
        //     this->getUserDecesionFromConsole(execContext);
    }
   // this->getUserDecesionFromConsole(execContext);
}
    

    
void ExecHelper::loadExecRules() {
    this->execRulesAnalyzer=PMEExecRulesAnalyzer(this->getExecScriptPath(),true);
    this->execRulesAnalyzer.parseRules();
    //this->execRulesAnalyzer.
}
    
void ExecHelper::execSendMessage(std::function<bool(std::string portName,std::string messageName)> sendMessage) {
    sendMessage(std::string("testPort"), std::string("testMessage"));
}
    

    
   /* pmd::Heap * ExecHelper::getHeap() {
        return this->heap;
    }
    
    void ExecHelper::setHeap(pmd::Heap * heap) {
        this->heap=heap;
    }*/
    
 /*   void ExecHelper::initCallBackMethodSendInternalMessage(std::function<int (Message)> pSendInternalMessage) {
        this->pSendInternalMessage=pSendInternalMessage;
    }
    
    void ExecHelper::initCallBackMethodSendMessage(std::function<int (Message)> pSendMessage) {
        this->pSendMessage=pSendMessage;
    }
    
    std::function<int(Message)>  ExecHelper::getCallBackMethodSendMessage(){
        return this->pSendMessage;
    }

    std::function<int(Message)>  ExecHelper::getCallBackMethodSendInternalMessage(){
        return this->pSendInternalMessage;
    }*/
}
