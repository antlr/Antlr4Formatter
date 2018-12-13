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


import org.antlr.parser.antlr4.ANTLRv4Lexer;
import org.antlr.parser.antlr4.ANTLRv4Parser;
import org.antlr.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Everett
 */
public class Antlr4Formatter {
   
   private static final Logger LOG = LoggerFactory.getLogger(Antlr4Formatter.class);


   public static String format(String string) {
      try {
         if (null != string) {
            StringWriter writer = new StringWriter();
            CodePointCharStream input = CharStreams.fromString(string);
            formatGrammar(input, writer);
            return writer.toString();
         } else {
            return "";
         }
      } catch (final Exception e) {
         throw new RuntimeException("Exception reading and parsing file", e);
      }
   }

   private static void formatGrammar(CharStream input, Writer output) {
      final ANTLRv4Lexer lexer = new ANTLRv4Lexer(input);
      final CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
      final ANTLRv4Parser parser = new ANTLRv4Parser(commonTokenStream);
      final GrammarSpecContext grammarSpecContext = parser.grammarSpec();
      ParseTreeWalker.DEFAULT.walk(new Antlr4ParseTreeListenerImpl(output, commonTokenStream), grammarSpecContext);
   }

   public static void format(InputStream inputStream, OutputStream outputStream) {

         if (null == inputStream) {
            throw new IllegalArgumentException("Input stream must not be null!");
         }
            final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            try(OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
               formatGrammar(CharStreams.fromReader(reader), writer);
            } catch (IOException e) {
               LOG.error("Could not format file", e);
            }
   }

   public static void formatDirectory(String inputDirOption) throws Exception {
      List<String> files = new ArrayList<>();
      files = listFilesFromDirectory(inputDirOption, files, ".g4");
      for (final String filename : files) {
         final File file = new File(filename);
         formatSingleFile(file, file);
      }
   }

   public static void formatSingleFile(File inputFile, File outputFile) throws Exception {

      if (inputFile.exists()) {

         final File tempFile = File.createTempFile(inputFile.getName(), ".g4");

         LOG.info("Formatting: {}", inputFile.getName());

         format(new FileInputStream(inputFile), new FileOutputStream(tempFile));

         Files.copy(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         Files.delete(tempFile.toPath());
      } else {
         throw new Exception("Unable to find: '" + inputFile + "'");
      }
   }

   public static void formatSingleFile(String inputFilename, String outputFilename) throws Exception {
      final File inputFile = new File(inputFilename);
      File outputFile;
      
      if (null == outputFilename) {
         outputFile = inputFile;
      } else {
         outputFile = new File(outputFilename);
      }

      formatSingleFile(inputFile, outputFile);
   }

   private static List<String> listFilesFromDirectory(String dir, List<String> files, String filter) {
      final File file = new File(dir);
      final String[] list = file.list();
      if (null != list) {
        for (String s : list) {
          final String fileName = dir + (dir.endsWith("/") ? "" : "/") + s;
          final File f2 = new File(fileName);
          if (!f2.isHidden()) {
            if (f2.isDirectory()) {
              listFilesFromDirectory(fileName + "/", files, filter);
            } else {
              if (fileName.endsWith(filter)) {
                files.add(fileName);
              }
            }
          }
        }
      }
      return files;
   }
}
