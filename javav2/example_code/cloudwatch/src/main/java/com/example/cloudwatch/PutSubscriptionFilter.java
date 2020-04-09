//snippet-sourcedescription:[PutSubscriptionFilter.java demonstrates how to creates a CloudWatch logs subscription filter.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

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

package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_subscription_filter.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutSubscriptionFilterRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutSubscriptionFilterResponse;
// snippet-end:[cloudwatch.java2.put_subscription_filter.import]

/**
 * Creates a CloudWatch Logs subscription filter.
 */

public class PutSubscriptionFilter {
    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply:\n" +
                        "* a filter name\n" +
                        "* filter pattern\n" +
                        "* log group name\n" +
                        "* a role arn \n" +
                        "* lambda function arn\n\n" +
                        "Ex: PutSubscriptionFilter <filter-name> \\\n" +
                        "                          <filter pattern> \\\n" +
                        "                          <log-group-name> \\\n" +
                        "                          <role-arn> \\\n" +
                        "                          <lambda-function-arn>\n";

        if (args.length != 5) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filter = args[0];
        String pattern = args[1];
        String logGroup = args[2];
        String roleArn = args[3];
        String functionArn = args[4];

        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cwl = CloudWatchLogsClient.builder()
                .region(region)
                .build();

        putSubFilters(cwl, filter, pattern, logGroup, roleArn, functionArn ) ;
    }

    // snippet-start:[cloudwatch.java2.put_subscription_filter.main]
    public static void putSubFilters(CloudWatchLogsClient cwl,
                                     String filter,
                                     String pattern,
                                     String logGroup,
                                     String roleArn,
                                     String functionArn) {

        try {
            PutSubscriptionFilterRequest request =
                    PutSubscriptionFilterRequest.builder()
                            .filterName(filter)
                            .filterPattern(pattern)
                            .roleArn(roleArn)
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
