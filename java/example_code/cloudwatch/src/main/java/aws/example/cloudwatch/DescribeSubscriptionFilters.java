 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cloudwatch]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
package aws.example.cloudwatch;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DescribeSubscriptionFiltersRequest;
import com.amazonaws.services.logs.model.DescribeSubscriptionFiltersResult;
import com.amazonaws.services.logs.model.SubscriptionFilter;

/**
 * Lists CloudWatch subscription filters associated with a log group.
 */
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

        final AWSLogs logs = AWSLogsClientBuilder.defaultClient();
        boolean done = false;

        DescribeSubscriptionFiltersRequest request =
                new DescribeSubscriptionFiltersRequest()
                    .withLogGroupName(log_group)
                    .withLimit(1);
        
        while(!done) {

            DescribeSubscriptionFiltersResult response =
                logs.describeSubscriptionFilters(request);

            for(SubscriptionFilter filter : response.getSubscriptionFilters()) {
                System.out.printf(
                    "Retrieved filter with name %s, " +
                    "pattern %s " +
                    "and destination arn %s",
                    filter.getFilterName(),
                    filter.getFilterPattern(),
                    filter.getDestinationArn());
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }
}

