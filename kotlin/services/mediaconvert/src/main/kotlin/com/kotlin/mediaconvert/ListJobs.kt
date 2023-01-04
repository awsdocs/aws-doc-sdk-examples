// snippet-sourcedescription:[ListJobs.kt demonstrates how to get information about all completed AWS Elemental MediaConvert jobs.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Elemental MediaConvert]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediaconvert

// snippet-start:[mediaconvert.kotlin.list_jobs.import]
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.model.DescribeEndpointsRequest
import aws.sdk.kotlin.services.mediaconvert.model.JobStatus
import aws.sdk.kotlin.services.mediaconvert.model.ListJobsRequest
import aws.smithy.kotlin.runtime.http.endpoints.Endpoint
import aws.smithy.kotlin.runtime.http.endpoints.EndpointProvider
import kotlin.system.exitProcess
// snippet-end:[mediaconvert.kotlin.list_jobs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main() {
    val mcClient = MediaConvertClient { region = "us-west-2" }
    listCompleteJobs(mcClient)
}

// snippet-start:[mediaconvert.kotlin.list_jobs.main]
suspend fun listCompleteJobs(mcClient: MediaConvertClient) {
    val describeEndpoints = DescribeEndpointsRequest {
        maxResults = 20
    }

    val res = mcClient.describeEndpoints(describeEndpoints)
    if (res.endpoints?.size!! <= 0) {
        println("Cannot find MediaConvert service endpoint URL!")
        exitProcess(0)
    }
    val endpointURL = res.endpoints!![0].url!!
    val mediaConvert = MediaConvertClient.fromEnvironment {
        region = "us-west-2"
        endpointProvider = EndpointProvider {
            Endpoint(endpointURL)
        }
    }

    val jobsRequest = ListJobsRequest {
        maxResults = 10
        status = JobStatus.fromValue("COMPLETE")
    }

    val jobsResponse = mediaConvert.listJobs(jobsRequest)
    val jobs = jobsResponse.jobs
    if (jobs != null) {
        for (job in jobs) {
            println("The JOB ARN is ${job.arn}")
        }
    }
}
// snippet-end:[mediaconvert.kotlin.list_jobs.main]
