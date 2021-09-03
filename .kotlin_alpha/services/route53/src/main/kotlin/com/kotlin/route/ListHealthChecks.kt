// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListHealthChecks.kt demonstrates how to list health checks.]
// snippet-keyword:[AWS SDK for Kotlin]
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

// snippet-start:[route53.kotlin.list_health_checks.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.ListHealthChecksRequest
import aws.sdk.kotlin.services.route53.model.HealthCheck
import aws.sdk.kotlin.services.route53.model.Route53Exception
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.list_health_checks.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val route53Client = Route53Client{region = "AWS_GLOBAL"}
    listAllHealthChecks(route53Client)
    route53Client.close()
}

// snippet-start:[route53.kotlin.list_health_checks.main]
suspend fun listAllHealthChecks(route53Client: Route53Client) {
        try {

            val requestOb = ListHealthChecksRequest{
                this.maxItems = 10
            }

            val checksResponse = route53Client.listHealthChecks(requestOb)
            val checklist: List<HealthCheck>? = checksResponse.healthChecks
            if (checklist != null) {
                for (check in checklist) {
                    System.out.println("The health check id is ${check.id}")
                    System.out.println("The health threshold is ${check.healthCheckConfig?.healthThreshold}")
                    System.out.println("The type is ${check.healthCheckConfig?.type.toString()}")
                }
            }

        } catch (e: Route53Exception) {
            System.err.println(e.message)
            exitProcess(0)
        }
 }
// snippet-end:[route53.kotlin.list_health_checks.main]