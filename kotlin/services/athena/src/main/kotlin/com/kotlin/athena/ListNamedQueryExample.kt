// snippet-sourcedescription:[ListNamedQueryExample.Kt demonstrates how to obtain a list of named query Id values.]
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

//snippet-start:[athena.kotlin.ListNamedQueryExample.import]
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.ListNamedQueriesRequest
//snippet-end:[athena.kotlin.ListNamedQueryExample.import]

suspend fun main() {
    listNamedQueries()
}

// snippet-start:[athena.kotlin.ListNamedQueryExample.main]
suspend fun listNamedQueries() {

        val request = ListNamedQueriesRequest{
            this.maxResults = 10
        }

        AthenaClient { region = "us-west-2" }.use { athenaClient ->
            val responses = athenaClient.listNamedQueries(request)
            responses.namedQueryIds?.forEach { queries ->
                println("Retrieved account alias $queries")
            }
        }
}
// snippet-end:[athena.kotlin.ListNamedQueryExample.main]