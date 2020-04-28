//
//  ExecRuleTypes.hpp
//  pmecore
//
//  Created by Mojtaba Bagherzadeh on 2018-09-14.
//

#ifndef ExecRuleTypes_hpp
#define ExecRuleTypes_hpp

#include <iostream>
#include<functional>
#include <sstream>

namespace pmd {
    typedef enum {CAPSULE, TRANSITION, STATE, SYSTEM} EXECRuleScope;
    typedef  struct ExecRule {
        std::string name;
        std::string capsule;
        std::string capsuleType;
        std::string loc;
        std::string when;
        std::string body;
        EXECRuleScope scope;
    } ExecRule;
    //bool operator==(const pmd::ExecRule &lhs, const pmd::ExecRule &rhs);
    inline bool operator==(const pmd::ExecRule &lhs, const pmd::ExecRule &rhs)
    {
        return lhs.loc == rhs.loc  && lhs.scope == rhs.scope && lhs.capsule == rhs.capsule && lhs.capsuleType == rhs.capsuleType;
    }

}
namespace std{
    template <>
    struct hash<pmd::ExecRule>
    {
        //std::size_t operator()(const pmd::ExecRule& k) const;
        inline  std::size_t operator()(const pmd::ExecRule& r) const
        {
            using std::hash;
            std::stringstream tempSS;
            tempSS << r.capsule <<r.capsuleType << r.loc << r.scope;
            std::size_t hashV=hash<string>()(tempSS.str());
            //std::cout<<"hash value of the rule  with capsule: "<< tempSS.str()<<": "<<hashV <<std::endl;
            return hashV;
            /*return ((hash<string>()(k.loc)
                     ^ (hash<string>()(k.capsule) << 1)) >> 1)
            ^ (hash<int>()(k.scope) << 1);*/
        }
    };
}

#endif /* ExecRuleTypes_hpp */
