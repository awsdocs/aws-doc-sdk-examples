// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.medicalimaging;

// snippet-start:[medicalimaging.java2.copy_imageset.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;

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

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imageSetId> <latestVersionId> <destinationImageSetId> <destinationVersionId>\n" +
                "    <force> [copySubset ...]" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imageSetId - The ID of the image set.\n" +
                "    latestVersionId - The latest version ID of the image set.\n" +
                "    destinationImageSetId - The optional destination image set ID.\n" +
                "    destinationVersionId - The optional destination version ID.\n" +
                "    force - The optional (true/false) for force copy.\n" +
                "    copy_subset - One or more optional subsets to copy.\n";

        if ((args.length < 3) || (args.length == 5) || (args.length == 6)) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String imageSetId = args[1];
        String versionId = args[2];
        String destinationImageSetId = null;
        String destinationVersionId = null;
        boolean force = false;
        Vector<String> subsets = new Vector<>();

        if (args.length > 5) {
            destinationImageSetId = args[3];
            destinationVersionId = args[4];
            force = args[5].equals("true");
        }
        if (args.length > 6) {
            for (int i = 6; i < args.length; i++) {
                subsets.add(args[i]);
            }
        }

        Region region = Region.US_EAST_1;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String copiedImageSetId = copyMedicalImageSet(medicalImagingClient, datastoreId, imageSetId,
                versionId, destinationImageSetId, destinationVersionId, force, subsets);

        System.out.println("The copied image set ID is " + copiedImageSetId);
        medicalImagingClient.close();
    }

    // snippet-start:[medicalimaging.java2.copy_imageset.main]
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

            if (!subsets.isEmpty()) {
                StringBuilder subsetInstanceToCopy = new StringBuilder("""
                    {
                      "SchemaVersion": 1.1,
                      "Study": {
                        "Series": {
                            "
                             """);
                        subsetInstanceToCopy.append(imageSetId);
                subsetInstanceToCopy.append("""
                                ": {
                                "Instances": {   
                        """);

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
                copySourceImageSetInformation.dicomCopies(MetadataCopies.builder()
                        .copiableAttributes(subsetInstanceToCopy)
                        .build());
            }

            CopyImageSetInformation.Builder copyImageSetBuilder = CopyImageSetInformation.builder()
                    .sourceImageSet(copySourceImageSetInformation.build());

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
            System.exit(1);
        }

        return "";
    }
    // snippet-end:[medicalimaging.java2.copy_imageset.main]
}
