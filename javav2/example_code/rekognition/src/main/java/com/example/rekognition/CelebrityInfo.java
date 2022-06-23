// snippet-sourcedescription:[CelebrityInfo.java demonstrates how to get information about a detected celebrity.]
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
// snippet-start:[rekognition.java2.celebrityInfo.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.GetCelebrityInfoRequest;
import software.amazon.awssdk.services.rekognition.model.GetCelebrityInfoResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
// snippet-end:[rekognition.java2.celebrityInfo.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CelebrityInfo {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <id>\n\n" +
                "Where:\n" +
                "   id - The id value of the celebrity. You can use the RecognizeCelebrities example to get the ID value. \n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String id = args[0];
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getCelebrityInfo(rekClient, id);
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.celebrityInfo.main]
    public static void getCelebrityInfo(RekognitionClient rekClient, String id ) {

       try {
        GetCelebrityInfoRequest info = GetCelebrityInfoRequest.builder()
                .id(id)
                .build();

        GetCelebrityInfoResponse response = rekClient.getCelebrityInfo(info);

        // Display celebrity information.
        System.out.println("celebrity name: " + response.name());
        System.out.println("Further information (if available):");
        for (String url: response.urls()){
            System.out.println(url);
        }

       } catch (RekognitionException e) {
           System.out.println(e.getMessage());
           System.exit(1);
       }
    }
    // snippet-end:[rekognition.java2.celebrityInfo.main]
}
