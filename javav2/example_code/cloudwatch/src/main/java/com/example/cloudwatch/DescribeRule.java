//snippet-sourcedescription:[DescribeRule.java demonstrates how to describe an existing rule and determine its schedule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudwatch;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.DescribeRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.DescribeRuleResponse;


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeRule {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "  DescribeRule <ruleName>\n\n" +
                "Where:\n" +
                "  ruleName - the name of the rule to describe.\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleName = args[0];
        CloudWatchEventsClient cwe =
                CloudWatchEventsClient.builder()
                        .region(Region.US_WEST_2)
                        .build();

        DescribeSpecificRule(cwe, ruleName);
        cwe.close();
    }

    public static void DescribeSpecificRule(CloudWatchEventsClient cwe, String ruleName) {

        try {

            DescribeRuleRequest ruleRequest = DescribeRuleRequest.builder()
                    .name(ruleName)
                    .build();

            DescribeRuleResponse ruleResp = cwe.describeRule(ruleRequest);
            String schedule = ruleResp.scheduleExpression();
            System.out.println("The schedule for this rule is "+schedule);

        } catch (
                CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }
}
