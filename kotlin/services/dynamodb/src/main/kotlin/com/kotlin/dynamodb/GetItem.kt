// snippet-sourcedescription:[GetItem.kt demonstrates how to retrieve an item from an Amazon DynamoDB table.]
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

// snippet-start:[dynamodb.kotlin.get_item.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.get_item.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <tableName> <key> <keyVal>

    Where:
        tableName - The Amazon DynamoDB table from which an item is retrieved (for example, Music3). 
        key - The key used in the Amazon DynamoDB table (for example, Artist). 
        keyval - The key value that represents the item to get (for example, Famous Band).
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    val key = args[1]
    val keyVal = args[2]
    getSpecificItem(tableName, key, keyVal)
}

// snippet-start:[dynamodb.kotlin.get_item.main]
suspend fun getSpecificItem(tableNameVal: String, keyName: String, keyVal: String) {

    val keyToGet = mutableMapOf<String, AttributeValue>()
    keyToGet[keyName] = AttributeValue.S(keyVal)

    val request = GetItemRequest {
        key = keyToGet
        tableName = tableNameVal
    }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        val returnedItem = ddb.getItem(request)
        val numbersMap = returnedItem.item
        numbersMap?.forEach { key1 ->
            println(key1.key)
            println(key1.value)
        }
    }
}
// snippet-end:[dynamodb.kotlin.get_item.main]
