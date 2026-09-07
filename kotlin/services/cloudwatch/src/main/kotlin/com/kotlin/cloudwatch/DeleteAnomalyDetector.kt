// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.delete_anomaly_detector.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.DeleteAnomalyDetectorRequest
import aws.sdk.kotlin.services.cloudwatch.model.SingleMetricAnomalyDetector
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.delete_anomaly_detector.import]

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
    deleteAnomalyDetector(namespace, metricName)
}

// snippet-start:[cloudwatch.kotlin.delete_anomaly_detector.main]
suspend fun deleteAnomalyDetector(
    namespaceVal: String,
    metricNameVal: String,
) {
    val singleMetricAnomalyDetectorVal =
        SingleMetricAnomalyDetector {
            namespace = namespaceVal
            metricName = metricNameVal
            stat = "Maximum"
        }

    val request =
        DeleteAnomalyDetectorRequest {
            singleMetricAnomalyDetector = singleMetricAnomalyDetectorVal
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        cwClient.deleteAnomalyDetector(request)
        println("Successfully deleted the anomaly detector for metric $metricNameVal.")
    }
}
// snippet-end:[cloudwatch.kotlin.delete_anomaly_detector.main]
