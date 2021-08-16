/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.demo.movielens;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyResponse;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.CreateRoleResponse;
import software.amazon.awssdk.services.iam.model.EntityAlreadyExistsException;
import software.amazon.awssdk.services.iam.model.GetPolicyRequest;
import software.amazon.awssdk.services.iam.model.GetPolicyResponse;
import software.amazon.awssdk.services.iam.model.GetRoleRequest;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesResponse;
import software.amazon.awssdk.services.iam.model.ListPoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListPoliciesResponse;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.Policy;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

import java.util.List;

public class DemoUtils {

    private static final String BUCKET_POLICY_TEMPLATE = "{" +
            "    \"Version\": \"2012-10-17\"," +
            "    \"Id\": \"PersonalizeS3BucketAccessPolicy\"," +
            "    \"Statement\": [" +
            "        {" +
            "            \"Sid\": \"PersonalizeS3BucketAccessPolicy\"," +
            "            \"Effect\": \"Allow\"," +
            "            \"Principal\": {" +
            "                \"Service\": \"personalize.amazonaws.com\"" +
            "            }," +
            "            \"Action\": [" +
            "                \"s3:GetObject\"," +
            "                \"s3:ListBucket\"" +
            "            ]," +
            "            \"Resource\": [" +
            "                \"arn:aws:s3:::{bucket}\"," +
            "                \"arn:aws:s3:::{bucket}/*\"" +
            "            ]" +
            "        }" +
            "    ]" +
            "}";


    private static final String ASSUME_ROLE_POLICY = "{" +
            "    \"Version\": \"2012-10-17\"," +
            "    \"Statement\": [" +
            "        {" +
            "          \"Effect\": \"Allow\"," +
            "          \"Principal\": {" +
            "            \"Service\": \"personalize.amazonaws.com\"" +
            "          }," +
            "          \"Action\": \"sts:AssumeRole\"" +
            "        }" +
            "    ]" +
            "}";
    public static final String PERSONALIZE_POLICY =
            "{" +
                    "  \"Version\": \"2012-10-17\"," +
                    "  \"Statement\": [" +
                    "    {" +
                    "        \"Effect\": \"Allow\",\n" +
                    "        \"Action\": [\n" +
                    "                \"personalize:*\"\n" +
                    "            ]," +
                    "       \"Resource\": \"*\"\n" +
                    "    },\n" +
                    "	 {" +
                    "            \"Effect\": \"Allow\",\n" +
                    "            \"Action\": [\n" +
                    "                \"iam:PassRole\"\n" +
                    "            ],\n" +
                    "            \"Resource\": \"*\",\n" +
                    "            \"Condition\": {\n" +
                    "                \"StringEquals\": {\n" +
                    "                    \"iam:PassedToService\": \"personalize.amazonaws.com\"\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }" +
                    "   ]" +
                    "}";

    public static void ensurePersonalizePermissionsOnS3Bucket(S3Client s3, String bucket) {
        final String bucketPolicy = BUCKET_POLICY_TEMPLATE.replace("{bucket}", bucket);
        //System.out.println("Bucket policy: " + bucketPolicy);

        PutBucketPolicyRequest policyRequest = PutBucketPolicyRequest.builder()
                .bucket(bucket)
                .policy(bucketPolicy)
                .build();
        s3.putBucketPolicy(policyRequest);
    }

    public static String createPersonalizeRole(IamClient iamClient, String roleName) {
        String roleArn = checkRoleExists(iamClient, roleName);
        String minimumPersonalizeAccessPolicyArn;

        try {
            if (roleArn.length() == 0) {
                CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
                        .roleName(roleName)
                        .assumeRolePolicyDocument(ASSUME_ROLE_POLICY)
                        .description("PersonalizeRole")
                        .build();

                CreateRoleResponse response = iamClient.createRole(createRoleRequest);
                roleArn = response.role().arn();
                System.out.println("The ARN of your Amazon Personalize service role is " + roleArn);
            }
            minimumPersonalizeAccessPolicyArn = createPersonalizeIamPolicy(iamClient, "minimumPersonalizeAccessPolicy");
            attachIamPolicyToRole(iamClient, roleName, minimumPersonalizeAccessPolicyArn);
            attachIamPolicyToRole(iamClient, roleName, "arn:aws:iam::aws:policy/AmazonS3FullAccess");
            return roleArn;
        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return roleArn;
    }

    private static String checkRoleExists(IamClient iam, String roleName) {
        try {
            GetRoleRequest getRoleRequest = GetRoleRequest.builder()
                    .roleName(roleName)
                    .build();
            return iam.getRole(getRoleRequest).role().arn();
        } catch (NoSuchEntityException e) {
            return "";
        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";

    }

    public static void attachIamPolicyToRole(IamClient iam, String roleName, String policyArn) {
        String policyName = policyArn.substring(policyArn.indexOf("/") + 1);
        try {

            ListAttachedRolePoliciesRequest request = ListAttachedRolePoliciesRequest.builder()
                    .roleName(roleName)
                    .build();

            ListAttachedRolePoliciesResponse response = iam.listAttachedRolePolicies(request);
            List<AttachedPolicy> attachedPolicies = response.attachedPolicies();

            // Ensure that the policy is not attached to this role
            String polArn;
            for (AttachedPolicy policy : attachedPolicies) {
                polArn = policy.policyArn();
                if (polArn.compareTo(policyArn) == 0) {
                    System.out.println("The " + policyName + " policy is already attached to this role.");
                    return;
                }
            }
            AttachRolePolicyRequest attachRequest =
                    AttachRolePolicyRequest.builder()
                            .roleName(roleName)
                            .policyArn(policyArn)
                            .build();

            iam.attachRolePolicy(attachRequest);
            System.out.println("Successfully attached policy " + policyArn +
                    " to role " + roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }

    private static String getIamPolicyArn(IamClient iamClient, String policyName) {
        try {
            ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder()
                    .build();
            ListPoliciesResponse listPoliciesResponse = iamClient.listPolicies(listPoliciesRequest);

            for (Policy policy : listPoliciesResponse.policies()) {
                if (policy.policyName().equals(policyName)) {
                    return policy.arn();
                }
            }
        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public static String createPersonalizeIamPolicy(IamClient iam, String policyName) {

        String policyArn = getIamPolicyArn(iam, policyName);
        if (policyArn != null) {
            return policyArn;
        }
        try {
            // Create an IamWaiter object
            IamWaiter iamWaiter = iam.waiter();

            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(PERSONALIZE_POLICY).build();

            CreatePolicyResponse response = iam.createPolicy(request);

            // Wait until the policy is created
            GetPolicyRequest polRequest = GetPolicyRequest.builder()
                    .policyArn(response.policy().arn())
                    .build();

            WaiterResponse<GetPolicyResponse> waitUntilPolicyExists = iamWaiter.waitUntilPolicyExists(polRequest);
            waitUntilPolicyExists.matched().response().ifPresent(System.out::println);

            return response.policy().arn();
        } catch (EntityAlreadyExistsException ex) {
            return "";
        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
