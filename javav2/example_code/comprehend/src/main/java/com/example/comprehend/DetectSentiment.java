// snippet-sourcedescription:[DetectSentiment demonstrates how to detect sentiments in the text.]
// snippet-service:[Amazon Comprehend]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Comprehend]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[6/3/2020]
// snippet-sourceauthor:[scmacdon AWS]


/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.example.comprehend;

//snippet-start:[comprehend.java2.detect_sentiment.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
//snippet-end:[comprehend.java2.detect_sentiment.import]


public class DetectSentiment {

    public static void main(String[] args) {

        String text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing.";
        Region region = Region.US_EAST_1;
        ComprehendClient comClient = ComprehendClient.builder()
                .region(region)
                .build();

        System.out.println("Calling DetectSentiment");
        detectSentiments(comClient, text);
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
        //snippet-end:[comprehend.java2.detect_sentiment.main]
   }
 }
