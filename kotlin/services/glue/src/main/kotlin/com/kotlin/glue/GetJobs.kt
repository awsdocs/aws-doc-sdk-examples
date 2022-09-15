// snippet-sourcedescription:[GetJobs.kt demonstrates how to list all AWS Glue jobs.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Glue]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

// snippet-start:[glue.kotlin.get_jobs.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetJobsRequest
// snippet-end:[glue.kotlin.get_jobs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    getAllJobs()
}

// snippet-start:[glue.kotlin.get_jobs.main]
suspend fun getAllJobs() {

    val request = GetJobsRequest {
        maxResults = 10
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getJobs(request)
        response.jobs?.forEach { job ->
            println("Job name is ${job.name}")
        }
    }
}
// snippet-end:[glue.kotlin.get_jobs.main]
