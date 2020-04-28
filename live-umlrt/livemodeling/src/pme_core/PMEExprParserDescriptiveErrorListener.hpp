//
//  PMEExprParserDescriptiveErrorListener.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-10-29.
//

#ifndef PMEExprParserDescriptiveErrorListener_hpp
#define PMEExprParserDescriptiveErrorListener_hpp

#include <stdio.h>
#include "antlr4-runtime.h"

namespace pmd {
    using namespace antlr4;
    class PMEExprParserDescriptiveErrorListener:public  antlr4::BaseErrorListener {
    public:
        //static const  PMEExprParserDescriptiveErrorListener INSTANCE;
        void syntaxError(antlr4::Recognizer *recognizer, antlr4::Token * offendingSymbol, size_t line, size_t charPositionInLine, const std::string &msg, std::exception_ptr e) override;
        //void reportAmbiguity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex, bool exact,const antlrcpp::BitSet &ambigAlts, atn::ATNConfigSet *configs) override;
        //void reportAttemptingFullContext(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,const antlrcpp::BitSet &conflictingAlts, atn::ATNConfigSet *configs) override;
        //void reportContextSensitivity(Parser *recognizer, const dfa::DFA &dfa, size_t startIndex, size_t stopIndex,size_t prediction, atn::ATNConfigSet *configs) override;
    };
}
#endif /* PMEExprParserDescriptiveErrorListener_hpp */
