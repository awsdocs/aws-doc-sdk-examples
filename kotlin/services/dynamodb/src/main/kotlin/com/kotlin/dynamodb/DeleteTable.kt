// snippet-sourcedescription:[DeleteTable.kt demonstrates how to delete an Amazon DynamoDB table.]
// snippet-keyword:[Code Sample]
// snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.delete_table.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.DeleteTableRequest
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.delete_table.import]

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
        tableName - The Amazon DynamoDB table to delete (for example, Music3).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    deleteDynamoDBTable(tableName)
}

// snippet-start:[dynamodb.kotlin.delete_table.main]
suspend fun deleteDynamoDBTable(tableNameVal: String) {

    val request = DeleteTableRequest {
        tableName = tableNameVal
    }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        ddb.deleteTable(request)
        println("$tableNameVal was deleted")
    }
}
// snippet-end:[dynamodb.kotlin.delete_table.main]
