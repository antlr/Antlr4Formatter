package com.khubla.antlr4formatter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class Antlr4FormatterTest {
   private String readFileAsUtf8ToString(String fileName) throws IOException, Antlr4FormatterException {
      try {
         final URI uri = Antlr4FormatterTest.class.getClassLoader().getResource(fileName).toURI();
         final Path path = Paths.get(uri);
         return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      } catch (final URISyntaxException e) {
         throw new Antlr4FormatterException(e);
      }
   }

   @Test
   public void testAction() throws Antlr4FormatterException, IOException {
      testGrammar("action.unformatted.g4", "action.formatted.g4");
   }

   @Test
   public void testANTLR4Lexer() throws Antlr4FormatterException, IOException {
      testGrammar("ANTLRv4Lexer.unformatted.g4", "ANTLRv4Lexer.formatted.g4");
   }

   @Test
   public void testArithmetic() throws Antlr4FormatterException, IOException {
      testGrammar("arithmetic.unformatted.g4", "arithmetic.formatted.g4");
   }

   @Test
   public void testAt() throws Antlr4FormatterException, IOException {
      testGrammar("at.unformatted.g4", "at.formatted.g4");
   }

   @Test
   public void testComment() throws Antlr4FormatterException, IOException {
      testGrammar("Comment.unformatted.g4", "Comment.formatted.g4");
   }

   @Test
   public void testComplexFragments() throws Antlr4FormatterException, IOException {
      testGrammar("ComplexFragments.unformatted.g4", "ComplexFragments.formatted.g4");
   }

   private void testGrammar(String unformatted, String formatted) throws Antlr4FormatterException, IOException {
      // given
      final String unformattedGrammar = readFileAsUtf8ToString(unformatted);
      final String formattedGrammar = readFileAsUtf8ToString(formatted);
      // when
      final String result = Antlr4Formatter.format(unformattedGrammar);
      System.out.println(result);
      // then
      assertThat(result).isEqualTo(formattedGrammar);
   }

   @Test
   public void testHello() throws Antlr4FormatterException, IOException {
      testGrammar("Hello.unformatted.g4", "Hello.formatted.g4");
   }

   @Test
   public void testInlineComments() throws Antlr4FormatterException, IOException {
      testGrammar("InlineComments.unformatted.g4", "InlineComments.formatted.g4");
   }

   @Test
   public void testJava8() throws Antlr4FormatterException, IOException {
      testGrammar("Java8.unformatted.g4", "Java8.formatted.g4");
   }

   @Test
   public void testLastToken() throws Antlr4FormatterException, IOException {
      testGrammar("lasttoken.unformatted.g4", "lasttoken.formatted.g4");
   }

   @Test
   public void testOr() throws Antlr4FormatterException, IOException {
      testGrammar("or.unformatted.g4", "or.formatted.g4");
   }

   @Test
   public void testParenth() throws Antlr4FormatterException, IOException {
      testGrammar("parenth.unformatted.g4", "parenth.formatted.g4");
   }

   private void testFormatterIdempotence(String unformatted, String formatted) throws Antlr4FormatterException, IOException {
      // given
      final String unformattedGrammar = readFileAsUtf8ToString(unformatted);
      final String formattedGrammar = readFileAsUtf8ToString(formatted);

      // when
      final String result = Antlr4Formatter.format(Antlr4Formatter.format(unformattedGrammar));

      // then
      assertThat(result).isEqualTo(formattedGrammar);
   }

   @Test
   public void testJava8Idempotence() throws Antlr4FormatterException, IOException {
      testFormatterIdempotence("Java8.unformatted.g4", "Java8.formatted.g4");
   }

   @Test
   public void testANTLR4LexerIdempotence() throws Antlr4FormatterException, IOException {
      testFormatterIdempotence("ANTLRv4Lexer.unformatted.g4", "ANTLRv4Lexer.formatted.g4");
   }

   @Test
   public void testArithmeticIdempotence() throws Antlr4FormatterException, IOException {
      testFormatterIdempotence("arithmetic.unformatted.g4", "arithmetic.formatted.g4");
   }
}
