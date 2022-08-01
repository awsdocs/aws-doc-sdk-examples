// snippet-sourcedescription:[ListTextTranslationJobs.kt demonstrates how to list all translation jobs.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[Amazon Translate]

package com.kotlin.translate

// snippet-start:[translate.kotlin._list_jobs.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.ListTextTranslationJobsRequest
// snippet-end:[translate.kotlin._list_jobs.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
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
