

/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.apache.hadoop.grumpy.tools

import groovy.util.logging.Commons
import org.apache.hadoop.conf.Configuration
import org.apache.commons.cli.Options
import org.apache.hadoop.util.GenericOptionsParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.hadoop.grumpy.ExitCodeException

@Commons
class GrumpyToolRunner {
/**
 * Take a configuration and sort it
 * @param conf config to process
 * @return a set of all the keys, whose iterator will be in
 * natural sorted order
 */
/**
 * Probe a port for being open
 *
 * @param address        address to check
 * @param connectTimeout timeout in milliseconds
 * @throws IOException failure to connect, including timeout
 */
/**
 * Probe a port for being open
 *
 * @param hostname hostname to check
 * @param port port to probe
 * @param connectTimeout timeout in milliseconds
 * @throws IOException failure to connect, including timeout
 */
/**
 * Find a jar that contains a class of the same name, if any.
 * It will return a jar file, even if that is not the first thing
 * on the class path that has a class with the same name.
 *
 *
 * This is from Hadoop 0.24 and contains the fix for incomplete uuencode plus some 
 * extra error handling
 * @param my_class the class to find.
 * @return a jar file that contains the class, or null.
 * @throws IOException
 */
/**
 * Join a list of anything together into a string, using the defined separator (Which is not appended to the last element)
 * @param elements list elements
 * @param separator string separator
 * @return a combined list
 */
/**
 * Clean up the output directory
 * @param dir
 * @return
 */
/**
 * Dump a dir to the log, and add up the total size of all files matching the pattern
 * @param dumpLog
 * @param dir
 * @param pattern to look for when counting
 * @return number of files if
 */

  private static final int HELP_WIDTH = 80

  /**
   * Main entry point for main() methods to relay to -runs the tool
   * and handles success and failures, as well as reporting exceptions.
   * 
   * <i>This method never returns</i>
   * @param tool tool to run
   * @param args command line args
   */
  public static void executeAndExit(RunnableTool tool, String... args) {
    try {
      int success = run(null, tool, args)
      System.exit(success)
    } catch (ExitCodeException e) {
      log.error("On arguments $args")
      log.error(e.toString())
      System.exit(e.exitCode)
    } catch (Exception e) {
      log.error("On arguments $args")
      log.error(e.toString(), e)
      System.exit(-2)
    }
  }

  /**
   * Run a tool
   * @param conf an initial configuration, anything in the args will overwrite/extend this
   * @param tool tool to run
   * @param args command line args
   * @return the exit code
   * @throws Exception anything that went wrong
   */
  public static int run(Configuration conf, RunnableTool tool, String... args)
  throws Exception {
    conf = conf ?:new Configuration() 
    Options options = tool.createToolSpecificOptions() ?: new Options()

    options
        .addOption("p", "dump", false, "dump the current configuration");
    options
        .addOption("u", "usage", false, "Print the Usage");
    //create and execute the parsing
    GenericOptionsParser parser = new GenericOptionsParser(conf, options, args);


    //set the configuration back, so that Tool can configure itself
    Configuration configuration = parser.configuration;
    CommandLine commandLine = parser.commandLine;

    //null command line implies parse failure, which will have triggered a usage message
    //already
    if (!commandLine) {
      GrumpyUtils.dumpArguments(args);
      return -1;
    }

    if (commandLine.hasOption("p")) {
      //dump the commands
      GrumpyUtils.dumpArguments(args);
      //dump the configuration
      GrumpyUtils.dumpConf(conf);
    }

    if (commandLine.hasOption("u")) {
      return usage(args, tool, options);
    }


    tool.conf =configuration;

    //get the args w/o generic hadoop args
    String[] toolArgs = parser.getRemainingArgs();
    return tool.run(commandLine, toolArgs)? 0 : -1
  }

  private static int usage(String[] args, RunnableTool tool, Options options) {
    HelpFormatter hf = new HelpFormatter();
    hf.printHelp(HELP_WIDTH,
                 tool.usageHeader,
                 tool.toolName,
                 options,
                 tool.usageFooter,
                 true);
    GrumpyUtils.dumpArguments(args);

    return -1;
  }


}