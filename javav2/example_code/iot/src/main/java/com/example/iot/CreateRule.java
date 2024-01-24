package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.Action;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.SnsAction;
import software.amazon.awssdk.services.iot.model.TopicRulePayload;

public class CreateRule {
    private static final String TOPIC = "your-iot-topic";
    public static void main(String[]args) {
           IotClient iotClient = IotClient.builder()
                .region(Region.US_EAST_1)
                .build();

           String roleARN = "arn:aws:iam::814548047983:role/AssumeRoleSNS";
            try {
                // Set the rule name
                String ruleName = "YourRuleName";

                // Set the rule SQL statement
                String sql = "SELECT * FROM '" + TOPIC + "'";

                // Set the action to be taken when the rule is triggered
                String action = "arn:aws:sns:us-east-1:814548047983:scott1111";


                SnsAction action1 = SnsAction.builder()
                    .targetArn(action)
                    .roleArn(roleARN)
                    .build();

                // Create the action
                Action myAction = Action.builder()
                    .sns(action1)
                    .build();

                // Create the topic rule payload
                TopicRulePayload topicRulePayload = TopicRulePayload.builder()
                    .sql(sql)
                    .actions(myAction)
                    .build();

                // Create the topic rule request
                CreateTopicRuleRequest topicRuleRequest = CreateTopicRuleRequest.builder()
                    .ruleName(ruleName)
                    .topicRulePayload(topicRulePayload)
                    .build();

                // Create the rule
                iotClient.createTopicRule(topicRuleRequest);
                System.out.println("IoT Rule created successfully. ");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

