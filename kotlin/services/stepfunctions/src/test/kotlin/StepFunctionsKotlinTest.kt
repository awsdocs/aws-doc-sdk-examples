// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.gson.Gson
import com.kotlin.stepfunctions.DASHES
import com.kotlin.stepfunctions.GetStream
import com.kotlin.stepfunctions.createActivity
import com.kotlin.stepfunctions.createIAMRole
import com.kotlin.stepfunctions.createMachine
import com.kotlin.stepfunctions.deleteActivity
import com.kotlin.stepfunctions.deleteMachine
import com.kotlin.stepfunctions.describeExe
import com.kotlin.stepfunctions.describeStateMachine
import com.kotlin.stepfunctions.getActivityTask
import com.kotlin.stepfunctions.listActivitesPagnator
import com.kotlin.stepfunctions.listMachines
import com.kotlin.stepfunctions.listStatemachinesPagnator
import com.kotlin.stepfunctions.sendTaskSuccess
import com.kotlin.stepfunctions.startWorkflow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.Scanner
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class StepFunctionsKotlinTest {
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
        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties") as InputStream
        val prop = Properties()
        prop.load(input)
        jsonFile = prop.getProperty("jsonFile")
        jsonFileSM = prop.getProperty("jsonFileSM")
        roleARN = prop.getProperty("roleARN")
        stateMachineName = prop.getProperty("stateMachineName")
        roleNameSC = prop.getProperty("roleNameSC")
        activityNameSC = prop.getProperty("activityNameSC")
        stateMachineNameSC = prop.getProperty("stateMachineNameSC")
         */
        }

    @Test
    @Order(1)
    fun listStateMachines() =
        runBlocking {
            listMachines()
            println("Test 4 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/stepfunctions"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
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
