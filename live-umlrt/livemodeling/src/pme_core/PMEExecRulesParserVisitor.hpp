//
//  ExexRulesParserVisitor.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#ifndef ExexRulesParserVisitor_hpp
#define ExexRulesParserVisitor_hpp

#include <unordered_set>
#include <functional>
#include "antlr4-runtime.h"
#include "PMEExecRulesLexer.h"
#include "PMEExecRulesParser.h"
#include "PMEExecRulesBaseVisitor.h"
#include "ExecRuleTypes.hpp"



namespace pmd {
    class PMEExecRulesParserVisitor : public PMEExecRulesBaseVisitor {
    public:
        PMEExecRulesParserVisitor(std::unordered_multiset<ExecRule> * execRules);
        antlrcpp::Any visitExecRule(PMEExecRulesParser::ExecRuleContext *ctx) override;
        antlrcpp::Any visitWhen(PMEExecRulesParser::WhenContext *ctx) override;
        antlrcpp::Any visitGuard(PMEExecRulesParser::GuardContext *ctx) override;
        antlrcpp::Any visitCapsuleLoc(PMEExecRulesParser::CapsuleLocContext *ctx) override;
        antlrcpp::Any visitTransLoc(PMEExecRulesParser::TransLocContext *ctx) override;
        antlrcpp::Any visitStateLoc(PMEExecRulesParser::StateLocContext *ctx) override;
    private:
        std::unordered_multiset<ExecRule> * execRules;
        ExecRule * currentExecRule;
    };
}
    

#endif /* ExexRulesParserVisitor_hpp */
