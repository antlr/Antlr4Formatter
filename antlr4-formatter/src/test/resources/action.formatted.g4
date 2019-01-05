grammar Hello
;

purespecifier
   : Assign val = Octalliteral
   {if($val.text.compareTo("0")!=0) throw new InputMismatchException(this)
   ;}
   ;


