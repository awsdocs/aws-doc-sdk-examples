//snippet-sourcedescription:[DeleteRule.java demonstrates how to delete an Amazon EventBridge rule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon EventBridge]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._delete_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
import software.amazon.awssdk.services.eventbridge.model.DeleteRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.DisableRuleRequest;
// snippet-end:[eventbridge.java2._delete_rule.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteRule {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <ruleName> \n\n" +
            "Where:\n" +
            "    ruleName - The name of the rule to delete. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String ruleName = args[0];
        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        deleteEBRule(eventBrClient, ruleName);
        eventBrClient.close();
    }

    // snippet-start:[eventbridge.java2._delete_rule.main]
    public static void deleteEBRule(EventBridgeClient eventBrClient, String ruleName) {

        try {
            DisableRuleRequest disableRuleRequest = DisableRuleRequest.builder()
                .name(ruleName)
                .eventBusName("default")
                .build();

            eventBrClient.disableRule(disableRuleRequest);
            DeleteRuleRequest ruleRequest = DeleteRuleRequest.builder()
                .name(ruleName)
                .eventBusName("default")
                .build();

            eventBrClient.deleteRule(ruleRequest);
            System.out.println("Rule "+ruleName + " was successfully deleted!");

        } catch (EventBridgeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[eventbridge.java2._delete_rule.main]
}

