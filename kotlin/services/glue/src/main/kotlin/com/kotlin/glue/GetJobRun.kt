// snippet-sourcedescription:[GetJobRun.kt demonstrates how to get a job run request.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Glue]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

// snippet-start:[glue.kotlin.get_job.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetJobRunRequest
import kotlin.system.exitProcess
// snippet-end:[glue.kotlin.get_job.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <jobName> <runId>

    Where:
        jobName - the name of the job. 
        runId - the run id value that you can obtain from the AWS Management Console. 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val jobName = args[0]
    val runId = args[1]
    getGlueJobRun(jobName, runId)
}

// snippet-start:[glue.kotlin.get_job.main]
suspend fun getGlueJobRun(jobNameVal: String?, runIdVal: String?) {

    val request = GetJobRunRequest {
        jobName = jobNameVal
        runId = runIdVal
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val runResponse = glueClient.getJobRun(request)
        println("Job status is ${runResponse.jobRun?.toString()}")
    }
}
// snippet-end:[glue.kotlin.get_job.main]
