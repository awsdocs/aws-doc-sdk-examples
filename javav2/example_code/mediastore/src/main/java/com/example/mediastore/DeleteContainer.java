//snippet-sourcedescription:[DeleteContainer.java demonstrates how to delete a given AWS Elemental MediaStore container.]
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

//snippet-start:[mediastore.java2.delete_container.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DeleteContainerRequest;
import software.amazon.awssdk.services.mediastore.model.MediaStoreException;
//snippet-end:[mediastore.java2.delete_container.import]

public class DeleteContainer {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of a container \n" +
                "\n" +
                "Ex: DeleteContainer <container-name>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String containerName = args[0];

        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
                .region(region)
                .build();

        deleteMediaContainer(mediaStoreClient, containerName);
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
