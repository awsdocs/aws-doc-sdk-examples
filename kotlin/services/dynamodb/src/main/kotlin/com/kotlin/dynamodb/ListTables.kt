// snippet-sourcedescription:[ListTables.kt demonstrates how to list all Amazon DynamoDB tables.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.list_tables.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.ListTablesRequest
// snippet-end:[dynamodb.kotlin.list_tables.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main() {

    listAllTables()
}

// snippet-start:[dynamodb.kotlin.list_tables.main]
suspend fun listAllTables() {

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        val response = ddb.listTables(ListTablesRequest {})
        response.tableNames?.forEach { tableName ->
            println("Table name is $tableName")
        }
    }
}
// snippet-end:[dynamodb.kotlin.list_tables.main]
