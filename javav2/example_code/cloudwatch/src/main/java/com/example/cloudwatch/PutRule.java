//snippet-sourcedescription:[PutRule.java demonstrates how to creates a CloudWatch event-routing rule.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[cloudwatch.java.put_rule.complete]
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
// snippet-start:[cloudwatch.java.put_rule.import]
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.RuleState;
// snippet-end:[cloudwatch.java.put_rule.import]

/**
 * Creates a CloudWatch event-routing rule
 */
public class PutRule {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a rule name and role arn\n" +
            "Ex: PutRule <rule-name> <role-arn>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String rule_name = args[0];
        String role_arn = args[1];

        // snippet-start:[cloudwatch.java.put_rule.main]
        CloudWatchEventsClient cwe =
        		CloudWatchEventsClient.builder().build();

        PutRuleRequest request = PutRuleRequest.builder()
            .name(rule_name)
            .roleArn(role_arn)
            .scheduleExpression("rate(5 minutes)")
            .state(RuleState.ENABLED)
            .build();

        PutRuleResponse response = cwe.putRule(request);
        // snippet-end:[cloudwatch.java.put_rule.main]

        System.out.printf(
            "Successfully created CloudWatch events rule %s with arn %s",
            rule_name, response.ruleArn());
    }
}
// snippet-end:[cloudwatch.java.put_rule.complete]