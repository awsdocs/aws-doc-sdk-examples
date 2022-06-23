//snippet-sourcedescription:[ListTextTranslationJobs.kt demonstrates how to list all translation jobs.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

package com.kotlin.translate

// snippet-start:[translate.kotlin._list_jobs.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.ListTextTranslationJobsRequest
// snippet-end:[translate.kotlin._list_jobs.import]

suspend fun main(){
    getTranslationJobs()
}

// snippet-start:[translate.kotlin._list_jobs.main]
suspend fun getTranslationJobs() {

        val textTranslationJobsRequest = ListTextTranslationJobsRequest {
            maxResults = 10
        }

        TranslateClient { region = "us-west-2" }.use { translateClient ->
           val response = translateClient.listTextTranslationJobs(textTranslationJobsRequest)
            response.textTranslationJobPropertiesList?.forEach { prop ->
                    println("The job name is ${prop.jobName}")
                    println("The job id is: ${prop.jobId}")
            }
        }
  }
// snippet-end:[translate.kotlin._list_jobs.main]