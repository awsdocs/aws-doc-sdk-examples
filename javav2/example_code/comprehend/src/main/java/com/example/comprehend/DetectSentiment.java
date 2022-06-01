// snippet-sourcedescription:[DetectSentiment demonstrates how to detect sentiments in the text.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Comprehend]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.comprehend;

//snippet-start:[comprehend.java2.detect_sentiment.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
//snippet-end:[comprehend.java2.detect_sentiment.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectSentiment {

    public static void main(String[] args) {

        String text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing.";
        Region region = Region.US_EAST_1;
        ComprehendClient comClient = ComprehendClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        System.out.println("Calling DetectSentiment");
        detectSentiments(comClient, text);
        comClient.close();
    }

    //snippet-start:[comprehend.java2.detect_sentiment.main]
    public static void detectSentiments(ComprehendClient comClient, String text){

    try {
        DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
                .text(text)
                .languageCode("en")
                .build();

        DetectSentimentResponse detectSentimentResult = comClient.detectSentiment(detectSentimentRequest);
        System.out.println("The Neutral value is " +detectSentimentResult.sentimentScore().neutral() );

     } catch (ComprehendException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
   }
    //snippet-end:[comprehend.java2.detect_sentiment.main]
 }