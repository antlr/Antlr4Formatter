package com.khubla.antlr4formatter;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class Antlr4FormatterTest {

  @Test
  public void formatString() throws IOException {
    // given
    Antlr4Formatter formatter = new Antlr4Formatter();
    String unformattedGrammar = readFileAsUtf8ToString("Hello.unformatted.g4");
    String formattedGrammar = readFileAsUtf8ToString("Hello.formatted.g4");

    // when
    String result = formatter.format(unformattedGrammar);

    // then
    assertThat(result).isEqualTo(formattedGrammar);
  }

  private String readFileAsUtf8ToString(String fileName) throws IOException {
    try {
      URI uri = Antlr4FormatterTest.class.getClassLoader().getResource(fileName).toURI();
      Path path = Paths.get(uri);
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}