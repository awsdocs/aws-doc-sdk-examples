//snippet-sourcedescription:[ListRules.java demonstrates how to list your Amazon EventBridge rules.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EventBridge]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/22/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._list_rules.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.ListRulesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListRulesResponse;
import software.amazon.awssdk.services.eventbridge.model.Rule;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
import java.util.List;
// snippet-end:[eventbridge.java2._list_rules.import]

public class ListRules {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
                .region(region)
                .build();

        listAllRules(eventBrClient);
    }

    // snippet-start:[eventbridge.java2._list_rules.main]
    public static void listAllRules(EventBridgeClient eventBrClient) {

        try {

            ListRulesRequest rulesRequest = ListRulesRequest.builder()
                .eventBusName("default")
                .limit(10)
                .build();

            ListRulesResponse response = eventBrClient.listRules(rulesRequest);
            List<Rule> rules = response.rules();

            for (Rule rule : rules) {
                System.out.println("The rule name is : "+rule.name());
                System.out.println("The rule ARN is : "+rule.arn());
            }
        } catch (EventBridgeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[eventbridge.java2._list_rules.main]
    }
}
