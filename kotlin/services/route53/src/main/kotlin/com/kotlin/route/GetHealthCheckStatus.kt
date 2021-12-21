// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetHealthCheckStatus.kt demonstrates how to get the status of a specific health check.]
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

// snippet-start:[route53.kotlin.get_health_check_status.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.GetHealthCheckStatusRequest
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.get_health_check_status.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <healthCheckId> 

    Where:
         healthCheckId - the health check id.
    """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
      }

    val healthCheckId = args[0]
    getHealthStatus( healthCheckId)
    }

// snippet-start:[route53.kotlin.get_health_check_status.main]
suspend  fun getHealthStatus(healthCheckIdVal: String?) {

        val statusRequest = GetHealthCheckStatusRequest {
                healthCheckId = healthCheckIdVal
        }

       Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
          val response = route53Client.getHealthCheckStatus(statusRequest)
          response.healthCheckObservations?.forEach { observation ->
                  println("(The health check observation status is ${observation.statusReport?.status}")
         }
       }
 }
// snippet-end:[route53.kotlin.get_health_check_status.main]