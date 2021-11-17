// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHostedZone.kt demonstrates how to create a hosted zone.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/5/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

// snippet-start:[route53.kotlin.create_hosted_zone.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.CreateHostedZoneRequest
import aws.sdk.kotlin.services.route53.model.Route53Exception
import java.util.UUID
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.create_hosted_zone.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <domainName> 

    Where:
        domainName - the fully qualified domain name. 
    """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
     }

    val domainName = args[0]
    val route53Client = Route53Client{region = "AWS_GLOBAL"}
    val id = createZone(route53Client, domainName)
    println("The hosted zone id is $id")
    route53Client.close()
}

// snippet-start:[route53.kotlin.create_hosted_zone.main]
suspend fun createZone(route53Client: Route53Client, domainName: String?): String? {
        try {

            // You must use a unique CallerReference string.
            val callerReferenceVal = UUID.randomUUID().toString()
            val zoneRequest = CreateHostedZoneRequest {
                callerReference = callerReferenceVal
                name= domainName
            }

            val zoneResponse = route53Client.createHostedZone(zoneRequest)
            return zoneResponse.hostedZone?.id

        } catch (e: Route53Exception) {
            System.err.println(e.message)
            exitProcess(0)
        }
    }
// snippet-end:[route53.kotlin.create_hosted_zone.main]