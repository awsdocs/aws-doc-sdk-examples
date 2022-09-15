// snippet-sourcedescription:[DeleteIndex.kt demonstrates how to delete an Amazon Kendra index.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[Amazon Kendra]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra

// snippet-start:[kendra.kotlin.delete.index.import]
import aws.sdk.kotlin.services.kendra.KendraClient
import aws.sdk.kotlin.services.kendra.model.DeleteIndexRequest
import kotlin.system.exitProcess
// snippet-end:[kendra.kotlin.delete.index.import]

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
    deleteSpecificIndex(indexId)
}

// snippet-start:[kendra.kotlin.delete.index.main]
suspend fun deleteSpecificIndex(indexId: String) {

    val deleteIndexRequest = DeleteIndexRequest {
        id = indexId
    }

    KendraClient { region = "us-east-1" }.use { kendra ->
        kendra.deleteIndex(deleteIndexRequest)
        println("$indexId was successfully deleted.")
    }
}
// snippet-end:[kendra.kotlin.delete.index.main]
