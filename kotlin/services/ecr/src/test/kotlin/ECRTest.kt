// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ECRTest {
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
    @Order(1)
    fun testScenario() =
        runBlocking {
            ecrActions?.createECRRepository(newRepoName)
            ecrActions?.setRepoPolicy(newRepoName, iamRole)
            ecrActions?.getRepoPolicy(newRepoName)
            ecrActions?.getRepositoryURI(newRepoName)
            ecrActions?.setLifeCyclePolicy(newRepoName)
            ecrActions?.pushDockerImage(newRepoName, newRepoName)
            ecrActions?.verifyImage(newRepoName, newRepoName)
            ecrActions?.deleteECRRepository(newRepoName)
            println("Test 1 passed")
        }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    fun testHello() =
        runBlocking {
            listImageTags(repoName)
            println("Test 2 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretClient =
            SecretsManagerClient {
                region = "us-east-1"
                credentialsProvider = EnvironmentCredentialsProvider()
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
