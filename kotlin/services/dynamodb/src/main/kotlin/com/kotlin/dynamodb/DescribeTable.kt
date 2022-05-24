// snippet-sourcedescription:[DescribeTable.kt demonstrates how to retrieve information about an Amazon DynamoDB table.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Code Sample]
// snippet-service:[Amazon DynamoDB]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/24/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.describe_table.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.DescribeTableRequest
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.describe_table.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <tableName>

    Where:
        tableName - The Amazon DynamoDB table to get information about (for example, Music3).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    describeDymamoDBTable(tableName)
}

// snippet-start:[dynamodb.kotlin.describe_table.main]
suspend fun describeDymamoDBTable(tableNameVal: String?) {

    val request = DescribeTableRequest {
        tableName = tableNameVal
    }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        val tableInfo = ddb.describeTable(request)
        println("Table name ${tableInfo.table?.tableName}")
        println("Table Arn:  ${tableInfo.table?.tableArn}")
        println("Table Status: ${tableInfo.table?.tableStatus}")
        println("Item count:  ${tableInfo.table?.itemCount}")
        println("Size (bytes): ${tableInfo.table?.tableSizeBytes}")
    }
}
// snippet-end:[dynamodb.kotlin.describe_table.main]
