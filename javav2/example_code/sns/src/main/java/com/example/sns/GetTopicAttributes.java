//snippet-sourcedescription:[GetTopicAttributes.java demonstrates how to retrieve the defaults for an AWS SNS Topic.]
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
//snippet-start:[sns.java2.GetTopicAttributes.complete]
package com.example.sns;

//snippet-start:[sns.java2.GetTopicAttributes.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.GetTopicAttributesRequest;
import software.amazon.awssdk.services.sns.model.GetTopicAttributesResponse;
//snippet-end:[sns.java2.GetTopicAttributes.import]

public class GetTopicAttributes {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "GetTopicAttributes - get attributes for an sns topic\n" +
                "Usage: GetTopicAttributes <topicArn>\n\n" +
                "Where:\n" +
                "  topicArn - the arn of the topic to look up.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        //snippet-start:[sns.java2.GetTopicAttributes.main]
        String topicArn = args[0];

        System.out.println("Getting attributes for a topic with name: " + topicArn);

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        GetTopicAttributesRequest request = GetTopicAttributesRequest.builder()
                .topicArn(topicArn)
                .build();

        GetTopicAttributesResponse result = snsClient.getTopicAttributes(request);

        System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nAttributes: \n\n" + result.attributes());
        //snippet-end:[sns.java2.GetTopicAttributes.main]
    }
}
//snippet-end:[sns.java2.GetTopicAttributes.complete]

