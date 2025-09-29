package com.weathertop.service;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.AssignPublicIp;
import software.amazon.awssdk.services.eventbridge.model.AwsVpcConfiguration;
import software.amazon.awssdk.services.eventbridge.model.EcsParameters;
import software.amazon.awssdk.services.eventbridge.model.LaunchType;
import software.amazon.awssdk.services.eventbridge.model.NetworkConfiguration;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.PutTargetsRequest;
import software.amazon.awssdk.services.eventbridge.model.RuleState;
import software.amazon.awssdk.services.eventbridge.model.Target;

import java.util.Locale;
import java.util.UUID;

public class EventBridgeScheduler {

    /**
     * Schedules an ECS task using EventBridge.
     *
     * @param taskDefinitionArnVal Full ARN of the ECS task definition (e.g., arn:aws:ecs:us-east-1:123456789012:task-definition/MyTask:1)
     * @param clusterName Name of the ECS cluster (e.g., MyCluster)
     * @param cron Valid EventBridge cron expression (e.g., "cron(0 0 ? * 1 *)")
     */
    public String setScheduler(String taskDefinitionArnVal, String clusterName, String cron, String ruleName) {
        Region region = Region.US_EAST_1;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .build();

        String roleArn = "arn:aws:iam::814548047983:role/EcsEventsRole";

        // Step 1: Create EventBridge Rule
        PutRuleRequest ruleRequest = PutRuleRequest.builder()
                .name(ruleName)
                .scheduleExpression(cron)
                .state(RuleState.ENABLED)
                .build();

        eventBrClient.putRule(ruleRequest);

        // Step 2: Define ECS Task Target
        Target target = Target.builder()
                .id("ecs-target")
                .arn("arn:aws:ecs:us-east-1:814548047983:cluster/" + clusterName)
                .roleArn(roleArn)
                .ecsParameters(EcsParameters.builder()
                        .taskDefinitionArn(taskDefinitionArnVal)
                        .launchType(LaunchType.FARGATE)
                        .networkConfiguration(NetworkConfiguration.builder()
                                .awsvpcConfiguration(AwsVpcConfiguration.builder()
                                        .subnets("subnet-03c28397a3a7cd314")
                                        .securityGroups("sg-0e357c99b6b13bf62")
                                        .assignPublicIp(AssignPublicIp.ENABLED)
                                        .build())
                                .build())
                        .build())
                .build();

        // Step 3: Attach ECS Task Target
        PutTargetsRequest targetsRequest = PutTargetsRequest.builder()
                .rule(ruleName)
                .targets(target)
                .build();

        eventBrClient.putTargets(targetsRequest);
        String msg = "✅ EventBridge rule set for: " + cron;
        return msg;
    }



}