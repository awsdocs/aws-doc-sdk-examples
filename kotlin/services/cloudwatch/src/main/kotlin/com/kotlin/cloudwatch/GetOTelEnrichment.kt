// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.get_otel_enrichment.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.GetOTelEnrichmentRequest
import aws.sdk.kotlin.services.cloudwatch.model.OTelEnrichmentStatus
// snippet-end:[cloudwatch.kotlin.get_otel_enrichment.import]

/**
Gets the current OTel enrichment status for the account.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getOTelEnrichmentStatus()
}

// snippet-start:[cloudwatch.kotlin.get_otel_enrichment.main]
suspend fun getOTelEnrichmentStatus(): OTelEnrichmentStatus? {
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.getOTelEnrichment(GetOTelEnrichmentRequest {})
        val status = response.status
        when (status) {
            is OTelEnrichmentStatus.Running ->
                println("OTel enrichment is running. Vended metrics are queryable with PromQL")
            is OTelEnrichmentStatus.Stopped ->
                println("OTel enrichment is stopped. Start it to enrich vended metrics")
            else -> println("OTel enrichment status is ${status?.value}")
        }
        return status
    }
}
// snippet-end:[cloudwatch.kotlin.get_otel_enrichment.main]
