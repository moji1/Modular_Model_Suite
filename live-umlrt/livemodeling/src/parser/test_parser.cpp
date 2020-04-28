#include <iostream>
#include "antlr4-runtime.h"
#include "PMEExprLexer.h"
#include "PMEExprParser.h"
#include "PMEExprBaseVisitor.h"
#include <boost/variant.hpp>
#include "../util/logging_config.hpp"
#include "../util/Heap.hpp"

//#include "boost/any.hpp"
//#include <boost/log/trivial.hpp>

//#define BOOST_LOG_DYN_LINK
//using namespace org::antlr::v4::runtime;
using namespace antlr4;
using namespace pmd;
using namespace logging::trivial;

     //int,double,bool,std::string,char,char *,const char *,WRONG_TYPE
/*    struct toAntlrAny : public boost::static_visitor<antlrcpp::Any>
    {
        antlrcpp::Any operator()(double d) const {return  d;}
        antlrcpp::Any operator()(char c) const { return c; }
        antlrcpp::Any operator()(std::string s) const { return s; }
        antlrcpp::Any operator()(char* s) const { return std::string(s); }
        antlrcpp::Any operator()(const char* s) const { return std::string(s); }
        antlrcpp::Any operator()(int s) const { return s; }
        antlrcpp::Any operator()(bool b) const { return b; }
        //antlrcpp::Any operator()(enum pmd::WRONG_TYPE s) const { return nullptr; }
    };

class PMEParserVisitor : public ExprBaseVisitor {
public:
    /// passing heap to visitor
    PMEParserVisitor(pmd::Heap heap){
        this->heap=heap;
    }
    antlrcpp::Any visitPrintExpr(ExprParser::PrintExprContext *ctx) override{
        std::cout<<"Printing expression\n";
        antlrcpp::Any a=visit(ctx->expr());
        //typeid(int);
        int i=0;
        if (a.is<int>()){
            i=static_cast<int>(a);
            std::cout<<i<<std::endl;
        }
        return 0;
    }
    
    antlrcpp::Any visitAssign(ExprParser::AssignContext *ctx) override {
        antlrcpp::Any rvalue=visit(ctx->expr());
        std::string lvalue=ctx->ID()->getText();
        if (rvalue.is<int>())
            heap.setVarValue(lvalue, static_cast<int>(rvalue));
        else if (rvalue.is<double>())
            heap.setVarValue(lvalue, static_cast<double>(rvalue));
        else if  (rvalue.is<std::string>())
            heap.setVarValue(lvalue, rvalue.as<std::string>());
        else if  (rvalue.is<char>())
            heap.setVarValue(lvalue, static_cast<char>(rvalue));
        else if (rvalue.is<bool>())
            heap.setVarValue(lvalue, static_cast<bool>(rvalue));
        else
            throw std::invalid_argument("Can't assign");
        return 0;
    }
    
    antlrcpp::Any visitBlank(ExprParser::BlankContext *ctx) override {
        return 0;
    }
    
    antlrcpp::Any visitParens(ExprParser::ParensContext *ctx) override {
        return visit(ctx->expr());
    }
    
    pmd::VarType antlrAnytoVarType(antlrcpp::Any a){
        if (a.is<int>())
            return static_cast<int>(a);
        else if ((a.is<double>()))
           return static_cast<double>(a);
        else if ((a.is<char>()))
            return static_cast<char>(a);
        else if ((a.is<bool>()))
            return static_cast<bool>(a);
        else if ((a.is<std::string>()))
            return a.as<std::string>();
        else
            return pmd::UNKOWN;
    }
    
    antlrcpp::Any visitMulDiv(ExprParser::MulDivContext *ctx) override {
        antlrcpp::Any left=visit(ctx->expr(0));
        antlrcpp::Any right=visit(ctx->expr(1));
        pmd::VarType leftV;
        pmd::VarType rightV;
        //pmd::VarType resultV;
        leftV=antlrAnytoVarType(left);
        rightV=antlrAnytoVarType(right);
        if (ctx->op->getType()==ExprParser::MUL)
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::mul{},leftV,rightV));
            //esultV=boost::apply_visitor(pmd::mul{},leftV,rightV);
        else
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::div{},leftV,rightV));
    }
    
    antlrcpp::Any visitAddSub(ExprParser::AddSubContext *ctx) override {
        antlrcpp::Any left=visit(ctx->expr(0));
        antlrcpp::Any right=visit(ctx->expr(1));
        pmd::VarType leftV;
        pmd::VarType rightV;
        //pmd::VarType resultV;
        leftV=antlrAnytoVarType(left);
        rightV=antlrAnytoVarType(right);
        if (ctx->op->getType()==ExprParser::ADD)
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::add{},leftV,rightV));
        //esultV=boost::apply_visitor(pmd::mul{},leftV,rightV);
        else
            return boost::apply_visitor(toAntlrAny{}, boost::apply_visitor(pmd::sub{},leftV,rightV));
    }
    
    antlrcpp::Any visitId(ExprParser::IdContext *ctx) override {
        std::string varName=ctx->ID()->getText();
        return boost::apply_visitor(toAntlrAny{},heap.getVarValue(varName));
    }
    
    antlrcpp::Any visitInt(ExprParser::IntContext *ctx) override {
        return std::stoi(static_cast<std::string>(ctx->INT()->getText()));
    }
private:
    pmd::Heap heap;
    
};
*/
class PMEExprVisitorImp : public PMEExprBaseVisitor {
     antlrcpp::Any visitViewSubcommands(PMEExprParser::ViewSubcommandsContext *ctx) override {
         std::cout<<ctx->subcmd->getText()<<std::endl;
         return 0;
        //return visitChildren(ctx);
    }
};
int main(int argc, const char* argv[]) {
    boost::variant< int, std::string > v;
    init_logging("log1",trace);
    logging::add_common_attributes();
    src::severity_logger< severity_level > lg;
    
    BOOST_LOG_SEV(lg, trace) << "A trace severity message";
    BOOST_LOG_SEV(lg, debug) << "A debug severity message";
    BOOST_LOG_SEV(lg, info) << "An informational severity message";
    BOOST_LOG_SEV(lg, warning) << "A warning severity message";
    BOOST_LOG_SEV(lg, error) << "An error severity message";
    BOOST_LOG_SEV(lg, fatal) << "A fatal severity message1";
    std::ifstream stream;
    std::stringstream stringTemp;
    //BOOST_LOG_TRIVIAL(info) << "A fatal severity message";
    stringTemp<<"view context";
    //stream.open(argv[1]);
   //std::cout<<argv[1]<<std::endl;
    ANTLRInputStream input(stringTemp);
    PMEExprLexer lexer(&input);
    std::cout<<lexer.getTokenNames().at(1)<<std::endl;
    CommonTokenStream tokens(&lexer);
    std::cout<<tokens.getText()<<std::endl;
    PMEExprParser parser(&tokens);

    tree::ParseTree *tree = parser.dbgCommands();
    std::cout<<tree->toStringTree()<<std::endl;
    std::cout<<parser.getTokenNames().size()<<std::endl;
    pmd::Heap heap;
    //PMEParserVisitor visitor(heap);
    PMEExprVisitorImp visitor;
    visitor.visit(tree);
    //tree::ParseTreeWalker::DEFAULT.walk(&listener, tree);
    //tree::ParseTreeWalker::DEFAULT->walk(&listener, tree);
    std::cout<<"Test"<<std::endl;
    return 0;
}

