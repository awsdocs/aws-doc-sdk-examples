//snippet-sourcedescription:[DescribeRule.java demonstrates how to describe an Amazon EventBridge rule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EventBridge]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._describe_rule.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
// snippet-end:[eventbridge.java2._describe_rule.import]

public class DescribeRule {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeRule <ruleName> \n\n" +
                "Where:\n" +
                "    ruleName - the name of the rule to describe. \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleName = args[0];
        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .build();

        describeSpecificRule(eventBrClient, ruleName) ;
        eventBrClient.close();
    }

    // snippet-start:[eventbridge.java2._describe_rule.main]
    public static void describeSpecificRule(EventBridgeClient eventBrClient, String ruleName) {

       try {

            DescribeRuleRequest ruleRequest = DescribeRuleRequest.builder()
                .name(ruleName)
                .eventBusName("default")
                .build();

            DescribeRuleResponse ruleResponse = eventBrClient.describeRule(ruleRequest);
            System.out.println("The rule ARN is " +ruleResponse.arn());
            System.out.println("The role ARN is " +ruleResponse.roleArn());

       } catch (EventBridgeException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }

        // snippet-end:[eventbridge.java2._describe_rule.main]
    }
}
