//snippet-sourcedescription:[ListItems.java demonstrates how to list objects and folders within an AWS Elemental MediaStore container.]
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

//snippet-start:[mediastore.java2.list_items.import]
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient ;
import software.amazon.awssdk.services.mediastoredata.model.ListItemsResponse;
import software.amazon.awssdk.services.mediastoredata.model.ListItemsRequest;
import software.amazon.awssdk.services.mediastoredata.model.Item;
import software.amazon.awssdk.services.mediastoredata.model.MediaStoreDataException;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
//snippet-end:[mediastore.java2.list_items.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListItems {

    public static void main(String[] args) throws URISyntaxException {

        final String usage = "\n" +
            "Usage: " +
            "ListItems <containerName> <completePath>\n\n" +
            "Where:\n" +
            "  containerName - The name of the container.\n" +
            "  completePath - The path in the container where the objects are located (for example, /Videos5).";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String containerName = args[0];
        String completePath = args[1];
        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));

        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
            .endpointOverride(uri)
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listAllItems(mediaStoreData, completePath);
        mediaStoreData.close();
    }

    //snippet-start:[mediastore.java2.list_items.main]
    public static void listAllItems(MediaStoreDataClient mediaStoreData, String completePath) {

        try {
            ListItemsRequest itemsRequest = ListItemsRequest.builder()
                .path(completePath)
                .build();

            ListItemsResponse itemsResponse = mediaStoreData.listItems(itemsRequest);
            boolean hasItems = itemsResponse.hasItems();
            if (hasItems) {
                List<Item> items = itemsResponse.items();
                for (Item item : items) {
                    System.out.println("Item name is: " + item.name());
                    System.out.println("Content type is:  " + item.contentType());
                }
            } else {
                System.out.println("There are no items");
            }

        } catch (MediaStoreDataException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private static String getEndpoint(String containerName){

        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
            .region(region)
            .build();

        DescribeContainerRequest containerRequest = DescribeContainerRequest.builder()
            .containerName(containerName)
            .build();

        DescribeContainerResponse response = mediaStoreClient.describeContainer(containerRequest);
        return response.container().endpoint();
    }
    //snippet-end:[mediastore.java2.list_items.main]
}
