//snippet-sourcedescription:[GetImageSet.java demonstrates how to delete an image set.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.delete_imageset.import]

//snippet-end:[medicalimaging.java2.delete_imageset.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DeleteImageSetRequest;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteImageSet {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imagesetId> <versionId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imagesetId - The ID of the image set.\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String imagesetId = args[1];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        deleteMedicalImageSet(medicalImagingClient, datastoreId, imagesetId);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.delete_imageset.main]
    public static void deleteMedicalImageSet(MedicalImagingClient medicalImagingClient,
                                             String datastoreId,
                                             String imagesetId) {
        try {
            DeleteImageSetRequest deleteImageSetRequest = DeleteImageSetRequest.builder()
                    .datastoreId(datastoreId)
                    .imageSetId(imagesetId)
                    .build();

            medicalImagingClient.deleteImageSet(deleteImageSetRequest);

            System.out.println("The image set was deleted.");
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
//snippet-end:[medicalimaging.java2.delete_imageset.main]
}
