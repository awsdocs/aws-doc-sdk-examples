// snippet-sourcedescription:[ListQueryExecutionsExample.kt demonstrates how to obtain a list of query execution Id values.]
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

// snippet-start:[athena.kotlin.ListNamedQueryExecutionsExample.import]
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.ListQueryExecutionsRequest
// snippet-end:[athena.kotlin.ListNamedQueryExecutionsExample.import]

suspend fun main() {
   listQueryIds()
}

// snippet-start:[athena.kotlin.ListNamedQueryExecutionsExample.main]
 suspend fun listQueryIds() {

        val request = ListQueryExecutionsRequest{
            maxResults = 10
        }

        AthenaClient { region = "us-west-2" }.use { athenaClient ->
                val response =  athenaClient.listQueryExecutions(request)
                response.queryExecutionIds?.forEach { queries ->
                   println("The value is $queries")
                }
           }
       }
// snippet-end:[athena.kotlin.ListNamedQueryExecutionsExample.main]