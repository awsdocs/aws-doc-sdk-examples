// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.stop_otel_enrichment.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.StopOTelEnrichmentRequest
// snippet-end:[cloudwatch.kotlin.stop_otel_enrichment.import]

/**
Turns off OTel enrichment for the account. Existing PromQL alarms are not deleted, but
vended metrics stop being enriched with resource ARN and tag labels, so queries that
select on those labels stop matching.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    stopOTelEnrichment()
}

// snippet-start:[cloudwatch.kotlin.stop_otel_enrichment.main]
suspend fun stopOTelEnrichment() {
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.stopOTelEnrichment(StopOTelEnrichmentRequest {})
        println("Successfully stopped OTel enrichment for this account")
    }
}
// snippet-end:[cloudwatch.kotlin.stop_otel_enrichment.main]
