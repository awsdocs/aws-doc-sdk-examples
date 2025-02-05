// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.route.checkDomainAvailability
import com.kotlin.route.checkDomainTransferability
import com.kotlin.route.createCheck
import com.kotlin.route.createZone
import com.kotlin.route.delHealthCheck
import com.kotlin.route.getOperationalDetail
import com.kotlin.route.listAllHealthChecks
import com.kotlin.route.listAllPrices
import com.kotlin.route.listBillingRecords
import com.kotlin.route.listDomainSuggestions
import com.kotlin.route.listDomains
import com.kotlin.route.listOperations
import com.kotlin.route.listZones
import com.kotlin.route.requestDomainRegistration
import com.kotlin.route.updateSpecificHealthCheck
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class Route53Test {
    private val logger: Logger = LoggerFactory.getLogger(Route53Test::class.java)
    private var domainName = ""
    private var healthCheckId = ""
    private var hostedZoneId = ""
    private var domainSuggestionSc = ""
    private var domainTypeSc = ""
    private var phoneNumerSc = ""
    private var emailSc = ""
    private var firstNameSc = ""
    private var lastNameSc = ""
    private var citySc = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            domainName = values.domainName.toString()
            domainSuggestionSc = values.domainSuggestionSc.toString()
            domainTypeSc = values.domainTypeSc.toString()
            phoneNumerSc = values.phoneNumerSc.toString()
            emailSc = values.emailSc.toString()
            firstNameSc = values.firstNameSc.toString()
            lastNameSc = values.lastNameSc.toString()
            citySc = values.citySc.toString()
        }

    @Test
    @Order(1)
    fun createHealthCheckTest() =
        runBlocking {
            healthCheckId = createCheck(domainName).toString()
            Assertions.assertFalse(healthCheckId.isEmpty())
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createHostedZone() =
        runBlocking {
            hostedZoneId = createZone(domainName).toString()
            Assertions.assertFalse(hostedZoneId.isEmpty())
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listHealthChecks() =
        runBlocking {
            listAllHealthChecks()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun updateHealthCheck() =
        runBlocking {
            updateSpecificHealthCheck(healthCheckId)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun listHostedZones() =
        runBlocking {
            listZones()
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun deleteHealthCheck() =
        runBlocking {
            delHealthCheck(healthCheckId)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun fullScenarioTest() =
        runBlocking {
            listDomains()
            listOperations()
            listBillingRecords()
            listAllPrices(domainTypeSc)
            listDomainSuggestions(domainSuggestionSc)
            checkDomainAvailability(domainSuggestionSc)
            checkDomainTransferability(domainSuggestionSc)
            val opId = requestDomainRegistration(domainSuggestionSc, phoneNumerSc, emailSc, firstNameSc, lastNameSc, citySc)
            opId?.let { Assertions.assertFalse(it.isEmpty()) }
            getOperationalDetail(opId)
            logger.info("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/route53"
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
    @DisplayName("A class used to get test values from test/route53 (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val domainName: String? = null
        val domainSuggestionSc: String? = null
        val domainTypeSc: String? = null
        val phoneNumerSc: String? = null
        val emailSc: String? = null
        val firstNameSc: String? = null
        val lastNameSc: String? = null
        val citySc: String? = null
    }
}
