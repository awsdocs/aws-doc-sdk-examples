// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.put_metric_data.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.MetricDatum
import aws.sdk.kotlin.services.cloudwatch.model.PutMetricDataRequest
import aws.sdk.kotlin.services.cloudwatch.model.StandardUnit
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_metric_data.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <namespace> <metricName>

    Where:
        namespace - The namespace to publish the metric data to, for example, SDK/Example.
        metricName - The name of the metric to publish data for.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val namespace = args[0]
    val metricName = args[1]
    addMetricData(namespace, metricName)
}

// snippet-start:[cloudwatch.kotlin.put_metric_data.main]
suspend fun addMetricData(
    namespaceVal: String,
    metricNameVal: String,
) {
    val time =
        aws.smithy.kotlin.runtime.time.Instant
            .now()

    val datum =
        MetricDatum {
            metricName = metricNameVal
            unit = StandardUnit.None
            value = 1001.00
            timestamp = time
        }

    val datum2 =
        MetricDatum {
            metricName = metricNameVal
            unit = StandardUnit.None
            value = 1002.00
            timestamp = time
        }

    val request =
        PutMetricDataRequest {
            namespace = namespaceVal
            metricData = listOf(datum, datum2)
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.putMetricData(request)
        println("Added metric values for metric $metricNameVal.")
    }
}
// snippet-end:[cloudwatch.kotlin.put_metric_data.main]
