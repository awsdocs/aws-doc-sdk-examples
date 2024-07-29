// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.medicalimaging;
import org.apache.commons.cli.*;

// snippet-start:[medicalimaging.java2.copy_imageset.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.CopyDestinationImageSet;
import software.amazon.awssdk.services.medicalimaging.model.CopyImageSetInformation;
import software.amazon.awssdk.services.medicalimaging.model.CopyImageSetRequest;
import software.amazon.awssdk.services.medicalimaging.model.CopyImageSetResponse;
import software.amazon.awssdk.services.medicalimaging.model.CopySourceImageSetInformation;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.medicalimaging.model.MetadataCopies;

import java.util.Collections;
import java.util.Vector;

// snippet-end:[medicalimaging.java2.copy_imageset.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CopyImageSet {

    /**
     * The main function.
     * <p>
     * usage: Copy Image Set
     * -c <arg>    One or more comma separated optional subsets to copy
     * -d <arg>    The ID of the data store
     * -di <arg>   The optional destination image set ID
     * -dv <arg>   The optional destination version ID
     * -f          The optional force copy flag
     * -h          Prints this help message
     * -i <arg>    The ID of the image set
     * -v <arg>    The latest version ID of the image set
     */
    public static void main(String[] args) {
        Options options = new Options();
        Option datastoreIdOption = Option.builder("d").hasArg().desc("The ID of the data store").build();
        Option imageSetIdOption = Option.builder("i").hasArg().desc("The ID of the image set").build();
        Option latestVersionIdOption = Option.builder("v").hasArg().desc("The latest version ID of the image set").build();
        Option destinationImageSetIdOption = Option.builder("di").hasArg().desc("The optional destination image set ID").build();
        Option destinationVersionIdOption = Option.builder("dv").hasArg().desc("The optional destination version ID").build();
        Option forceOption = Option.builder("f").desc("The optional force copy flag").build();
        Option copySubset = Option.builder("c").hasArg().desc("One or more comma separated optional subsets to copy").build();
        Option help = Option.builder("h").desc("Prints this help message").build();
        options.addOption(datastoreIdOption);
        options.addOption(imageSetIdOption);
        options.addOption(latestVersionIdOption);
        options.addOption(destinationImageSetIdOption);
        options.addOption(destinationVersionIdOption);
        options.addOption(forceOption);
        options.addOption(copySubset);
        options.addOption(help);
        CommandLineParser parser = new DefaultParser();

        String datastoreId = null;
        String imageSetId = null;
        String versionId = null;
        String destinationImageSetId = null;
        String destinationVersionId = null;
        boolean force = false;
        Vector<String> subsets = null;
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(help)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Copy Image Set", options);
                System.exit(0);
            }

            datastoreId = cmd.getOptionValue(datastoreIdOption);
            imageSetId = cmd.getOptionValue(imageSetIdOption);
            versionId = cmd.getOptionValue(latestVersionIdOption);
            destinationImageSetId = cmd.getOptionValue(destinationImageSetIdOption);
            destinationVersionId = cmd.getOptionValue(destinationVersionIdOption);
            force = cmd.hasOption(forceOption);

            if (cmd.hasOption(copySubset)) {
                subsets = new Vector<>();
                String commaSeparatedSubsets = cmd.getOptionValue(copySubset);
                String[] subsetsArray = commaSeparatedSubsets.split(",");
                Collections.addAll(subsets, subsetsArray);
            }

        } catch (UnrecognizedOptionException ex) {
            System.out.println(ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Copy Image Set", options);
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }

        if (datastoreId == null || imageSetId == null || versionId == null) {
            System.err.println("Data store ID, image set ID, and version ID are required");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Copy Image Set", options);
            System.exit(1);
        }

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        try {
            String copiedImageSetId = copyMedicalImageSet(medicalImagingClient, datastoreId, imageSetId,
                    versionId, destinationImageSetId, destinationVersionId, force, subsets);

            System.out.println("The copied image set ID is " + copiedImageSetId);
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        medicalImagingClient.close();
    }

    // snippet-start:[medicalimaging.java2.copy_imageset.main]

    /**
     * Copy an AWS HealthImaging image set.
     *
     * @param medicalImagingClient  - The AWS HealthImaging client object.
     * @param datastoreId           - The datastore ID.
     * @param imageSetId            - The image set ID.
     * @param latestVersionId       - The version ID.
     * @param destinationImageSetId - The optional destination image set ID, ignored if null.
     * @param destinationVersionId  - The optional destination version ID, ignored if null.
     * @param force                 - The force flag.
     * @param subsets               - The optional subsets to copy, ignored if null.
     * @return The image set ID of the copy.
     * @throws MedicalImagingException - Base exception for all service exceptions thrown by AWS Health Imaging.
     */
    public static String copyMedicalImageSet(MedicalImagingClient medicalImagingClient,
                                             String datastoreId,
                                             String imageSetId,
                                             String latestVersionId,
                                             String destinationImageSetId,
                                             String destinationVersionId,
                                             boolean force,
                                             Vector<String> subsets) {

        try {
            CopySourceImageSetInformation.Builder copySourceImageSetInformation = CopySourceImageSetInformation.builder()
                    .latestVersionId(latestVersionId);

            // Optionally copy a subset of image instances.
            if (subsets != null) {
                String subsetInstanceToCopy = getCopiableAttributesJSON(imageSetId, subsets);
                copySourceImageSetInformation.dicomCopies(MetadataCopies.builder()
                        .copiableAttributes(subsetInstanceToCopy)
                        .build());
            }

            CopyImageSetInformation.Builder copyImageSetBuilder = CopyImageSetInformation.builder()
                    .sourceImageSet(copySourceImageSetInformation.build());

            // Optionally designate a destination image set.
            if (destinationImageSetId != null) {
                copyImageSetBuilder = copyImageSetBuilder.destinationImageSet(CopyDestinationImageSet.builder()
                        .imageSetId(destinationImageSetId)
                        .latestVersionId(destinationVersionId)
                        .build());
            }

            CopyImageSetRequest copyImageSetRequest = CopyImageSetRequest.builder()
                    .datastoreId(datastoreId)
                    .sourceImageSetId(imageSetId)
                    .copyImageSetInformation(copyImageSetBuilder.build())
                    .force(force)
                    .build();

            CopyImageSetResponse response = medicalImagingClient.copyImageSet(copyImageSetRequest);

            return response.destinationImageSetProperties().imageSetId();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    // snippet-end:[medicalimaging.java2.copy_imageset.main]

    // snippet-start:[medicalimaging.java2.copy_imageset.copiable_attributes]

    /**
     * Create a JSON string of copiable image instances.
     *
     * @param imageSetId - The image set ID.
     * @param subsets    - The subsets to copy.
     * @return A JSON string of copiable image instances.
     */
    private static String getCopiableAttributesJSON(String imageSetId, Vector<String> subsets) {
        StringBuilder subsetInstanceToCopy = new StringBuilder(
                """
                        {
                          "SchemaVersion": 1.1,
                          "Study": {
                            "Series": {
                                "
                                 """
        );

        subsetInstanceToCopy.append(imageSetId);

        subsetInstanceToCopy.append(
                """
                                ": {
                                "Instances": {
                        """
        );

        for (String subset : subsets) {
            subsetInstanceToCopy.append('"' + subset + "\": {},");
        }
        subsetInstanceToCopy.deleteCharAt(subsetInstanceToCopy.length() - 1);
        subsetInstanceToCopy.append("""
                         }
                       }
                    }
                  }
                }
                """);
        return subsetInstanceToCopy.toString();
    }
    // snippet-end:[medicalimaging.java2.copy_imageset.copiable_attributes]
}
