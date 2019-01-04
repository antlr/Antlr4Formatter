package org.antlr.parser.antlr4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

public abstract class LexerAdaptor extends Lexer {
   /**
   * Track whether we are inside of a rule and whether it is lexical parser. currentRuleType==Token.INVALID_TYPE means that we are outside of a rule. At the first sign of a rule name reference and
   * currentRuleType==invalid, we can assume that we are starting a parser rule. Similarly, seeing a token reference when not already in rule means starting a token rule. The terminating ';' of a
   * rule, flips this back to invalid type. This is not perfect logic but works. For example, "grammar T;" means that we start and stop a lexical rule for the "T;". Dangerous but works. The whole
   * point of this state information is to distinguish between [..arg actions..] and [charsets]. Char sets can only occur in lexical rules and arg actions cannot occur.
   */
   private int currentRuleType = Token.INVALID_TYPE;

   public LexerAdaptor(CharStream input) {
      super(input);
   }

   @Override
   public Token emit() {
      if (_type == ANTLRv4Lexer.ID) {
         final String firstChar = _input.getText(Interval.of(_tokenStartCharIndex, _tokenStartCharIndex));
         if (Character.isUpperCase(firstChar.charAt(0))) {
            _type = ANTLRv4Lexer.TOKEN_REF;
         } else {
            _type = ANTLRv4Lexer.RULE_REF;
         }
         if (currentRuleType == Token.INVALID_TYPE) { // if outside of rule def
            currentRuleType = _type; // set to inside lexer or parser rule
         }
      } else if (_type == ANTLRv4Lexer.SEMI) { // exit rule def
         currentRuleType = Token.INVALID_TYPE;
      }
      return super.emit();
   }

   public int getCurrentRuleType() {
      return currentRuleType;
   }

   protected void handleBeginArgument() {
      if (inLexerRule()) {
         pushMode(ANTLRv4Lexer.LexerCharSet);
         more();
      } else {
         pushMode(ANTLRv4Lexer.Argument);
      }
   }

   protected void handleEndAction() {
      popMode();
      if (_modeStack.size() > 0) {
         setType(ANTLRv4Lexer.ACTION_CONTENT);
      }
   }

   protected void handleEndArgument() {
      popMode();
      if (_modeStack.size() > 0) {
         setType(ANTLRv4Lexer.ARGUMENT_CONTENT);
      }
   }

   private boolean inLexerRule() {
      return currentRuleType == ANTLRv4Lexer.TOKEN_REF;
   }

   public void setCurrentRuleType(int ruleType) {
      currentRuleType = ruleType;
   }
}
