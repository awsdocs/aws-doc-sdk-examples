//snippet-sourcedescription:[PutSubscriptionFilter.java demonstrates how to creates a CloudWatch logs subscription filter.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

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

package com.example.cloudwatch;
// snippet-start:[cloudwatch.java2.put_subscription_filter.complete]
// snippet-start:[cloudwatch.java2.put_subscription_filter.import]
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutSubscriptionFilterRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutSubscriptionFilterResponse;
// snippet-end:[cloudwatch.java2.put_subscription_filter.import]

/**
 * Creates a CloudWatch Logs subscription filter.
 */
// snippet-start:[cloudwatch.java2.put_subscription_filter.main]
public class PutSubscriptionFilter {
    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply:\n" +
                        "* a filter name\n" +
                        "* filter pattern\n" +
                        "* log group name\n" +
                        "* lambda function arn\n\n" +
                        "Ex: PutSubscriptionFilter <filter-name> \\\n" +
                        "                          <filter pattern> \\\n" +
                        "                          <log-group-name> \\\n" +
                        "                          <lambda-function-arn>\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filter = args[0];
        String pattern = args[1];
        String logGroup = args[2];
        String functionArn = args[3];

        try {
            CloudWatchLogsClient cwl = CloudWatchLogsClient.builder()
                    .build();

            PutSubscriptionFilterRequest request =
                    PutSubscriptionFilterRequest.builder()
                            .filterName(filter)
                            .filterPattern(pattern)
                            .logGroupName(logGroup)
                            .destinationArn(functionArn)
                            .build();

            PutSubscriptionFilterResponse response =
                    cwl.putSubscriptionFilter(request);

        } catch (CloudWatchLogsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.printf(
                "Successfully created CloudWatch logs subscription filter %s",
                filter);
    }
}
// snippet-end:[cloudwatch.java2.put_subscription_filter.main]
// snippet-end:[cloudwatch.java2.put_subscription_filter.complete]
