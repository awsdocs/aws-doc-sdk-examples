//snippet-sourcedescription:[DescribeSubscriptionFilters.kt demonstrates how to get a list of Amazon CloudWatch subscription filters associated with a log group.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/11/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.describe_subscription_filters.import]
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.DescribeSubscriptionFiltersRequest
import aws.sdk.kotlin.services.cloudwatchlogs.model.DescribeSubscriptionFiltersResponse
import aws.sdk.kotlin.services.cloudwatchlogs.model.SubscriptionFilter
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.describe_subscription_filters.import]

suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <logGroup>  

    Where:
         logGroup - a log group name (testgroup).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val logGroup = args[0]
    val cwlClient = CloudWatchLogsClient{region="us-west-2"}
    describeFilters(cwlClient, logGroup)
    cwlClient.close()
}

// snippet-start:[cloudwatch.kotlin.describe_subscription_filters.main]
suspend fun describeFilters(cwlClient: CloudWatchLogsClient, logGroup: String?) {
    try {
        var done = false
        var newToken: String? = null
        while (!done) {
            var response: DescribeSubscriptionFiltersResponse

            if (newToken == null) {
                val request = DescribeSubscriptionFiltersRequest {
                    logGroupName = logGroup
                    limit = 1
                }
                response = cwlClient.describeSubscriptionFilters(request)
            } else {

                val request = DescribeSubscriptionFiltersRequest {
                    nextToken = newToken
                    logGroupName = logGroup
                    limit = 1
                }
                response = cwlClient.describeSubscriptionFilters(request)
            }

            val subList = response.subscriptionFilters
            if (subList != null) {
                for (filter:SubscriptionFilter in subList)
                    println("Retrieved filter with name  ${filter.filterName} pattern ${filter.filterPattern} and destination ${filter.destinationArn}" )

            }

            if (response.nextToken == null) {
                done = true
            } else {
                newToken = response.nextToken
            }
        }
    } catch (ex: CloudWatchException) {
        println(ex.message)
        cwlClient.close()
        exitProcess(0)
    }
 }
// snippet-end:[cloudwatch.kotlin.describe_subscription_filters.main]