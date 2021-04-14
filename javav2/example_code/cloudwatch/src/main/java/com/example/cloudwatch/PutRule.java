//snippet-sourcedescription:[PutRule.java demonstrates how to creates a CloudWatch event-routing rule.]
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

// snippet-start:[cloudwatch.java2.put_rule.import]
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.RuleState;
// snippet-end:[cloudwatch.java2.put_rule.import]

public class PutRule {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                "Usage:\n" +
                "  PutRule <ruleName> roleArn> \n\n" +
                "Where:\n" +
                "  ruleName - a rule name (for example, myrule).\n" +
                "  roleArn - a role ARN value (for example, arn:aws:iam::xxxxxx047983:user/MyUser).\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleName = args[0];
        String roleArn = args[1];

        CloudWatchEventsClient cwe =
                CloudWatchEventsClient.builder().build();

        putCWRule(cwe, ruleName, roleArn) ;
        cwe.close();
    }

    // snippet-start:[cloudwatch.java2.put_rule.main]
    public static void putCWRule(CloudWatchEventsClient cwe, String ruleName, String roleArn) {

        try {
            PutRuleRequest request = PutRuleRequest.builder()
                .name(ruleName)
                .roleArn(roleArn)
                .scheduleExpression("rate(5 minutes)")
                .state(RuleState.ENABLED)
                .build();

            PutRuleResponse response = cwe.putRule(request);
            System.out.printf(
                    "Successfully created CloudWatch events rule %s with arn %s",
                    roleArn, response.ruleArn());
        } catch (
            CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.put_rule.main]
}
