// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.example.iot.attachCertificateToThing
import com.example.iot.createCertificate
import com.example.iot.createIoTRule
import com.example.iot.createIoTThing
import com.example.iot.deleteCertificate
import com.example.iot.deleteIoTThing
import com.example.iot.describeEndpoint
import com.example.iot.describeThing
import com.example.iot.detachThingPrincipal
import com.example.iot.getPayload
import com.example.iot.listAllThings
import com.example.iot.listCertificates
import com.example.iot.listIoTRules
import com.example.iot.searchThings
import com.example.iot.updateShawdowThing
import com.example.iot.updateThing
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IoTTest {
    private val logger: Logger = LoggerFactory.getLogger(IoTTest::class.java)
    private var roleARN = ""
    private var snsAction = ""
    private var thingName = "foo"
    private var queryString = "thingName:"
    private var ruleName = "rule"

    @BeforeAll
    fun setup() =
        runBlocking {
            val random = Random()
            val randomNumber = random.nextInt(1001)
            thingName = thingName + randomNumber
            queryString = queryString + thingName
            ruleName = ruleName + randomNumber

            // Get the values from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            roleARN = values.roleARN.toString()
            snsAction = values.snsAction.toString()
        }

    @Test
    @Order(1)
    fun helloIoTTest() =
        runBlocking {
            listAllThings()
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun testScenario() =
        runBlocking {
            createIoTThing(thingName)
            describeThing(thingName)
            val certificateArn = createCertificate()
            attachCertificateToThing(thingName, certificateArn)
            updateThing(thingName)
            describeEndpoint()
            listCertificates()
            updateShawdowThing(thingName)
            getPayload(thingName)
            createIoTRule(roleARN, ruleName, snsAction)
            listIoTRules()
            searchThings(queryString)
            if (certificateArn != null) {
                detachThingPrincipal(thingName, certificateArn)
            }
            if (certificateArn != null) {
                deleteCertificate(certificateArn)
            }
            deleteIoTThing(thingName)
            logger.info("Test 2 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretClient =
            SecretsManagerClient {
                region = "us-east-1"
            }
        val secretName = "test/iot"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        val valueResponse = secretClient.getSecretValue(valueRequest)
        return valueResponse.secretString.toString()
    }

    @Nested
    @DisplayName("A class used to get test values from test/iot (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val roleARN: String? = null
        val snsAction: String? = null
    }
}
