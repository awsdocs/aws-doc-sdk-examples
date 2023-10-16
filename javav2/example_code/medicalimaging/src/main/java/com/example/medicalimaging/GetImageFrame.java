//snippet-sourcedescription:[GetImageSet.java demonstrates how to retrieve an image set image frame.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.get_image_frame.import]

//snippet-end:[medicalimaging.java2.get_image_frame.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.GetImageFrameRequest;
import software.amazon.awssdk.services.medicalimaging.model.ImageFrameInformation;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;

import java.nio.file.FileSystems;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetImageFrame {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <destinationPath> <datastoreId> <imagesetId> <imageFrameId>\n\n" +
                "Where:\n" +
                "    destinationPath - The destination path for the downloaded file.\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imagesetId - The ID of the image set.\n" +
                "    imageFrameId - The ID of the image frame.\n";


        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String destinationPath = args[0];
        String datastoreId = args[1];
        String imagesetId = args[2];
        String imageFrameId = args[3];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getMedicalImageSetFrame(medicalImagingClient, destinationPath,
                datastoreId, imagesetId, imageFrameId);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.get_image_frame.main]
    public static void getMedicalImageSetFrame(MedicalImagingClient medicalImagingClient,
                                               String destinationPath,
                                               String datastoreId,
                                               String imagesetId,
                                               String imageFrameId) {

        try {
            GetImageFrameRequest getImageSetMetadataRequest = GetImageFrameRequest.builder()
                    .datastoreId(datastoreId)
                    .imageSetId(imagesetId)
                    .imageFrameInformation(ImageFrameInformation.builder()
                            .imageFrameId(imageFrameId)
                            .build())
                    .build();
            medicalImagingClient.getImageFrame(getImageSetMetadataRequest,
                    FileSystems.getDefault().getPath(destinationPath));

            System.out.println("Image frame downloaded to " + destinationPath);
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
//snippet-end:[medicalimaging.java2.get_image_frame.main]
}
