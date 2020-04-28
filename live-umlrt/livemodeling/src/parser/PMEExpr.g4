/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Camilo Sanchez (Camiloasc1)
 * Copyright (c) 2018 Mojtaba Bagherzadeh (Camiloasc1)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

grammar PMEExpr;
script: statments+;

statments: (scriptStatment|interactiveStatment) ;
scriptStatment: simpleStatment|complexStatment ;
complexStatment: 'for while';


interactiveStatment: (dbgCommands|umlrtCmd);

umlrtCmd: sendMsgCmd | replyMsgCmd | receiptMsgCmd | replayMsgCmd | informInCmd;
replyMsgCmd: 'reply' 'with' (random | (signalName=ID  ('(' payload=params ')')?)) ;

sendMsgCmd: 'send' signalName=ID 'on' portName=ID  ('index' index=Integerliteral)?  ('with' '(' payload=params ')')?;
params: ((pair  (',' pair)*) | random);
pair: name=ID ':' val=value;
random: 'random';

receiptMsgCmd: 'receipt' signalName=ID 'on' portName=ID ('index' index=Integerliteral)?;
replayMsgCmd: 'replay';

value: ID| literal | random;


dbgCommands: viewCmd  | selectCmd| simpleStatment | visited | controlCMD | editStateCmd | addTransitionCmd | deleteTransitionCmd | editEffectCmd | breakCmd | deleteBreakpointCmd | help;

help:'help' ;

controlCMD: cmd=('next'|'exit'|'continue') ;

editStateCmd: cmd=('add' | 'delete') 'state' stateName=(ID|SID);

addTransitionCmd: 'add' 'transition' (transName=ID)? from=(ID|SID) '->' to=(ID|SID) 'when' signalName=ID 'on' portName=ID;
deleteTransitionCmd: 'delete' 'transition' ((transName=ID) | (from=(ID|SID) '->' to=(ID|SID)));

informInCmd: 'inform' 'in' sec=literal 's' ('and' ns=literal 'ns')? 'on' portName=ID;

editEffectCmd: cmd=('edit' | 'delete') 'effect' transName=ID ;

breakCmd: 'break' 'on' transName=ID ;
deleteBreakpointCmd: 'delete' 'breakpoint' 'on' transName=ID ;

visited: 'visitedState' '(' sateName=ID ')' |   'visitedTransition' '(' transName=ID ')' ;

selectCmd: 'select' possiblePaths ;
possiblePaths: (randomPath=random | optionId=Integerliteral | 'transition' transName=ID | 'state' stateName=ID ('using' transName=ID |random)?) ;

viewCmd: ('view'|'show') subcommands=viewSubcommands;
viewSubcommands: 		subcmd='context' | 
						subcmd='var' ('-name' name=ID)?  |
						subcmd='trace' ('-n' cnt=Integerliteral)?|
						subcmd='connections'  |
						subcmd='options' |
						subcmd='inputs' |
						subcmd='outputs'|
						subcmd='message' |
						 'decisions' ('-n' cnt=Integerliteral)? ;


simpleStatment: 
	expr # printExpr |
	(ID|QID) '=' expr # assign
;


expr: expr op=('*'|'/') expr # MulDiv
| expr op=('+'|'-') expr # AddSub
| literal #literalinExpr
| ID # id 
| QID #qualifiedID
| '(' expr ')' # parens
;

// assigns token name to '*' used above in grammar
MUL : '*' ; 
DIV : '/' ;
ADD : '+' ;
SUB : '-' ;

// common rules
//ID : [a-zA-Z]+ ; // match identifiers
ID: Identifier ;
QID: Identifier ('.'Identifier)*;
SID: Identifier ('::'Identifier)+;
//INT : [0-9]+ ; // match integers
//NEWLINE:'\r'? '\n' { skip(); };
//NEWLINE:'\r'? '\n' ; // return newlines to parser (end-statement signal)
//WS : ' ' \t+ -> skip ; // toss out whitespace



Identifier
:
/*
	Identifiernondigit
	| Identifier Identifiernondigit
	| Identifier DIGIT
	*/
	Identifiernondigit
	(
		Identifiernondigit
		| DIGIT
	)*
;

fragment
Identifiernondigit
:
	NONDIGIT
	/* other implementation defined characters*/
;

fragment
NONDIGIT
:
	[a-zA-Z_]
;

fragment
DIGIT
:
	[0-9]
;

literal 
:
	Integerliteral     
	| Characterliteral 
	| Floatingliteral   
	| Stringliteral   
	| booleanliteral   
  
;

Integerliteral
:
  '0'
  | (SIGN? NONZERODIGIT
	(
		 DIGIT
	)*)
;

fragment
NONZERODIGIT
:
	[1-9]
;

Characterliteral
:
	'\'' (~['\\\r\n]
	| Escapesequence | .) '\''
;


fragment
Escapesequence
:
	Simpleescapesequence
;
fragment
Simpleescapesequence
:
	'\\\''
	| '\\"'
	| '\\?'
	| '\\\\'
	| '\\a'
	| '\\b'
	| '\\f'
	| '\\n'
	| '\\r'
	| '\\t'
	| '\\v'
;

Floatingliteral
:
	Fractionalconstant Exponentpart? Floatingsuffix?
	| Digitsequence Exponentpart Floatingsuffix?
;

fragment
Fractionalconstant
:
	SIGN? Digitsequence? '.' Digitsequence
	| SIGN? Digitsequence '.'
;

fragment
Exponentpart
:
	'e' SIGN? Digitsequence
	| 'E' SIGN? Digitsequence
;

fragment
SIGN
:
	[+-]
;

fragment
Digitsequence
:
	DIGIT
	(
		'\''? DIGIT
	)*
;

fragment
Floatingsuffix
:
	[flFL]
;

Stringliteral
:
	 Rawstring
;




fragment
Rawstring /* '"' dcharsequence? '(' rcharsequence? ')' dcharsequence? '"' */
:
	//'"' .*? '(' .*? ')' .*? '"'
	'"' .*? '"'
;
booleanliteral
:
	'false'
	| 'true'
;
pointerliteral
:
	'Nullptr'
;

Whitespace
:
	[ \t]+ -> skip
;
Newline
:
	(
		'\r' '\n'?
		| '\n'
	) -> skip
;
BlockComment
:
	'/*' .*? '*/' -> skip
;
LineComment
:
	'//' ~[\r\n]* -> skip
;
Brackets
:
	(
		'{' 
		| '}'
	) -> skip
;

