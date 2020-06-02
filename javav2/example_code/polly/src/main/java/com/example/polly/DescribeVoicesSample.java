// snippet-sourcedescription:[DescribeVoicesSample Produces a list of all voices available for use when requesting speech synthesis with Amazon Polly..]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[5/7/2020]
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

package com.example.polly;

// snippet-start:[polly.java2.describe_voice.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.PollyException;
import software.amazon.awssdk.services.polly.model.Voice;
import java.util.Collection;
import java.util.Iterator;
// snippet-end:[polly.java2.describe_voice.import]


public class DescribeVoicesSample {

    public static void main(String args[]) {

        PollyClient polly = PollyClient.builder()
                .region(Region.US_WEST_2)
                .build();

        describeVoice(polly) ;
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
        // snippet-end:[polly.java2.describe_voice.main]
    }
}
