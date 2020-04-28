//
//  DescriptiveErrorListener.cpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#include "RuleParserDescriptiveErrorListener.hpp"
namespace pmd {
    void RuleParserDescriptiveErrorListener::syntaxError(antlr4::Recognizer *recognizer, antlr4::Token * offendingSymbol, size_t line, size_t charPositionInLine, const std::string &msg, std::exception_ptr e) {
        std::stringstream sTemp;
        sTemp << "Parsing error at character " << charPositionInLine << " of line " <<line <<std::endl << "Error Message: "<< msg;
        std::cout<<sTemp.str();
        exit(-1);
    }
}
