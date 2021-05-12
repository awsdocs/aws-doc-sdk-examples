//snippet-sourcedescription:[DeleteObject.java demonstrates how to delete an object within an AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediastore;

//snippet-start:[mediastore.java2.delete_object.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient;
import software.amazon.awssdk.services.mediastoredata.model.DeleteObjectRequest;
import software.amazon.awssdk.services.mediastoredata.model.MediaStoreDataException;
import java.net.URI;
import java.net.URISyntaxException;
//snippet-end:[mediastore.java2.delete_object.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteObject {

    public static void main(String[] args) throws URISyntaxException {

        final String USAGE = "\n" +
                "Usage: " +
                "DeleteObject  <completePath> <containerName>\n\n" +
                "Where:\n" +
                "  completePath - the path (including the container) of the item to delete.\n"+
                "  containerName - the name of the container.\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String completePath = args[0];
        String containerName = args[1];

        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));

        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();

        deleteMediaObject(mediaStoreData, completePath);
        mediaStoreData.close();
    }

    //snippet-start:[mediastore.java2.delete_object.main]
    public static void deleteMediaObject(MediaStoreDataClient mediaStoreData, String completePath){

        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .path(completePath)
                .build();

            mediaStoreData.deleteObject(deleteObjectRequest);

        } catch (MediaStoreDataException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
             System.exit(1);
    }
 }
    //snippet-end:[mediastore.java2.delete_object.main]

    private static String getEndpoint(String containerName){

        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
                .region(region)
                .build();

        DescribeContainerRequest containerRequest = DescribeContainerRequest.builder()
                .containerName(containerName)
                .build();

        DescribeContainerResponse response = mediaStoreClient.describeContainer(containerRequest);
        mediaStoreClient.close();
        return response.container().endpoint();
    }
}
