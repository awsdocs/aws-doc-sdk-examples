//snippet-sourcedescription:[DeleteSamplingRule.java demonstrates how to delete an AWS X-Ray Service rule.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS X-Ray Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/29/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.xray;

// snippet-start:[xray.java2_delete_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.DeleteSamplingRuleRequest;
import software.amazon.awssdk.services.xray.model.XRayException;
// snippet-end:[xray.java2_delete_rule.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteSamplingRule {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <ruleName>\n\n" +
                "Where:\n" +
                "   ruleName - The name of the rule to delete \n\n";

         if (args.length != 1) {
             System.out.println(usage);
             System.exit(1);
         }

        String ruleName = args[0];
        Region region = Region.US_EAST_1;
        XRayClient xRayClient = XRayClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        deleteRule( xRayClient, ruleName );
    }

    // snippet-start:[xray.java2_delete_rule.main]
    public static void deleteRule( XRayClient xRayClient, String ruleName ) {

        try {
            DeleteSamplingRuleRequest ruleRequest = DeleteSamplingRuleRequest.builder()
                .ruleName(ruleName)
                .build();

            xRayClient.deleteSamplingRule(ruleRequest);
        } catch (XRayException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[xray.java2_delete_rule.main]
}
