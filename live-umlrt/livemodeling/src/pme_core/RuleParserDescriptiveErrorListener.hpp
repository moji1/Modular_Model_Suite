//
//  DescriptiveErrorListener.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#ifndef RuleParserDescriptiveErrorListener_hpp
#define RuleParserDescriptiveErrorListener_hpp

#include <iostream>
#include "antlr4-runtime.h"
#include "PMEExecRulesLexer.h"
#include "PMEExecRulesParser.h"

namespace pmd {
    
    class RuleParserDescriptiveErrorListener:public  antlr4::BaseErrorListener {
    public:
        void syntaxError(antlr4::Recognizer *recognizer, antlr4::Token * offendingSymbol, size_t line, size_t charPositionInLine, const std::string &msg, std::exception_ptr e) override;
    };
}
#endif /* DescriptiveErrorListener_hpp */
