//
//  PMDTypes.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-10-23.
//
#include <boost/any.hpp>

#ifndef PMDTypes_h
#define PMDTypes_h
namespace pmd{
// values for controlling format
    
class ExecHelper;

typedef enum {SYNTAXERROR,LOGICALERROR,DEVIDEBYSERO,UNKOWNERROR,NOERROR,GONEXT,CONTINUETONEXTBREAK} RESULTCODE;
typedef std::map<std::string,boost::any> AnyTypePayload;  // generic datatype to save message payloads
enum ExecMode{INTERACTIVE,SCRIPT,RANDOM,INVALID}; // used to control how to read userd' decesion
    struct ExecutionStep {  // save the option to be selected by users
        std::string transName;
        std::string targetState;
        std::string sourceState;
        std::string signal;
} ;// the first string refers to transtion name, the second refers the target state of the transition

typedef std::map<std::string,std::map<std::string,std::map<std::string,std::string>>> MessagesMap;
typedef std::map<std::string,std::string> EffectMap;
typedef std::map<std::string,std::map<std::string,std::string>> MessageParams;
enum PartialityPattern{NOTRIGER,NOACTIONCODE,NOINITIALSTATE,DEADSTATE,
        ISOLATEDSTATE,MISSINGGUARD,UNHANDLEDINPUT,
        MISSINGOUTPUT,FULLPARTIALITY,ENVIRONMENTPARTIALITY,BREAKPOINT,NOPARTIALITY};

inline std::string ToString(PartialityPattern v){
        std::string tempS;
        switch (v){
            case NOTRIGER:       return "NoTrigger";
            case NOACTIONCODE:   return "NoActionCode";
            case NOINITIALSTATE: return "NoInitialState";
            case DEADSTATE :     return "DeadState";
            case ISOLATEDSTATE:   return "IsolatedState";
            case MISSINGGUARD:  return "MissingGuard";
            case UNHANDLEDINPUT:  return "UnHandlesInput";
            case MISSINGOUTPUT:  return "MissingOutput";
            case FULLPARTIALITY:  return "FullPartiality";
            case ENVIRONMENTPARTIALITY:  return "EnvironmentPartiality";
            case BREAKPOINT:       return "Breakpoint";
            case NOPARTIALITY:  return "NoPartiality";
            default:      return "[UnknownPartiality]";
        }
}
    
//
inline PartialityPattern ToPartialityPattern(std::string PartialityPatternsText ){
        std::string tempS;
        if (PartialityPatternsText=="NoTrigger")
            return NOTRIGER;
        else if (PartialityPatternsText=="NoActionCode")
            return NOACTIONCODE;
        else if (PartialityPatternsText=="NoInitialState")
            return NOINITIALSTATE;
        else if (PartialityPatternsText=="DeadState")
            return DEADSTATE ;
        else if (PartialityPatternsText=="IsolatedState")
            return ISOLATEDSTATE;
        else if (PartialityPatternsText=="MissingGuard")
            return MISSINGGUARD;
        else if (PartialityPatternsText=="UnHandlesInput")
            return UNHANDLEDINPUT;
        else if (PartialityPatternsText=="MissingOutput")
            return MISSINGOUTPUT;
        else if (PartialityPatternsText=="FullPartiality")
            return FULLPARTIALITY;
        else if (PartialityPatternsText=="EnvironmentPartiality")
            return ENVIRONMENTPARTIALITY;
        else if (PartialityPatternsText=="Breakpoint")
            return BREAKPOINT;
        else
            return NOPARTIALITY;
    }
//
struct Message {  // data type to save a message
    std::string portName;
    std::string signal;
    int index;
    //map<string,string> payload;
    AnyTypePayload payload;
} ;// the first string refers to transtion name, the second refers the target state of the transition

typedef Message Message;
typedef ExecutionStep ExecutionStep;
typedef enum ExecLocationType{STATELOC,TRANSITIONLOC} ExecLocationType;
    
typedef struct ExecLocation{  // current state or transtion euing execution
    ExecLocationType type;
    std::string locName;
}ExecLocation;

//
struct ExecContextForRule{
        std::string capsule;
        std::string loc;
        std::string capsuleType;
        EXECRuleScope scope;
        PartialityPattern partialityPattern;
        Heap * heap;
        std::string msgId;
};
    
} //end of namespace
#endif /* PMDTypes_h */
