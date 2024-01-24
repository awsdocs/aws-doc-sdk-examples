package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.Action;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.IotException;
import software.amazon.awssdk.services.iot.model.SnsAction;
import software.amazon.awssdk.services.iot.model.TopicRulePayload;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class CreateRule {
    private static final String TOPIC = "your-iot-topic";
    public static void main(String[]args) {
        final String usage = """

            Usage:
               <thingName>

            Where:
               thingName - The name of the AWS IoT Thing.\s
            """;

        String roleARN = "arn:aws:iam::814548047983:role/AssumeRoleSNS";
        String ruleName = "YourRuleName";
        String action = "arn:aws:sns:us-east-1:814548047983:scott1111";
        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();


        createTopicRule(iotClient, roleARN, ruleName, action);
    }

    public static void createTopicRule(IotClient iotClient, String roleARN, String ruleName, String action) {
            try {
                // Set the rule SQL statement.
                String sql = "SELECT * FROM '" + TOPIC + "'";

                // Set the action to be taken when the rule is triggered.
                SnsAction action1 = SnsAction.builder()
                    .targetArn(action)
                    .roleArn(roleARN)
                    .build();

                // Create the action.
                Action myAction = Action.builder()
                    .sns(action1)
                    .build();

                // Create the topic rule payload.
                TopicRulePayload topicRulePayload = TopicRulePayload.builder()
                    .sql(sql)
                    .actions(myAction)
                    .build();

                // Create the topic rule request.
                CreateTopicRuleRequest topicRuleRequest = CreateTopicRuleRequest.builder()
                    .ruleName(ruleName)
                    .topicRulePayload(topicRulePayload)
                    .build();

                // Create the rule.
                iotClient.createTopicRule(topicRuleRequest);
                System.out.println("IoT Rule created successfully. ");

            } catch (IotException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }