//snippet-sourcedescription:[PutTargets.java demonstrates how to creates a CloudWatch event-routing rule target.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[cloudwatch.java.put_targets.complete]
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
// snippet-start:[cloudwatch.java.put_targets.import]
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.Target;
// snippet-end:[cloudwatch.java.put_targets.import]

/**
 * Creates a CloudWatch event-routing rule target
 */
public class PutTargets {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply:\n" +
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

        // snippet-start:[cloudwatch.java.put_targets.main]
        CloudWatchEventsClient cwe =
        		CloudWatchEventsClient.builder().build();

        Target target = Target.builder()
            .arn(function_arn)
            .id(target_id)
            .build();

        PutTargetsRequest request = PutTargetsRequest.builder()
            .targets(target)
            .rule(rule_name)
            .build();

        PutTargetsResponse response = cwe.putTargets(request);
        // snippet-end:[cloudwatch.java.put_targets.main]

        System.out.printf(
            "Successfully created CloudWatch events target for rule %s",
            rule_name);
    }
}
// snippet-end:[cloudwatch.java.put_targets.complete]