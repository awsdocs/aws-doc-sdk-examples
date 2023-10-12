//snippet-sourcedescription:[GetImageSet.java demonstrates how to retrieve information about an image set.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.get_imageset.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.GetImageSetRequest;
import software.amazon.awssdk.services.medicalimaging.model.GetImageSetResponse;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;

//snippet-end:[medicalimaging.java2.get_imageset.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetImageSet {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imagesetId> <versionId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imagesetId - The ID of the image set.\n" +
                "    versionId - The optional version ID of the image set.\n";

        if ((args.length != 2) && (args.length != 3)) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String imagesetId = args[1];
        String versionid = null;
        if (args.length == 3) {
            versionid = args[2];
        }

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        GetImageSetResponse getImageSetResponse = getMedicalImageSet(medicalImagingClient, datastoreId, imagesetId,
                versionid);


        System.out.println("The get image response is " + getImageSetResponse);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.get_imageset.main]
    public static GetImageSetResponse getMedicalImageSet(MedicalImagingClient medicalImagingClient,
                                                         String datastoreId,
                                                         String imagesetId,
                                                         String versionId) {
        try {
            GetImageSetRequest.Builder  getImageSetRequestBuilder = GetImageSetRequest.builder()
                    .datastoreId(datastoreId)
                    .imageSetId(imagesetId);

            if (versionId != null) {
                getImageSetRequestBuilder = getImageSetRequestBuilder.versionId(versionId);
            }

            return medicalImagingClient.getImageSet(getImageSetRequestBuilder.build());
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
//snippet-end:[medicalimaging.java2.get_imageset.main]
}
