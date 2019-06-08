/*
 * Antlr4Formatter Copyright 2015, khubla.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.khubla.antlr4formatter;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.antlr.parser.antlr4.ANTLRv4Parser.ActionBlockContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.GrammarDeclContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ModeSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.OptionsSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import org.antlr.parser.antlr4.ANTLRv4Parser.PrequelConstructContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.khubla.antlr4formatter.listener.FormatterListener;

public class Antlr4FormatterListenerImpl implements FormatterListener {
   /**
    * tabs or space. vi or emacs.
    *
    * @author tom
    */
   public static enum IndentType {
      tab, space
   }

   /**
    * logger
    */
   private static final Logger logger = LoggerFactory.getLogger(Antlr4FormatterListenerImpl.class);
   /**
    * tokens that do not need a space before them
    */
   private static final Set<String> noSpacingBeforeTokens = new HashSet<>(Arrays.asList(new String[] { "?", "*", ";", ")", "+" }));
   /**
    * tokens that do not need a space after them
    */
   private static final Set<String> noSpacingAfterTokens = new HashSet<>(Arrays.asList(new String[] { "(" }));
   /**
    * rules which need a NL after the rule
    */
   private static final Set<Class<?>> newlineAfterRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { GrammarDeclContext.class, ParserRuleSpecContext.class, LexerRuleSpecContext.class }));
   /**
    * rules which colon on new line
    */
   private static final Set<Class<?>> colonOnNewlineRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { ParserRuleSpecContext.class, LexerRuleSpecContext.class }));
   /**
    * rules which need a NL before the rule
    */
   private static final Set<Class<?>> newlineBeforeRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { PrequelConstructContext.class, GrammarDeclContext.class, ParserRuleSpecContext.class,
         LexerRuleSpecContext.class, OptionsSpecContext.class, ModeSpecContext.class, ActionBlockContext.class }));
   /**
    * rules which need an indent
    */
   private static final Set<Class<?>> indentedeRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { ParserRuleSpecContext.class, LexerRuleSpecContext.class }));
   /**
    * tokens which need a newline before them
    */
   private static final Set<String> newlineBeforeTokens = new HashSet<String>(Arrays.asList(new String[] { ":", "|" }));
   /**
    * rules which are interpreted as literal
    */
   private static final Set<Class<?>> interpretAsLiteralRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { ActionBlockContext.class }));
   /**
    * default indent size
    */
   private static final int DEFAULT_INDENT_SIZE = 3;
   /**
    * default indent type
    */
   private static final IndentType DEFAULT_INDENT_TYPE = IndentType.space;
   /**
    * debug
    */
   private static final boolean DEBUG = false;
   /**
    * indent size
    */
   private int indent = 0;
   /**
    * newline
    */
   private boolean newline = true;
   /**
    * PrintStream
    */
   private final Writer writer;
   /**
    * previous token
    */
   private String previousToken = "";
   /**
    * indent size
    */
   private int indentSize;
   /**
    * indent type
    */
   private IndentType indentType;
   /**
    * parenth count
    */
   private int parenthCount = 0;
   /**
    * started output? rules in 'newlineBeforeRules' output a NL before they output the rule. This is done to ensure they're on a new line. However, if they are the first rule, this results in empty
    * NL's at the top of the file. This prevents that.
    */
   private boolean outputStarted = false;

   /**
    * ctor
    */
   public Antlr4FormatterListenerImpl(Writer writer) {
      this.writer = writer;
      indentSize = DEFAULT_INDENT_SIZE;
      indentType = DEFAULT_INDENT_TYPE;
   }

   /**
    * build indent
    */
   private String buildIndent(int indent) {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < (indent * indentSize); i++) {
         if (indentType == IndentType.space) {
            sb.append(" ");
         } else {
            sb.append("/t");
         }
      }
      return sb.toString();
   }

   @Override
   public void enterEveryRule(ParserRuleContext ctx) {
      logger.debug("enter rule: " + ctx.getClass().getSimpleName());
      if (DEBUG) {
         System.out.println("enter rule: " + ctx.getClass().getSimpleName());
      }
      if (newlineBeforeRules.contains(ctx.getClass())) {
         writeCR();
      }
      if (indentedeRules.contains(ctx.getClass())) {
         indent++;
      }
   }

   @Override
   public void exitEveryRule(ParserRuleContext ctx) {
      logger.debug("exit rule: " + ctx.getClass().getSimpleName());
      if (DEBUG) {
         System.out.println("exit rule: " + ctx.getClass().getSimpleName());
      }
      if (indentedeRules.contains(ctx.getClass())) {
         indent--;
      }
      if (newlineAfterRules.contains(ctx.getClass())) {
         if (false == newline) {
            writeCR();
         }
      }
   }

   public int getIndentSize() {
      return indentSize;
   }

   public IndentType getIndentType() {
      return indentType;
   }

   /**
    * are we currently in a parenth?
    */
   protected boolean isInParenth() {
      return (parenthCount == 0) ? false : true;
   }

   public void setIndentSize(int indentSize) {
      this.indentSize = indentSize;
   }

   public void setIndentType(IndentType indentType) {
      this.indentType = indentType;
   }

   @Override
   public void visitComment(Token token, boolean left, CommentType commentType, boolean nl) {
      if ((false == newline) && (false == nl)) {
         write(" ");
      }
      if (nl) {
         if (commentType == CommentType.line) {
            writeCR();
         } else {
            writeSimple("\n");
         }
      }
      if (left) {
         writeSimple(token.getText());
      } else {
         writeSimple(token.getText());
      }
      /*
       * block comments need a CR or comments to the left of the current token
       */
      if ((commentType == CommentType.block) || (left)) {
         writeCR();
      }
      if ((commentType == CommentType.line) || (!left)) {
         writeCR();
      }
   }

   @Override
   public void visitErrorNode(ErrorNode node) {
      System.out.println("Error: " + node.getText());
   }

   @Override
   public void visitTerminal(TerminalNode node) {
      /*
       * eof
       */
      if (node.getSymbol().getType() != Recognizer.EOF) {
         /*
          * count parenths
          */
         if (false == interpretAsLiteralRules.contains(node.getParent().getClass())) {
            if (node.toString().compareTo("(") == 0) {
               parenthCount++;
            } else if (node.toString().compareTo(")") == 0) {
               parenthCount--;
            }
         }
         /*
          * tokens which require a newline before the token, like '|' and ':'
          */
         if (newlineBeforeTokens.contains(node.toString())) {
            if (false == newline) {
               if (false == interpretAsLiteralRules.contains(node.getParent().getClass())) {
                  if (false == isInParenth()) {
                     writeCR();
                  }
               }
            }
         }
         /*
          * some rules want the ';' on a new line (like ParserRuleSpecContext, LexerRuleSpecContext)
          */
         if (node.toString().compareTo(";") == 0) {
            if (colonOnNewlineRules.contains(node.getParent().getClass())) {
               writeCR();
            }
         }
         /*
          * write the node
          */
         write(node);
      } else {
         writeCR();
      }
   }

   /**
    * write to the output stream
    */
   protected void write(String str) {
      /*
       * print token
       */
      writeSimple(str);
      if (true == newline) {
         newline = false;
      }
      /*
       * save the previous
       */
      previousToken = str;
   }

   /**
    * write to the output stream
    */
   private void write(TerminalNode node) {
      logger.debug("Writing: '" + node.getText() + "'");
      /*
       * space before the output
       */
      if ((false == newline) && (false == noSpacingAfterTokens.contains(previousToken)) && (false == noSpacingBeforeTokens.contains(node.getText()))) {
         if (false == interpretAsLiteralRules.contains(node.getParent().getClass())) {
            writeSimple(" ");
         }
      }
      /*
       * print token
       */
      writeSimple(node.getText());
      if (true == newline) {
         newline = false;
      }
      /*
       * save the previous
       */
      previousToken = node.getText();
   }

   /**
    * write a CR
    */
   private void writeCR() {
      if (true == outputStarted) {
         writeSimple("\n");
         writeSimple(buildIndent(indent));
         newline = true;
      }
   }

   private void writeSimple(String string) {
      try {
         if (DEBUG) {
            System.out.print(string);
         }
         writer.write(string);
         if ((string.trim().length() > 0) && (outputStarted == false)) {
            outputStarted = true;
         }
      } catch (final IOException e) {
         throw new RuntimeException("Could not write to writer", e);
      }
   }
}
