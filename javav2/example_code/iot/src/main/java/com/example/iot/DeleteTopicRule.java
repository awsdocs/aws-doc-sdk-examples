package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.DeleteTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.DisableTopicRuleRequest;

public class DeleteTopicRule {

    public static void main(String[]args) {
        // Specify the thing name
        String thingName = "foo122";
        String roleARN = "arn:aws:iam::814548047983:role/AssumeRoleSNS";
        String ruleName = "ScottRule22";

        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Disable the rule before deleting it.
        DisableTopicRuleRequest disableRequest = DisableTopicRuleRequest.builder()
            .ruleName(ruleName)
            .build();

        iotClient.disableTopicRule(disableRequest);

        DeleteTopicRuleRequest deleteTopicRuleRequest = DeleteTopicRuleRequest.builder()
            .ruleName(ruleName)
            .build();

        iotClient.deleteTopicRule(deleteTopicRuleRequest);
        System.out.println(ruleName +" was deleted.");
    }
}
