// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeParameters.java demonstrates how to get information about Amazon Simple Systems Management (Amazon SSM) parameters.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Systems Management]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
// snippet-start:[ssm.Java2.get_params.complete]
package com.example.ssm;

// snippet-start:[ssm.Java2.get_params.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DescribeParametersRequest;
import software.amazon.awssdk.services.ssm.model.DescribeParametersResponse;
import software.amazon.awssdk.services.ssm.model.ParameterMetadata;
import software.amazon.awssdk.services.ssm.model.SsmException;
// snippet-end:[ssm.Java2.get_params.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeParameters {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeParams(ssmClient);
        ssmClient.close();
    }

    // snippet-start:[ssm.Java2.get_params.main]
    public static void describeParams(SsmClient ssmClient) {

        try {
            // Create a DescribeParametersRequest object
            DescribeParametersRequest desRequest = DescribeParametersRequest.builder()
                .maxResults(10)
                .build();

            // Get SSM Parameters (you can define them in the AWS Console).
            DescribeParametersResponse desResponse = ssmClient.describeParameters(desRequest);
            for (ParameterMetadata paraMeta: desResponse.parameters()) {
                System.out.println(paraMeta.name());
                System.out.println(paraMeta.description());
            }

        } catch (SsmException e) {
            e.getStackTrace();
        }
    }
    // snippet-end:[ssm.Java2.get_params.main]
}
// snippet-end:[ssm.Java2.get_params.complete]

