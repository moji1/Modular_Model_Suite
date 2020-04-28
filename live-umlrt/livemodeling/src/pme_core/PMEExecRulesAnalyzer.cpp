/*
 * ExecRulesAnalyzer.cpp
 *
 *  Created on: Sep 8, 2018
 *      Author: mojtababagherzadeh
 */

#include "PMEExecRulesAnalyzer.hpp"
using namespace antlr4;

using namespace logging::trivial;

namespace pmd {



PMEExecRulesAnalyzer::~PMEExecRulesAnalyzer() {
	// TODO Auto-generated destructor stub
}
    
    PMEExecRulesAnalyzer::PMEExecRulesAnalyzer(std::string rulesFilePath, bool fromFile) {
        std::ifstream rulesFile;
        rulesFile.open(rulesFilePath);
        this->parserInput= ANTLRInputStream(rulesFile);
        rulesFile.close();
        const char *s="fk;lk";
        std::string s1= string(s);
    }
    

    PMEExecRulesAnalyzer::PMEExecRulesAnalyzer(std::string rulesTxt) { 
        this->parserInput= ANTLRInputStream(rulesTxt);
    }
    
    void PMEExecRulesAnalyzer::parseRules() {
        PMEExecRulesLexer lexer(&parserInput);
        lexer.removeErrorListeners();
        //std::cout<<lexer.getTokenNames().size()<<std::endl;
        CommonTokenStream tokens(&lexer);
        //std::cout<<"Tokens: "<<tokens.getText()<<std::endl;
        PMEExecRulesParser parser(&tokens);
        parser.removeParseListeners();
        parser.addErrorListener(new RuleParserDescriptiveErrorListener);
        tree::ParseTree *tree = parser.rules();
        //std::cout<<"Tree: "<<tree->toStringTree()<<std::endl;
        //BOOST_LOG_SEV(log, trace) << "A grammer is parsed, its tree is: " << tree->toStringTree();
        //std::cout<<parser.getTokenNames().size()<<std::endl;
        PMEExecRulesParserVisitor visitor(&execRules);
        visitor.visit(tree);
    }
    
    void PMEExecRulesAnalyzer::printExecRules() {
        std::unordered_multiset<ExecRule>::iterator it1;
        for (it1=this->execRules.begin();it1!=this->execRules.end();it1++){
            std::cout<<it1->name<<","<<it1->capsuleType<<","<<it1->capsule<<","<<it1->loc<<","<<it1->when<<","<<it1->scope<<","<<it1->body<<std::endl;
        }
    }
    
    pmd::ExecRule * PMEExecRulesAnalyzer::getApplicableRuleOfElement(pmd::ExecContextForRule ctx) {
        pmd::ExecRule * result=nullptr;
        ExecRule r;
        r.capsule=ctx.capsule;
        r.scope=ctx.scope;
        r.loc=ctx.loc;
        r.capsuleType=ctx.capsuleType;
        // check for exact context ( same capsule, capsuleType, same scop, and same loc)
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the same scope, same capsule and all(*) loc
        r.loc="*";
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the same scope, same loc and all(*) capsules
        r.capsule="*";
        r.loc=ctx.loc;
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the same scop and capsule * and loc *
        r.capsule="*";
        r.loc="*";
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the scope capsule  r.capsule=ctx.capsule
        r.scope=pmd::CAPSULE;
        r.capsule=ctx.capsule;
        r.loc=ctx.capsule;
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the scope capsule  and * capsule and * loc
        r.scope=pmd::CAPSULE;
        r.capsule="*";
        r.loc="*";
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the same scope  and * capsule and * loc and capsuleType
        r.capsuleType="*";
        result=this->lookup(ctx,r);
        return result;
    }
    ////
    pmd::ExecRule * PMEExecRulesAnalyzer::lookup(pmd::ExecContextForRule ctx, pmd::ExecRule r){
        std::pair<ExecRules::iterator, ExecRules::iterator> range = this->execRules.equal_range(r);
        for (ExecRules::iterator it=range.first; it!=range.second;it++)
            if (this->checkGurad(ctx, *range.first))
                return const_cast<pmd::ExecRule *> (&(*range.first));
        return nullptr;
    }
    bool PMEExecRulesAnalyzer::checkGurad(pmd::ExecContextForRule ctx, pmd::ExecRule rule) {
        if (rule.when=="*")
            return true;
        else if (ToString(ctx.partialityPattern)==rule.when)
            return true;
        else
            return false;
    }
    ////
    pmd::ExecRule *PMEExecRulesAnalyzer::getApplicableRule(pmd::ExecContextForRule ctx) {
        if (ctx.scope==CAPSULE)
            return this->getApplicableRuleOfCapsule(ctx);
        else
            return this->getApplicableRuleOfElement(ctx);
    }
    ///
    pmd::ExecRule *PMEExecRulesAnalyzer::getApplicableRuleOfCapsule(pmd::ExecContextForRule ctx) {
        pmd::ExecRule * result=nullptr;
        ExecRule r;
        r.capsule=ctx.capsule;
        r.scope=ctx.scope;
        r.loc=ctx.loc;
        r.capsuleType=ctx.capsuleType;
        // check for exact context (exactly same capsule, same scop, and same loc)
        result=this->lookup(ctx,r);
        if (result)
            return result;
        // else based on the same scope and capsule type, all(*) capsule and all(*) loc
        r.loc="*";
        r.capsule="*";
        result=this->lookup(ctx,r);
        if (result)
            return result;
        //// else based on the same scope, all(*) capsule and all(*) loc
        r.capsuleType="*";
        result=this->lookup(ctx,r);
        return result;
    }
    
    PMEExecRulesAnalyzer::PMEExecRulesAnalyzer() { 
    }
    
    
    
    
    

} /* namespace pmd */
