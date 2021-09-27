//snippet-sourcedescription:[DescribeTextTranslationJob.kt demonstrates how to describe a translation job.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/02/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.translate

// snippet-start:[translate.kotlin._describe_jobs.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.DescribeTextTranslationJobRequest
import aws.sdk.kotlin.services.translate.model.TranslateException
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
    val translateClient = TranslateClient { region = "us-west-2" }
    describeTranslationJob(translateClient, jobId)
    translateClient.close()
}

// snippet-start:[translate.kotlin._describe_jobs.main]
suspend fun describeTranslationJob(translateClient: TranslateClient, id: String?) {
        try {
            val textTranslationJobRequest = DescribeTextTranslationJobRequest {
                jobId = id!!
            }

            val jobResponse =  translateClient.describeTextTranslationJob(textTranslationJobRequest)
            println("The job status is ${jobResponse.textTranslationJobProperties?.jobStatus}.")

        } catch (ex: TranslateException) {
            println(ex.message)
            exitProcess(0)
        }
    }
// snippet-end:[translate.kotlin._describe_jobs.main]