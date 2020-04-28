//
//  ExexRulesParserVisitor.cpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#include "PMEExecRulesParserVisitor.hpp"

namespace pmd {

        PMEExecRulesParserVisitor::PMEExecRulesParserVisitor(std::unordered_multiset<ExecRule> * execRules) {
            this->execRules=execRules;
        }
    
        antlrcpp::Any PMEExecRulesParserVisitor::visitExecRule(PMEExecRulesParser::ExecRuleContext *ctx)   {
            ExecRule r;
            this->currentExecRule=&r;
            r.name=ctx->ID()->getText();
            r.body=ctx->Body()->getText();
            r.when=visitWhen(ctx->when()).as<std::string>();
            visitWhere(ctx->where());
            this->execRules->insert(*this->currentExecRule);
            this->currentExecRule=nullptr;
            return nullptr;
        }
    
        antlrcpp::Any PMEExecRulesParserVisitor::visitWhen(PMEExecRulesParser::WhenContext *ctx)  {
            if (ctx)
                return visit(ctx->guard());
            else{
                std::string sTemp="*";
                return sTemp;
            }
        }
    
        antlrcpp::Any PMEExecRulesParserVisitor::visitGuard(PMEExecRulesParser::GuardContext *ctx)  {
            return ctx->guardExp->getText();
        }
        
    
        antlrcpp::Any PMEExecRulesParserVisitor::visitCapsuleLoc(PMEExecRulesParser::CapsuleLocContext *ctx)  {
            this->currentExecRule->scope=CAPSULE;
            if (ctx->captype){
                this->currentExecRule->capsule=ctx->capName->getText();
                this->currentExecRule->capsuleType=ctx->captype->getText();
                this->currentExecRule->loc=ctx->capName->getText();
            }
            else{
                this->currentExecRule->capsule=ctx->capName->getText();
                this->currentExecRule->capsuleType="*";
                this->currentExecRule->loc=ctx->capName->getText();
            }
            return ctx->capName->getText();
        }

        //////
        antlrcpp::Any PMEExecRulesParserVisitor::visitTransLoc(PMEExecRulesParser::TransLocContext *ctx) {
            this->currentExecRule->scope=TRANSITION;
            if (ctx->captype){
                this->currentExecRule->capsule=ctx->capName->getText();
                this->currentExecRule->capsuleType=ctx->captype->getText();
                this->currentExecRule->loc=ctx->transName->getText();
            }
            else if (ctx->capName){
                this->currentExecRule->capsule=ctx->capName->getText();
                this->currentExecRule->loc=ctx->transName->getText();
                this->currentExecRule->capsuleType="*";
            }
            else{
                this->currentExecRule->loc=ctx->transName->getText();
                this->currentExecRule->capsuleType="*";
                this->currentExecRule->capsule="*";
            }
            return ctx->transName->getText();
        }
        ////////
        antlrcpp::Any PMEExecRulesParserVisitor::visitStateLoc(PMEExecRulesParser::StateLocContext *ctx)  {
            this->currentExecRule->scope=STATE;
            if (ctx->captype){
                this->currentExecRule->capsule=ctx->capName->getText();
                this->currentExecRule->capsuleType=ctx->captype->getText();
                this->currentExecRule->loc=ctx->stateName->getText();
            }
            else if (ctx->capName){
                this->currentExecRule->capsule=ctx->capName->getText();
                this->currentExecRule->loc=ctx->stateName->getText();
                this->currentExecRule->capsuleType="*";
            }
            else{
                this->currentExecRule->loc=ctx->stateName->getText();
                this->currentExecRule->capsuleType="*";
                this->currentExecRule->capsule="*";
            }
            return ctx->stateName->getText();
        }
}

