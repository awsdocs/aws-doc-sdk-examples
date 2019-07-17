//snippet-sourcedescription:[DescribeSubscriptionFilters.java demonstrates how to get a list of CloudWatch subscription filters associated with a log group.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[cloudwatch.java2.describe_subscription_filters.complete]
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
// snippet-start:[cloudwatch.java2.describe_subscription_filters.import]
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeSubscriptionFiltersRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeSubscriptionFiltersResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.SubscriptionFilter;
// snippet-end:[cloudwatch.java2.describe_subscription_filters.import]

/**
 * Lists CloudWatch subscription filters associated with a log group.
 */
// snippet-start:[cloudwatch.java2.describe_subscription_filters.main]
public class DescribeSubscriptionFilters {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a log group name\n" +
            "Ex: DescribeSubscriptionFilters <log-group-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String log_group = args[0];

        CloudWatchLogsClient logs = CloudWatchLogsClient.builder().build();

        boolean done = false;
        String new_token = null;

        while(!done) {

        	DescribeSubscriptionFiltersResponse response;

        	if (new_token == null) {
        		DescribeSubscriptionFiltersRequest request =
                        DescribeSubscriptionFiltersRequest.builder()
                        .logGroupName(log_group)
                        .limit(1).build();

                    response = logs.describeSubscriptionFilters(request);
        	}
        	else {
        		DescribeSubscriptionFiltersRequest request =
        				DescribeSubscriptionFiltersRequest.builder()
                        .nextToken(new_token)
                        .logGroupName(log_group)
                        .limit(1).build();

                    response = logs.describeSubscriptionFilters(request);
        	}


            for(SubscriptionFilter filter : response.subscriptionFilters()) {
                System.out.printf(
                    "Retrieved filter with name %s, " +
                    "pattern %s " +
                    "and destination arn %s",
                    filter.filterName(),
                    filter.filterPattern(),
                    filter.destinationArn());
                System.out.println("");
            }

            if(response.nextToken() == null) {
                done = true;
            }
            else {
            	new_token = response.nextToken();
            }
        }
    }
}
// snippet-end:[cloudwatch.java2.describe_subscription_filters.main]
// snippet-end:[cloudwatch.java2.describe_subscription_filters.complete]