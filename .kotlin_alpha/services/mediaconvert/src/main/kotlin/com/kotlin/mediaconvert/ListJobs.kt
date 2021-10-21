//snippet-sourcedescription:[ListJobs.kt demonstrates how to get information about all completed AWS Elemental MediaConvert jobs.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/28/2021]
//snippet-sourceauthor:[smacdon - AWS ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediaconvert


// snippet-start:[mediaconvert.kotlin.list_jobs.import]
import aws.sdk.kotlin.runtime.endpoint.Endpoint
import aws.sdk.kotlin.runtime.endpoint.EndpointResolver
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.model.DescribeEndpointsRequest
import aws.sdk.kotlin.services.mediaconvert.model.JobStatus
import aws.sdk.kotlin.services.mediaconvert.model.ListJobsRequest
import aws.sdk.kotlin.services.mediaconvert.model.MediaConvertException
import java.net.URI
import kotlin.system.exitProcess

// snippet-end:[mediaconvert.kotlin.list_jobs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val mcClient = MediaConvertClient{region="us-west-2"}
    listCompleteJobs(mcClient)
}

// snippet-start:[mediaconvert.kotlin.list_jobs.main]
suspend fun listCompleteJobs(mcClient: MediaConvertClient) {

    try {

        val describeEndpoints = DescribeEndpointsRequest {
            maxResults = 20
        }

        val res = mcClient.describeEndpoints(describeEndpoints)

        if (res.endpoints?.size!! <= 0) {
            println("Cannot find MediaConvert service endpoint URL!")
            exitProcess(0)
        }
        val endpointURL = res.endpoints!!.get(0).url.toString()
        val uri = URI(endpointURL)
        val domain = uri.host

        // Need to set an Endpoint override here.
       val emc = MediaConvertClient {
            region = "us-west-2"
            endpointResolver = object : EndpointResolver {
                override suspend fun resolve(service: String, region: String): Endpoint {
                    return Endpoint(domain, "https")
                }
            }
       }

        val jobsRequest = ListJobsRequest {
            maxResults = 10
            status = JobStatus.fromValue("COMPLETE")
        }

        val jobsResponse = emc.listJobs(jobsRequest)

        val jobs = jobsResponse.jobs
        if (jobs != null) {
            for (job in jobs) {
                System.out.println("The JOB ARN is ${job.arn}")
            }
        }

    } catch (ex: MediaConvertException) {
        println(ex.message)
        mcClient.close()
        exitProcess(0)
    }
}
// snippet-end:[mediaconvert.kotlin.list_jobs.main]