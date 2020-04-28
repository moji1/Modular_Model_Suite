/*
 * Heap.cpp
 *
 *  Created on: Sep 1, 2018
 *      Author: mojtababagherzadeh
 */

#include "Heap.hpp"

namespace pmd {

    Heap::Heap() {
	// TODO Auto-generated constructor stub

    }

    Heap::~Heap() {
	// TODO Auto-generated destructor stub
        this->varsValue.clear();
    }
    
    bool Heap::isVarExist(std::string name) { 
        ValueTable::iterator it;
        it = varsValue.find(name);
        if (it != varsValue.end())
            return true;
        else
            return false;
    }
    
    
    void Heap::setVarValue(std::string name, VarType value) {
        VarType varType,valueType,tempVarType;
        varType=getVarType(name);
        valueType=boost::apply_visitor(checkType{}, value);
        if (isVarExist(name)){
            if (varType==valueType)
                varsValue[name]=value;
            else if (varType==VarType{STRING})
                varsValue[name]=boost::apply_visitor(stringValue{}, value);
            else if (varType==VarType{STRING})
                varsValue[name]=boost::apply_visitor(stringValue{}, value);
            else if (varType==VarType{DOUBLE} and valueType==VarType{INT})
                varsValue[name]=(double)boost::get<int>(value);
            else if (varType==VarType{INT} and valueType==VarType{BOOL})
                varsValue[name]=(int)boost::get<bool>(value);
            else if (varType==VarType{INT} and valueType==VarType{CHAR})
                varsValue[name]=(int)boost::get<char>(value);
            else if (varType==VarType{CHAR} and valueType==VarType{BOOL})
                varsValue[name]=(char)boost::get<bool>(value);
            else {// wrong assignment // raise an exception{
                std::cout<<"Wrong assignment"<<std::endl;
            }
        //else write a swicth for autocasting and raise exception if no casting is possible
        }
        else
            varsValue[name]=value;
    }
    

    
    VarType Heap::getVarValue(std::string name) {
        ValueTable::iterator it;
        it=varsValue.find(name);
        if (it!=varsValue.end())
            return it->second;
        else
            return UNKOWN;
    }
    
    void Heap::removeVar(std::string name) { 
        ValueTable::iterator it1;
        it1 = varsValue.find(name);
        if (it1 != varsValue.end())
            varsValue.erase(it1);

    }
    
    SUPPORTED_TYPES Heap::getVarType(std::string name) {
        ValueTable::iterator it;
        it = varsValue.find(name);
        if (it!=varsValue.end()){
            return boost::apply_visitor(checkType{}, it->second);
        }
        else{
            return WRONG_TYPE;
        }
            
    }
    void Heap::loadVarFromHeap(std::string name, int * var) {
        if (getVarType(name)==INT)
            *var=boost::get<int>(getVarValue(name));
    }
    void Heap::loadVarFromHeap(std::string name, std::string * var) {
        if (getVarType(name)==STRING)
            *var=boost::get<std::string>(getVarValue(name));
    }
    void Heap::loadVarFromHeap(std::string name, char * var) {
        if (getVarType(name)==CHAR)
            *var=boost::get<char>(getVarValue(name));
    }
    void Heap::loadVarFromHeap(std::string name, bool * var) {
        if (getVarType(name)==BOOL)
            *var=boost::get<bool>(getVarValue(name));
    }
    void Heap::loadVarFromHeap(std::string name, double * var) {
        if (getVarType(name)==DOUBLE)
            *var=boost::get<double>(getVarValue(name));
    }
    
    
    void Heap::clearHeap(){
        varsValue.clear();
    }
    
    void Heap::showVar() { 
        int numFields=3;
        int totalWidth=nameWidth*2+sep.size()*numFields+strWidth;
        std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
        std::cout<<std::endl<<line<<std::endl<<sep
        <<std::setw(nameWidth)<<"Name"<< sep<<std::setw(nameWidth)<<"Type" << sep<<std::setw(strWidth)<<"Value" <<sep <<std::endl;
        std::cout<<line<<std::endl;
        for (ValueTable::iterator it1=this->varsValue.begin();it1!=varsValue.end();it1++){
            std::string varName=it1->first;
            std::string varTypeStr=boost::apply_visitor(getTypeStr(), it1->second);
            std::string varValueStr=boost::apply_visitor(stringValue(), it1->second);
            std::cout<<sep<<std::setw(nameWidth)<<varName<< sep<<std::setw(nameWidth)<<varTypeStr << sep<<std::setw(strWidth)<<varValueStr<<sep <<std::endl;
        }
        std::cout<<line<<std::endl;
    }
    
    void Heap::showVar(std::string varName) {
        VarType varValue=this->getVarValue(varName);
        if (boost::apply_visitor(checkType(),varValue)!=WRONG_TYPE){
            int numFields=3;
            int totalWidth=nameWidth*2+sep.size()*numFields+strWidth;
            std::string line = sep + std::string( totalWidth-1, '-' ) + '|' ;
            std::cout<<std::endl<<line<<std::endl<<sep
            <<std::setw(nameWidth)<<"Name"<< sep<<std::setw(nameWidth)<<"Type" << sep<<std::setw(strWidth)<<"Value" <<sep <<std::endl;
            std::cout<<line<<std::endl;
            std::string varTypeStr=boost::apply_visitor(getTypeStr(), varValue);
            std::string varValueStr=boost::apply_visitor(stringValue(),varValue);
            std::cout<<sep<<std::setw(nameWidth)<<varName<< sep<<std::setw(nameWidth)<<varTypeStr << sep<<std::setw(strWidth)<<varValue<<sep <<std::endl;
            std::cout<<line<<std::endl;
        } else
            std::cout<<" Variable \"" << varName << "\" is not defined " <<std::endl;
    
    
    }
    
    
    
    
    

    } /* namespace pmd */
    
    
    
