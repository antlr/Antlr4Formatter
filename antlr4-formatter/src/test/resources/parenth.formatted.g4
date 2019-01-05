grammar g;

multiplyingExpression
   : argument (('*' | '/' | 'mod' | 'and') argument)*
   ;
