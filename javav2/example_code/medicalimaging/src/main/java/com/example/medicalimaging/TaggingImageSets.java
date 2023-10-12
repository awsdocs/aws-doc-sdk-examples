//snippet-sourcedescription:[GetImageSet.java demonstrates tagging, untagging and listing tags for an image set.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.tagging_imagesets.import]

//snippet-end:[medicalimaging.java2.tagging_imagesets.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.ListTagsForResourceResponse;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Collections;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class TaggingImageSets {

    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        // Set imageSetArn to the ARN of an actual image set.
        //snippet-start:[medicalimaging.java2.tagging_imagesets.datastore_arn]
        final String imageSetArn =
                "arn:aws:medical-imaging:us-east-1:123456789012:datastore/12345678901234567890123456789012/imageset/12345678901234567890123456789012";
        //snippet-end:[medicalimaging.java2.tagging_imagesets.datastore_arn]

        //snippet-start:[medicalimaging.java2.tagging_imagesets.tag]
        TagResource.tagMedicalImagingResource(medicalImagingClient, imageSetArn, ImmutableMap.of("Deployment", "Development"));
        //snippet-end:[medicalimaging.java2.tagging_imagesets.tag]

        //snippet-start:[medicalimaging.java2.tagging_imagesets.list]
        ListTagsForResourceResponse result = ListTagsForResource.listMedicalImagingResourceTags(medicalImagingClient, imageSetArn);
        if (result != null) {
            System.out.println("Tags for resource: " + result.tags());
        }
        //snippet-end:[medicalimaging.java2.tagging_imagesets.list]

        //snippet-start:[medicalimaging.java2.tagging_imagesets.untag]
        UntagResource.untagMedicalImagingResource(medicalImagingClient, imageSetArn, Collections.singletonList("Deployment"));
        //snippet-end:[medicalimaging.java2.tagging_imagesets.untag]

        medicalImagingClient.close();
    }
}
