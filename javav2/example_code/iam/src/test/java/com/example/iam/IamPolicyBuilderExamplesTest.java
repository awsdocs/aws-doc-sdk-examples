/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.example.iam;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.accessanalyzer.AccessAnalyzerClient;
import software.amazon.awssdk.services.accessanalyzer.model.PolicyType;
import software.amazon.awssdk.services.accessanalyzer.model.ValidatePolicyResponse;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetPolicyResponse;

class IamPolicyBuilderExamplesTest {
    private static final Logger logger = LoggerFactory.getLogger(IamPolicyBuilderExamplesTest.class);
    private IamPolicyBuilderExamples examples;
    private IamClient iam;

    private static void analyze(String policyJson, PolicyType policyType) {
        try (AccessAnalyzerClient analyzerClient = AccessAnalyzerClient.create()) {
            final ValidatePolicyResponse response = analyzerClient.validatePolicy(b -> b
                    .policyDocument(policyJson)
                    .policyType(policyType));
            response.findings().forEach(f ->
                    logger.info("Type [{}]; Detail [{}]", f.findingType().name(), f.findingDetails()));
            Assertions.assertEquals(0, response.findings().size());
        }
    }

    @BeforeEach
    void setUp() {
        examples = new IamPolicyBuilderExamples();
        iam = IamClient.builder().region(Region.AWS_GLOBAL).build();
    }

    @AfterEach
    void tearDown() {
        if (iam != null) {
            iam.close();
        }
    }

    /**
     * If this test succeeds, the syntax of the policy that is created is checked by the IAM service on upload.
     */
    @Test
    @Tag("IntegrationTest")
    void createAndUploadPolicyExample() {
        String accountId = examples.getAccountID();
        String policyName = "AllowPutItemToExampleTable";
        String jsonPolicy = examples.createAndUploadPolicyExample(iam, accountId, policyName);
        logger.info(jsonPolicy);

        GetPolicyResponse putItemPolicy = iam.getPolicy(b -> b.policyArn("arn:aws:iam::" + accountId + ":policy/" + policyName));
        iam.deletePolicy(b -> b.policyArn(putItemPolicy.policy().arn()));
        logger.info("Policy [{}] deleted", putItemPolicy.policy().arn());
    }

    /**
     * If this test succeeds, the syntax of the policies that are created and uploaded will be checked by the IAM service.
     */
    @Test
    @Tag("IntegrationTest")
    void createNewBasedOnExisingPolicyExample() {
        String accountID = examples.getAccountID();
        String policyName = "AllowPutItemToExampleTable";
        String newPolicyName = "AllowGetAndPutItemToExampleTable";
        // First part of the example is to create the policy.
        String jsonPolicy = examples.createAndUploadPolicyExample(iam, accountID, policyName);
        logger.info(jsonPolicy);

        String jsonNewPolicy = examples.createNewBasedOnExistingPolicyExample(iam, accountID, policyName, newPolicyName);
        logger.info(jsonNewPolicy);

        // Delete the two policies
        GetPolicyResponse putItemPolicy = iam.getPolicy(b -> b.policyArn("arn:aws:iam::" + accountID + ":policy/" + policyName));
        iam.deletePolicy(b -> b.policyArn(putItemPolicy.policy().arn()));
        logger.info("Policy [{}] deleted", putItemPolicy.policy().arn());

        GetPolicyResponse getAndPutItemPolicy = iam.getPolicy(b -> b.policyArn("arn:aws:iam::" + accountID + ":policy/" + newPolicyName));
        iam.deletePolicy(b -> b.policyArn(getAndPutItemPolicy.policy().arn()));
        logger.info("Policy [{}] deleted", getAndPutItemPolicy.policy().arn());
    }

    @Test
    @Tag("IntegrationTest")
    void multipleConditionsExample() {
        String jsonPolicy = examples.multipleConditionsExample();
        logger.info(jsonPolicy);
        analyze(jsonPolicy, PolicyType.IDENTITY_POLICY);
    }

    @Test
    @Tag("IntegrationTest")
    void timeBasedPolicyExample() {
        String policyJson = examples.timeBasedPolicyExample();
        logger.info(policyJson);
        analyze(policyJson, PolicyType.IDENTITY_POLICY);
    }

    @Test
    @Tag("IntegrationTest")
    void specifyPrincipalsExample() {
        String policyJson = examples.specifyPrincipalsExample();
        logger.info(policyJson);
        analyze(policyJson, PolicyType.RESOURCE_POLICY);

    }

    @Test
    @Tag("IntegrationTest")
    void allowCrossAccountAccessExample() {
        String policyJson = examples.allowCrossAccountAccessExample();
        logger.info(policyJson);
        analyze(policyJson, PolicyType.RESOURCE_POLICY);
    }

}