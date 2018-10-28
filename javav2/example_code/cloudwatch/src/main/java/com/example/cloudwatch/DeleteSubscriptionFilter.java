//snippet-sourcedescription:[DeleteSubscriptionFilter.java demonstrates how to delete CloudWatch log subscription filters.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;
import software.amazon.awssdk.services.cloudwatchlogs.model.DeleteSubscriptionFilterRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DeleteSubscriptionFilterResponse;

/**
 * Deletes a CloudWatch Logs subscription filter.
 */
public class DeleteSubscriptionFilter {
    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a filter name and log group name\n" +
            "Ex: DeleteSubscriptionFilter <filter-name> <log-group-name>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filter = args[0];
        String log_group = args[1];

        CloudWatchLogsClient logs = CloudWatchLogsClient.builder().build();

        DeleteSubscriptionFilterRequest request =
            DeleteSubscriptionFilterRequest.builder()
                .filterName(filter)
                .logGroupName(log_group).build();

        DeleteSubscriptionFilterResponse response =
                logs.deleteSubscriptionFilter(request);
    	System.out.printf(
                "Successfully deleted CloudWatch logs subscription filter %s",
                filter);
    }
}
