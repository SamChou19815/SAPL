/**
 * {@code PLLexer} is the lexer of the PL.
 * No parser rules should ever appear in this file.
 */
lexer grammar PLLexerPart;

/*
 * ----------------------------------------------------------------------------
 * PART 1: Keywords
 * ----------------------------------------------------------------------------
 */

IMPORT : 'import';

CLASS : 'class';
OF : 'of';
VAL : 'val';
FUN : 'fun';

PRIVATE : 'private';

IF : 'if';
THEN : 'then';
ELSE : 'else';
MATCH : 'match';
WITH : 'with';

THROW : 'throw';
TRY : 'try';
CATCH : 'catch';

WILDCARD : '_';

// Forbidden Identifiers
PROGRAM : 'Program';
MAIN : 'Main';
FUNCTION : 'function';
COPY : 'copy';

/*
 * ----------------------------------------------------------------------------
 * PART 2: Parentheses
 * ----------------------------------------------------------------------------
 */

LPAREN : '(';
RPAREN : ')';

LBRACE : '{';
RBRACE : '}';

LBRACKET : '[';
RBRACKET : ']';

// SEPARATORS

SEMICOLON : ';';
COLON : ':';
COMMA : ',';
DOT : '.';

ARROW : '->';

/*
 * ----------------------------------------------------------------------------
 * PART 3: Operators
 * ----------------------------------------------------------------------------
 */

ASSIGN : '=';

NOT : '!';

SHL : 'shl';
SHR : 'shr';
USHR : 'ushr';
XOR : 'xor';
LAND : '&';
LOR : '|';

MUL : '*';
DIV : '/';
MOD : '%';
F_MUL : '.';
F_DIV : '/.';

PLUS : '+';
MINUS : '-';
F_PLUS : '+.';
F_MINUS : '-.';
STR_CONCAT : '^';

STRUCT_EQ : '==';
LT : '<';
LE : '<=';
GT : '>';
GE : '>=';
STRUCT_NE : '!=';

AND : '&&';
OR : '||';

/*
 * ----------------------------------------------------------------------------
 * PART 4: Literals
 * ----------------------------------------------------------------------------
 */

UNIT : '()';

IntegerLiteral : HexLiteral | OctalLiteral | DecimalLiteral;

BooleanLiteral : 'true' | 'false';

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    |   ('0x' | '0X') (HexDigit )*
        ('.' (HexDigit)*)?
        ( 'p' | 'P' )
        ( '+' | '-' )?
        ( '0' .. '9' )+
        FloatTypeSuffix?
    ;

fragment Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral : '\'' ( EscapeSequence | ~('\''|'\\') ) '\'';

StringLiteral : '"' ( EscapeSequence | ~('\\'|'"') )* '"';

fragment EscapeSequence
    : '\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\')
    | UnicodeEscape
    | OctalEscape
    ;

fragment OctalEscape
    : '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    | '\\' ('0'..'7') ('0'..'7')
    | '\\' ('0'..'7')
    ;

fragment UnicodeEscape : '\\' 'u' HexDigit HexDigit HexDigit HexDigit;

LowerIdentifier : LowerLetter (Letter | Digit)*;

UpperIdentifier : UpperLetter (Letter | Digit)*;

fragment Letter : LowerLetter | UpperLetter;

fragment LowerLetter : 'a'..'z';

fragment UpperLetter : 'A'..'Z';

fragment Digit : NonZeroDigit | ZeroDigit;

fragment NonZeroDigit : '1'..'9';

fragment ZeroDigit : '0';

/*
 * ----------------------------------------------------------------------------
 * PART 5: Comments
 * ----------------------------------------------------------------------------
 */

COMMENT : '/*' .*? '*/' -> channel(HIDDEN); // match anything between /* and */
WS : [ \r\t\u000C\n]+ -> channel(HIDDEN); // white space
LINE_COMMENT : '//' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN);


