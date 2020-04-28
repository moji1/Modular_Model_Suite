//
//  PMEExprVisitorImp.cpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-10-29.
//

#include "PMEExprVisitorImp.hpp"
#include <cstdlib>

namespace pmd {
antlrcpp::Any PMEExprVisitorImp::visitViewSubcommands(PMEExprParser::ViewSubcommandsContext *ctx)  {
    std::string subcmd="";
    if (ctx->subcmd){
        subcmd=ctx->subcmd->getText();
        if (subcmd=="context")
            this->execContext->showContext();
        else if (subcmd=="var"){
            if (ctx->name)
               this->execContext->showVar(ctx->name->getText());
            else
                this->execContext->showVar();
        }
        else if (subcmd=="trace"){
            if (ctx->cnt)
                this->execContext->showTrace(stoi(ctx->cnt->getText()));
            else
                this->execContext->showTrace();
        }
        else if (subcmd=="connections")
            std::cout<<"Executing view connection command"<<std::endl;
        else if (subcmd=="options")
            this->execContext->showOptions();
        else if (subcmd=="decisions"){
            this->execContext->showDecesions();
        }
        else if (subcmd=="inputs"){
            this->execContext->showInputMsgs();
        }
        else if (subcmd=="outputs"){
            this->execContext->showOutputMsgs();
        }
        else if (subcmd=="message"){
            this->execContext->showLastMsg();
        }
        setVisitResult(NOERROR);
    } else
        setVisitResult(SYNTAXERROR);
    return 0;
}

antlrcpp::Any PMEExprVisitorImp::visitPossiblePaths(PMEExprParser::PossiblePathsContext *ctx)  {
    ExecutionStep step;
    if (ctx->optionId){
        int pathid=stoi(ctx->optionId->getText());
        if (pathid<=0 or pathid>this->execContext->getPossibleExecPath().size()+1)
            setVisitResult(LOGICALERROR);
        else{
            this->execContext->setSelectedPath(stoi(ctx->optionId->getText()));
            setVisitResult(NOERROR);
        }
    }
    else if (ctx->transName){ // later add more validation here. e.g., check and make sure the requited path is valid and ...
        step.transName=ctx->transName->getText();
        this->execContext->setSelectedPath(step);
        setVisitResult(NOERROR);
    }else if (ctx->stateName){
        step.targetState=ctx->stateName->getText();
        this->execContext->setSelectedPath(step);
        setVisitResult(NOERROR);
    } else if (ctx->randomPath) {
        int r=RandomGenerator::generateInt(1, this->execContext->getPossibleExecPath().size()+1);
        this->execContext->setSelectedPath(r);
    }
    return 0;
}
    
void PMEExprVisitorImp::setVisitResult(pmd::RESULTCODE resultCode) {
        this->visitResult=resultCode;
    }
    
pmd::RESULTCODE PMEExprVisitorImp::getResultCode() {
        return this->visitResult;
}
    
void PMEExprVisitorImp::setExecContextt(ExecContextOfCapsule *execContext){
        this->execContext=execContext;
}
    
antlrcpp::Any PMEExprVisitorImp::visitControlCMD(PMEExprParser::ControlCMDContext *ctx) {
    if (ctx->cmd){
        if (ctx->cmd->getText()=="next")
            setVisitResult(GONEXT);
        else if (ctx->cmd->getText()=="exit")
            exit(0);
        else if (ctx->cmd->getText()=="continue")
            setVisitResult(CONTINUETONEXTBREAK);
    }
    
    return 0;
}
    
antlrcpp::Any PMEExprVisitorImp::visitHelp(PMEExprParser::HelpContext *ctx) {
    std::cout<<"Avialable Commands"<<std::endl;
    return 0;
}

antlrcpp::Any PMEExprVisitorImp::visitReplyMsgCmd(PMEExprParser::ReplyMsgCmdContext *ctx) {
    Message msg;
    msg.portName=this->execContext->getLastMsg().portName;
    msg.index=this->execContext->getLastMsg().index;
    msg.signal=ctx->signalName->getText();
    std::map<std::string,std::string> msgParams=this->execContext->getMessageParam(msg.portName, msg.signal);
    // check port and signal, and payload validity later
    if (ctx->params())
        prepareMessagePayload(&msg, ctx->params());
    if (msg.payload.size()==msgParams.size()){
        this->execContext->getCallBackMethodSendMessage()(msg);
        setVisitResult(NOERROR);
    }
    else
        setVisitResult(LOGICALERROR);
    return 0;
}
    
antlrcpp::Any PMEExprVisitorImp::visitSendMsgCmd(PMEExprParser::SendMsgCmdContext *ctx) {
    Message msg;
    msg.signal=ctx->signalName->getText();
    msg.portName=ctx->portName->getText();
    std::map<std::string,std::string> msgParams=this->execContext->getMessageParam(msg.portName, msg.signal);
    if (ctx->index)
        msg.index=stoi(ctx->index->getText());
    else
        msg.index=0;
    // check port and signal, and payload validity later
    if (ctx->params())
        prepareMessagePayload(&msg, ctx->params());
    if (msg.payload.size()==msgParams.size()){
        this->execContext->getCallBackMethodSendMessage()(msg);
        setVisitResult(NOERROR);
    }
    else
        setVisitResult(LOGICALERROR);
    
    return 0;
}
    
/*antlrcpp::Any PMEExprVisitorImp::visitIntliteral(PMEExprParser::IntliteralContext *ctx) {
    return stoi(ctx->Integerliteral()->getText());
}
 
antlrcpp::Any PMEExprVisitorImp::visitCharliteral(PMEExprParser::CharliteralContext *ctx) {
    return ctx->Characterliteral()->getText()[0];
}
 
antlrcpp::Any PMEExprVisitorImp::visitFloatliteral(PMEExprParser::FloatliteralContext *ctx) {
    return stof(ctx->Floatingliteral()->getText());
}
 
antlrcpp::Any PMEExprVisitorImp::visitStringliteral(PMEExprParser::StringliteralContext *ctx) {
    return ctx->getText();
}
*/

antlrcpp::Any PMEExprVisitorImp::visitBooleanliteral(PMEExprParser::BooleanliteralContext  *ctx) {
    if (ctx->getText()=="true")
        return true;
    else
        return false;
}
antlrcpp::Any PMEExprVisitorImp::visitLiteral(PMEExprParser::LiteralContext *ctx){
    if (ctx->Integerliteral())
        return stoi(ctx->Integerliteral()->getText());
    else if (ctx->booleanliteral())
        return visitBooleanliteral(ctx->booleanliteral());
    else if (ctx->Characterliteral())
        return ctx->Characterliteral()->getText()[1];
    else if (ctx->Stringliteral())
        return ctx->Stringliteral()->getText().substr(1,ctx->Stringliteral()->getText().length()-2);
    else if (ctx->Floatingliteral())
        return stof(ctx->Floatingliteral()->getText());
    return NULL;
}
antlrcpp::Any PMEExprVisitorImp::visitValue(PMEExprParser::ValueContext *ctx)  {
    if (ctx->literal())
        return visitLiteral(ctx->literal());
    else if (ctx->ID())
        return this->execContext->getCapsuleHeap()->getVarValue(ctx->ID()->getText());
    else if (ctx->random())
        return 0;
    
    return 0;
}
/////
bool PMEExprVisitorImp::validteValue(antlrcpp::Any value,std::string type){
     if (type=="Integer" and value.is<int>())
         return true;
     else if (type=="Double" and value.is<double>())
         return true;
     else if (type=="String" and value.is<std::string>())
         return true;
     else if (type=="Char" and value.is<char>())
         return true;
     else if (type=="Boolean" and value.is<bool>())
         return true;
    else
        return false;
}
////
void PMEExprVisitorImp::prepareMessagePayload(Message *msg, PMEExprParser::ParamsContext * payload){
    //std::vector<PMEExprParser::PairContext*> params=ctx->payload->pair();
    std::map<std::string,std::string> msgParams=this->execContext->getMessageParam(msg->portName, msg->signal);
    if (payload->random()){
        //
    }else {
        std::vector<PMEExprParser::PairContext*> params=payload->pair();
        for (std::vector<PMEExprParser::PairContext*>::iterator it1=params.begin();it1!=params.end();it1++){
            std::string name=(*it1)->ID()->getText();
            antlrcpp::Any value=visitValue((*it1)->value());
            std::map<std::string,std::string>::iterator it2=msgParams.find(name);
            if (it2!=msgParams.end()){
                if (validteValue(value,msgParams[name]))
                    msg->payload[name]=toAnyBoost(value);
            }
        }
    }
}

boost::any  PMEExprVisitorImp::toAnyBoost(antlrcpp::Any antlrAny){
    boost::any result;
    if (antlrAny.is<int>())
        result=antlrAny.as<int>();
    else if (antlrAny.is<std::string>())
        result=antlrAny.as<std::string>();
    else if (antlrAny.is<char>())
        result=antlrAny.as<char>();
    else if (antlrAny.is<bool>())
        result=antlrAny.as<bool>();
    else if (antlrAny.is<double>())
        result=antlrAny.as<double>();
    return result;
        
}

antlrcpp::Any PMEExprVisitorImp::visitPrintExpr(PMEExprParser::PrintExprContext *ctx) {
        //std::cout<<"Printing expression\n";
        antlrcpp::Any a=visit(ctx->expr());
        if (a.is<int>())
            std::cout<<a.as<int>()<<std::endl;
        else if (a.is<std::string>())
            std::cout<<a.as<std::string>()<<std::endl;
        else if (a.is<char>())
            std::cout<<a.as<char>()<<std::endl;
        else if (a.is<bool>()){
           if (a.as<bool>())
               std::cout<<"true"<<std::endl;
            else
               std::cout<<"false"<<std::endl;
        }
        else if (a.is<double>())
            std::cout<<a.as<double>()<<std::endl;
        return 0;
}
    
antlrcpp::Any PMEExprVisitorImp::visitAssign(PMEExprParser::AssignContext *ctx)  {
        antlrcpp::Any rvalue=visit(ctx->expr());
        std::string lvalue;
        if (ctx->ID())
            lvalue=ctx->ID()->getText();
        else if (ctx->QID())
            lvalue=ctx->QID()->getText();
        if (rvalue.is<int>())
            this->execContext->getCapsuleHeap()->setVarValue(lvalue, static_cast<int>(rvalue));
        else if (rvalue.is<double>() )
            this->execContext->getCapsuleHeap()->setVarValue(lvalue, static_cast<double>(rvalue));
        else if (rvalue.is<float>() )
            this->execContext->getCapsuleHeap()->setVarValue(lvalue,(double) static_cast<float>(rvalue));
        else if  (rvalue.is<std::string>())
            this->execContext->getCapsuleHeap()->setVarValue(lvalue, rvalue.as<std::string>());
        else if  (rvalue.is<char>())
            this->execContext->getCapsuleHeap()->setVarValue(lvalue, static_cast<char>(rvalue));
        else if (rvalue.is<bool>())
            this->execContext->getCapsuleHeap()->setVarValue(lvalue, static_cast<bool>(rvalue));
        else
            std::cout<<"Assignment failed"<<std::endl ;
        return 0;
}

// antlrcpp::Any PMEExprVisitorImp::visitBlank(PMEExprParser::BlankContext *ctx)  {
//         return 0;
// }
    
antlrcpp::Any PMEExprVisitorImp::visitParens(PMEExprParser::ParensContext *ctx)  {
        return visit(ctx->expr());
}
    
pmd::VarType PMEExprVisitorImp::antlrAnytoVarType(antlrcpp::Any a){
        if (a.is<int>())
            return static_cast<int>(a);
        else if ((a.is<double>()))
            return static_cast<double>(a);
        else if ((a.is<char>()))
            return static_cast<char>(a);
        else if ((a.is<bool>()))
            return static_cast<bool>(a);
        else if ((a.is<std::string>()))
            return a.as<std::string>();
        else
            return pmd::UNKOWN;
}
    
antlrcpp::Any PMEExprVisitorImp::visitMulDiv(PMEExprParser::MulDivContext *ctx)  {
        antlrcpp::Any left=visit(ctx->expr(0));
        antlrcpp::Any right=visit(ctx->expr(1));
        pmd::VarType leftV;
        pmd::VarType rightV;
        //pmd::VarType resultV;
        leftV=antlrAnytoVarType(left);
        rightV=antlrAnytoVarType(right);
        if (ctx->op->getType()==PMEExprParser::MUL)
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::mul{},leftV,rightV));
        //esultV=boost::apply_visitor(pmd::mul{},leftV,rightV);
        else
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::div{},leftV,rightV));
}
    
antlrcpp::Any PMEExprVisitorImp::visitAddSub(PMEExprParser::AddSubContext *ctx)  {
        antlrcpp::Any left=visit(ctx->expr(0));
        antlrcpp::Any right=visit(ctx->expr(1));
        pmd::VarType leftV;
        pmd::VarType rightV;
        //pmd::VarType resultV;
        leftV=antlrAnytoVarType(left);
        rightV=antlrAnytoVarType(right);
        if (ctx->op->getType()==PMEExprParser::ADD)
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::add{},leftV,rightV));
        //esultV=boost::apply_visitor(pmd::mul{},leftV,rightV);
        else
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::sub{},leftV,rightV));
}
    
antlrcpp::Any PMEExprVisitorImp::visitId(PMEExprParser::IdContext *ctx)  {
        std::string varName=ctx->ID()->getText();
        return boost::apply_visitor(toAntlrAny{},this->execContext->getCapsuleHeap()->getVarValue(varName));
}
    
antlrcpp::Any PMEExprVisitorImp::visitQualifiedID(PMEExprParser::QualifiedIDContext *ctx)  {
    std::string varName=ctx->QID()->getText();
    return boost::apply_visitor(toAntlrAny{},this->execContext->getCapsuleHeap()->getVarValue(varName));
}

antlrcpp::Any PMEExprVisitorImp::visitReceiptMsgCmd(PMEExprParser::ReceiptMsgCmdContext *ctx) {
    std::string signalName = ctx->signalName->getText();
    std::string portName = ctx->portName->getText();

    int index = 0;
    if(ctx->index != nullptr)
        index = std::stoi(ctx->index->getText());

    Message lastMessage = this->execContext->getLastMsg();
    if(lastMessage.portName == portName && lastMessage.signal == signalName && lastMessage.index == index) {
        std::cout<<"True"<<std::endl;
        return true;
    }

    std::cout<<"False"<<std::endl;
    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitEditStateCmd(PMEExprParser::EditStateCmdContext *ctx) {
    std::string stateName = ctx->stateName->getText();

    if (ctx->cmd->getText()=="add")
        this->execContext->getCallBackMethodAddState()(stateName.c_str());
    else
        this->execContext->getCallBackMethodRemoveState()(stateName.c_str());

    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitAddTransitionCmd(PMEExprParser::AddTransitionCmdContext *ctx) {
    std::string from = ctx->from->getText();
    std::string to = ctx->to->getText();
    std::string port = ctx->portName->getText();
    std::string signal = ctx->signalName->getText();

    std::string transName;
    if(ctx->transName != NULL) {
        transName = ctx->transName->getText();
    }
    else {
        int num = rand() % 100 + 1;
        transName = from + std::string("_") + to + std::string("_") + std::to_string(num);
    }

    this->execContext->getCallBackMethodAddTransition()(transName.c_str(), from.c_str(), to.c_str(), port.c_str(), signal.c_str());
    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitDeleteTransitionCmd(PMEExprParser::DeleteTransitionCmdContext *ctx) {
    if(ctx->transName != NULL) {
        std::string transName = ctx->transName->getText();
        this->execContext->getCallBackMethodRemoveTransition()(transName.c_str());
    }
    else {
        std::string from = ctx->from->getText();
        std::string to = ctx->to->getText();
        this->execContext->getCallBackMethodRemoveTransitions()(from.c_str(), to.c_str());
    }
    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitBreakCmd(PMEExprParser::BreakCmdContext *ctx) {
    std::string transName = ctx->transName->getText();
    this->execContext->getCallBackMethodAddBreakpoint()(transName.c_str());
    //this->execContext->addBreakpoint(transName);
    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitDeleteBreakpointCmd(PMEExprParser::DeleteBreakpointCmdContext *ctx) {
    std::string transName = ctx->transName->getText();
    this->execContext->getCallBackMethodRemoveBreakpoint()(transName.c_str());
    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitReplayMsgCmd(PMEExprParser::ReplayMsgCmdContext *ctx) {
    this->execContext->getCallBackMethodReplayMessage()();
    setVisitResult(GONEXT);
    return true;
}

antlrcpp::Any PMEExprVisitorImp::visitInformInCmd(PMEExprParser::InformInCmdContext *ctx) {
    std::string portName = ctx->portName->getText();
    int sec = stoi(ctx->sec->Integerliteral()->getText());
    int ns = 0;
    if(ctx->ns != NULL)
        ns = stoi(ctx->ns->Integerliteral()->getText());

    this->execContext->getCallBackMethodSetTimer()((char*) portName.c_str(), sec, ns);
    return true;
}

/*antlrcpp::Any PMEExprVisitorImp::visitInt(PMEExprParser::IntContext *ctx)  {
        return std::stoi(static_cast<std::string>(ctx->INT()->getText()));
}*/
    
} // end of name space
