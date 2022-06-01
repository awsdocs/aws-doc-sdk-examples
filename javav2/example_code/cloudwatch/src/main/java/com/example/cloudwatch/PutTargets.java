//snippet-sourcedescription:[PutTargets.java demonstrates how to creates an Amazon CloudWatch event-routing rule target.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

 package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_targets.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.Target;
// snippet-end:[cloudwatch.java2.put_targets.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutTargets {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "  <ruleName> <functionArn> <targetId> \n\n" +
                "Where:\n" +
                "  ruleName - A rule name (for example, myrule).\n" +
                "  functionArn - An AWS Lambda function ARN (for example, arn:aws:lambda:us-west-2:xxxxxx047983:function:lamda1).\n" +
                "  targetId - A target id value.\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String ruleName = args[0];
        String functionArn = args[1];
        String targetId = args[2];

        CloudWatchEventsClient cwe = CloudWatchEventsClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

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
