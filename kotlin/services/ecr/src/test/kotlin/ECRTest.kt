// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.example.ecr.listImageTags
import com.example.ecr.scenario.ECRActions
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ECRTest {
    private val logger: Logger = LoggerFactory.getLogger(ECRTest::class.java)
    private var repoName = ""
    private var newRepoName = ""
    private var iamRole = ""
    private var ecrActions: ECRActions? = null

    @BeforeAll
    fun setup() =
        runBlocking {
            ecrActions = ECRActions()
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            newRepoName = values.repoName.toString()
            iamRole = values.iamRole.toString()
            repoName = values.existingRepo.toString()
        }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    fun testHello() =
        runBlocking {
            listImageTags(repoName)
            logger.info("Test 1 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretClient =
            SecretsManagerClient {
                region = "us-east-1"
            }
        val secretName = "test/ecr"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        val valueResponse = secretClient.getSecretValue(valueRequest)
        return valueResponse.secretString.toString()
    }

    @Nested
    @DisplayName("A class used to get test values from test/ecr (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val repoName: String? = null
        val iamRole: String? = null
        val imageName: String? = null
        val existingRepo: String? = null
    }
}
