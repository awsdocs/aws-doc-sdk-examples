// snippet-sourcedescription:[DeleteNamedQueryExample.kt demonstrates how to delete a named query by using the named query Id value.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Athena]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/25/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

// snippet-start:[athena.kotlin.DeleteNamedQueryExample.import]
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.DeleteNamedQueryRequest
import kotlin.system.exitProcess
// snippet-end:[athena.kotlin.DeleteNamedQueryExample.import]

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <queryId> 

    Where:
        queryId - The id of the Amazon Athena query (for example, b34e7780-903b-4842-9d2c-6c99bebc82aa).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }
    val queryId = args[0]
    deleteQueryName(queryId)
}

// snippet-start:[athena.kotlin.DeleteNamedQueryExample.main]
suspend fun deleteQueryName(sampleNamedQueryId: String?) {

    val request = DeleteNamedQueryRequest {
        namedQueryId = sampleNamedQueryId
    }

    AthenaClient { region = "us-west-2" }.use { athenaClient ->
        athenaClient.deleteNamedQuery(request)
        println("$sampleNamedQueryId was deleted!")
    }
}
// snippet-end:[athena.kotlin.DeleteNamedQueryExample.main]
