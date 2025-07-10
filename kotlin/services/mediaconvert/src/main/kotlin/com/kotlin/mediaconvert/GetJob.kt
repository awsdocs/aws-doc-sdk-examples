// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.mediaconvert

// snippet-start:[mediaconvert.kotlin.get_job.import]
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.model.DescribeEndpointsRequest
import aws.sdk.kotlin.services.mediaconvert.model.GetJobRequest
import kotlin.system.exitProcess
// snippet-end:[mediaconvert.kotlin.get_job.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """
        GetJob <jobId> 

    Where:
        jobId - the job id value.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val jobId = args[0]
    val mcClient = MediaConvertClient.fromEnvironment  { region = "us-west-2" }
    getSpecificJob(mcClient, jobId)
}

// snippet-start:[mediaconvert.kotlin.get_job.main]
suspend fun getSpecificJob(mcClient: MediaConvertClient, jobId: String) {
    // 1. Discover the correct endpoint
    val res = mcClient.describeEndpoints(DescribeEndpointsRequest { maxResults = 1 })
    var endpointUrl = res.endpoints?.firstOrNull()?.url
        ?: error(" No MediaConvert endpoint found")

    // 2. Create a new client using the endpoint
    val clientWithEndpoint = MediaConvertClient {
        region = "us-west-2"
        endpointUrl = endpointUrl
    }

    // 3. Get the job details
    val jobResponse = clientWithEndpoint.getJob(GetJobRequest { id = jobId })
    val job = jobResponse.job

    println("Job status: ${job?.status}")
    println("Job ARN: ${job?.arn}")
    println("Output group count: ${job?.settings?.outputGroups?.size}")
}
// snippet-end:[mediaconvert.kotlin.get_job.main]
