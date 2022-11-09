// snippet-sourcedescription:[SupportScenario.kt demonstrates how to perform AWS Support operations using the AWS SDK for Java v2.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Support]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.support

import aws.sdk.kotlin.services.support.SupportClient
import aws.sdk.kotlin.services.support.model.AddAttachmentsToSetRequest
import aws.sdk.kotlin.services.support.model.AddCommunicationToCaseRequest
import aws.sdk.kotlin.services.support.model.Attachment
import aws.sdk.kotlin.services.support.model.CreateCaseRequest
import aws.sdk.kotlin.services.support.model.DescribeAttachmentRequest
import aws.sdk.kotlin.services.support.model.DescribeCasesRequest
import aws.sdk.kotlin.services.support.model.DescribeCommunicationsRequest
import aws.sdk.kotlin.services.support.model.DescribeServicesRequest
import aws.sdk.kotlin.services.support.model.DescribeSeverityLevelsRequest
import aws.sdk.kotlin.services.support.model.ResolveCaseRequest
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.system.exitProcess

// snippet-start:[support.kotlin.scenario.main]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.
For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
In addition, you must have the AWS Business Support Plan to use the AWS Support Java API. For more information, see:
https://aws.amazon.com/premiumsupport/plans/
This Kotlin example performs the following tasks:
1. Gets and displays available services.
2. Gets and displays severity levels.
3. Creates a support case by using the selected service, category, and severity level.
4. Gets a list of open cases for the current day.
5. Creates an attachment set with a generated file.
6. Adds a communication with the attachment to the support case.
7. Lists the communications of the support case.
8. Describes the attachment set included with the communication.
9. Resolves the support case.
10. Gets a list of resolved cases for the current day.
*/

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <fileAttachment> 
    Where:
         fileAttachment - The file can be a simple saved .txt file to use as an email attachment.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val fileAttachment = args[0]
    println("***** Welcome to the AWS Support case example scenario.")
    println("***** Step 1. Get and display available services.")
    val sevCatList = displayServices()

    println("***** Step 2. Get and display Support severity levels.")
    val sevLevel = displaySevLevels()

    println("***** Step 3. Create a support case using the selected service, category, and severity level.")
    val caseIdVal = createSupportCase(sevCatList, sevLevel)
    if (caseIdVal != null) {
        println("Support case $caseIdVal was successfully created!")
    } else {
        println("A support case was not successfully created!")
        exitProcess(1)
    }

    println("***** Step 4. Get open support cases.")
    getOpenCase()

    println("***** Step 5. Create an attachment set with a generated file to add to the case.")
    val attachmentSetId = addAttachment(fileAttachment)
    println("The Attachment Set id value is$attachmentSetId")

    println("***** Step 6. Add communication with the attachment to the support case.")
    addAttachSupportCase(caseIdVal, attachmentSetId)

    println("***** Step 7. List the communications of the support case.")
    val attachId = listCommunications(caseIdVal)
    println("The Attachment id value is$attachId")

    println("***** Step 8. Describe the attachment set included with the communication.")
    describeAttachment(attachId)

    println("***** Step 9. Resolve the support case.")
    resolveSupportCase(caseIdVal)

    println("***** Step 10. Get a list of resolved cases for the current day.")
    getResolvedCase()
    println("***** This Scenario has successfully completed")
}

// snippet-start:[support.kotlin.get.resolve.main]
suspend fun getResolvedCase() {
    // Specify the start and end time.
    val now = Instant.now()
    LocalDate.now()
    val yesterday = now.minus(1, ChronoUnit.DAYS)
    val describeCasesRequest = DescribeCasesRequest {
        maxResults = 30
        afterTime = yesterday.toString()
        beforeTime = now.toString()
        includeResolvedCases = true
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeCases(describeCasesRequest)
        response.cases?.forEach { sinCase ->
            println("The case status is " + sinCase.status)
            println("The case Id is " + sinCase.caseId)
            println("The case subject is " + sinCase.subject)
        }
    }
}
// snippet-end:[support.kotlin.get.resolve.main]

// snippet-start:[support.kotlin.resolve.main]
suspend fun resolveSupportCase(caseIdVal: String) {
    val caseRequest = ResolveCaseRequest {
        caseId = caseIdVal
    }
    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.resolveCase(caseRequest)
        println("The status of case $caseIdVal is ${response.finalCaseStatus}")
    }
}
// snippet-end:[support.kotlin.resolve.main]

// snippet-start:[support.kotlin.des.attachment.main]
suspend fun describeAttachment(attachId: String?) {
    val attachmentRequest = DescribeAttachmentRequest {
        attachmentId = attachId
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeAttachment(attachmentRequest)
        println("The name of the file is ${response.attachment?.fileName}")
    }
}
// snippet-end:[support.kotlin.des.attachment.main]

// snippet-start:[support.kotlin.list.comms.main]
suspend fun listCommunications(caseIdVal: String?): String? {
    var attachId: String? = null
    val communicationsRequest = DescribeCommunicationsRequest {
        caseId = caseIdVal
        maxResults = 10
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeCommunications(communicationsRequest)
        response.communications?.forEach { comm ->
            println("the body is: " + comm.body)
            comm.attachmentSet?.forEach { detail ->
                return detail.attachmentId
            }
        }
    }
    return ""
}
// snippet-end:[support.kotlin.list.comms.main]

// snippet-start:[support.kotlin.add.attach.case.main]
suspend fun addAttachSupportCase(caseIdVal: String?, attachmentSetIdVal: String?) {
    val caseRequest = AddCommunicationToCaseRequest {
        caseId = caseIdVal
        attachmentSetId = attachmentSetIdVal
        communicationBody = "Please refer to attachment for details."
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.addCommunicationToCase(caseRequest)
        if (response.result) {
            println("You have successfully added a communication to an AWS Support case")
        } else {
            println("There was an error adding the communication to an AWS Support case")
        }
    }
}
// snippet-end:[support.kotlin.add.attach.case.main]

// snippet-start:[support.kotlin.add.attach.main]
suspend fun addAttachment(fileAttachment: String): String? {
    val myFile = File(fileAttachment)
    val sourceBytes = (File(fileAttachment).readBytes())
    val attachmentVal = Attachment {
        fileName = myFile.name
        data = sourceBytes
    }

    val setRequest = AddAttachmentsToSetRequest {
        attachments = listOf(attachmentVal)
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.addAttachmentsToSet(setRequest)
        return response.attachmentSetId
    }
}
// snippet-end:[support.kotlin.add.attach.main]

// snippet-start:[support.kotlin.get.open.cases.main]
suspend fun getOpenCase() {
    // Specify the start and end time.
    val now = Instant.now()
    LocalDate.now()
    val yesterday = now.minus(1, ChronoUnit.DAYS)
    val describeCasesRequest = DescribeCasesRequest {
        maxResults = 20
        afterTime = yesterday.toString()
        beforeTime = now.toString()
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeCases(describeCasesRequest)
        response.cases?.forEach { sinCase ->
            println("The case status is " + sinCase.status)
            println("The case Id is " + sinCase.caseId)
            println("The case subject is " + sinCase.subject)
        }
    }
}
// snippet-end:[support.kotlin.get.open.cases.main]

// snippet-start:[support.kotlin.create.case.main]
suspend fun createSupportCase(sevCatListVal: List<String>, sevLevelVal: String): String? {
    val serCode = sevCatListVal[0]
    val caseCategory = sevCatListVal[1]
    val caseRequest = CreateCaseRequest {
        categoryCode = caseCategory.lowercase(Locale.getDefault())
        serviceCode = serCode.lowercase(Locale.getDefault())
        severityCode = sevLevelVal.lowercase(Locale.getDefault())
        communicationBody = "Test issue with " + serCode.lowercase(Locale.getDefault())
        subject = "Test case, please ignore"
        language = "en"
        issueType = "technical"
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.createCase(caseRequest)
        return response.caseId
    }
}
// snippet-end:[support.kotlin.create.case.main]

// snippet-start:[support.kotlin.display.sev.main]
suspend fun displaySevLevels(): String {
    var levelName = ""
    val severityLevelsRequest = DescribeSeverityLevelsRequest {
        language = "en"
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeSeverityLevels(severityLevelsRequest)
        response.severityLevels?.forEach { sevLevel ->
            println("The severity level name is: " + sevLevel.name)
            if (sevLevel.name == "High") {
                levelName = sevLevel.name!!
            }
        }
        return levelName
    }
}
// snippet-end:[support.kotlin.display.sev.main]

// snippet-start:[support.kotlin.display.main]
// Return a List that contains a Service name and Category name.
suspend fun displayServices(): List<String> {
    var serviceCode = ""
    var catName = ""
    val sevCatList = mutableListOf<String>()
    val servicesRequest = DescribeServicesRequest {
        language = "en"
    }

    SupportClient { region = "us-west-2" }.use { supportClient ->
        val response = supportClient.describeServices(servicesRequest)
        println("Get the first 10 services")
        var index = 1

        response.services?.forEach { service ->
            if (index == 11) {
                return@forEach
            }

            println("The Service name is: " + service.name)
            if (service.name == "Account") {
                serviceCode = service.code.toString()
            }

            // Get the categories for this service.
            service.categories?.forEach { cat ->
                println("The category name is: " + cat.name)
                if (cat.name == "Security") {
                    catName = cat.name!!
                }
            }
            index++
        }
    }

    // Push the two values to the list.
    serviceCode.let { sevCatList.add(it) }
    catName.let { sevCatList.add(it) }
    return sevCatList
}
// snippet-end:[support.kotlin.display.main]
// snippet-end:[support.kotlin.scenario.main]
