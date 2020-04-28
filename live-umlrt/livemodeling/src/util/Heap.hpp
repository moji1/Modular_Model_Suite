/*
 * Heap.h
 *
 *  Created on: Sep 1, 2018
 *      Author: mojtababagherzadeh
 */
#include <boost/variant.hpp>
#include<string>
#include<iostream>
#include "../pme_core/PMDConfig.hpp"
#include <iomanip>
#ifndef SRC_UTIL_HEAP_H_
#define SRC_UTIL_HEAP_H_

namespace pmd {
	enum WRONG_VALUE {UNKOWN};
    //typedef  boost::variant<int,double,bool,std::string,char,char *,const char *> VarType;
    typedef  boost::variant<int,double,bool,std::string,char,WRONG_VALUE> VarType;
    enum SUPPORTED_TYPES {INT,DOUBLE,BOOL,STRING,CHAR,CSTRING,CONSTCSTRING,WRONG_TYPE};
    enum VarScope  {CAPSULEVAR,RULEVAR,SCRIPTVAR,MESSAGEVAR};
    //typedef std::map<std::string,SUPPORTED_TYPES> VarTable;
   /* typedef struct {
        VarType value;
        VarScope scope;
    } VarValue;*/
    //typedef std::map<std::string,VarType> ValueTable;
    typedef std::map<std::string,VarType> ValueTable;
    struct checkType : public boost::static_visitor<SUPPORTED_TYPES>
    {
        SUPPORTED_TYPES operator()(double d) const {return  DOUBLE;}
        SUPPORTED_TYPES operator()(char c) const { return CHAR; }
        SUPPORTED_TYPES operator()(char* c) const { return CSTRING; }
        SUPPORTED_TYPES operator()(const char* c) const { return CONSTCSTRING; }
        SUPPORTED_TYPES operator()(std::string s) const { return STRING; }
        SUPPORTED_TYPES operator()(int s) const { return INT; }
        SUPPORTED_TYPES operator()(bool s) const { return BOOL; }
        SUPPORTED_TYPES operator()(enum WRONG_VALUE s) const { return WRONG_TYPE; }
    };
    struct getTypeStr : public boost::static_visitor<std::string>
    {
        std::string operator()(double d) const {return  "double";}
        std::string operator()(char c) const { return "char"; }
        std::string operator()(char* c) const { return "string"; }
        std::string operator()(const char* c) const { return "const string"; }
        std::string operator()(std::string s) const { return "std::string"; }
        std::string operator()(int s) const { return "int"; }
        std::string operator()(bool s) const { return "boolean"; }
        std::string operator()(enum WRONG_VALUE s) const { return "unsupported_type"; }
    };
    // the following function (add/sub/mul/div) written based on the code copied from https://stackoverflow.com/questions/28349166/how-to-implement-arithmetic-operator-for-boostvariant-so-that-it-supports-diff
    struct mul : public boost::static_visitor<VarType>
    {
        template<typename T, typename U>
        auto operator()(const T& a, const U& b) const
        -> decltype(VarType(a*b)){
            return a*b;
        }
        template<typename... Ts>
        VarType operator()(const Ts... params) const {
            //throw std::invalid_argument("can't multiply");
            std::cout<<"Can't multiply"<<std::endl;
        }
    };
    struct div : public boost::static_visitor<VarType>
    {
        template<typename T, typename U>
        auto operator()(const T& a, const U& b) const
        -> decltype(VarType(a/b)){
            return a/b;
        }
        template<typename... Ts>
        VarType operator()(const Ts... params) const {
            //throw std::invalid_argument("can't divide");
            std::cout<<"Can't divide"<<std::endl;
        }
    };
    struct add : public boost::static_visitor<VarType>
    {
        template<typename T, typename U>
        auto operator()(const T& a, const U& b) const
        -> decltype(VarType(a+b)){
            return a+b;
        }
        template<typename... Ts>
        VarType operator()(const Ts... params) const {
            //throw std::invalid_argument("can't add");
            std::cout<<"Can't add"<<std::endl;
        }
    };
    
    struct sub : public boost::static_visitor<VarType>
    {
        template<typename T, typename U>
        auto operator()(const T& a, const U& b) const
        -> decltype(VarType(a-b)){
            return a-b;
        }
        template<typename... Ts>
        VarType operator()(const Ts... params) const {
            //throw std::invalid_argument("can't sub");
            std::cout<<"Can't subtract"<<std::endl;
        }
    };
    

    
    struct stringValue : public boost::static_visitor<std::string>
    {
        std::string operator()(double d) const {return  std::to_string(d);}
        std::string operator()(char c) const { return std::string(1,c); }
        std::string operator()(std::string s) const { return s; }
        std::string operator()(char* s) const { return std::string(s); }
        std::string operator()(const char* s) const { return std::string(s); }
        std::string operator()(int i) const { return std::to_string(i); }
        std::string operator()(bool b) const { return std::to_string(b); }
        //std::string operator()(enum WRONG_TYPE w) const { return "WRONG_TYPE"; }
    };
    
class Heap {
public:
	Heap();
    virtual ~Heap();
    //void defineVar(std::string name,SUPPORTED_TYPES type);
    void  setVarValue(std::string name, VarType value);
    void  loadVarFromHeap(std::string name,int * var);
    void  loadVarFromHeap(std::string name,std::string * var);
    void  loadVarFromHeap(std::string name,double * var);
    void  loadVarFromHeap(std::string name,bool * var);
    void  loadVarFromHeap(std::string name,char * var);
    bool isVarExist(std::string name);
    void removeVar(std::string name);
    SUPPORTED_TYPES getVarType(std::string name);
    VarType getVarValue(std::string name);
    int get(int i){return 1;}
    char get(char c){return 2;}
    void clearHeap();
    void showVar();
    void showVar(std::string name);
    
private:
    //VarTable varsTable;
    ValueTable varsValue;
    
    
};

} /* namespace pmd */

#endif /* SRC_UTIL_HEAP_H_ */
