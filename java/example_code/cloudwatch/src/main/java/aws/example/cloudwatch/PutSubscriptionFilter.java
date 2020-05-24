//snippet-sourcedescription:[PutSubscriptionFilter.java demonstrates how to create a CloudWatch Logs subscription filter.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cloudwatch]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-15]
//snippet-sourceauthor:[soo-aws]
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
package aws.example.cloudwatch;
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
        String log_group = args[2];
        String function_arn = args[3];

        final AWSLogs cwl = AWSLogsClientBuilder.defaultClient();

        PutSubscriptionFilterRequest request =
            new PutSubscriptionFilterRequest()
                .withFilterName(filter)
                .withFilterPattern(pattern)
                .withLogGroupName(log_group)
                .withDestinationArn(function_arn);

        PutSubscriptionFilterResult response =
            cwl.putSubscriptionFilter(request);

        System.out.printf(
            "Successfully created CloudWatch logs subscription filter %s",
            filter);
    }
}
