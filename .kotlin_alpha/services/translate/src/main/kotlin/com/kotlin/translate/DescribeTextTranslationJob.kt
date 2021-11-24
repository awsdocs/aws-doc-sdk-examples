//snippet-sourcedescription:[DescribeTextTranslationJob.kt demonstrates how to describe a translation job.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.translate

// snippet-start:[translate.kotlin._describe_jobs.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.DescribeTextTranslationJobRequest
import kotlin.system.exitProcess
// snippet-end:[translate.kotlin._describe_jobs.import]

suspend fun main(args: Array<String>){

    val usage = """
    Usage:
        <jobId> 

    Where:
         jobId - a translation job ID value. You can obtain this value from the BatchTranslation example.
    """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
       }

    val jobId = args[0]
    describeTranslationJob(jobId)
}

// snippet-start:[translate.kotlin._describe_jobs.main]
suspend fun describeTranslationJob(id: String?) {

        val textTranslationJobRequest = DescribeTextTranslationJobRequest {
            jobId = id!!
         }

        TranslateClient { region = "us-west-2" }.use { translateClient ->
            val jobResponse =  translateClient.describeTextTranslationJob(textTranslationJobRequest)
            println("The job status is ${jobResponse.textTranslationJobProperties?.jobStatus}.")
        }
    }
// snippet-end:[translate.kotlin._describe_jobs.main]