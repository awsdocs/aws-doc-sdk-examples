//snippet-sourcedescription:[DescribeContainer.java demonstrates how to describe a given AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Elemental MediaStore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediastore;

//snippet-start:[mediastore.java2.describe_container.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import software.amazon.awssdk.services.mediastore.model.MediaStoreException;
//snippet-end:[mediastore.java2.describe_container.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeContainer {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <containerName>\n\n" +
            "Where:\n" +
            "   containerName - The name of the container to describe.\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String containerName = args[0];
        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        System.out.println("Status is "+ checkContainer(mediaStoreClient, containerName));
        mediaStoreClient.close();
    }

    //snippet-start:[mediastore.java2.describe_container.main]
    public static String checkContainer(MediaStoreClient mediaStoreClient, String containerName) {

        try{
            DescribeContainerRequest describeContainerRequest = DescribeContainerRequest.builder()
                .containerName(containerName)
                .build();

            DescribeContainerResponse containerResponse = mediaStoreClient.describeContainer(describeContainerRequest);
            System.out.println("The container name is "+containerResponse.container().name());
            System.out.println("The container ARN is "+containerResponse.container().arn());
            return containerResponse.container().status().toString();

        } catch (MediaStoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return"";
    }
    //snippet-end:[mediastore.java2.describe_container.main]
}
