//snippet-sourcedescription:[GetImageSet.java demonstrates how to retrieve the versions for an image set.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.list_imageset_versions.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.ImageSetProperties;
import software.amazon.awssdk.services.medicalimaging.model.ListImageSetVersionsRequest;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.medicalimaging.paginators.ListImageSetVersionsIterable;

import java.util.ArrayList;
import java.util.List;

//snippet-end:[medicalimaging.java2.list_imageset_versions.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListImageSetVersions {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imagesetId>\n\n" +
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

        List<ImageSetProperties> imageSetProperties = listMedicalImageSetVersions(medicalImagingClient, datastoreId, imagesetId);


        System.out.println("The image set versions are " + imageSetProperties);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.list_imageset_versions.main]
    public static List<ImageSetProperties> listMedicalImageSetVersions(MedicalImagingClient medicalImagingClient,
                                                                       String datastoreId,
                                                                       String imagesetId) {
        try {
            ListImageSetVersionsRequest getImageSetRequest = ListImageSetVersionsRequest.builder()
                    .datastoreId(datastoreId)
                    .imageSetId(imagesetId)
                    .build();

            ListImageSetVersionsIterable responses = medicalImagingClient.listImageSetVersionsPaginator(getImageSetRequest);
            List<ImageSetProperties> imageSetProperties = new ArrayList<>();
            responses.stream().forEach(response -> imageSetProperties.addAll(response.imageSetPropertiesList()));

            return imageSetProperties;
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
//snippet-end:[medicalimaging.java2.list_imageset_versions.main]
}
