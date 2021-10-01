//snippet-sourcedescription:[GetJobs.kt demonstrates how to list all AWS Glue jobs.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[6/4/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.get_jobs.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetJobsRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.get_jobs.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val glueClient= GlueClient{region ="us-east-1"}
    getAllJobs(glueClient)
    glueClient.close()
}

//snippet-start:[glue.kotlin.get_jobs.main]
suspend fun getAllJobs(glueClient: GlueClient) {

    try {

        val jobsRequest = GetJobsRequest {
            maxResults = 10
        }
        val jobsResponse = glueClient.getJobs(jobsRequest)
        val jobs = jobsResponse.jobs

        if (jobs != null) {
            for (job in jobs) {
                println("Job name is ${job.name}")
            }
        }

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.get_jobs.main]