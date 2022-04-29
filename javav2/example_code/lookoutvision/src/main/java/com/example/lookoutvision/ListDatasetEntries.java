/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[lookoutvision.java2.list_dataset_entries.complete]

package com.example.lookoutvision;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.LookoutVisionException;

import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

// Lists the JSON lines in an Amazon Lookout for Vision dataset.
public class ListDatasetEntries {

    public static final Logger logger = Logger.getLogger(ListDatasetEntries.class.getName());

    // Command line option constants.
    public static final String PROJECT = "project";
    public static final String TYPE = "type";
    public static final String SOURCE = "source";
    public static final String CLASSIFICATION = "classification";
    public static final String LABELED = "labeled";
    public static final String AFTER = "after";
    public static final String BEFORE = "before";
    public static final String HELP = "help";

    /*
     * Set up command line options for the dataset and filter options.
     */
    public static Options setupCommandLine() {

        Options options = new Options();

        Option option = new Option("p", PROJECT, true, "The project name.");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("t", TYPE, true, "The dataset type (train or test).");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("s", SOURCE, true, "Filter on source reference.");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("c", CLASSIFICATION, true, "Filter on image classification (normal or anomaly).");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("l", LABELED, true, "Filter on labeled images (yes to include labeled images).");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("a", AFTER, true, "Include images created after specified creation date. ");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("b", BEFORE, true, "Include images created before specified creation date.");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(HELP, "Shows this help message.");
        options.addOption(option);

        return options;

    }

    public static void main(String[] args) {

        String datasetType = null;
        String projectName = null;
        String sourceRef = null;
        String classification = null;
        Instant afterCreationDate = null;
        Instant beforeCreationDate = null;
        Boolean labeled = null;

        // Create the commandline parser and help formatter.
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = setupCommandLine();
        CommandLine line = null;

        try {
            // Parse the command line arguments.
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            // Parsing failed.
            System.out.println("Commandline parsing failed: " + exp.getMessage());
            formatter.printHelp("Lists the entries (JSON Lines) in an Amazon Lookout for Vision dataset.", options);
            System.exit(1);
        }

        // Get the command line arguments.
        projectName = line.getOptionValue(PROJECT).trim();
        datasetType = line.getOptionValue(TYPE).trim();

        if (line.hasOption(SOURCE)) {
            sourceRef = line.getOptionValue(SOURCE).trim();
        }
        if (line.hasOption(CLASSIFICATION)) {
            classification = line.getOptionValue(CLASSIFICATION).trim();
        }
        if (line.hasOption(LABELED)) {
            String arglabeled = line.getOptionValue(LABELED).trim();
            if (arglabeled.equals("yes")) {
                labeled = true;
            } else
                labeled = false;
        }

        if (line.hasOption(AFTER)) {
            afterCreationDate = Instant.parse(line.getOptionValue(AFTER).trim());

        }

        if (line.hasOption(BEFORE)) {
            beforeCreationDate = Instant.parse(line.getOptionValue(BEFORE).trim());
        }

        if (line.hasOption(HELP)) {
            formatter.printHelp("Lists the entries (JSON Lines) in an Amazon Lookout for Vision dataset.", options);
            System.exit(1);
        }

        try {

            // Get the lookoutvision client.
            LookoutVisionClient lfvClient = LookoutVisionClient.builder().build();

            System.out.println(
                    String.format("Listing JSON lines for %s dataset in project %s.", datasetType, projectName));

            // Get the dataset JSON Lines.
            List<String> jsonLines = Datasets.listDatasetEntries(lfvClient, projectName, datasetType, sourceRef,
                    classification, labeled, beforeCreationDate, afterCreationDate);

            // Print each JSON Line.
            for (String jsonLine : jsonLines) {
                System.out.println(jsonLine);
            }

            System.out.println("Finished.");

            System.out.println(String.format("Finished listing dataset: %s for project: %s", datasetType, projectName));

        } catch (LookoutVisionException lfvError) {
            logger.log(Level.SEVERE, "Could not list dataset entries: {0}: {1}",
                    new Object[] { lfvError.awsErrorDetails().errorCode(),
                            lfvError.awsErrorDetails().errorMessage() });
            System.out.println(String.format("Could not list dataset entries: %s", lfvError.getMessage()));
            System.exit(1);
        }
    }

}
// snippet-end:[lookoutvision.java2.list_dataset_entries.complete]