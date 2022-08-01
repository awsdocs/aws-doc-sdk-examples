// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[UpdateHealthCheck.kt demonstrates how to update a health check.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

// snippet-start:[route53.kotlin.update_health_check.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.UpdateHealthCheckRequest
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.update_health_check.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <healthCheckId> 

    Where:
         healthCheckId - The health check id.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val healthCheckId = args[0]
    updateSpecificHealthCheck(healthCheckId)
}

// snippet-start:[route53.kotlin.update_health_check.main]
suspend fun updateSpecificHealthCheck(id: String?) {

    val checkRequest = UpdateHealthCheckRequest {
        healthCheckId = id
        disabled = true
    }

    Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
        val healthResponse = route53Client.updateHealthCheck(checkRequest)
        println("The health check with id ${healthResponse.healthCheck?.id} was updated!")
    }
}
// snippet-end:[route53.kotlin.update_health_check.main]
