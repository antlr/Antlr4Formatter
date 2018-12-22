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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.antlr4.ANTLRv4Lexer;
import org.antlr.parser.antlr4.ANTLRv4Parser;
import org.antlr.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

/**
 * @author Tom Everett
 */
public class Antlr4Formatter {
   /**
    * input option
    */
   private static final String INPUT_OPTION = "input";
   /**
    * output option
    */
   private static final String OUTPUT_OPTION = "output";
   /**
    * output option
    */
   private static final String DIR_OPTION = "dir";

   /**
    * format an input file
    */
   private static void format(InputStream inputStream, OutputStream outputStream) throws Exception {
      try {
         if (null != inputStream) {
            final ANTLRv4Lexer lexer = new ANTLRv4Lexer(CharStreams.fromStream(inputStream));
            final CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
            final ANTLRv4Parser parser = new ANTLRv4Parser(commonTokenStream);
            final GrammarSpecContext grammarSpecContext = parser.grammarSpec();
            ParseTreeWalker.DEFAULT.walk(new Antlr4ParseTreeListenerImpl(new PrintStream(outputStream), commonTokenStream), grammarSpecContext);
         } else {
            throw new IllegalArgumentException();
         }
      } catch (final Exception e) {
         throw new Exception("Exception reading and parsing file", e);
      }
   }

   static void formatDirectory(String inputDirOption) throws Exception {
      List<String> files = new ArrayList<String>();
      files = getFiles(inputDirOption, files, ".g4");
      for (final String filename : files) {
         final File file = new File(filename);
         formatFile(file, file);
      }
   }

   /*
    * format a single file
    */
   static void formatFile(File inputFile, File outputFile) throws Exception {
      /*
       * filenames
       */
      if (true == inputFile.exists()) {
         /*
          * temp file
          */
         final File tempFile = File.createTempFile(inputFile.getName(), ".g4");
         /*
          * show the filename
          */
         System.out.println("Formatting: " + inputFile.getName());
         /*
          * format
          */
         format(new FileInputStream(inputFile), new FileOutputStream(tempFile));
         /*
          * copy file
          */
         FileUtils.copyFile(tempFile, outputFile);
         tempFile.delete();
      } else {
         throw new Exception("Unable to find: '" + inputFile + "'");
      }
   }

   static void formatSingleFile(String inputFilename, String outputFilename) throws Exception {
      /*
       * files
       */
      final File inputFile = new File(inputFilename);
      File outputFile;
      if (null == outputFilename) {
         /*
          * no output specified, use a temp file
          */
         outputFile = inputFile;
      } else {
         outputFile = new File(outputFilename);
      }
      /*
       * format
       */
      formatFile(inputFile, outputFile);
   }

   /**
    * get a list of all files in a directory
    */
   private static List<String> getFiles(String dir, List<String> files, String filter) throws Exception {
      final File file = new File(dir);
      final String[] list = file.list();
      if (null != list) {
         for (int i = 0; i < list.length; i++) {
            final String fileName = dir + (dir.endsWith("/") ? "" : "/") + list[i];
            final File f2 = new File(fileName);
            if (false == f2.isHidden()) {
               if (f2.isDirectory()) {
                  getFiles(fileName + "/", files, filter);
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

   public static void main(String[] args) {
      try {
         System.out.println("khubla.com Antlr4 Formatter");
         /*
          * options
          */
         final Options options = new Options();
         final Option o1 = Option.builder().argName(INPUT_OPTION).longOpt(INPUT_OPTION).type(String.class).hasArg().required(false).desc("input file").build();
         options.addOption(o1);
         final Option o2 = Option.builder().argName(OUTPUT_OPTION).longOpt(OUTPUT_OPTION).type(String.class).hasArg().required(false).desc("output file").build();
         options.addOption(o2);
         final Option o3 = Option.builder().argName(DIR_OPTION).longOpt(DIR_OPTION).type(String.class).hasArg().required(false).desc("input dir").build();
         options.addOption(o3);
         /*
          * parse
          */
         final CommandLineParser parser = new DefaultParser();
         CommandLine cmd = null;
         try {
            cmd = parser.parse(options, args);
         } catch (final Exception e) {
            e.printStackTrace();
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("posix", options);
            System.exit(0);
         }
         /*
          * get the file
          */
         final String inputFilename = cmd.getOptionValue(INPUT_OPTION);
         final String outputFilename = cmd.getOptionValue(OUTPUT_OPTION);
         final String inputDirOption = cmd.getOptionValue(DIR_OPTION);
         if (null == inputDirOption) {
            formatSingleFile(inputFilename, outputFilename);
         } else {
            formatDirectory(inputDirOption);
         }
      } catch (final Exception e) {
         e.printStackTrace();
      }
   }
}
