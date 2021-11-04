//snippet-sourcedescription:[ListQueryExecutionsExample.kt demonstrates how to obtain a list of query execution Id values.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

//snippet-start:[athena.kotlin.ListNamedQueryExecutionsExample.import]
import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.ListQueryExecutionsRequest
import kotlin.system.exitProcess
//snippet-end:[athena.kotlin.ListNamedQueryExecutionsExample.import]

suspend fun main() {

    val athenaClient = AthenaClient { region = "us-west-2" }
    listQueryIds(athenaClient)
    athenaClient.close()
}

//snippet-start:[athena.kotlin.ListNamedQueryExecutionsExample.main]
 suspend fun listQueryIds(athenaClient: AthenaClient) {
        try {

            val listQueryExecutionsRequest = ListQueryExecutionsRequest{
                maxResults = 10
            }
            val listQueryExecutionResponses =  athenaClient.listQueryExecutions(listQueryExecutionsRequest)
            listQueryExecutionResponses.queryExecutionIds?.forEach { queries ->
                println("The value is $queries")
            }

        } catch (ex: AwsServiceException) {
            println(ex.message)
            athenaClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[athena.kotlin.ListNamedQueryExecutionsExample.main]