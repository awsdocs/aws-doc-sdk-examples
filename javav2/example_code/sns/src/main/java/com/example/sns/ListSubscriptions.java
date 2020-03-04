//snippet-sourcedescription:[ListSubscriptions.java demonstrates how to list existing Amazon SNS subscriptions.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
//snippet-sourceauthor:[scmacdon AWS]

/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[sns.java2.ListSubscriptions.complete]
package com.example.sns;

//snippet-start:[sns.java2.ListSubscriptions.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsRequest;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.ListSubscriptions.import]

public class ListSubscriptions {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "ListSubscriptions - returns a list of Amazon SNS subscriptions.\n" +
                "Usage: ListSubscriptions \n\n";

        //snippet-start:[sns.java2.ListSubscriptions.main]
        try {

            SnsClient snsClient = SnsClient.builder()
                    .region(Region.US_WEST_2)
                    .build();

            ListSubscriptionsRequest request = ListSubscriptionsRequest.builder()
                    .build();

            ListSubscriptionsResponse result = snsClient.listSubscriptions(request);
            System.out.println(result.subscriptions());

        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[sns.java2.ListSubscriptions.main]
    }
}
//snippet-end:[sns.java2.ListSubscriptions.complete]
