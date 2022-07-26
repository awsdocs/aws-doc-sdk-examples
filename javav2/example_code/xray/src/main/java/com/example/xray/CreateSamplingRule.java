//snippet-sourcedescription:[CreateSamplingRule.java demonstrates how to create a rule to control sampling behavior for instrumented applications.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-service:[AWS X-Ray Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.xray;

// snippet-start:[xray.java2_create_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.CreateSamplingRuleRequest;
import software.amazon.awssdk.services.xray.model.SamplingRule;
import software.amazon.awssdk.services.xray.model.XRayException;
import software.amazon.awssdk.services.xray.model.CreateSamplingRuleResponse;
// snippet-end:[xray.java2_create_rule.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateSamplingRule {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <ruleName>\n\n" +
                "Where:\n" +
                "   ruleName - The name of the rule \n\n";

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

        createRule(xRayClient, ruleName) ;
    }

    // snippet-start:[xray.java2_create_rule.main]
    public static void createRule(XRayClient xRayClient, String ruleName) {

        try{

        SamplingRule rule = SamplingRule.builder()
                .ruleName(ruleName)
                .priority(1)
                .httpMethod("*")
                .serviceType("*")
                .serviceName("*")
                .urlPath("*")
                .version(1)
                .host("*")
                .resourceARN("*")
                .build();

            CreateSamplingRuleRequest ruleRequest = CreateSamplingRuleRequest.builder()
                .samplingRule(rule)
                .build();

            CreateSamplingRuleResponse ruleResponse = xRayClient.createSamplingRule(ruleRequest);
            System.out.println("The ARN of the new rule is "+ruleResponse.samplingRuleRecord().samplingRule().ruleARN());

    } catch (XRayException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
  }
    // snippet-end:[xray.java2_create_rule.main]
 }
