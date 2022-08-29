//snippet-sourcedescription:[CreateRuleSchedule.java demonstrates how to create an Amazon EventBridge rule that has a target and runs on a schedule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon EventBridge]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._create_schedule_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.PutRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
import software.amazon.awssdk.services.eventbridge.model.PutTargetsRequest;
import software.amazon.awssdk.services.eventbridge.model.Target;
// snippet-end:[eventbridge.java2._create_schedule_rule.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateRuleSchedule {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <ruleName> <cronExpression> <lambdaARN> <json> <targetId>\n\n" +
            "Where:\n" +
            "    ruleName - The name of the rule to create. \n" +
            "    cronExpression - The scheduling expression. For example, rate(5 minutes)" +
            "    lambdaARN - The ARN value of a Lambda function that is the target" +
            "    json  - The JSON to pass the Lambda function"+
            "    targetId - The ID of the target within the specified rule ";

            if (args.length != 5) {
                System.out.println(usage);
                System.exit(1);
            }

            String ruleName = args[0];
            String cronExpression = args[1];
            String lambdaARN = args[2];
            String json = args[3];
            String targetId = args[4];

            Region region = Region.US_WEST_2;
            EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

            createEBRule(eventBrClient, ruleName, cronExpression);
            putRuleTarget(eventBrClient, ruleName, lambdaARN, json, targetId );
            eventBrClient.close();
        }

        // snippet-start:[eventbridge.java2._create_schedule_rule.main]
        public static void createEBRule(EventBridgeClient eventBrClient, String ruleName, String cronExpression) {

            try {
                PutRuleRequest ruleRequest = PutRuleRequest.builder()
                    .name(ruleName)
                    .eventBusName("default")
                    .scheduleExpression(cronExpression)
                    .state("ENABLED")
                    .description("A test rule that runs on a schedule created by the Java API")
                    .build();

                PutRuleResponse ruleResponse = eventBrClient.putRule(ruleRequest);
                System.out.println("The ARN of the new rule is "+ ruleResponse.ruleArn());

            } catch (EventBridgeException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }
        // snippet-end:[eventbridge.java2._create_schedule_rule.main]

        // snippet-start:[eventbridge.java2._create_schedule_rule_target.main]
        public static void putRuleTarget(EventBridgeClient eventBrClient, String ruleName, String lambdaARN, String json, String targetId ) {

            try {
                Target lambdaTarget = Target.builder()
                    .arn(lambdaARN)
                    .id(targetId)
                    .input(json)
                    .build();

                PutTargetsRequest targetsRequest = PutTargetsRequest.builder()
                    .eventBusName("default")
                    .rule(ruleName)
                    .targets(lambdaTarget)
                    .build();

                eventBrClient.putTargets(targetsRequest);
                System.out.println("The "+lambdaARN + " was successfully used as a target");

            } catch (EventBridgeException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }
        // snippet-end:[eventbridge.java2._create_schedule_rule_target.main]
}
