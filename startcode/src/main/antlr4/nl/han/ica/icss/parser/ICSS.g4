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
stylesheet: (stylerule | variableassignment)+;

stylerule: tagselector OPEN_BRACE (declaration | variableassignment | ifstatement)+ CLOSE_BRACE; //ASSUMPTION: at least one declaration or variableassignment

variableassignment: CAPITAL_IDENT ASSIGNMENT_OPERATOR value SEMICOLON;

declaration: property COLON value SEMICOLON;

property: LOWER_IDENT; //ASSUMPTION: only lowercase identifiers

tagselector: LOWER_IDENT | ID_IDENT | CLASS_IDENT;

value: expression | COLOR | PIXELSIZE | PERCENTAGE | TRUE | FALSE | CAPITAL_IDENT;

expression:
    expression MUL expression |
    expression (PLUS | MIN) expression |
    SCALAR |
    PIXELSIZE |
    PERCENTAGE |
    CAPITAL_IDENT;

ifstatement: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE (declaration | variableassignment | ifstatement)+ CLOSE_BRACE (ELSE OPEN_BRACE (declaration | variableassignment)+ CLOSE_BRACE)?;