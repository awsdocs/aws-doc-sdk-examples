// snippet-sourcedescription:[DescribeCollection.java demonstrates how to retrieve the description of an Amazon Rekognition collection.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.describe_collection.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DescribeCollectionRequest;
import software.amazon.awssdk.services.rekognition.model.DescribeCollectionResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
// snippet-end:[rekognition.java2.describe_collection.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeCollection {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <collectionName>\n\n" +
                "Where:\n" +
                "   collectionName - The name of the Amazon Rekognition collection. \n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String collectionName = args[0];
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeColl(rekClient, collectionName);
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.describe_collection.main]
    public static void describeColl(RekognitionClient rekClient, String collectionName) {

    try {

        DescribeCollectionRequest describeCollectionRequest = DescribeCollectionRequest.builder()
             .collectionId(collectionName)
             .build();

        DescribeCollectionResponse describeCollectionResponse = rekClient.describeCollection(describeCollectionRequest);

         System.out.println("Collection Arn : " +
             describeCollectionResponse.collectionARN());
         System.out.println("Created : " +
             describeCollectionResponse.creationTimestamp().toString());

    } catch(RekognitionException e) {
        System.out.println(e.getMessage());
        System.exit(1);
    }
   }
    // snippet-end:[rekognition.java2.describe_collection.main]
}
