// Define a grammar called Hello

grammar Hello;

// match keyword hello followed by an identifier
r
   : 'hello' ID
   ;

// match lower-case identifiers

ID
   : [a-z] +
   ;

// skip spaces, tabs, newlines

WS
   : [ \t\r\n] + -> skip
   ;
