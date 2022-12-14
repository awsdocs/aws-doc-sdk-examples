/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class Route53Test {
    val DASH: String? = String(CharArray(80)).replace("\u0000", "-")
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
    fun setup() {
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
    }

    @Test
    @Order(1)
    fun createHealthCheckTest() = runBlocking {
        healthCheckId = createCheck(domainName).toString()
        Assertions.assertFalse(healthCheckId.isEmpty())
        println("The health check id is $healthCheckId")
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createHostedZone() = runBlocking {
        hostedZoneId = createZone(domainName).toString()
        Assertions.assertFalse(hostedZoneId.isEmpty())
        println("The hosted zone id is $hostedZoneId")
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun listHealthChecks() = runBlocking {
        listAllHealthChecks()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun updateHealthCheck() = runBlocking {
        updateSpecificHealthCheck(healthCheckId)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun listHostedZones() = runBlocking {
        listZones()
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun deleteHealthCheck() = runBlocking {
        delHealthCheck(healthCheckId)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun ScenarioTest() = runBlocking {
        println(DASH)
        println("1. List current domains.")
        listDomains()
        println(DASH)

        println(DASH)
        println("2. List operations in the past year.")
        listOperations()
        println(DASH)

        println(DASH)
        println("3. View billing for the account in the past year.")
        listBillingRecords()
        println(DASH)

        println(DASH)
        println("4. View prices for domain types.")
        listAllPrices(domainTypeSc)
        println(DASH)

        println(DASH)
        println("5. Get domain suggestions.")
        listDomainSuggestions(domainSuggestionSc)
        println(DASH)

        println(DASH)
        println("6. Check domain availability.")
        checkDomainAvailability(domainSuggestionSc)
        println(DASH)

        println(DASH)
        println("7. Check domain transferability.")
        checkDomainTransferability(domainSuggestionSc)
        println(DASH)

        println(DASH)
        println("8. Request a domain registration.")
        val opId = requestDomainRegistration(domainSuggestionSc, phoneNumerSc, emailSc, firstNameSc, lastNameSc, citySc)
        opId?.let { Assertions.assertFalse(it.isEmpty()) }
        println(DASH)

        println(DASH)
        println("9. Get operation details.")
        getOperationalDetail(opId)
        println(DASH)

        println("Test 7 passed")
    }
}
