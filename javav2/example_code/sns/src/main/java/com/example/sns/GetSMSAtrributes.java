//snippet-sourcedescription:[GetSMSAtrributes.java demonstrates how to retrieve the default SMS type for Amazon SNS.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/6/2020]
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

package com.example.sns;

//snippet-start:[sns.java2.GetSMSAtrributes.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.GetSubscriptionAttributesRequest;
import software.amazon.awssdk.services.sns.model.GetSubscriptionAttributesResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import java.util.Iterator;
import java.util.Map;
//snippet-end:[sns.java2.GetSMSAtrributes.import]

public class GetSMSAtrributes {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "GetSMSAtrributes - retrieve your default SMS type for Amazon SNS.\n" +
                "Usage: GetSMSAtrributes <topicArn>\n\n" +
                "Where:\n" +
                "  topicArn - the arn of the topic from which to retrieve attributes.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        String topicArn = args[0];

        // Create an SnsClient object
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        getSNSAttrutes(snsClient, topicArn);
    }

    //snippet-start:[sns.java2.GetSMSAtrributes.main]
    public static void getSNSAttrutes(SnsClient snsClient,String topicArn ) {

        try {
            GetSubscriptionAttributesRequest request = GetSubscriptionAttributesRequest.builder()
                    .subscriptionArn(topicArn)
                    .build();

            // Get the Subscription attributes
            GetSubscriptionAttributesResponse res = snsClient.getSubscriptionAttributes(request);
            Map<String, String> map = res.attributes();

            // Iterate through the map
            Iterator iter = map.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                System.out.println("[Key] : " + entry.getKey() + " [Value] : " + entry.getValue());
            }

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n\nStatus was good");
        //snippet-end:[sns.java2.GetSMSAtrributes.main]
    }
}
