// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHostedZone.kt demonstrates how to create a hosted zone.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

// snippet-start:[route53.kotlin.create_hosted_zone.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.CreateHostedZoneRequest
import java.util.UUID
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.create_hosted_zone.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <domainName> 

    Where:
        domainName - The fully qualified domain name. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val domainName = args[0]
    val id = createZone(domainName)
    println("The hosted zone id is $id")
}

// snippet-start:[route53.kotlin.create_hosted_zone.main]
suspend fun createZone(domainName: String?): String? {

    // You must use a unique CallerReference string.
    val callerReferenceVal = UUID.randomUUID().toString()
    val zoneRequest = CreateHostedZoneRequest {
        callerReference = callerReferenceVal
        name = domainName
    }

    Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
        val zoneResponse = route53Client.createHostedZone(zoneRequest)
        return zoneResponse.hostedZone?.id
    }
}
// snippet-end:[route53.kotlin.create_hosted_zone.main]
