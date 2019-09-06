//snippet-sourcedescription:[SubscribeTextSMS.java demonstrates how to send a confirmation message as a text message.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[sns.java2.SubscribeTextSMS.complete]
package com.example.sns;

//snippet-start:[sns.java2.SubscribeTextSMS.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
//snippet-end:[sns.java2.SubscribeTextSMS.import]

public class SubscribeTextSMS {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "SubscribeTextSMS - send a confirmation message as a text message.\n" +
                "Usage: SubscribeTextSMS <topicArn> <phoneNumber>\n\n" +
                "Where:\n" +
                "  topicArn - the arn of the topic to subscribe.\n\n" +
                "  phoneNumber - phone number to look up. Example: +1XXX5550100\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        //snippet-start:[sns.java2.SubscribeTextSMS.main]
        String topicArn = args[0];
        String phoneNumber = args[1];

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("sms")
                .endpoint(phoneNumber)
                .returnSubscriptionArn(true)
                .topicArn(topicArn)
                .build();

        SubscribeResponse result = snsClient.subscribe(request);

        System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status was " + result.sdkHttpResponse().statusCode());
        //snippet-end:[sns.java2.SubscribeTextSMS.main]
    }
}
//snippet-end:[sns.java2.SubscribeTextSMS.complete]