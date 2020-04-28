/*
 * RuleBodyExecutor.cpp
 *
 *  Created on: Sep 8, 2018
 *      Author: mojtababagherzadeh
 */

#include "PMEScriptExecutor.hpp"
#include <chrono> 

namespace pmd {
    using namespace antlr4;

PMEScriptExecutor::PMEScriptExecutor() {
	// TODO Auto-generated constructor stub

}

PMEScriptExecutor::~PMEScriptExecutor() {
	// TODO Auto-generated destructor stub
}
    
int PMEScriptExecutor::ExecuteDBGCommand(pmd::ExecContextOfCapsule *execContext, std::string cmdLine) {
    ANTLRInputStream input(cmdLine);
    PMEExprLexer lexer(&input);
    lexer.removeErrorListeners();
    PMEExprParserDescriptiveErrorListener errorListener=PMEExprParserDescriptiveErrorListener();
    lexer.addErrorListener(&errorListener);
    CommonTokenStream tokens(&lexer);
    PMEExprParser parser(&tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(&errorListener);
    //tree::ParseTree *tree = parser.dbgCommands();
    tree::ParseTree *tree = parser.interactiveStatment();
    //std::cout<<tree->getText()<<std::endl;
    int result=parser.getNumberOfSyntaxErrors();
    if (result>0)
        return SYNTAXERROR;
    PMEExprVisitorImp visitor;
    visitor.setExecContextt(execContext);
    visitor.visit(tree);
    return visitor.getResultCode();
}
    
int PMEScriptExecutor::ExecuteScript(pmd::ExecContextOfCapsule *execContext, std::string body) {
    auto start = std::chrono::high_resolution_clock::now(); 

    ANTLRInputStream input(body);
    PMEExprLexer lexer(&input);
    lexer.removeErrorListeners();
    PMEExprParserDescriptiveErrorListener errorListener=PMEExprParserDescriptiveErrorListener();
    lexer.addErrorListener(&errorListener);
    CommonTokenStream tokens(&lexer);
    PMEExprParser parser(&tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(&errorListener);

    tree::ParseTree *tree = parser.script();
    //std::cout<<tree->getText()<<std::endl;
    int result=parser.getNumberOfSyntaxErrors();
    if (result>0)
        return SYNTAXERROR;

    PMEExprVisitorImp visitor;
    visitor.setExecContextt(execContext);
    visitor.visit(tree);

    auto stop = std::chrono::high_resolution_clock::now(); 
    auto duration = std::chrono::duration_cast<std::chrono::nanoseconds>(stop - start);
//    std::cout << "Parsing time: " << duration.count() << "\n";

    return visitor.getResultCode();
}
    

} /* namespace pmd */
