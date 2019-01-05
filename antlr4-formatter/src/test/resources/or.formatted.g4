grammar agc
;

prog
   : line+
   ;

line
   : comment_line
   | blank_line
   | instruction_line
   | erase_line
   | assignment_line
   ;


