//snippet-sourcedescription:[ListTextTranslationJobs.kt demonstrates how to list all translation jobs.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/02/2021]
//snippet-sourceauthor:[scmacdon-aws]

package com.kotlin.translate

// snippet-start:[translate.kotlin._list_jobs.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.ListTextTranslationJobsRequest
import aws.sdk.kotlin.services.translate.model.TranslateException
import kotlin.system.exitProcess

// snippet-end:[translate.kotlin._list_jobs.import]

suspend fun main(){

    val translateClient = TranslateClient { region = "us-west-2" }
    getTranslationJobs(translateClient)
    translateClient.close()
}

// snippet-start:[translate.kotlin._list_jobs.main]
suspend fun getTranslationJobs(translateClient: TranslateClient) {
        try {
            val textTranslationJobsRequest = ListTextTranslationJobsRequest {
                maxResults = 10
            }

            val jobsResponse = translateClient.listTextTranslationJobs(textTranslationJobsRequest)
            val props = jobsResponse.textTranslationJobPropertiesList

            if (props != null) {
                for (prop in props) {
                    println("The job name is ${prop.jobName}")
                    println("The job id is: ${prop.jobId}")
                }
            }

        } catch (ex: TranslateException) {
            println(ex.message)
            exitProcess(0)
        }
  }
// snippet-end:[translate.kotlin._list_jobs.main]