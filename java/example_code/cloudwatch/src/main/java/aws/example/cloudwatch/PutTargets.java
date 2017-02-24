/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package cloudwatch.src.main.java.aws.example.cloudwatch;

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

        final String USAGE =
            "To run this example, supply a rule name, lambda function arn and target id\n" +
            "Ex: PutTargets <rule-name> <lambda-function-arn> <target-id>\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleName = args[0];
        String lambdaFunctionArn = args[1];
        String targetId = args[2];

        final AmazonCloudWatchEvents cloudWatchEvents = AmazonCloudWatchEventsClientBuilder.defaultClient();

        Target target = new Target()
            .withArn(lambdaFunctionArn)
            .withId(targetId);

        PutTargetsRequest request = new PutTargetsRequest()
            .withTargets(target)
            .withRule(ruleName);

        PutTargetsResult response = cloudWatchEvents.putTargets(request);

        System.out.printf("Successfully created CloudWatch events target for rule %s", ruleName);
    }
}
