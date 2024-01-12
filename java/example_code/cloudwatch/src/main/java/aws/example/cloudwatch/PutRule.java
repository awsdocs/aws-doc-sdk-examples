// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutRuleResult;
import com.amazonaws.services.cloudwatchevents.model.RuleState;

/**
 * Creates a CloudWatch event-routing rule
 */
public class PutRule {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a rule name and role arn\n" +
                "Ex: PutRule <rule-name> <role-arn>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String rule_name = args[0];
        String role_arn = args[1];

        final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();

        PutRuleRequest request = new PutRuleRequest()
                .withName(rule_name)
                .withRoleArn(role_arn)
                .withScheduleExpression("rate(5 minutes)")
                .withState(RuleState.ENABLED);

        PutRuleResult response = cwe.putRule(request);

        System.out.printf(
                "Successfully created CloudWatch events rule %s with arn %s",
                rule_name, response.getRuleArn());
    }
}
