/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package cloudwatch.src.main.java.aws.example.cloudwatch;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.PutSubscriptionFilterRequest;
import com.amazonaws.services.logs.model.PutSubscriptionFilterResult;

/**
 * Creates a CloudWatch Logs subscription filter.
 */
public class PutSubscriptionFilter {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a filter name, filter pattern, log group name and lambda function arn\n" +
            "Ex: PutSubscriptionFilter <filter-name> <filter pattern> <log-group-name> <lambda-function-arn>\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filterName = args[0];
        String filterPattern = args[1];
        String logGroupName = args[2];
        String lambdaFunctionArn = args[3];

        final AWSLogs cloudWatchLogs = AWSLogsClientBuilder.defaultClient();

        PutSubscriptionFilterRequest request = new PutSubscriptionFilterRequest()
            .withFilterName(filterName)
            .withFilterPattern(filterPattern)
            .withLogGroupName(logGroupName)
            .withDestinationArn(lambdaFunctionArn);

        PutSubscriptionFilterResult response = cloudWatchLogs.putSubscriptionFilter(request);

        System.out.printf("Successfully created CloudWatch logs subscription filter %s", filterName);
    }
}
