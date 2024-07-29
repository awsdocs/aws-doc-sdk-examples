// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.translate

// snippet-start:[translate.kotlin._batch.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.DescribeTextTranslationJobRequest
import aws.sdk.kotlin.services.translate.model.InputDataConfig
import aws.sdk.kotlin.services.translate.model.OutputDataConfig
import aws.sdk.kotlin.services.translate.model.StartTextTranslationJobRequest
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[translate.kotlin._batch.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <s3Uri> <s3UriOut> <jobName> <dataAccessRoleArn>

    Where:
        s3Uri - The URI of the Amazon S3 bucket where the documents to translate are located.
        s3UriOut - The URI of the Amazon S3 bucket where the translated documents are saved to.
        jobName - The job name.
        dataAccessRoleArn - The Amazon Resource Name (ARN) value of the role required for translation jobs.
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val s3Uri = args[0]
    val s3UriOut = args[1]
    val jobName = args[2]
    val dataAccessRoleArn = args[3]
    translateDocuments(s3Uri, s3UriOut, jobName, dataAccessRoleArn)
}

// snippet-start:[translate.kotlin._batch.main]
suspend fun translateDocuments(
    s3UriVal: String?,
    s3UriOutVal: String?,
    jobNameVal: String?,
    dataAccessRoleArnVal: String?,
): String? {
    val sleepTime: Long = 5
    val dataConfig =
        InputDataConfig {
            s3Uri = s3UriVal
            contentType = "text/plain"
        }

    val outputDataConfigVal =
        OutputDataConfig {
            s3Uri = s3UriOutVal
        }

    val myList = mutableListOf<String>()
    myList.add("fr")

    val textTranslationJobRequest =
        StartTextTranslationJobRequest {
            jobName = jobNameVal
            dataAccessRoleArn = dataAccessRoleArnVal
            inputDataConfig = dataConfig
            outputDataConfig = outputDataConfigVal
            sourceLanguageCode = "en"
            targetLanguageCodes = myList
        }

    TranslateClient { region = "us-west-2" }.use { translateClient ->
        val textTranslationJobResponse = translateClient.startTextTranslationJob(textTranslationJobRequest)

        // Keep checking until job is done.
        val jobDone = false
        var jobStatus: String
        val jobIdVal: String? = textTranslationJobResponse.jobId

        val jobRequest =
            DescribeTextTranslationJobRequest {
                jobId = jobIdVal
            }

        while (!jobDone) {
            // Check status on each loop.
            val response = translateClient.describeTextTranslationJob(jobRequest)
            jobStatus = response.textTranslationJobProperties?.jobStatus.toString()
            println(jobStatus)

            if (jobStatus.contains("COMPLETED")) {
                break
            } else {
                print(".")

                delay(sleepTime * 1000)
            }
        }
        return textTranslationJobResponse.jobId
    }
}
// snippet-end:[translate.kotlin._batch.main]
