//snippet-sourcedescription:[GetSamplingRules.java demonstrates how to retrieve all sampling rules.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-service:[AWS X-Ray Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.xray;

// snippet-start:[xray.java2_get_rules.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetSamplingRulesResponse;
import software.amazon.awssdk.services.xray.model.SamplingRuleRecord;
import software.amazon.awssdk.services.xray.model.XRayException;
import java.util.List;
// snippet-end:[xray.java2_get_rules.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetSamplingRules {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        XRayClient xRayClient = XRayClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getRules(xRayClient);
    }

    // snippet-start:[xray.java2_get_rules.main]
    public static void getRules(XRayClient xRayClient) {

        try {
            GetSamplingRulesResponse response = xRayClient.getSamplingRules(r->r.build());
            List<SamplingRuleRecord> records = response.samplingRuleRecords();

            for (SamplingRuleRecord record: records) {
                System.out.println("The rule name is: "+record.samplingRule().ruleName());
                System.out.println("The related service is: "+record.samplingRule().serviceName());
            }
        } catch (XRayException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
     }
    // snippet-end:[xray.java2_get_rules.main]
   }
