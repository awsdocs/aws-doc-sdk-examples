// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.emr.listAllClusters
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
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class EMRTest {
    private val logger: Logger = LoggerFactory.getLogger(EMRTest::class.java)
    private var jar = ""
    private var myClass = ""
    private var keys = ""
    private var logUri = ""
    private var name = ""
    private var existingClusterId = ""

    @BeforeAll
    @Throws(IOException::class)
    fun setUp() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            jar = values.jar.toString()
            myClass = values.myClass.toString()
            keys = values.keys.toString()
            logUri = values.logUri.toString()
            name = values.name.toString()
            existingClusterId = values.existingClusterId.toString()
        }

    @Test
    @Order(1)
    fun listClustersTest() =
        runBlocking {
            listAllClusters()
            logger.info("Test 1 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "text/emr"
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
    @DisplayName("A class used to get test values from test/emr (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val existingClusterId: String? = null
        val jar: String? = null
        val myClass: String? = null
        val keys: String? = null
        val name: String? = null
        val logUri: String? = null
    }
}
