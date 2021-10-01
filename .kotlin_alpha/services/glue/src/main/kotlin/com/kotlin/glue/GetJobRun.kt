//snippet-sourcedescription:[GetJobRun.kt demonstrates how to get a job run request.]
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

//snippet-start:[glue.kotlin.get_job.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetJobRunRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.get_job.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

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
    val glueClient= GlueClient{region ="us-east-1"}
    getGlueJobRun(glueClient, jobName, runId)
    glueClient.close()
}

//snippet-start:[glue.kotlin.get_job.main]
suspend fun getGlueJobRun(glueClient: GlueClient, jobNameVal: String?, runIdVal: String?) {

    try {

        val jobRunRequest = GetJobRunRequest {
            jobName = jobNameVal
            runId = runIdVal
        }

        val runResponse = glueClient.getJobRun(jobRunRequest)
        println("Job status is ${runResponse.jobRun?.toString()}")

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.get_job.main]