// Define a grammar called Hello

grammar Hello;

r
   : 'hello' ID
   ;

// match keyword hello followed by an identifier
purespecifier
   : Assign val = Octalliteral{if($val.text.compareTo("0")!=0) throw new InputMismatchException(this);};


   AT
      : '@'
      ;


   ELLIPSIS
      : '...'
      ;
