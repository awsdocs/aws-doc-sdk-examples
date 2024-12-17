// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3;
//

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.policybuilder.iam.IamConditionKey;
import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.IamEffect;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.PutRolePolicyResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoesBucketExistTest {
    private static final String ROLE_NAME = "minimal-s3-perms-role";
    private static final String POLICY_NAME = "minimum-s3-ability-policy";
    private static final IamClient iamClient = IamClient.builder().build();

    @Test
    @Tag("IntegrationTest")
    void doesBucketExist_exists_but_not_in_client_region_should_return_true() {

        S3Client usWestS3Client = S3Client.builder().region(Region.US_WEST_2).build();
        final String bucketName = "my-us-west2-bucket-" + UUID.randomUUID();
        createBucket(usWestS3Client, bucketName);

        DoesBucketExist doesBucketExist = new DoesBucketExist();
        S3Client euCentralS3Client = S3Client.builder().region(Region.EU_CENTRAL_1).build();
        boolean exists = doesBucketExist.doesBucketExist(bucketName, euCentralS3Client);
        assertTrue(exists);

        deleteBucket(usWestS3Client, bucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void doesBucketExist_does_not_exist_should_return_false() {

        DoesBucketExist doesBucketExist = new DoesBucketExist();
        final String bucketName = "xx-xx-xxxx-xxxx" + UUID.randomUUID();

        boolean exists = doesBucketExist.doesBucketExist(bucketName, S3Client.create());
        assertFalse(exists);
    }

    @Test
    @Tag("IntegrationTest")
    void doesBucketExist_returns_true_when_bucket_exists_but_caller_does_not_have_permission() {
        StsClient stsClient = StsClient.create();
        createAssumableRole(stsClient);

        S3Client s3Client = S3Client.create();
        final String bucketName = "my-bucket-" + UUID.randomUUID();
        createBucket(s3Client, bucketName);

        try {

            String roleArn = iamClient.getRole(b -> b.roleName(ROLE_NAME)).role().arn();

            S3Client s3ClientWithoutPermission = S3Client.builder()
                    .credentialsProvider(StsAssumeRoleCredentialsProvider.builder()
                            .stsClient(stsClient)
                            .refreshRequest(arr -> arr
                                    .roleArn(roleArn)
                                    .roleSessionName("test-session"))
                            .build())
                    .build();
            DoesBucketExist doesBucketExist = new DoesBucketExist();
            boolean existsButNoAccess = doesBucketExist.doesBucketExist(bucketName, s3ClientWithoutPermission);
            assertTrue(existsButNoAccess);

            boolean exists = doesBucketExist.doesBucketExist("non-existent-bucket" + UUID.randomUUID(), s3ClientWithoutPermission);
            assertFalse(exists);
        } finally {
            deleteBucket(s3Client, bucketName);
            deleteRole();

        }
    }

    private static void createBucket(S3Client s3Client, String bucketName) {
        s3Client.createBucket(b -> b.bucket(bucketName));
        s3Client.waiter().waitUntilBucketExists(b -> b.bucket(bucketName));
    }

    private static void deleteBucket(S3Client s3Client, String bucketName) {
        s3Client.deleteBucket(b -> b.bucket(bucketName));
        s3Client.waiter().waitUntilBucketNotExists(b -> b.bucket(bucketName));
    }


    private static void createAssumableRole(StsClient stsClient) {
        final String accountID = stsClient.getCallerIdentity().account();

        IamPolicy trustIamPolicyForAnyoneInSameAccount = IamPolicy.builder()
                .addStatement(statement -> statement
                        .effect(IamEffect.ALLOW)
                        .addPrincipal(principal -> principal
                                .type(IamPrincipalType.AWS)
                                .id("arn:aws:iam::" + accountID + ":root")
                        )
                        .addAction("sts:AssumeRole")
                        .addConditions(IamConditionOperator.STRING_EQUALS,
                                IamConditionKey.create("aws:PrincipalType"),
                                List.of("User", "AssumedRole")
                        ).addConditions(IamConditionOperator.STRING_LIKE,
                                "aws:userId",
                                List.of("AIDAX*", "AROA*:*", "AIDA*:*"))
                )
                .build();

        iamClient.createRole(crb -> crb
                .roleName(ROLE_NAME)
                .assumeRolePolicyDocument(trustIamPolicyForAnyoneInSameAccount.toJson())
        );

        IamPolicy getBucketLocationOnlyPolicy = IamPolicy.builder()
                .addStatement(statement -> statement
                        .effect(IamEffect.ALLOW)
                        .addAction("s3:GetBucketLocation")
                        .addResource("arn:aws:s3:::*")
                )
                .build();

        iamClient.putRolePolicy(prprb -> prprb
                .roleName(ROLE_NAME)
                .policyName(POLICY_NAME)
                .policyDocument(getBucketLocationOnlyPolicy.toJson()));

        // Add a delay for the role to propagate.
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void deleteRole(){

        iamClient.deleteRolePolicy(drbrb -> drbrb
                .roleName(ROLE_NAME)
                .policyName(POLICY_NAME));

        iamClient.deleteRole(drb -> drb
                .roleName(ROLE_NAME)
        );
    }
}

