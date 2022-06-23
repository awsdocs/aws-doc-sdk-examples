//snippet-sourcedescription:[CreateContainer.java demonstrates how to create an AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediastore;

//snippet-start:[mediastore.java2.create_container.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.model.CreateContainerRequest;
import software.amazon.awssdk.services.mediastore.model.CreateContainerResponse;
import software.amazon.awssdk.services.mediastore.model.MediaStoreException;
//snippet-end:[mediastore.java2.create_container.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateContainer {

    public static long sleepTime = 10;

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <containerName>\n\n" +
                "Where:\n" +
                "   containerName - The name of the container to create.\n";

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

        createMediaContainer(mediaStoreClient, containerName);
        mediaStoreClient.close();
    }

    //snippet-start:[mediastore.java2.create_container.main]
    public static void createMediaContainer( MediaStoreClient mediaStoreClient, String containerName) {

        try {
            CreateContainerRequest containerRequest = CreateContainerRequest.builder()
                .containerName(containerName)
                .build();

            CreateContainerResponse containerResponse = mediaStoreClient.createContainer(containerRequest);
            String status = containerResponse.container().status().toString();

            // Wait unitl the container is in an active state
            while (!status.equalsIgnoreCase("Active")){
                status = DescribeContainer.checkContainer(mediaStoreClient, containerName);
                System.out.println("Status - "+ status);
                Thread.sleep(sleepTime * 1000);
                }

            System.out.println("The container ARN value is "+containerResponse.container().arn());
            System.out.println("Finished ");

        } catch (MediaStoreException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[mediastore.java2.create_container.main]
}
