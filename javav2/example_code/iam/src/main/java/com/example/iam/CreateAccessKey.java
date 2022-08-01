//snippet-sourcedescription:[CreateAccessKey.java demonstrates how to create an access key for an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[IAM]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.create_access_key.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.create_access_key.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateAccessKey {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "   <user> \n\n" +
            "Where:\n" +
            "   user - An AWS IAM user that you can obtain from the AWS Management Console.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String user = args[0];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String keyId = createIAMAccessKey(iam, user);
        System.out.println("The Key Id is " +keyId);
        iam.close();
    }

    // snippet-start:[iam.java2.create_access_key.main]
    public static String createIAMAccessKey(IamClient iam,String user) {

        try {
            CreateAccessKeyRequest request = CreateAccessKeyRequest.builder()
                .userName(user)
                .build();

            CreateAccessKeyResponse response = iam.createAccessKey(request);
            return response.accessKey().accessKeyId();

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[iam.java2.create_access_key.main]
}
