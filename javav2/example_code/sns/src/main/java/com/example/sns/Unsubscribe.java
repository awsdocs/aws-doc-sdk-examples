//snippet-sourcedescription:[Unsubscribe.java demonstrates how to remove an SNS subscription.]
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
//snippet-start:[sns.java2.Unsubscribe.complete]

package com.example.sns;

//snippet-start:[sns.java2.Unsubscribe.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;
//snippet-end:[sns.java2.Unsubscribe.import]

public class Unsubscribe {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Unsubscribe - removes a subscription from a topic \n" +
                "Usage: Unsubscribe <subscriptionToken>\n\n" +
                "Where:\n" +
                "  subscriptionToken - endpoint token from Subscribe action.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        //snippet-start:[sns.java2.Unsubscribe.main]
        String subscriptionToken = args[0];

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        UnsubscribeRequest request = UnsubscribeRequest.builder()
                .subscriptionArn(subscriptionToken)
                .build();

        UnsubscribeResponse result = snsClient.unsubscribe(request);

        System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode()
                + "\n\nSubscription was removed for " + request.subscriptionArn());
        //snippet-end:[sns.java2.Unsubscribe.main]
    }
}
//snippet-end:[sns.java2.Unsubscribe.complete]