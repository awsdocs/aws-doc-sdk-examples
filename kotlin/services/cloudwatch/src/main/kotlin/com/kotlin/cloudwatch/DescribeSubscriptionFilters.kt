//snippet-sourcedescription:[DescribeSubscriptionFilters.kt demonstrates how to get a list of Amazon CloudWatch subscription filters associated with a log group.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.describe_subscription_filters.import]
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.DescribeSubscriptionFiltersRequest
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
    describeFilters(logGroup)
 }

// snippet-start:[cloudwatch.kotlin.describe_subscription_filters.main]
suspend fun describeFilters(logGroup: String) {

        val request =  DescribeSubscriptionFiltersRequest {
          logGroupName = logGroup
          limit = 1
        }

        CloudWatchLogsClient { region = "us-west-2" }.use { cwlClient ->
          val response = cwlClient.describeSubscriptionFilters(request)
          response.subscriptionFilters?.forEach { filter ->
              println("Retrieved filter with name  ${filter.filterName} pattern ${filter.filterPattern} and destination ${filter.destinationArn}" )
          }
        }
 }
// snippet-end:[cloudwatch.kotlin.describe_subscription_filters.main]