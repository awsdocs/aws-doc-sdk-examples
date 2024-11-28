// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iam;

import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.PutRolePolicyRequest;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateObjectLockRole {

    public static void main(String[] args) {
        final String roleName = "<Enter role name>";
        IamClient iam = IamClient.builder().build();
        createObjectLockRole(iam, roleName);
    }

    // snippet-start:[iam.java2.s3_role.main]
    /**
     * Creates an IAM role with the necessary permissions to perform object lock operations on an S3 bucket.
     *
     * @param iam       An instance of the {@link IamClient} class, which is used to interact with the AWS IAM service.
     * @param roleName  The name of the IAM role to be created.
     */
    public static void createObjectLockRole(IamClient iam, String roleName) {
        final String bopsPermissions = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Action": "s3:GetBucketObjectLockConfiguration",
                        "Resource": [
                            "arn:aws:s3:::<ENTER Bucket Name>"
                        ]
                    },
                    {
                        "Effect": "Allow",
                        "Action": [
                            "s3:GetObject",
                            "s3:GetObjectVersion",
                            "s3:GetBucketLocation"
                        ],
                        "Resource": [
                            "arn:aws:s3:::<ENTER Bucket Name>/*"
                        ]
                    },
                    {
                        "Effect": "Allow",
                        "Action": [
                            "s3:PutObject",
                            "s3:GetBucketLocation"
                        ],
                        "Resource": [
                            "arn:aws:s3:::<ENTER Bucket Name>/*"
                        ]
                    }
                ]
            }""";

        CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
            .assumeRolePolicyDocument(bopsPermissions)
            .roleName(roleName)
            .build();

        iam.createRole(createRoleRequest);
        PutRolePolicyRequest putRolePolicyRequest = PutRolePolicyRequest.builder()
            .policyDocument(bopsPermissions)
            .policyName("batch_operations-permissions")
            .roleName(roleName)
            .build();

        iam.putRolePolicy(putRolePolicyRequest);
    }
    // snippet-end:[iam.java2.s3_role.main]
}
