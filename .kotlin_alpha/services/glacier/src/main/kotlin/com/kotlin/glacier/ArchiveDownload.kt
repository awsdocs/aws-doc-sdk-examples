//snippet-sourcedescription:[ArchiveDownload.kt demonstrates how to create a job start to retrieve inventory for an Amazon Glacier vault.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glacier

// snippet-start:[glacier.kotlin.download.import]
import aws.sdk.kotlin.services.glacier.GlacierClient
import aws.sdk.kotlin.services.glacier.model.JobParameters
import aws.sdk.kotlin.services.glacier.model.InitiateJobRequest
import aws.sdk.kotlin.services.glacier.model.DescribeJobRequest
import aws.sdk.kotlin.services.glacier.model.GetJobOutputRequest
import aws.sdk.kotlin.services.glacier.model.GlacierException
import aws.smithy.kotlin.runtime.content.toByteArray
import kotlinx.coroutines.delay
import java.io.*
import kotlin.system.exitProcess
// snippet-end:[glacier.kotlin.download.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <vaultName> <accountId> <path>

    Where:
        vaultName - the name of the vault.
        accountId - the account ID value.
        path - the path where the file is written to.
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(1)
    }

    val vaultName = args[0]
    val accountId = args[1]
    val path = args[2]
    val glacierClient = GlacierClient { region = "us-east-1" }
    val jobNum = createJob(glacierClient, vaultName, accountId)
    if (jobNum != null) {
        checkJob(glacierClient, jobNum, vaultName, accountId, path)
    }
    glacierClient.close()
}

// snippet-start:[glacier.kotlin.download.main]
suspend fun createJob(glacier: GlacierClient, vaultNameVal: String?, accountIdVal: String?): String? {
    try
    {
        val job = JobParameters {
            type ="inventory-retrieval"
         }

        val initJob = InitiateJobRequest {
            jobParameters = job
            accountId = accountIdVal
            vaultName = vaultNameVal
        }

        val response = glacier.initiateJob(initJob)
        println("The job ID is: " + response.jobId)
        println("The relative URI path of the job is: " + response.location)
        return response.jobId

    } catch (e: GlacierException) {
        println(e.message)
        glacier.close()
        exitProcess(0)
    }
  }

//  Poll S3 Glacier = Polling a Job may take 4-6 hours according to the Documentation.
suspend fun checkJob(glacier: GlacierClient, jobIdVal: String, name: String, account: String, path: String) {
    try {
        var finished = false
        var jobStatus: String
        var yy = 0
        while (!finished) {
            val jobRequest = DescribeJobRequest {
                jobId = jobIdVal
                accountId = account
                vaultName = name
            }
            val response = glacier.describeJob(jobRequest)
            jobStatus = response.statusCode.toString()
            if (jobStatus.compareTo("Succeeded") == 0) finished = true else {
                println("$yy status is: $jobStatus")
                delay(1000)
            }
            yy++
        }
        println("Job has Succeeded")

        val jobOutputRequest = GetJobOutputRequest {
            jobId = jobIdVal
            vaultName = name
            accountId = account
        }

        // Handle the return value
        glacier.getJobOutput(jobOutputRequest) { resp ->

            val data = resp.body?.toByteArray()
            val myFile = File(path)
            val os: OutputStream = FileOutputStream(myFile)
            os.write(data)
            println("Successfully obtained bytes from a Glacier vault")
            os.close()
        }

    } catch (e: GlacierException) {
        println(e.message)
        glacier.close()
        exitProcess(0)
    } catch (e: InterruptedException) {
        println(e.message)
        exitProcess(1)
    } catch (e: IOException) {
        println(e.message)
        exitProcess(1)
    }
}
// snippet-end:[glacier.kotlin.download.main]
