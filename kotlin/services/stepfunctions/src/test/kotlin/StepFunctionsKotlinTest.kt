// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.stepfunctions.listMachines
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class StepFunctionsKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(StepFunctionsKotlinTest::class.java)
    private var roleNameSC = ""
    private var activityNameSC = ""
    private var stateMachineNameSC = ""
    private var jsonFile = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            roleNameSC = values.roleNameSC.toString() + UUID.randomUUID()
            activityNameSC = values.activityNameSC.toString() + UUID.randomUUID()
            stateMachineNameSC = values.stateMachineNameSC.toString() + UUID.randomUUID()
            jsonFile = values.machineFile.toString()
        }

    @Test
    @Order(1)
    fun listStateMachines() =
        runBlocking {
            listMachines()
            logger.info("Test 1 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/stepfunctions"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/stepfunctions (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val roleNameSC: String? = null
        val activityNameSC: String? = null
        val stateMachineNameSC: String? = null
        val machineFile: String? = null
    }
}
