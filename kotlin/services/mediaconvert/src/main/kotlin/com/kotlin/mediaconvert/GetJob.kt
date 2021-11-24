//snippet-sourcedescription:[GetJob.kt demonstrates how to get information about a specific AWS Elemental MediaConvert job.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[smacdon - AWS ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediaconvert

// snippet-start:[mediaconvert.kotlin.get_job.import]
import aws.sdk.kotlin.runtime.endpoint.*
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.model.*
import kotlin.system.exitProcess
// snippet-end:[mediaconvert.kotlin.get_job.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

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
    val mcClient = MediaConvertClient{region="us-west-2"}
    getSpecificJob(mcClient, jobId)
}

// snippet-start:[mediaconvert.kotlin.get_job.main]
suspend fun getSpecificJob(mcClient: MediaConvertClient, jobId: String?) {

    try {
        val describeEndpoints = DescribeEndpointsRequest {
            maxResults = 20
        }

        val res = mcClient.describeEndpoints(describeEndpoints)

        if (res.endpoints?.size!! <= 0) {
            println("Cannot find MediaConvert service endpoint URL!")
            exitProcess(0)
        }
        val endpointURL = res.endpoints!!.get(0).url!!
        val mediaConvertClient = MediaConvertClient {

            region = "us-west-2"
            endpointResolver = AwsEndpointResolver { service, region ->
                AwsEndpoint(endpointURL, CredentialScope(region = "us-west-2"))
            }
        }

        val jobRequest = GetJobRequest {
            id = jobId
        }

        val response: GetJobResponse = mediaConvertClient.getJob(jobRequest)
        System.out.println("The ARN of the job is ${response.job?.arn}.")

    } catch (ex: MediaConvertException) {
        println(ex.message)
        mcClient.close()
        exitProcess(0)
    }
}
// snippet-end:[mediaconvert.kotlin.get_job.main]