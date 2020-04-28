//
//  PMEExprVisitorImp.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-10-29.
//

#ifndef PMEExprVisitorImp_hpp
#define PMEExprVisitorImp_hpp

#include <stdio.h>
#include "PMEExprBaseVisitor.h"
#include "ExecContext.hpp"
#include "../util/RandomGenerator.hpp"
namespace   pmd {

struct toAntlrAny : public boost::static_visitor<antlrcpp::Any>
{
    antlrcpp::Any operator()(double d) const {return  d;}
    antlrcpp::Any operator()(char c) const { return c; }
    antlrcpp::Any operator()(std::string s) const { return s; }
    antlrcpp::Any operator()(char* s) const { return std::string(s); }
    antlrcpp::Any operator()(const char* s) const { return std::string(s); }
    antlrcpp::Any operator()(int s) const { return s; }
    antlrcpp::Any operator()(bool b) const { return b; }
     //antlrcpp::Any operator()(enum pmd::WRONG_TYPE s) const { return nullptr; }
 
 };
     
class PMEExprVisitorImp : public PMEExprBaseVisitor {
private:
    ExecContextOfCapsule *execContext;
    RESULTCODE visitResult;
    void setVisitResult(RESULTCODE resultCode);
public:
    virtual antlrcpp::Any visitPossiblePaths(PMEExprParser::PossiblePathsContext *ctx) override;
    virtual antlrcpp::Any visitViewSubcommands(PMEExprParser::ViewSubcommandsContext *ctx) override;
    virtual antlrcpp::Any visitControlCMD(PMEExprParser::ControlCMDContext *ctx) override;
    virtual antlrcpp::Any visitHelp(PMEExprParser::HelpContext *ctx) override;
    virtual antlrcpp::Any visitReplyMsgCmd(PMEExprParser::ReplyMsgCmdContext *ctx) override ;
    virtual antlrcpp::Any visitSendMsgCmd(PMEExprParser::SendMsgCmdContext *ctx) override;
    virtual antlrcpp::Any visitLiteral(PMEExprParser::LiteralContext *ctx) override;
    virtual antlrcpp::Any visitBooleanliteral(PMEExprParser::BooleanliteralContext *ctx) override;
    virtual antlrcpp::Any visitValue(PMEExprParser::ValueContext *ctx) override;
    virtual antlrcpp::Any visitPrintExpr(PMEExprParser::PrintExprContext *ctx) override;
    virtual antlrcpp::Any visitAssign(PMEExprParser::AssignContext *ctx) override;
    //virtual antlrcpp::Any visitBlank(PMEExprParser::BlankContext *ctx) override;
    virtual antlrcpp::Any visitParens(PMEExprParser::ParensContext *ctx) override;
    virtual antlrcpp::Any visitMulDiv(PMEExprParser::MulDivContext *ctx) override;
    virtual antlrcpp::Any visitAddSub(PMEExprParser::AddSubContext *ctx) override;
    virtual antlrcpp::Any visitId(PMEExprParser::IdContext *ctx) override;
    virtual antlrcpp::Any visitQualifiedID(PMEExprParser::QualifiedIDContext *ctx) override;
    virtual antlrcpp::Any visitReceiptMsgCmd(PMEExprParser::ReceiptMsgCmdContext *ctx) override;
    virtual antlrcpp::Any visitEditStateCmd(PMEExprParser::EditStateCmdContext *ctx) override;
    virtual antlrcpp::Any visitAddTransitionCmd(PMEExprParser::AddTransitionCmdContext *ctx) override;
    virtual antlrcpp::Any visitDeleteTransitionCmd(PMEExprParser::DeleteTransitionCmdContext *ctx) override;
    virtual antlrcpp::Any visitBreakCmd(PMEExprParser::BreakCmdContext *ctx) override;
    virtual antlrcpp::Any visitDeleteBreakpointCmd(PMEExprParser::DeleteBreakpointCmdContext *ctx) override;
    virtual antlrcpp::Any visitReplayMsgCmd(PMEExprParser::ReplayMsgCmdContext *ctx) override;
    virtual antlrcpp::Any visitInformInCmd(PMEExprParser::InformInCmdContext *ctx) override;


    //virtual antlrcpp::Any visitInt(PMEExprParser::IntContext *ctx) override;
    pmd::VarType antlrAnytoVarType(antlrcpp::Any a);
    RESULTCODE getResultCode();
    void setExecContextt(ExecContextOfCapsule *execContext);
    bool validteValue(antlrcpp::Any value,std::string type);
    void prepareMessagePayload(Message *msg, PMEExprParser::ParamsContext * params);
    boost::any  toAnyBoost(antlrcpp::Any antlrAny);
};

}// end of name space
#endif /* PMEExprVisitorImp_hpp */
