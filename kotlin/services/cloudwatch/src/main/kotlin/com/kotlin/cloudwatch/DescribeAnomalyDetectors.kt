// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.describe_anomaly_detectors.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DescribeAnomalyDetectorsRequest
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_anomaly_detectors.import]

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
        namespace - The namespace that contains the metric, for example, AWS/EC2.
        metricName - The name of the metric, for example, CPUUtilization.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val namespace = args[0]
    val metricName = args[1]
    describeAnomalyDetectors(namespace, metricName)
}

// snippet-start:[cloudwatch.kotlin.describe_anomaly_detectors.main]
suspend fun describeAnomalyDetectors(
    namespaceVal: String,
    metricNameVal: String,
) {
    val request =
        DescribeAnomalyDetectorsRequest {
            maxResults = 10
            namespace = namespaceVal
            metricName = metricNameVal
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.describeAnomalyDetectors(request)
        response.anomalyDetectors?.forEach { detector ->
            println("Metric name: ${detector.singleMetricAnomalyDetector?.metricName}")
            println("State: ${detector.stateValue}")
        }
    }
}
// snippet-end:[cloudwatch.kotlin.describe_anomaly_detectors.main]
