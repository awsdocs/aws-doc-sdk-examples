// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose:
// CommandLineParserTestClass.cs contains a class for testing the command-line parser defined for
//  parsing command lines in .NET console applications.
// The code for the command-line parser class must be included in the project and is locate at:
//  "github.com/awsdocs/aws-doc-sdk-examples/dotnetv3/extras/CommandLineParser/CommandLine.cs"

// Running the code:
// Run the application without arguments to see the help text.

using System;

namespace CommandLineParser
{
  class CommandLineParserTestClass
  {
    //
    // Main method.
    static void Main(string[] args)
    {
      Console.WriteLine("Testing command line parser");
      var parsedArgs = CommandLine.Parse(args);
      if(parsedArgs.Count == 0)
      {
        Console.WriteLine("No arguments found.");
        PrintHelp();
      }
      else
      {
        // Display the arguments that the user included on the command line.
        Console.WriteLine("\nAll arguments...");
        foreach(var arg in parsedArgs)
        {
          Console.WriteLine($"{arg.Key}: {arg.Value}");
        }

        // Get the defined application arguments from the parsed arguments.
        Console.WriteLine("\nDefined app arguments...");
        var requiredArg = CommandLine.GetArgument(parsedArgs, "-r was not provided", "-r", "--required");
        var optionalArg = CommandLine.GetArgument(parsedArgs, "-o was not provided", "-o");
        var optionalArgLongKey = CommandLine.GetArgument(parsedArgs, null, "-ol", "--optional-long");

        // Display the defined application arguments.
        Console.WriteLine($"Value for -r (--required): {requiredArg}");
        Console.WriteLine($"Value for -o: {optionalArg}");
        if(string.IsNullOrEmpty(optionalArgLongKey))
          Console.WriteLine("Argument -ol (--optional-long) was not provided");
        else
          Console.WriteLine($"Value for -ol (--optional-long): {optionalArgLongKey}");
      }
    }

    //
    // Command-line help
    private static void PrintHelp()
    {
      Console.WriteLine("\nA command-line parser for .NET console applications");
      Console.WriteLine("Argument can take the following forms:");
      Console.WriteLine("- A key-value pair (e.g., \"-k|--key value\")");
      Console.WriteLine("- A key without a value (e.g., \"--key1 --key2 value2\")");
      Console.WriteLine("- A value with a key (e.g., \"lone-value1 -k value lone-value2\")");
      Console.WriteLine("\nUsage: CommandLineParser -r required [-o optional -ol optional-long]");
      Console.WriteLine("  -r, --required: A required argument.");
      Console.WriteLine("  -o: An optional argument with no long key.");
      Console.WriteLine("  -ol, --optional-long: Another optional argument, this one with a long key.");
      Console.WriteLine("\nExamples:");
      Console.WriteLine(" CommandLineParser -a valueA");
      Console.WriteLine(" CommandLineParser lone-value1 -a valueA lone-value2");
      Console.WriteLine(" CommandLineParser lone-value1 -a valueA lone-value2 -b");
      Console.WriteLine(" CommandLineParser --arg valueA -r requiredValue --optional-long \"optional value with long key\"");
    }

  }
}
