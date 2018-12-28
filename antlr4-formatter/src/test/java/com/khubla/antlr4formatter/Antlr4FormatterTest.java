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

	@Test
	public void formatSimpleHelloWorldGrammar() throws Antlr4FormatterException, IOException {
		assertExpectedFormatting("Hello");
	}

	@Test
	public void formatSimpleHelloWorldGrammarIsIdempotent() throws Antlr4FormatterException, IOException {
		assertExpectedFormattingIsIdempotend("Hello");
	}

	@Test
	public void formatInlineComments() throws Antlr4FormatterException, IOException {
		assertExpectedFormatting("InlineComments");
	}

	@Test
	public void formatInlineCommentsIsIdempotent() throws Antlr4FormatterException, IOException {
		assertExpectedFormattingIsIdempotend("InlineComments");
	}

	private void assertExpectedFormatting(String grammar) throws Antlr4FormatterException, IOException {
		// given
		String unformattedGrammar = readFileAsUtf8ToString(grammar + ".unformatted.g4");
		String formattedGrammar = readFileAsUtf8ToString(grammar + ".formatted.g4");

		// when
		String result = Antlr4Formatter.format(unformattedGrammar);

		// then
		assertThat(result).isEqualTo(formattedGrammar);
	}

	private void assertExpectedFormattingIsIdempotend(String grammar) throws Antlr4FormatterException, IOException {
		// given
		String unformattedGrammar = readFileAsUtf8ToString(grammar + ".unformatted.g4");
		String formattedGrammar = readFileAsUtf8ToString(grammar + ".formatted.g4");

		// when
		String firstPass = Antlr4Formatter.format(unformattedGrammar);
		String result = Antlr4Formatter.format(firstPass);

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
