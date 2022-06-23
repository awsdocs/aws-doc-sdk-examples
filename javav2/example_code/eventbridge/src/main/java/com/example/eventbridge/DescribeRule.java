//snippet-sourcedescription:[DescribeRule.java demonstrates how to describe an Amazon EventBridge rule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EventBridge]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._describe_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
// snippet-end:[eventbridge.java2._describe_rule.import]

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
                "    <ruleName> \n\n" +
                "Where:\n" +
                "    ruleName - The name of the rule to describe. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String ruleName = args[0];
        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
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
   }
    // snippet-end:[eventbridge.java2._describe_rule.main]
}
