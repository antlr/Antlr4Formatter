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
   *
   */
   private static final Logger logger = LoggerFactory.getLogger(Antlr4FormatterListenerImpl.class);
   /**
    * non space tokens
    */
   private static final Set<String> noSpacingBeforeTokens = new HashSet<>(Arrays.asList(new String[] { "?", "*", ";", ")", "+" }));
   private static final Set<String> noSpacingAfterTokens = new HashSet<>(Arrays.asList(new String[] { "(" }));
   /**
    * rules which need a NL after the rule
    */
   private static final Set<Class<?>> newlineAfterRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { GrammarDeclContext.class, ParserRuleSpecContext.class, LexerRuleSpecContext.class }));
   /**
    * rules which need a NL before the rule
    */
   private static final Set<Class<?>> newlineBeforeRules = new HashSet<Class<?>>(
         Arrays.asList(new Class<?>[] { ParserRuleSpecContext.class, LexerRuleSpecContext.class, OptionsSpecContext.class, ModeSpecContext.class, ActionBlockContext.class }));
   /**
    * rules which need an indent
    */
   private static final Set<Class<?>> indentedeRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { ParserRuleSpecContext.class, LexerRuleSpecContext.class }));
   /**
    * tokens which need a newline before them
    */
   private static final Set<String> newlineBeforeTokens = new HashSet<String>(Arrays.asList(new String[] { ":", "|", ";" }));
   /**
    * rules which are interpreted as literal
    */
   private static final Set<Class<?>> interpretAsLiteralRules = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { ActionBlockContext.class }));
   /**
    * indent
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
    * ctor
    */
   public Antlr4FormatterListenerImpl(Writer writer) {
      this.writer = writer;
   }

   /**
    * build indent
    */
   private String buildIndent(int indent) {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < (indent * 3); i++) {
         sb.append(" ");
      }
      return sb.toString();
   }

   @Override
   public void enterEveryRule(ParserRuleContext ctx) {
      logger.debug("enter rule: " + ctx.getClass().getSimpleName());
      // System.out.println("enter rule: " + ctx.getClass().getSimpleName());
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
      // System.out.println("exit rule: " + ctx.getClass().getSimpleName());
      if (indentedeRules.contains(ctx.getClass())) {
         indent--;
      }
      if (newlineAfterRules.contains(ctx.getClass())) {
         writeCR();
      }
   }

   @Override
   public void visitComment(Token token, boolean left) {
      if (false == newline) {
         write(" ");
      }
      if (left) {
         write(token.getText());
         writeCR();
      } else {
         write(token.getText());
         // writeCR();
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
         if (newlineBeforeTokens.contains(node.toString())) {
            if (false == interpretAsLiteralRules.contains(node.getParent().getClass())) {
               writeCR();
            }
         }
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
      writeSimple("\n");
      writeSimple(buildIndent(indent));
      newline = true;
   }

   private void writeSimple(String string) {
      try {
         // System.out.print(string);
         writer.write(string);
      } catch (final IOException e) {
         throw new RuntimeException("Could not write to writer", e);
      }
   }
}
