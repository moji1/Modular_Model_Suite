#include <iostream>
#include "../pme_core/PMEExecRulesAnalyzer.hpp"
#include "../pme_core/PMDTypes.hpp"

int main(int argc, char** argv){
    //pmd::ExecRulesAnalyzer execRulesAnalyzer("test");
    std::string fileName;
    if (argc>1)
        fileName=string(argv[1]);
    pmd::PMEExecRulesAnalyzer execRulesAnalyzer(fileName,true);
    //pmd::ExecRulesAnalyzer execRulesAnalyzer("rule test applies (state *) when (NoTrigger)  begin test end \r\n");
    execRulesAnalyzer.parseRules();
    execRulesAnalyzer.printExecRules();
    pmd::ExecContextForRule  ctx;
    ctx.capsule="test";
    ctx.capsuleType="capsulegg";
    ctx.loc="s1";
    ctx.partialityPattern=pmd::NOTRIGER;
    ctx.scope=pmd::TRANSITION;
    pmd::ExecRule * t=execRulesAnalyzer.getApplicableRule(ctx);
    if (t)
        std::cout<<"applicable rule is: " << t->name <<std::endl;
        
	std::cout<<"Test is done\n";
	//main1();
	return 0;
}


