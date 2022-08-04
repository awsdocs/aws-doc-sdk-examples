//snippet-sourcedescription:[PutRule.java demonstrates how to creates a CloudWatch event-routing rule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudWatch]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.RuleState;
// snippet-end:[cloudwatch.java2.put_rule.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutRule {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "  <ruleName> roleArn> \n\n" +
            "Where:\n" +
            "  ruleName - A rule name (for example, myrule).\n" +
            "  roleArn - A role ARN value (for example, arn:aws:iam::xxxxxx047983:user/MyUser).\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String ruleName = args[0];
        String roleArn = args[1];
        CloudWatchEventsClient cwe = CloudWatchEventsClient.builder()
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

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
