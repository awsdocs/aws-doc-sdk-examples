//snippet-sourcedescription:[StartQueryExample.kt demonstrates how to submit a query to Amazon Athena for execution, wait until the results are available, and then process the results.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/14/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

//snippet-start:[athena.kotlin.StartQueryExample.import]
import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.QueryExecutionContext
import aws.sdk.kotlin.services.athena.model.ResultConfiguration
import aws.sdk.kotlin.services.athena.model.StartQueryExecutionRequest
import aws.sdk.kotlin.services.athena.model.GetQueryExecutionRequest
import aws.sdk.kotlin.services.athena.model.GetQueryExecutionResponse
import aws.sdk.kotlin.services.athena.model.QueryExecutionState
import aws.sdk.kotlin.services.athena.model.GetQueryResultsRequest
import aws.sdk.kotlin.services.athena.model.AthenaException
import aws.sdk.kotlin.services.athena.model.Row
import kotlinx.coroutines.delay
import java.lang.RuntimeException
import kotlin.system.exitProcess
//snippet-end:[athena.kotlin.StartQueryExample.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <queryString> <database> <outputLocation>

    Where:
        queryString - the query string to use (for example, "SELECT * FROM mydatabase"; ).
        database - the name of the database to use (for example, mydatabase ).
        outputLocation - the output location (for example, the name of an Amazon S3 bucket - s3://mybucket). 
        
    """

   if (args.size != 3) {
        println(usage)
        exitProcess(0)
   }

    val queryString = args[0]
    val database = args[1]
    val outputLocation = args[2]
    val athenaClient = AthenaClient { region = "us-west-2" }
    val queryExecutionId = submitAthenaQuery(athenaClient, queryString, database, outputLocation)
    waitForQueryToComplete(athenaClient, queryExecutionId)
    processResultRows(athenaClient, queryExecutionId)
    athenaClient.close()
}

//snippet-start:[athena.kotlin.StartQueryExample.main]
suspend fun submitAthenaQuery(athenaClient: AthenaClient, queryStringVal:String, databaseVal:String, outputLocationVal:String  ): String? {
    try {

        // The QueryExecutionContext allows us to set the database.
        val queryExecutionContextOb = QueryExecutionContext {
            database = databaseVal
        }

        // The result configuration specifies where the results of the query should go.
        val resultConfigurationOb = ResultConfiguration {
            outputLocation =outputLocationVal
        }

        val startQueryExecutionRequest = StartQueryExecutionRequest {
            queryString = queryStringVal
            queryExecutionContext = queryExecutionContextOb
            resultConfiguration = resultConfigurationOb
        }

        val startQueryExecutionResponse =  athenaClient.startQueryExecution(startQueryExecutionRequest)
        return startQueryExecutionResponse.queryExecutionId

    } catch (ex: AwsServiceException) {
        println(ex.message)
        athenaClient.close()
        exitProcess(0)
    }
}

// Wait for an Amazon Athena query to complete, fail or to be cancelled.
suspend fun waitForQueryToComplete(athenaClient: AthenaClient, queryExecutionIdVal: String?) {

    val getQueryExecutionRequest = GetQueryExecutionRequest {
        queryExecutionId = queryExecutionIdVal
    }
    var getQueryExecutionResponse: GetQueryExecutionResponse
    var isQueryStillRunning = true

    while (isQueryStillRunning) {
        getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest)
        val queryState: String = getQueryExecutionResponse.queryExecution?.status?.state.toString()

        if (queryState == QueryExecutionState.Failed.toString()) {
            throw RuntimeException("The Amazon Athena query failed to run with error message: ${getQueryExecutionResponse.queryExecution?.status?.stateChangeReason}")

        } else if (queryState == QueryExecutionState.Cancelled.toString()) {
            throw RuntimeException("The Amazon Athena query was cancelled.")
        } else if (queryState == QueryExecutionState.Succeeded.toString()) {
            isQueryStillRunning = false
        } else {
            // Sleep an amount of time before retrying again
            delay(1000)
        }
        println("The current status is: $queryState")
    }
}

// This code retrieves the results of a query.
suspend fun processResultRows(athenaClient: AthenaClient, queryExecutionIdVal: String?) {
    try {

        // Max Results can be set but if it's not set, it will choose the maximum page size.
        val getQueryResultsRequest = GetQueryResultsRequest {
            queryExecutionId = queryExecutionIdVal
        }

        val getQueryResultsResults  = athenaClient.getQueryResults(getQueryResultsRequest)
        val results = getQueryResultsResults.resultSet

        for (result in listOf(results)) {
            val columnInfoList = result?.resultSetMetadata?.columnInfo
            val results = result?.rows
            if (results != null) {
                if (columnInfoList != null) {
                    processRow(results)
                }
            }
        }
    } catch (e: AthenaException) {
        e.printStackTrace()
        exitProcess(0)
    }
}


private fun processRow(row: List<Row>) {
    for (myRow in row) {
        val allData = myRow.data
        if (allData != null) {
            for (data in allData) {
                println("The value of the column is " + data.varCharValue)
            }
        }
    }
}
//snippet-end:[athena.kotlin.StartQueryExample.main]
