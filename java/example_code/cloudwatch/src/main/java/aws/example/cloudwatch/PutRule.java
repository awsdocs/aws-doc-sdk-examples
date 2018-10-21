//snippet-sourcedescription:[PutRule.java demonstrates how to create a CloudWatch event-routing rule.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Cloudwatch]
//snippet-sourcetype:[<<snippet or full-example>>]
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

        final String USAGE =
            "To run this example, supply a rule name and role arn\n" +
            "Ex: PutRule <rule-name> <role-arn>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String rule_name = args[0];
        String role_arn = args[1];

        final AmazonCloudWatchEvents cwe =
            AmazonCloudWatchEventsClientBuilder.defaultClient();

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
