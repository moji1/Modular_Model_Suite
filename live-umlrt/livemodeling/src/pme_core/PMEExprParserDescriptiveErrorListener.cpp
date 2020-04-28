//
//  PMEExprParserDescriptiveErrorListener.cpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-10-29.
//

#include "PMEExprParserDescriptiveErrorListener.hpp"

namespace pmd {
    void PMEExprParserDescriptiveErrorListener::syntaxError(antlr4::Recognizer *recognizer, antlr4::Token * offendingSymbol, size_t line, size_t charPositionInLine, const std::string &msg, std::exception_ptr e) {
        std::stringstream sTemp;
        sTemp << "Parsing the command failed at character " << charPositionInLine << " of line " <<line <<std::endl << "Error detail: "<< msg;
        std::cout<<sTemp.str()<<std::endl;
        std::cout<<"Please use 'help' command to see supported commands"<<std::endl;
        //exit(-1);
    }
    //void PMEExprParserDescriptiveErrorListener::reportAmbiguity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex, bool exact,const antlrcpp::BitSet &ambigAlts, atn::ATNConfigSet *configs) {};
    //void PMEExprParserDescriptiveErrorListener::reportAttemptingFullContext(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,const antlrcpp::BitSet &conflictingAlts, atn::ATNConfigSet *configs) {};
    //void PMEExprParserDescriptiveErrorListener::reportContextSensitivity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,size_t prediction, atn::ATNConfigSet *configs) {};
    
}
