grammar Crux;

SemiColon: ';';

Integer
 : '0'
 | [1-9] [0-9]*
 ;

True: 'true';
False: 'false';


WhiteSpaces
 : [ \t\r\n]+ -> skip
 ;

Comment
 : '//' ~[\r\n]* -> skip
 ;

 AND
 : '&&'
 ;

 OR
 : '||'
 ;

 NOT
 : '!'
 ;

 IF
 : 'if'
 ;

 ELSE
  : 'else'
  ;

 FOR
  : 'for'
  ;

 BREAK
  : 'break'
  ;

 RETURN
  : 'return'
  ;

 OPEN_PAREN
 : '('
 ;

 CLOSE_PAREN
  : ')'
  ;

 OPEN_BRACE
  : '{'
  ;

 CLOSE_BRACE
  : '}'
  ;

 OPEN_BRACKET
  : '['
  ;

 CLOSE_BRACKET
  : ']'
  ;

 ADD
  : '+'
  ;

 SUB
  : '-'
  ;

 MUL
  : '*'
  ;

 DIV
  : '/'
  ;

 GREATER_EQUAL
  : '>='
  ;

 LESSER_EQUAL
  : '<='
  ;

NOT_EQUAL
 : '!='
 ;

 EQUAL
  : '=='
  ;

 GREATER_THAN
  : '>'
  ;

 LESS_THAN
  : '<'
  ;

 ASSIGN
  : '='
  ;

 COMMA
  : ','
  ;


literal
 : Integer
 | True
 | False
 ;

designator : Identifier  ('[' expr0 ']')*  ;
type
 : 'void'
 | 'bool'
 | 'int'
 | Identifier
 ;

op0 : '>=' | '<=' | '!=' | '==' | '>' | '<' ;
op1 : '+' | '-' | '||' ;
op2 : '*' | '/' | '&&' ;

expr0 : expr1 (op0 expr1)? ;
expr1 : expr2
       | expr1 op1 expr2 ;
expr2 : expr3
       | expr2 op2 expr3 ;
expr3 : '!' expr3
       | '(' expr0 ')'
       | designator
       | callExpr
       | literal ;
callExpr : Identifier '(' exprList ')' ;
exprList : (expr0 (',' expr0)*)? ;

param : type Identifier ;
paramList : (param (',' param)*)? ;

varDecl
 : type Identifier ';'
 ;
arrayDecl : type Identifier '[' Integer ']' ';' ;
functionDefn : type Identifier '(' paramList ')' stmtBlock ;
decl
 : varDecl
 | arrayDecl
 | functionDefn
 ;
declList
 : decl*
 ;

assignStmt : designator '=' expr0 ';' ;
callStmt : callExpr ';' ;
ifStmt : 'if' expr0 stmtBlock ('else' stmtBlock )? ;
loopStmt : 'loop' stmtBlock ;
breakStmt : 'break' ';' ;
continueStmt : 'continue' ';' ;
returnStmt : 'return' expr0 ';' ;
stmt : varDecl
      | callStmt
      | assignStmt
      | ifStmt
      | loopStmt
      | breakStmt
      | continueStmt
      | returnStmt ;
stmtList : (stmt*)? ;
stmtBlock : '{' stmtList '}' ;


program
 : declList EOF
 ;

 Identifier
  : [a-zA-Z] [a-zA-Z0-9_]*
  ;
