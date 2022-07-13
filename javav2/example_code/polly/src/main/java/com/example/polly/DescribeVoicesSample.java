// snippet-sourcedescription:[DescribeVoicesSample Produces a list of all voices available for use when requesting speech synthesis with Amazon Polly..]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.polly;

// snippet-start:[polly.java2.describe_voice.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.PollyException;
import software.amazon.awssdk.services.polly.model.Voice;
import java.util.Collection;
import java.util.Iterator;
// snippet-end:[polly.java2.describe_voice.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeVoicesSample {

    public static void main(String args[]) {

        PollyClient polly = PollyClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeVoice(polly) ;
        polly.close();
    }

    // snippet-start:[polly.java2.describe_voice.main]
    public static void describeVoice(PollyClient polly) {

       try {
        DescribeVoicesRequest voicesRequest = DescribeVoicesRequest.builder()
                .languageCode("en-US")
                .build();

         DescribeVoicesResponse enUsVoicesResult = polly.describeVoices(voicesRequest);
         Collection<Voice> voices = enUsVoicesResult.voices();
         Iterator<Voice> iterator = voices.iterator();

           // Get each voice
           while (iterator.hasNext()) {
               Voice myVoice = iterator.next();
               System.out.println("The ID of the voice is " +myVoice.id());
               System.out.println("The gender of the voice is " + myVoice.gender());
           }

        } catch (PollyException e) {
            System.err.println("Exception caught: " + e);
           System.exit(1);
        }
    }
    // snippet-end:[polly.java2.describe_voice.main]
}
