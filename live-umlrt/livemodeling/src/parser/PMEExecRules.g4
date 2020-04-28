grammar PMEExecRules;

import PMEExpr;

rules: execRule*;
execRule: 'rule' ruleName=ID 'where' where when?
 body=Body  
;

when: 'when' guard;
guard: guardExp=('NoTrigger' |'NoActionCode' | 'NoInitialState'|'DeadState'|'IsolatedState'|'MissingGuard'
	|'UnHandlesInput'|'MissingOutput'|'FullPartiality'|'EnvironmentPartiality'|'NoPartiality' 
) ;


where: stateLoc|capsuleLoc;
capsuleLoc: 'component'    (captype=(ID|WILDCARDID) '::')?  capName=(ID|WILDCARDID) ; 

transLoc:   'transition' ( (captype=(ID|WILDCARDID) '::')? capName=(ID|WILDCARDID) '::') ? transName=(ID|WILDCARDID);

stateLoc:   'state'      ( (captype=(ID|WILDCARDID) '::')? capName=(ID|WILDCARDID) '::') ? stateName=(ID|WILDCARDID);

Body : '{' .+? '}';
ID : [_a-zA-Z]+[0-9a-zA-Z]* ;
WILDCARDID : [*_a-zA-Z]+[*_0-9a-zA-Z]* ;