// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.route

// snippet-start:[route53.kotlin.list_health_checks.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.ListHealthChecksRequest
// snippet-end:[route53.kotlin.list_health_checks.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllHealthChecks()
}

// snippet-start:[route53.kotlin.list_health_checks.main]
suspend fun listAllHealthChecks() {
    val requestOb =
        ListHealthChecksRequest {
            this.maxItems = 10
        }

    Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
        val response = route53Client.listHealthChecks(requestOb)
        response.healthChecks?.forEach { check ->
            println("The health check id is ${check.id}")
            println("The health threshold is ${check.healthCheckConfig?.healthThreshold}")
            println("The type is ${check.healthCheckConfig?.type}")
        }
    }
}
// snippet-end:[route53.kotlin.list_health_checks.main]
