//snippet-sourcedescription:[DeleteSubscriptionFilter.java demonstrates how to delete a CloudWatch subscription filter.]
//snippet-keyword:[Java]
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
import com.amazonaws.services.logs.model.DeleteSubscriptionFilterRequest;
import com.amazonaws.services.logs.model.DeleteSubscriptionFilterResult;

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

        final AWSLogs logs = AWSLogsClientBuilder.defaultClient();

        DeleteSubscriptionFilterRequest request =
            new DeleteSubscriptionFilterRequest()
                .withFilterName(filter)
                .withLogGroupName(log_group);

        DeleteSubscriptionFilterResult response =
            logs.deleteSubscriptionFilter(request);

        System.out.printf(
            "Successfully deleted CloudWatch logs subscription filter %s",
            filter);
    }
}
