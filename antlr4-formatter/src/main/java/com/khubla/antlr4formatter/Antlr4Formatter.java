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

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.parser.antlr4.*;
import org.antlr.parser.antlr4.ANTLRv4Parser.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.slf4j.*;

import com.khubla.antlr4formatter.listener.*;

/**
 * @author Tom Everett
 */
public class Antlr4Formatter {
	private static final Logger LOG = LoggerFactory.getLogger(Antlr4Formatter.class);

	public static void format(InputStream inputStream, OutputStream outputStream) {
		if (null == inputStream) {
			throw new IllegalArgumentException("Input stream must not be null!");
		}
		final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
			formatGrammar(CharStreams.fromReader(reader), writer);
		} catch (final IOException e) {
			LOG.error("Could not format file", e);
		}
	}

	public static String format(String string) throws Antlr4FormatterException {
		try {
			if (null != string) {
				final StringWriter writer = new StringWriter();
				final CodePointCharStream input = CharStreams.fromString(string);
				formatGrammar(input, writer);
				return writer.toString();
			} else {
				return "";
			}
		} catch (final Exception e) {
			throw new Antlr4FormatterException("Exception reading and parsing file", e);
		}
	}

	public static void formatDirectory(final String inputDirOption) throws Antlr4FormatterException {
        final List<String> files = listFilesFromDirectory(inputDirOption, ".g4");
		for (final String filename : files) {
			final File file = new File(filename);
			formatSingleFile(file, file);
		}
	}

	private static void formatGrammar(CharStream input, Writer output) {
		final ANTLRv4Lexer lexer = new ANTLRv4Lexer(input);
		final CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		final ANTLRv4Parser parser = new ANTLRv4Parser(commonTokenStream);
		final GrammarSpecContext grammarSpecContext = parser.grammarSpec();
		ParseTreeWalker.DEFAULT.walk(new FormatterParseTreeListenerImpl(new Antlr4FormatterListenerImpl(output), commonTokenStream), grammarSpecContext);
	}

	public static void formatSingleFile(File inputFile, File outputFile) throws Antlr4FormatterException {
		try {
			if (inputFile.exists()) {
				final File tempFile = File.createTempFile(inputFile.getName(), ".g4");
				LOG.info("Formatting: {}", inputFile.getName());
				format(new FileInputStream(inputFile), new FileOutputStream(tempFile));
				Files.copy(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Files.delete(tempFile.toPath());
			} else {
				throw new Exception("Unable to find: '" + inputFile + "'");
			}
		} catch (final Exception e) {
			throw new Antlr4FormatterException("Exception fromatting file", e);
		}
	}

	public static void formatSingleFile(String inputFilename, String outputFilename) throws Antlr4FormatterException {
		final File inputFile = new File(inputFilename);
		File outputFile;
		if (null == outputFilename) {
			outputFile = inputFile;
		} else {
			outputFile = new File(outputFilename);
		}
		formatSingleFile(inputFile, outputFile);
	}

    private static List<String> listFilesFromDirectory(final String dir, final String filter) {
        try (Stream<Path> stream = Files.walk(Path.of(dir))) {
            return stream.filter(p -> {
                try {
                    return Files.isRegularFile(p) && !Files.isHidden(p) && p.toString().endsWith(filter);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).map(p -> p.toString()).collect(Collectors.toList());
        } catch (IOException ioe) {
            LOG.error("Could not list files from directory {}", ioe.getMessage());
            return Collections.emptyList();
        }
    }

	private Antlr4Formatter() {
	}
}
