// snippet-sourcedescription:[Route53Scenario.kt demonstrates how to perform multiple Amazon Route 53 domain operations using the AWS SDK for Kotlin.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.route

import aws.sdk.kotlin.services.route53domains.Route53DomainsClient
import aws.sdk.kotlin.services.route53domains.model.CheckDomainAvailabilityRequest
import aws.sdk.kotlin.services.route53domains.model.CheckDomainTransferabilityRequest
import aws.sdk.kotlin.services.route53domains.model.ContactDetail
import aws.sdk.kotlin.services.route53domains.model.ContactType
import aws.sdk.kotlin.services.route53domains.model.CountryCode
import aws.sdk.kotlin.services.route53domains.model.GetDomainDetailRequest
import aws.sdk.kotlin.services.route53domains.model.GetDomainSuggestionsRequest
import aws.sdk.kotlin.services.route53domains.model.GetOperationDetailRequest
import aws.sdk.kotlin.services.route53domains.model.ListOperationsRequest
import aws.sdk.kotlin.services.route53domains.model.ListPricesRequest
import aws.sdk.kotlin.services.route53domains.model.RegisterDomainRequest
import aws.sdk.kotlin.services.route53domains.model.ViewBillingRequest
import aws.smithy.kotlin.runtime.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import kotlin.system.exitProcess

// import java.util.*

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

This Kotlin code example performs the following operations:

1. List current domains.
2. List operations in the past year.
3. View billing for the account in the past year.
4. View prices for domain types.
5. Get domain suggestions.
6. Check domain availability.
7. Check domain transferability.
8. Request a domain registration.
9. Get operation details.
10. Optionally, get domain details.
 */

val DASHES: String = String(CharArray(80)).replace("\u0000", "-")
suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <domainType> <phoneNumber> <email> <domainSuggestion> <firstName> <lastName> <city>
        Where:
           domainType - The domain type (for example, com). 
           phoneNumber - The phone number to use (for example, +91.9966564xxx)      
           email - The email address to use.      
           domainSuggestion - The domain suggestion (for example, findmy.accountants). 
           firstName - The first name to use to register a domain. 
           lastName -  The last name to use to register a domain. 
           city - The city to use to register a domain. 
    """

    if (args.size != 7) {
        println(usage)
        exitProcess(1)
    }

    val domainType = args[0]
    val phoneNumber = args[1]
    val email = args[2]
    val domainSuggestion = args[3]
    val firstName = args[4]
    val lastName = args[5]
    val city = args[6]

    println(DASHES)
    println("Welcome to the Amazon Route 53 domains example scenario.")
    println(DASHES)

    println(DASHES)
    println("1. List current domains.")
    listDomains()
    println(DASHES)

    println(DASHES)
    println("2. List operations in the past year.")
    listOperations()
    println(DASHES)

    println(DASHES)
    println("3. View billing for the account in the past year.")
    listBillingRecords()
    println(DASHES)

    println(DASHES)
    println("4. View prices for domain types.")
    listAllPrices(domainType)
    println(DASHES)

    println(DASHES)
    println("5. Get domain suggestions.")
    listDomainSuggestions(domainSuggestion)
    println(DASHES)

    println(DASHES)
    println("6. Check domain availability.")
    checkDomainAvailability(domainSuggestion)
    println(DASHES)

    println(DASHES)
    println("7. Check domain transferability.")
    checkDomainTransferability(domainSuggestion)
    println(DASHES)

    println(DASHES)
    println("8. Request a domain registration.")
    val opId = requestDomainRegistration(domainSuggestion, phoneNumber, email, firstName, lastName, city)
    println(DASHES)

    println(DASHES)
    println("9. Get operation details.")
    getOperationalDetail(opId)
    println(DASHES)

    println(DASHES)
    println("10. Get domain details.")
    println("Note: you must have a registered domain to get details.")
    println("Otherwise an exception is thrown that states ")
    println("Domain xxxxxxx not found in xxxxxxx account.")
    getDomainDetails(domainSuggestion)
    println(DASHES)
}

// snippet-start:[route.kotlin.domaindetails.main]
suspend fun getDomainDetails(domainSuggestion: String?) {
    val detailRequest = GetDomainDetailRequest {
        domainName = domainSuggestion
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.getDomainDetail(detailRequest)
        println("The contact first name is ${response.registrantContact?.firstName}")
        println("The contact last name is ${response.registrantContact?.lastName}")
        println("The contact org name is ${response.registrantContact?.organizationName}")
    }
}
// snippet-end:[route.kotlin.domaindetails.main]

// snippet-start:[route.kotlin.domainoperations.main]
suspend fun getOperationalDetail(opId: String?) {
    val detailRequest = GetOperationDetailRequest {
        operationId = opId
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.getOperationDetail(detailRequest)
        println("Operation detail message is ${response.message}")
    }
}
// snippet-end:[route.kotlin.domainoperations.main]

// snippet-start:[route.kotlin.domainreg.main]
suspend fun requestDomainRegistration(domainSuggestion: String?, phoneNumberVal: String?, emailVal: String?, firstNameVal: String?, lastNameVal: String?, cityVal: String?): String? {
    val contactDetail = ContactDetail {
        contactType = ContactType.Company
        state = "LA"
        countryCode = CountryCode.In
        email = emailVal
        firstName = firstNameVal
        lastName = lastNameVal
        city = cityVal
        phoneNumber = phoneNumberVal
        organizationName = "My Org"
        addressLine1 = "My Address"
        zipCode = "123 123"
    }

    val domainRequest = RegisterDomainRequest {
        adminContact = contactDetail
        registrantContact = contactDetail
        techContact = contactDetail
        domainName = domainSuggestion
        autoRenew = true
        durationInYears = 1
    }

    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.registerDomain(domainRequest)
        println("Registration requested. Operation Id: ${response.operationId}")
        return response.operationId
    }
}
// snippet-end:[route.kotlin.domainreg.main]

// snippet-start:[route.kotlin.checkdomaintransfer.main]
suspend fun checkDomainTransferability(domainSuggestion: String?) {
    val transferabilityRequest = CheckDomainTransferabilityRequest {
        domainName = domainSuggestion
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.checkDomainTransferability(transferabilityRequest)
        println("Transferability: ${response.transferability?.transferable}")
    }
}
// snippet-end:[route.kotlin.checkdomaintransfer.main]

// snippet-start:[route.kotlin.checkdomainavailability.main]
suspend fun checkDomainAvailability(domainSuggestion: String) {
    val availabilityRequest = CheckDomainAvailabilityRequest {
        domainName = domainSuggestion
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.checkDomainAvailability(availabilityRequest)
        println("$domainSuggestion is ${response.availability}")
    }
}
// snippet-end:[route.kotlin.checkdomainavailability.main]

// snippet-start:[route.kotlin.domainsuggestions.main]
suspend fun listDomainSuggestions(domainSuggestion: String?) {
    val suggestionsRequest = GetDomainSuggestionsRequest {
        domainName = domainSuggestion
        suggestionCount = 5
        onlyAvailable = true
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.getDomainSuggestions(suggestionsRequest)
        response.suggestionsList?.forEach { suggestion ->
            println("Suggestion Name: ${suggestion.domainName}")
            println("Availability: ${suggestion.availability}")
            println(" ")
        }
    }
}
// snippet-end:[route.kotlin.domainsuggestions.main]

// snippet-start:[route.kotlin.domainprices.main]
suspend fun listAllPrices(domainType: String?) {
    val pricesRequest = ListPricesRequest {
        tld = domainType
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.listPrices(pricesRequest)
        response.prices?.forEach { price ->
            println("Name: ${price.name}")
            println("Registration price: ${price.registrationPrice}")
            println("Renewal: ${price.renewalPrice}")
        }
    }
}
// snippet-end:[route.kotlin.domainprices.main]

// snippet-start:[route.kotlin.domainbillingrecords.main]
suspend fun listBillingRecords() {
    val currentDate = Date()
    val localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val zoneOffset = ZoneOffset.of("+01:00")
    val localDateTime2 = localDateTime.minusYears(1)
    val myStartTime = localDateTime2.toInstant(zoneOffset)
    val myEndTime = localDateTime.toInstant(zoneOffset)
    val timeStart: Instant? = myStartTime?.let { Instant(it) }
    val timeEnd: Instant? = myEndTime?.let { Instant(it) }

    val viewBillingRequest = ViewBillingRequest {
        start = timeStart
        end = timeEnd
    }
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.viewBilling(viewBillingRequest)
        response.billingRecords?.forEach { billing ->
            println("Bill Date: ${billing.billDate}")
            println("Operation: ${billing.operation}")
            println("Price: ${billing.price}")
        }
    }
}
// snippet-end:[route.kotlin.domainbillingrecords.main]

// snippet-start:[route.kotlin.domainlistops.main]
suspend fun listOperations() {
    val currentDate = Date()
    var localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val zoneOffset = ZoneOffset.of("+01:00")
    localDateTime = localDateTime.minusYears(1)
    val myTime: java.time.Instant? = localDateTime.toInstant(zoneOffset)
    val time2: Instant? = myTime?.let { Instant(it) }
    val operationsRequest = ListOperationsRequest {
        submittedSince = time2
    }

    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.listOperations(operationsRequest)
        response.operations?.forEach { content ->
            println("Operation Id: ${content.operationId}")
            println("Status: ${content.status}")
            println("Date: ${content.submittedDate}")
        }
    }
}
// snippet-end:[route.kotlin.domainlistops.main]

// snippet-start:[route.kotlin.domainlist.main]
suspend fun listDomains() {
    Route53DomainsClient { region = "us-east-1" }.use { route53DomainsClient ->
        val response = route53DomainsClient.listDomains()
        if (response.domains?.isEmpty() == true) {
            println("There are no domains")
        } else {
            response.domains?.forEach { content ->
                println("The domain name is ${content.domainName}")
            }
        }
    }
}
// snippet-end:[route.kotlin.domainlist.main]
