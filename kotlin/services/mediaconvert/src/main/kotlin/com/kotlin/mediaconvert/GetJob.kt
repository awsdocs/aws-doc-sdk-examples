// snippet-sourcedescription:[GetJob.kt demonstrates how to get information about a specific AWS Elemental MediaConvert job.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Elemental MediaConvert]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediaconvert

// snippet-start:[mediaconvert.kotlin.get_job.import]
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.endpoints.EndpointProvider
import aws.sdk.kotlin.services.mediaconvert.model.DescribeEndpointsRequest
import aws.sdk.kotlin.services.mediaconvert.model.GetJobRequest
import aws.sdk.kotlin.services.mediaconvert.model.GetJobResponse
import aws.smithy.kotlin.runtime.http.endpoints.Endpoint
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
    val mcClient = MediaConvertClient { region = "us-west-2" }
    getSpecificJob(mcClient, jobId)
}

// snippet-start:[mediaconvert.kotlin.get_job.main]
suspend fun getSpecificJob(mcClient: MediaConvertClient, jobId: String?) {
    val describeEndpoints = DescribeEndpointsRequest {
        maxResults = 20
    }

    val res = mcClient.describeEndpoints(describeEndpoints)
    if (res.endpoints?.size!! <= 0) {
        println("Cannot find MediaConvert service endpoint URL!")
        exitProcess(0)
    }

    val endpointURL = res.endpoints!!.get(0).url!!
    val mediaConvert = MediaConvertClient.fromEnvironment {
        region = "us-west-2"
        endpointProvider = EndpointProvider {
            Endpoint(endpointURL)
        }
    }

    val jobRequest = GetJobRequest {
        id = jobId
    }

    val response: GetJobResponse = mediaConvert.getJob(jobRequest)
    println("The ARN of the job is ${response.job?.arn}.")
}
// snippet-end:[mediaconvert.kotlin.get_job.main]
