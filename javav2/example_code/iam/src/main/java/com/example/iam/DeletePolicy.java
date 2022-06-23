//snippet-sourcedescription:[DeletePolicy.java demonstrates how to delete a fixed policy with a provided policy name.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.iam;

// snippet-start:[iam.java2.delete_policy.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.iam.model.DeletePolicyRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.delete_policy.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeletePolicy {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <policyARN> \n\n" +
                "Where:\n" +
                "    policyARN - A policy ARN value to delete. \n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String policyARN = args[0];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        deleteIAMPolicy(iam, policyARN);
        iam.close();
    }

    // snippet-start:[iam.java2.delete_policy.main]
    public static void deleteIAMPolicy(IamClient iam,String policyARN) {

        try {
            DeletePolicyRequest request = DeletePolicyRequest.builder()
                   .policyArn(policyARN)
                   .build();

            iam.deletePolicy(request);
            System.out.println("Successfully deleted the policy");

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[iam.java2.delete_policy.main]
}

