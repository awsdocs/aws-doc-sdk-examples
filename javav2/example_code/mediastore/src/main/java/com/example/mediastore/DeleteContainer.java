//snippet-sourcedescription:[DeleteContainer.java demonstrates how to delete a given AWS Elemental MediaStore container.]
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

//snippet-start:[mediastore.java2.delete_container.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DeleteContainerRequest;
import software.amazon.awssdk.services.mediastore.model.MediaStoreException;
//snippet-end:[mediastore.java2.delete_container.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteContainer {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "DeleteContainer <containerName>\n\n" +
                "Where:\n" +
                "  containerName - The name of the container to delete.\n";

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

        deleteMediaContainer(mediaStoreClient, containerName);
        mediaStoreClient.close();
    }

    //snippet-start:[mediastore.java2.delete_container.main]
    public static void deleteMediaContainer(MediaStoreClient mediaStoreClient, String containerName) {

     try{
        DeleteContainerRequest deleteContainerRequest = DeleteContainerRequest.builder()
            .containerName(containerName)
            .build();

        mediaStoreClient.deleteContainer(deleteContainerRequest);

    } catch (MediaStoreException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
  }
    //snippet-end:[mediastore.java2.delete_container.main]
}
