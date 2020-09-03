//snippet-sourcedescription:[CreateContainer.java demonstrates how to create an AWS Elemental MediaStore container.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaStore]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/1/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.mediastore;

//snippet-start:[mediastore.java2.create_container.import]
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.model.CreateContainerRequest;
import software.amazon.awssdk.services.mediastore.model.CreateContainerResponse;
import software.amazon.awssdk.services.mediastore.model.MediaStoreException;
//snippet-end:[mediastore.java2.create_container.import]

public class CreateContainer {

    public static long sleepTime = 10;

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "CreateContainer - Create an AWS Elemental MediaStore container.\n\n" +
                "Usage: CreateContainer <containerName>\n\n" +
                "Where:\n" +
                "  containerName - The name of the container to create.\n";

         if (args.length < 1) {
            System.out.println(USAGE);
             System.exit(1);
        }

        /* Read the name from command args */
        final String containerName = args[0];

        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
                .region(region)
                .build();

        createMediaContainer(mediaStoreClient, containerName);
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

            System.out.println("The container Amazon Resource Name (ARN) value is "+containerResponse.container().arn());
            System.out.println("Finished ");

        } catch (MediaStoreException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[mediastore.java2.create_container.main]
}
