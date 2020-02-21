//snippet-sourcedescription:[PutEvents.java demonstrates how to put a sample CloudWatch event.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[cloudwatch.java2.put_events.complete]
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
package com.example.cloudwatch;
// snippet-start:[cloudwatch.java2.put_events.import]
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutEventsRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.cloudwatchevents.model.PutEventsResponse;
// snippet-end:[cloudwatch.java2.put_events.import]

/**
 * Puts a sample CloudWatch event
 */
public class PutEvents {
    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a resource arn\n" +
                        "Ex: PutEvents <resource-arn>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String resourceArn = args[0];

        // snippet-start:[cloudwatch.java2.put_events.main]
        CloudWatchEventsClient cwe =
                CloudWatchEventsClient.builder().build();

        try {

            final String EVENT_DETAILS =
                "{ \"key1\": \"value1\", \"key2\": \"value2\" }";

            PutEventsRequestEntry requestEntry = PutEventsRequestEntry.builder()
                .detail(EVENT_DETAILS)
                .detailType("sampleSubmitted")
                .resources(resourceArn)
                .source("aws-sdk-java-cloudwatch-example").build();

            PutEventsRequest request = PutEventsRequest.builder()
                .entries(requestEntry).build();

            PutEventsResponse response = cwe.putEvents(request);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[cloudwatch.java2.put_events.main]
        System.out.println("Successfully put CloudWatch event");
    }
}
// snippet-end:[cloudwatch.java2.put_events.complete]
