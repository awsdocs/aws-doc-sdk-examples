// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateHealthCheck.kt demonstrates how to create a new health check.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[5-10-2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

// snippet-start:[route53.kotlin.create_health_check.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.HealthCheckConfig
import aws.sdk.kotlin.services.route53.model.HealthCheckType
import aws.sdk.kotlin.services.route53.model.CreateHealthCheckRequest
import aws.sdk.kotlin.services.route53.model.Route53Exception
import java.util.UUID
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.create_health_check.import]

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
    val id = createCheck(route53Client, domainName)
    println("The health check id is $id")
    route53Client.close()
}

// snippet-start:[route53.kotlin.create_health_check.main]
suspend fun createCheck(route53Client: Route53Client, domainName: String?): String? {
        try {

            // You must use a unique CallerReference string.
            val callerReferenceVal = UUID.randomUUID().toString()

            val config = HealthCheckConfig {
                fullyQualifiedDomainName =domainName
                port =80
                type = HealthCheckType.Http
            }

            val healthCheckRequest = CreateHealthCheckRequest {
                callerReference = callerReferenceVal
                healthCheckConfig = config
            }

            // Create the Health Check and return the id value.
            val healthResponse = route53Client.createHealthCheck(healthCheckRequest)
            return healthResponse.healthCheck?.id

        } catch (e: Route53Exception) {
            System.err.println(e.message)
            exitProcess(0)
        }
      }
// snippet-end:[route53.kotlin.create_health_check.main]
