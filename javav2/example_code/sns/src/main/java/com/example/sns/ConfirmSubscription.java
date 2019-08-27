//snippet-sourcedescription:[ConfirmSubscription.java demonstrates how to retrieve the defaults for an AWS SNS Topic.]
//snippet-keyword:[Java]
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
//snippet-start:[sns.java2.ConfirmSubscription.complete]
package com.example.sns;

//snippet-start:[sns.java2.ConfirmSubscription.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ConfirmSubscriptionRequest;
import software.amazon.awssdk.services.sns.model.ConfirmSubscriptionResponse;
//snippet-end:[sns.java2.ConfirmSubscription.import]

public class ConfirmSubscription {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "ConfirmSubscription - confirm a subscription to an sns topic\n" +
                "Usage: ConfirmSubscription <subscriptionToken> <topicArn>\n\n" +
                "Where:\n" +
                "  subscriptionToken - endpoint token from Subscribe action.\n\n" +
                "  topicArn - the arn of the topic to delete.\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[sns.java2.ConfirmSubscription.main]
        String subscriptionToken = args[0];
        String topicArn = args[1];

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        ConfirmSubscriptionRequest request = ConfirmSubscriptionRequest.builder()
                .token(subscriptionToken)
                .topicArn(topicArn)
                .build();

        ConfirmSubscriptionResponse result = snsClient.confirmSubscription(request);

        System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nSubscription Arn: \n\n" + result.subscriptionArn());
        //snippet-end:[sns.java2.ConfirmSubscription.main]
    }
}
//snippet-end:[sns.java2.ConfirmSubscription.complete]

