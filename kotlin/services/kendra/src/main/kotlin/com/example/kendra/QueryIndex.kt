// snippet-sourcedescription:[QueryIndex.kt demonstrates how to query an Amazon Kendra index.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[Amazon Kendra]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra

// snippet-start:[kendra.kotlin.query.index.import]
import aws.sdk.kotlin.services.kendra.KendraClient
import aws.sdk.kotlin.services.kendra.model.QueryRequest
import aws.sdk.kotlin.services.kendra.model.QueryResultType
import kotlin.system.exitProcess
// snippet-end:[kendra.kotlin.query.index.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <indexId> <text>

        Where:
            indexId - The id value of the index.
            text - The text to use.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(1)
    }

    val indexId = args[0]
    val text = args[1]
    querySpecificIndex(indexId, text)
}

// snippet-start:[kendra.kotlin.query.index.main]
suspend fun querySpecificIndex(indexIdVal: String?, text: String?) {

    val queryRequest = QueryRequest {
        indexId = indexIdVal
        queryResultTypeFilter = QueryResultType.Document
        queryText = text
    }

    KendraClient { region = "us-east-1" }.use { kendra ->
        val response = kendra.query(queryRequest)
        response.resultItems?.forEach { item ->
            println("The document title is ${item.documentTitle}")
            println("Text:")
            println(item.documentExcerpt?.text)
        }
    }
}
// snippet-end:[kendra.kotlin.query.index.main]
