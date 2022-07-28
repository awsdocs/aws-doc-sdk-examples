// snippet-sourcedescription:[GetMetricData.kt demonstrates how to get Amazon CloudWatch metric data.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.get_metric_data.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.GetMetricDataRequest
import aws.sdk.kotlin.services.cloudwatch.model.Metric
import aws.sdk.kotlin.services.cloudwatch.model.MetricDataQuery
import aws.sdk.kotlin.services.cloudwatch.model.MetricStat
// snippet-end:[cloudwatch.kotlin.get_metric_data.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getMetData()
}

// snippet-start:[cloudwatch.kotlin.get_metric_data.main]
suspend fun getMetData() {

    val start = aws.smithy.kotlin.runtime.time.Instant.fromIso8601("2019-10-23T10:12:35Z")
    val endDate = aws.smithy.kotlin.runtime.time.Instant.now()
    val met = Metric {
        metricName = "DiskReadBytes"
        namespace = "AWS/EC2"
    }

    val metStat = MetricStat {
        stat = "Minimum"
        period = 60
        metric = met
    }

    val dataQUery = MetricDataQuery {
        metricStat = metStat
        id = "foo2"
        returnData = true
    }

    val dq = mutableListOf<MetricDataQuery>()
    dq.add(dataQUery)

    val request = GetMetricDataRequest {
        maxDatapoints = 100
        startTime = start
        endTime = endDate
        metricDataQueries = dq
    }

    CloudWatchClient { region = "us-east-1" }.use { cwClient ->
        val response = cwClient.getMetricData(request)
        response.metricDataResults?.forEach { item ->
            println("The label is ${item.label}")
            println("The status code is ${item.statusCode}")
        }
    }
}
// snippet-end:[cloudwatch.kotlin.get_metric_data.main]
