// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


package com.example.s3.batch;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.PutRolePolicyRequest;

public class CreateObjectLockRole {
    public static void main(String[] args) {
        final String usage = """

            Usage:    <roleName>

            Where:
               roleName - the IAM role name.
            """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }
        String roleName = args[0];
        createLockRole(roleName);
    }

    // snippet-start:[S3Lock.javav2.lock.role.main]
    /**
     * Creates an IAM role for AWS S3 Batch Operations to manage object locks.
     */
    public static void createLockRole(String roleName) {
        // Trust policy
        final String trustPolicy = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "batchoperations.s3.amazonaws.com"
                        },
                        "Action": "sts:AssumeRole"
                    }
                ]
            }
            """;


        // Permissions policy
        final String bopsPermissions = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Action": "s3:GetBucketObjectLockConfiguration",
                        "Resource": "arn:aws:s3:::amzn-s3-demo-manifest-bucket"
                    },
                    {
                        "Effect": "Allow",
                        "Action": [
                            "s3:GetObject",
                            "s3:GetObjectVersion",
                            "s3:GetBucketLocation"
                        ],
                        "Resource": "arn:aws:s3:::amzn-s3-demo-manifest-bucket/*"
                    },
                    {
                        "Effect": "Allow",
                        "Action": [
                            "s3:PutObject",
                            "s3:GetBucketLocation"
                        ],
                        "Resource": "arn:aws:s3:::amzn-s3-demo-completion-report-bucket/*"
                    }
                ]
            }
            """;

        // Create IAM client
        final IamClient iam = IamClient.builder()
            .region(Region.US_WEST_2)
            .build();

        // Create the role with the trust policy
        final CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
            .assumeRolePolicyDocument(trustPolicy)
            .roleName(roleName)
            .build();

        iam.createRole(createRoleRequest);

        // Attach the permissions policy to the role
        final PutRolePolicyRequest putRolePolicyRequest = PutRolePolicyRequest.builder()
            .policyDocument(bopsPermissions)
            .policyName("batch_operations-permissions")
            .roleName(roleName)
            .build();

        iam.putRolePolicy(putRolePolicyRequest);
        System.out.println("The object lock role was created.");
    }
    // snippet-end:[S3Lock.javav2.lock.role.main]
}
