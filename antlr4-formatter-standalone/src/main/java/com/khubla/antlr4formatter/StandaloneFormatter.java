package com.khubla.antlr4formatter;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneFormatter {

   private static final Logger LOG = LoggerFactory.getLogger(StandaloneFormatter.class);

   private static final String CLI_INPUT_OPTION = "input";
   private static final String CLI_OUTPUT_OPTION = "output";
   private static final String CLI_DIR_OPTION = "dir";

   private StandaloneFormatter() {}

   public static void main(String[] args) {
      try {
         LOG.info("khubla.com Antlr4 Formatter");

         final Options options = createCliOptions();

         CommandLine cmd = parseCliArguments(args, options);

         /*
          * get the file
          */
         final String inputFilename = cmd.getOptionValue(CLI_INPUT_OPTION);
         final String outputFilename = cmd.getOptionValue(CLI_OUTPUT_OPTION);
         final String inputDirOption = cmd.getOptionValue(CLI_DIR_OPTION);

         if (null == inputDirOption) {
            Antlr4Formatter.formatSingleFile(inputFilename, outputFilename);
         } else {
            Antlr4Formatter.formatDirectory(inputDirOption);
         }
      } catch (final Exception e) {
         LOG.error("Could not format files", e);
      }
   }

   private static CommandLine parseCliArguments(String[] args, Options options) {
      final CommandLineParser parser = new DefaultParser();
      CommandLine cmd = null;
      try {
         cmd = parser.parse(options, args);
      } catch (final ParseException e) {
         LOG.error("Could not parse command line", e);

         showHelpAndExit(options);
      }
      return cmd;
   }

   private static Options createCliOptions() {
      final Options options = new Options();
      final Option o1 = Option.builder().argName(CLI_INPUT_OPTION).longOpt(CLI_INPUT_OPTION).type(String.class).hasArg().required(false).desc("input file").build();
      options.addOption(o1);
      final Option o2 = Option.builder().argName(CLI_OUTPUT_OPTION).longOpt(CLI_OUTPUT_OPTION).type(String.class).hasArg().required(false).desc("output file").build();
      options.addOption(o2);
      final Option o3 = Option.builder().argName(CLI_DIR_OPTION).longOpt(CLI_DIR_OPTION).type(String.class).hasArg().required(false).desc("input dir").build();
      options.addOption(o3);
      return options;
   }

   private static void showHelpAndExit(Options options) {
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("posix", options);
      System.exit(1);
   }
}
