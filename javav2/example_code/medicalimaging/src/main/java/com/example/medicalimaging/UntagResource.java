//snippet-sourcedescription:[GetImageSet.java demonstrates how to remove tags from a resource.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.untag_resource.import]

//snippet-end:[medicalimaging.java2.untag_resource.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.medicalimaging.model.UntagResourceRequest;

import java.util.Collection;
import java.util.Collections;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UntagResource {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <resourceArn>\n\n" +
                "Where:\n" +
                "    resourceArn - The Amazon Resource Name (ARN) of the resource.\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String resourceArn = args[0];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        untagMedicalImagingResource(medicalImagingClient, resourceArn, Collections.singletonList("Deployment"));


        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.untag_resource.main]
    public static void untagMedicalImagingResource(MedicalImagingClient medicalImagingClient,
                                                   String resourceArn,
                                                   Collection<String> tagKeys) {
        try {
            UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                    .resourceArn(resourceArn)
                    .tagKeys(tagKeys)
                    .build();

            medicalImagingClient.untagResource(untagResourceRequest);

            System.out.println("Tags have been removed from the resource.");
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
//snippet-end:[medicalimaging.java2.untag_resource.main]
}
