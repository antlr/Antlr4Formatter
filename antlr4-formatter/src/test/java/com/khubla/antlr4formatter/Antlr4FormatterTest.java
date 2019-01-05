package com.khubla.antlr4formatter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

public class Antlr4FormatterTest {
   @Test
   public void testHello() throws Antlr4FormatterException, IOException {
      // given
      String unformattedGrammar = readFileAsUtf8ToString("Hello.unformatted.g4");
      String formattedGrammar = readFileAsUtf8ToString("Hello.formatted.g4");
      // when
      String result = Antlr4Formatter.format(unformattedGrammar);
      System.out.println(result);
      // then
      assertThat(result).isEqualTo(formattedGrammar);
   }

   @Test
   public void testComment() throws Antlr4FormatterException, IOException {
      // given
      String unformattedGrammar = readFileAsUtf8ToString("Comment.unformatted.g4");
      String formattedGrammar = readFileAsUtf8ToString("Comment.formatted.g4");
      // when
      String result = Antlr4Formatter.format(unformattedGrammar);
      System.out.println(result);
      // then
      assertThat(result).isEqualTo(formattedGrammar);
   }

   @Test
   public void testAt() throws Antlr4FormatterException, IOException {
      // given
      String unformattedGrammar = readFileAsUtf8ToString("at.unformatted.g4");
      String formattedGrammar = readFileAsUtf8ToString("at.formatted.g4");
      // when
      String result = Antlr4Formatter.format(unformattedGrammar);
      System.out.println(result);
      // then
      assertThat(result).isEqualTo(formattedGrammar);
   }

   @Ignore
   @Test
   public void testAction() throws Antlr4FormatterException, IOException {
      // given
      String unformattedGrammar = readFileAsUtf8ToString("action.unformatted.g4");
      String formattedGrammar = readFileAsUtf8ToString("action.formatted.g4");
      // when
      String result = Antlr4Formatter.format(unformattedGrammar);
      System.out.println(result);
      // then
      assertThat(result).isEqualTo(formattedGrammar);
   }

   private String readFileAsUtf8ToString(String fileName) throws IOException, Antlr4FormatterException {
      try {
         URI uri = Antlr4FormatterTest.class.getClassLoader().getResource(fileName).toURI();
         Path path = Paths.get(uri);
         return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      } catch (URISyntaxException e) {
         throw new Antlr4FormatterException(e);
      }
   }
}
