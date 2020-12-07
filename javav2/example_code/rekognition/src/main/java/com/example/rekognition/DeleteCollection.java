// snippet-sourcedescription:[DeleteCollection.java demonstrates how to delete an Amazon Rekognition collection.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11-03-2020]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.delete_collection.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DeleteCollectionRequest;
import software.amazon.awssdk.services.rekognition.model.DeleteCollectionResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
// snippet-end:[rekognition.java2.delete_collection.import]

public class DeleteCollection {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "DeleteCollection <collectionId> \n\n" +
                "Where:\n" +
                "  collectionId - the id of the collection to delete. \n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String collectionId = args[0];
        Region region = Region.US_EAST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        System.out.println("Deleting collection: " +
                collectionId);

        deleteMyCollection(rekClient, collectionId);
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.delete_collection.main]
    public static void deleteMyCollection(RekognitionClient rekClient,String collectionId ) {

    try {

        DeleteCollectionRequest deleteCollectionRequest = DeleteCollectionRequest.builder()
                .collectionId(collectionId)
                .build();

        DeleteCollectionResponse deleteCollectionResponse = rekClient.deleteCollection(deleteCollectionRequest);
        System.out.println(collectionId + ": " + deleteCollectionResponse.statusCode().toString());

    } catch(RekognitionException e) {
        System.out.println(e.getMessage());
        System.exit(1);
    }
  }
    // snippet-end:[rekognition.java2.delete_collection.main]
 }

