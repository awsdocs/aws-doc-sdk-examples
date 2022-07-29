// snippet-sourcedescription:[StartQueryExample.kt demonstrates how to submit a query to Amazon Athena for execution, wait until the results are available, and then process the results.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Athena]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

// snippet-start:[athena.kotlin.StartQueryExample.import]
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.GetQueryExecutionRequest
import aws.sdk.kotlin.services.athena.model.GetQueryResultsRequest
import aws.sdk.kotlin.services.athena.model.QueryExecutionContext
import aws.sdk.kotlin.services.athena.model.QueryExecutionState
import aws.sdk.kotlin.services.athena.model.ResultConfiguration
import aws.sdk.kotlin.services.athena.model.Row
import aws.sdk.kotlin.services.athena.model.StartQueryExecutionRequest
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[athena.kotlin.StartQueryExample.import]

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <queryString> <database> <outputLocation>

    Where:
        queryString - The query string to use (for example, "SELECT * FROM mydatabase"; ).
        database - The name of the database to use (for example, mydatabase ).
        outputLocation - The output location (for example, the name of an Amazon S3 bucket - s3://mybucket). 
        
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val queryString = args[0]
    val database = args[1]
    val outputLocation = args[2]
    val queryExecutionId = submitAthenaQuery(queryString, database, outputLocation)
    waitForQueryToComplete(queryExecutionId)
    processResultRows(queryExecutionId)
}

// snippet-start:[athena.kotlin.StartQueryExample.main]
suspend fun submitAthenaQuery(queryStringVal: String, databaseVal: String, outputLocationVal: String): String? {

    // The QueryExecutionContext allows us to set the database.
    val queryExecutionContextOb = QueryExecutionContext {
        database = databaseVal
    }

    // The result configuration specifies where the results of the query should go.
    val resultConfigurationOb = ResultConfiguration {
        outputLocation = outputLocationVal
    }

    val request = StartQueryExecutionRequest {
        queryString = queryStringVal
        queryExecutionContext = queryExecutionContextOb
        resultConfiguration = resultConfigurationOb
    }

    AthenaClient { region = "us-west-2" }.use { athenaClient ->
        val response = athenaClient.startQueryExecution(request)
        return response.queryExecutionId
    }
}

// Wait for an Amazon Athena query to complete, fail or to be cancelled.
suspend fun waitForQueryToComplete(queryExecutionIdVal: String?) {

    var isQueryStillRunning = true
    while (isQueryStillRunning) {

        val request = GetQueryExecutionRequest {
            queryExecutionId = queryExecutionIdVal
        }

        AthenaClient { region = "us-west-2" }.use { athenaClient ->

            val response = athenaClient.getQueryExecution(request)
            val queryState = response.queryExecution?.status?.state.toString()
            if (queryState == QueryExecutionState.Succeeded.toString()) {
                isQueryStillRunning = false
            } else {
                // Sleep an amount of time before retrying again.
                delay(1000)
            }
            println("The current status is: $queryState")
        }
    }
}

// This code retrieves the results of a query.
suspend fun processResultRows(queryExecutionIdVal: String?) {

    val request = GetQueryResultsRequest {
        queryExecutionId = queryExecutionIdVal
    }
    AthenaClient { region = "us-west-2" }.use { athenaClient ->
        val getQueryResultsResults = athenaClient.getQueryResults(request)
        val results = getQueryResultsResults.resultSet
        for (result in listOf(results)) {
            val columnInfoList = result?.resultSetMetadata?.columnInfo
            val response = result?.rows
            if (response != null) {
                if (columnInfoList != null) {
                    processRow(response)
                }
            }
        }
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
// snippet-end:[athena.kotlin.StartQueryExample.main]
