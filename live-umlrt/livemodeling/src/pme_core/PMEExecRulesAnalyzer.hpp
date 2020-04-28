/*
 * ExecRulesAnalyzer.hpp
 *
 *  Created on: Sep 8, 2018
 *      Author: mojtababagherzadeh
 */
#ifndef ExecRulesAnalyzer_hpp
#define ExecRulesAnalyzer_hpp

#include <iostream>
#include <string>
#include <unordered_set>
//#include <functional>
//#include "antlr4-runtime.h"
//#include "ExecRulesLexer.h"
//#include "ExecRulesParser.h"
//#include "ExecRulesBaseVisitor.h"
#include "../util/logging_config.hpp"
#include "ExecRuleTypes.hpp"
#include "PMEExecRulesParserVisitor.hpp"
#include "ExecContext.hpp"
#include "RuleParserDescriptiveErrorListener.hpp"

//#include <stdexcept>


using namespace antlr4;
using namespace logging::trivial;


namespace pmd{
    typedef std::unordered_multiset<pmd::ExecRule> ExecRules;
    class PMEExecRulesAnalyzer {
    public:
        void parseRules();
        PMEExecRulesAnalyzer();
        PMEExecRulesAnalyzer(std::string rulesFilePath, bool fromFile);
        PMEExecRulesAnalyzer(std::string rulesTxt);
        ExecRule * getApplicableRule(ExecContextForRule ctx);
        bool checkGurad(ExecContextForRule ctx,ExecRule rule);
        ExecRule * lookup(ExecContextForRule ctx, pmd::ExecRule r);
        void printExecRules();
        virtual ~PMEExecRulesAnalyzer();
    private:
        ExecRules execRules;
        std::string rulesTxt;
        antlr4::ANTLRInputStream parserInput;
        src::severity_logger<severity_level> log;
        ExecRule * getApplicableRuleOfElement(ExecContextForRule ctx);
        ExecRule * getApplicableRuleOfCapsule(ExecContextForRule ctx);
};
    


} /* namespace pmd */

#endif /* ExecRulesAnalyzer_hpp */
