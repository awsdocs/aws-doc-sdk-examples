// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteHealthCheck.kt demonstrates how to delete a health check.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

// snippet-start:[route53.kotlin.delete_health_check.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.DeleteHealthCheckRequest
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.delete_health_check.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <id> 

    Where:
        id - The health check id. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val id = args[0]
    delHealthCheck(id)
}

// snippet-start:[route53.kotlin.delete_health_check.main]
suspend fun delHealthCheck(id: String?) {

    val delRequest = DeleteHealthCheckRequest {
        healthCheckId = id
    }

    Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
        route53Client.deleteHealthCheck(delRequest)
        println("The HealthCheck with id $id was deleted")
    }
}
// snippet-end:[route53.kotlin.delete_health_check.main]
