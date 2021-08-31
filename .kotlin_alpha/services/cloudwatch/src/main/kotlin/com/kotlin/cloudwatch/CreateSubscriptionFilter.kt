//snippet-sourcedescription:[CreateSubscriptionFilter.kt demonstrates how to create an Amazon CloudWatch log subscription filter.]
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

// snippet-start:[cloudwatch.kotlin.create_filter.import]
import aws.sdk.kotlin.services.cloudwatchlogs.CloudWatchLogsClient
import aws.sdk.kotlin.services.cloudwatchlogs.model.PutSubscriptionFilterRequest
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.create_filter.import]

suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <filter> <pattern> <logGroup> <functionArn> 

    Where:
        filter - a filter name (for example, myfilter).
        pattern - a filter pattern (for example, ERROR).
        logGroup - a log group name (testgroup).
        functionArn - an AWS Lambda function ARN (for example, arn:aws:lambda:us-west-2:xxxxxx047983:function:lamda1) .
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val filter =  args[0]
    val pattern = args[1]
    val logGroup = args[2]
    val functionArn = args[3]

    val cwlClient = CloudWatchLogsClient{region="us-west-2"}
    putSubFilters(cwlClient, filter, pattern, logGroup, functionArn)
    cwlClient.close()
}

// snippet-start:[cloudwatch.kotlin.create_filter.main]
suspend fun putSubFilters(
    cwlClient: CloudWatchLogsClient,
    filter: String?,
    pattern: String?,
    logGroup: String?,
    functionArn: String?
) {
    try {
        val request = PutSubscriptionFilterRequest {
            filterName = filter
            filterPattern = pattern
            logGroupName = logGroup
            destinationArn = functionArn
        }

        cwlClient.putSubscriptionFilter(request)
        println("Successfully created CloudWatch logs subscription filter named $filter")

    } catch (ex: CloudWatchException) {
        println(ex.message)
        cwlClient.close()
        exitProcess(0)
    }
}
// snippet-end:[cloudwatch.kotlin.create_filter.main]