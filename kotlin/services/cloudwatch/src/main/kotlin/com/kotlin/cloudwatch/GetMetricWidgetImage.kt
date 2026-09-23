// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.get_metric_widget_image.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.GetMetricWidgetImageRequest
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.get_metric_widget_image.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <fileName>

    Where:
        fileName - The name of the PNG file to write the graph image to, for example, metric.png.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val fileName = args[0]
    getAndOpenMetricImage(fileName)
}

// snippet-start:[cloudwatch.kotlin.get_metric_widget_image.main]
suspend fun getAndOpenMetricImage(fileName: String) {
    println("Getting image data for a custom metric.")
    val myJSON = """{
        "title": "Example Metric Graph",
        "view": "timeSeries",
        "stacked ": false,
        "period": 10,
        "width": 1400,
        "height": 600,
        "metrics": [
            [
            "AWS/Billing",
            "EstimatedCharges",
            "Currency",
            "USD"
            ]
        ]
        }"""

    val request =
        GetMetricWidgetImageRequest {
            metricWidget = myJSON
        }

    CloudWatchClient.fromEnvironment { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.getMetricWidgetImage(request)
        val bytes = response.metricWidgetImage
        if (bytes != null) {
            File(fileName).writeBytes(bytes)
            println("You have successfully written data to $fileName.")
        }
    }
}
// snippet-end:[cloudwatch.kotlin.get_metric_widget_image.main]
