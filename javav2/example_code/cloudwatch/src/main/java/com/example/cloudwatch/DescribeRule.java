//snippet-sourcedescription:[DescribeRule.java demonstrates how to describe an existing rule and determine its schedule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudwatch;

// snippet-start:[cloudwatch.javav2.describe_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.DescribeRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.DescribeRuleResponse;
// snippet-end:[cloudwatch.javav2.describe_rule.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeRule {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "  <ruleName>\n\n" +
            "Where:\n" +
            "  ruleName - The name of the rule to describe.\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String ruleName = args[0];
        CloudWatchEventsClient cwe = CloudWatchEventsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeSpecificRule(cwe, ruleName);
        cwe.close();
    }

    // snippet-start:[cloudwatch.javav2.describe_rule.main]
    public static void describeSpecificRule(CloudWatchEventsClient cwe, String ruleName) {
        try {

            DescribeRuleRequest ruleRequest = DescribeRuleRequest.builder()
                .name(ruleName)
                .build();

            DescribeRuleResponse ruleResp = cwe.describeRule(ruleRequest);
            String schedule = ruleResp.scheduleExpression();
            System.out.println("The schedule for this rule is "+schedule);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.javav2.describe_rule.main]
}
