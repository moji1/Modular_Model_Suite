/*
 * RuleBodyExecutor.hpp
 *
 *  Created on: Sep 8, 2018
 *      Author: mojtababagherzadeh
 */

#ifndef SRC_PARSER_RULEBODYEXECUTOR_HPP_
#define SRC_PARSER_RULEBODYEXECUTOR_HPP_
#include "ExecContext.hpp"
#include "PMEExprVisitorImp.hpp"
#include "antlr4-runtime.h"
#include "PMEExprLexer.h"
#include "PMEExprParser.h"
#include "PMEExprBaseVisitor.h"
#include "PMEExprParserDescriptiveErrorListener.hpp"

namespace pmd {

class PMEScriptExecutor {
public:
	PMEScriptExecutor();
    int ExecuteDBGCommand(ExecContextOfCapsule * execContext, std::string cmdLine);
    int ExecuteScript(ExecContextOfCapsule * execContext, std::string cmdLine);
	virtual ~PMEScriptExecutor();
};

} /* namespace pmd */

#endif /* SRC_PARSER_RULEBODYEXECUTOR_HPP_ */
