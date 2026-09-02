// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.start_otel_enrichment.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.StartOTelEnrichmentRequest
// snippet-end:[cloudwatch.kotlin.start_otel_enrichment.import]

/**
Turns on OTel enrichment for the account. Once enrichment is running, CloudWatch vended
metrics that carry a resource identifier dimension, such as the Amazon EC2
CPUUtilization metric with its InstanceId dimension, are decorated with resource ARN and
resource tag labels and become queryable with PromQL.

Resource tags on telemetry must already be enabled for the account before you call this
operation.

Note that OTLP metric ingestion is not an AWS SDK operation. To send OpenTelemetry
metrics to CloudWatch, point an OpenTelemetry collector or the AWS Distro for
OpenTelemetry (ADOT) SDK at the CloudWatch OTLP metrics endpoint,
https://monitoring.<region>.amazonaws.com/v1/metrics.

Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    startOTelEnrichment()
}

// snippet-start:[cloudwatch.kotlin.start_otel_enrichment.main]
suspend fun startOTelEnrichment() {
    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.startOTelEnrichment(StartOTelEnrichmentRequest {})
        println("Successfully started OTel enrichment for this account")
    }
}
// snippet-end:[cloudwatch.kotlin.start_otel_enrichment.main]
