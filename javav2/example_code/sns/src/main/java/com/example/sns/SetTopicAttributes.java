//snippet-sourcedescription:[SetTopicAttributes.java demonstrates how to update the defaults for an AWS SNS Topic.]
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
//snippet-start:[sns.java2.SetTopicAttributes.complete]

package com.example.sns;

//snippet-start:[sns.java2.SetTopicAttributes.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SetTopicAttributesRequest;
import software.amazon.awssdk.services.sns.model.SetTopicAttributesResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.SetTopicAttributes.import]

public class SetTopicAttributes {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "SetTopicAttributes - update defaults from a topic.\n" +
                "Usage: SetTopicAttributes <attribute> <topicArn> <value>\n\n" +
                "Where:\n" +
                "  attribute - Attribute action to use. Valid parameters : Policy | DisplayName | DeliveryPolicy .\n" +
                "  topicArn - The arn of the topic to update. \n" +
                "  value - New value for the attribute.\n\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        //snippet-start:[sns.java2.SetTopicAttributes.main]
        String attribute = args[0];
        String topicArn = args[1];
        String value = args[2];

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try {

            SetTopicAttributesRequest request = SetTopicAttributesRequest.builder()
                .attributeName(attribute)
                .attributeValue(value)
                .topicArn(topicArn)
                .build();

            SetTopicAttributesResponse result = snsClient.setTopicAttributes(request);

            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nTopic " + request.topicArn()
                + " updated " + request.attributeName() + " to " + request.attributeValue());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[sns.java2.SetTopicAttributes.main]
    }
}
//snippet-end:[sns.java2.SetTopicAttributes.complete]
