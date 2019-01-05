grammar Hello;

r
   : 'hello' ID; // match keyword hello followed by an xxxxxx

fragment EscapeSequence
   : '\\' [btnfr"'\\] | OctalEscape | UnicodeEscape // This is not in the spec but prevents having to preprocess the input;

ID
   : [a-z]+; // match lower-case identifiers