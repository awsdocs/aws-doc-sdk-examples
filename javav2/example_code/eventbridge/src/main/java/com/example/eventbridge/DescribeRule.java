//snippet-sourcedescription:[DescribeRule.java demonstrates how to describe an Amazon EventBridge rule.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EventBridge]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/22/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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
                "    ruleName - The rule name to describe \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleName = "scott-rule1" ; //args[0];

        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .build();

        describeSpecificRule(eventBrClient, ruleName) ;
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
