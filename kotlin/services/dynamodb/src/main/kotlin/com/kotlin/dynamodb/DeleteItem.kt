// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.delete_item.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.delete_item.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <tableName> <key> <keyval>

    Where:
        tableName - The Amazon DynamoDB table to delete the item from (for example, Music3).
        key - The key used in the Amazon DynamoDB table (for example, Artist). 
        keyval - The key value that represents the item to delete (for example, Famous Band).
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    val key = args[1]
    val keyVal = args[2]
    deleteDynamoDBItem(tableName, key, keyVal)
}

// snippet-start:[dynamodb.kotlin.delete_item.main]
suspend fun deleteDynamoDBItem(
    tableNameVal: String,
    keyName: String,
    keyVal: String,
) {
    val keyToGet = mutableMapOf<String, AttributeValue>()
    keyToGet[keyName] = AttributeValue.S(keyVal)

    val request =
        DeleteItemRequest {
            tableName = tableNameVal
            key = keyToGet
        }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        ddb.deleteItem(request)
        println("Item with key matching $keyVal was deleted")
    }
}
// snippet-end:[dynamodb.kotlin.delete_item.main]
