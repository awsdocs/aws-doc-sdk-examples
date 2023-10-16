//snippet-sourcedescription:[GetImageSet.java demonstrates how to copy an image set.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.copy_imageset.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;

//snippet-end:[medicalimaging.java2.copy_imageset.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CopyImageSet {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imageSetId> <latestVersionId> <destinationImageSetId> <destinationVersionId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imageSetId - The ID of the image set.\n" +
                "    latestVersionId - The latest version ID of the image set.\n" +
                "    destinationImageSetId - The optional destination image set ID.\n" +
                "    destinationVersionId - The optional destination version ID.\n";

        if ((args.length != 3) && (args.length != 5)) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String imageSetId = args[1];
        String versionId = args[2];
        String destinationImageSetId = null;
        String destinationVersionId = null;

        if (args.length == 5) {
            destinationImageSetId = args[3];
            destinationVersionId = args[4];
        }

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String copiedImageSetId = copyMedicalImageSet(medicalImagingClient, datastoreId, imageSetId,
                versionId, destinationImageSetId, destinationVersionId);

        System.out.println("The copied image set ID is " + copiedImageSetId);
        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.copy_imageset.main]
    public static String copyMedicalImageSet(MedicalImagingClient medicalImagingClient,
                                             String datastoreId,
                                             String imageSetId,
                                             String latestVersionId,
                                             String destinationImageSetId,
                                             String destinationVersionId) {

        try {
            CopySourceImageSetInformation copySourceImageSetInformation = CopySourceImageSetInformation.builder()
                    .latestVersionId(latestVersionId)
                    .build();

            CopyImageSetInformation.Builder copyImageSetBuilder = CopyImageSetInformation.builder()
                    .sourceImageSet(copySourceImageSetInformation);

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
                    .build();

            CopyImageSetResponse response = medicalImagingClient.copyImageSet(copyImageSetRequest);

            return response.destinationImageSetProperties().imageSetId();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }
//snippet-end:[medicalimaging.java2.copy_imageset.main]
}
