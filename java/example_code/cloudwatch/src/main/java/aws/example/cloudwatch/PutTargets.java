// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsResult;
import com.amazonaws.services.cloudwatchevents.model.Target;

/**
 * Creates a CloudWatch event-routing rule target
 */
public class PutTargets {

        public static void main(String[] args) {

                final String USAGE = "To run this example, supply:\n" +
                                "* a rule name\n" +
                                "* lambda function arn\n" +
                                "* target id\n\n" +
                                "Ex: PutTargets <rule-name> <lambda-function-arn> <target-id>\n";

                if (args.length != 3) {
                        System.out.println(USAGE);
                        System.exit(1);
                }

                String rule_name = args[0];
                String function_arn = args[1];
                String target_id = args[2];

                final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();

                Target target = new Target()
                                .withArn(function_arn)
                                .withId(target_id);

                PutTargetsRequest request = new PutTargetsRequest()
                                .withTargets(target)
                                .withRule(rule_name);

                PutTargetsResult response = cwe.putTargets(request);

                System.out.printf(
                                "Successfully created CloudWatch events target for rule %s",
                                rule_name);
        }
}
