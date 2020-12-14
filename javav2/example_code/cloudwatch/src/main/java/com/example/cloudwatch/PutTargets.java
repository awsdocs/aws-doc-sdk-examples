//snippet-sourcedescription:[PutTargets.java demonstrates how to creates an Amazon CloudWatch event-routing rule target.]
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

// snippet-start:[cloudwatch.java2.put_targets.import]
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.Target;
// snippet-end:[cloudwatch.java2.put_targets.import]

public class PutTargets {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply:\n" +
                        "* a rule name\n" +
                        "* a lambda function ARN value \n" +
                        "* a target id\n\n" +
                        "Ex: PutTargets <ruleName> <functionArn> <targetId>\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String ruleName = args[0];
        String functionArn = args[1];
        String targetId = args[2];

        CloudWatchEventsClient cwe =
                CloudWatchEventsClient.builder().build();

        putCWTargets(cwe, ruleName, functionArn, targetId);
        cwe.close();
    }

    // snippet-start:[cloudwatch.java2.put_targets.main]
    public static void putCWTargets(CloudWatchEventsClient cwe, String ruleName, String functionArn, String targetId ) {

        try {
            Target target = Target.builder()
                .arn(functionArn)
                .id(targetId)
                .build();

            PutTargetsRequest request = PutTargetsRequest.builder()
                .targets(target)
                .rule(ruleName)
                .build();

            PutTargetsResponse response = cwe.putTargets(request);
            System.out.printf(
                "Successfully created CloudWatch events target for rule %s",
                ruleName);
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.put_targets.main]
}
