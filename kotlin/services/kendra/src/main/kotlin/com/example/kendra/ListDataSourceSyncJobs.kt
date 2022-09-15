// snippet-sourcedescription:[ListDataSourceSyncJobs.kt demonstrates how to get statistics about synchronizing Amazon Kendra with a data source.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[Amazon Kendra]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra

// snippet-start:[kendra.kotlin.list.sync.import]
import aws.sdk.kotlin.services.kendra.KendraClient
import aws.sdk.kotlin.services.kendra.model.ListDataSourceSyncJobsRequest
import kotlin.system.exitProcess
// snippet-end:[kendra.kotlin.list.sync.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <indexId> 

        Where:
            indexId - The id value of the index.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val indexId = args[0]
    val dataSourceId = args[1]
    listSyncJobs(indexId, dataSourceId)
}

// snippet-start:[kendra.kotlin.list.sync.main]
suspend fun listSyncJobs(indexIdVal: String?, dataSourceId: String?) {

    val jobsRequest = ListDataSourceSyncJobsRequest {
        indexId = indexIdVal
        maxResults = 10
        id = dataSourceId
    }

    KendraClient { region = "us-east-1" }.use { kendra ->
        val response = kendra.listDataSourceSyncJobs(jobsRequest)
        response.history?.forEach { job ->
            println("Execution id is ${job.executionId}")
            println("Job status ${job.status}")
        }
    }
}
// snippet-end:[kendra.kotlin.list.sync.main]
