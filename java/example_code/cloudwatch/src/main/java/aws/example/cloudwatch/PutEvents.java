// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.PutEventsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutEventsRequestEntry;
import com.amazonaws.services.cloudwatchevents.model.PutEventsResult;

/**
 * Puts a sample CloudWatch event
 */
public class PutEvents {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a resource arn\n" +
                "Ex: PutEvents <resource-arn>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String resource_arn = args[0];

        final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();

        final String EVENT_DETAILS = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";

        PutEventsRequestEntry request_entry = new PutEventsRequestEntry()
                .withDetail(EVENT_DETAILS)
                .withDetailType("sampleSubmitted")
                .withResources(resource_arn)
                .withSource("aws-sdk-java-cloudwatch-example");

        PutEventsRequest request = new PutEventsRequest()
                .withEntries(request_entry);

        PutEventsResult response = cwe.putEvents(request);

        System.out.println("Successfully put CloudWatch event");
    }
}
