grammar InlineComments
;

root
   : NUMBER
   ; // match keyword hello followed by an identifier

NUMBER
   : DIGITS // do not erase me
   ; // match lower-case identifiers

WS
   : [ \t\r\n]+ -> skip
   ; // skip spaces, tabs, newlines

fragment DIGITS
   : '1' .. '9' '0' .. '9'* // do not erase me
   ;


