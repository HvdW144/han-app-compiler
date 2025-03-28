grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: (stylerule | variableAssignment)+;

stylerule: selector OPEN_BRACE (declaration | variableAssignment | ifClause)+ CLOSE_BRACE; //ASSUMPTION: at least one declaration or variableAssignment

//--------------Variables--------------
variableAssignment: variableReference ASSIGNMENT_OPERATOR (literal | expression) SEMICOLON;
variableReference: CAPITAL_IDENT;

//--------------Declarations--------------
declaration: propertyName COLON (literal | expression) SEMICOLON;

propertyName: LOWER_IDENT; //ASSUMPTION: only lowercase identifiers

//--------------Selectors--------------
selector: tagSelector | classSelector | idSelector;

tagSelector: LOWER_IDENT;
classSelector: CLASS_IDENT;
idSelector: ID_IDENT;

//--------------Expressions--------------
expression:
    expression MUL expression |
    expression (PLUS | MIN) expression |
    scalarLiteral |
    pixelLiteral |
    percentageLiteral |
    variableReference |
    boolLiteral;

//--------------IF support--------------
ifClause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE
                OPEN_BRACE (declaration | variableAssignment | ifClause)+ CLOSE_BRACE
             elseClause?;
elseClause: ELSE OPEN_BRACE (declaration | variableAssignment)+ CLOSE_BRACE;

//--------------Literals--------------
literal: boolLiteral | pixelLiteral | percentageLiteral | scalarLiteral | colorLiteral | variableReference;

boolLiteral: TRUE  | FALSE;
pixelLiteral: PIXELSIZE;
percentageLiteral: PERCENTAGE;
scalarLiteral: SCALAR;
colorLiteral: COLOR;


