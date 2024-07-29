// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class Route53Test {
    val dash: String? = String(CharArray(80)).replace("\u0000", "-")
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

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        domainName = prop.getProperty("domainName")
        domainSuggestionSc = prop.getProperty("domainSuggestionSc")
        domainTypeSc = prop.getProperty("domainTypeSc")
        phoneNumerSc = prop.getProperty("phoneNumerSc")
        emailSc = prop.getProperty("emailSc")
        firstNameSc = prop.getProperty("firstNameSc")
        lastNameSc = prop.getProperty("lastNameSc")
        citySc = prop.getProperty("citySc")
         */
        }

    @Test
    @Order(1)
    fun createHealthCheckTest() =
        runBlocking {
            healthCheckId = createCheck(domainName).toString()
            Assertions.assertFalse(healthCheckId.isEmpty())
            println("The health check id is $healthCheckId")
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createHostedZone() =
        runBlocking {
            hostedZoneId = createZone(domainName).toString()
            Assertions.assertFalse(hostedZoneId.isEmpty())
            println("The hosted zone id is $hostedZoneId")
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listHealthChecks() =
        runBlocking {
            listAllHealthChecks()
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun updateHealthCheck() =
        runBlocking {
            updateSpecificHealthCheck(healthCheckId)
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun listHostedZones() =
        runBlocking {
            listZones()
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun deleteHealthCheck() =
        runBlocking {
            delHealthCheck(healthCheckId)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun fullScenarioTest() =
        runBlocking {
            println(dash)
            println("1. List current domains.")
            listDomains()
            println(dash)

            println(dash)
            println("2. List operations in the past year.")
            listOperations()
            println(dash)

            println(dash)
            println("3. View billing for the account in the past year.")
            listBillingRecords()
            println(dash)

            println(dash)
            println("4. View prices for domain types.")
            listAllPrices(domainTypeSc)
            println(dash)

            println(dash)
            println("5. Get domain suggestions.")
            listDomainSuggestions(domainSuggestionSc)
            println(dash)

            println(dash)
            println("6. Check domain availability.")
            checkDomainAvailability(domainSuggestionSc)
            println(dash)

            println(dash)
            println("7. Check domain transferability.")
            checkDomainTransferability(domainSuggestionSc)
            println(dash)

            println(dash)
            println("8. Request a domain registration.")
            val opId = requestDomainRegistration(domainSuggestionSc, phoneNumerSc, emailSc, firstNameSc, lastNameSc, citySc)
            opId?.let { Assertions.assertFalse(it.isEmpty()) }
            println(dash)

            println(dash)
            println("9. Get operation details.")
            getOperationalDetail(opId)
            println(dash)
            println("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/route53"
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
