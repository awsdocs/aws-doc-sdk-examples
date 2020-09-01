//snippet-sourcedescription:[ListItems.java demonstrates how to list objects and folders within an AWS Elemental MediaStore container.]
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

//snippet-start:[mediastore.java2.list_items.import]
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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

public class ListItems {

    public static void main(String[] args) throws URISyntaxException {

        final String USAGE = "\n" +
                "To run this example, supply the name of a container that contains items \n" +
                "\n" +
                "Ex: ListItems <container-name> \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String containerName = args[0];

        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));

        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();

        listAllItems(mediaStoreData, containerName);
    }

    //snippet-start:[mediastore.java2.list_items.main]
    public static void listAllItems(MediaStoreDataClient mediaStoreData, String containerName) {

       try {
            ListItemsRequest itemsRequest = ListItemsRequest.builder()
                .path(containerName+"/")
                .build();

            ListItemsResponse itemsResponse = mediaStoreData.listItems(itemsRequest);

            Boolean hasItems = itemsResponse.hasItems();
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
    //snippet-end:[mediastore.java2.list_items.main]
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
}
