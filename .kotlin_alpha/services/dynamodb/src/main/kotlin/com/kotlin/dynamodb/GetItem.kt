//snippet-sourcedescription:[GetItem.kt demonstrates how to retrieve an item from an Amazon DynamoDB table.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.get_item.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.get_item.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <tableName> <key> <keyVal>

    Where:
        tableName - the Amazon DynamoDB table from which an item is retrieved (for example, Music3). 
        key - the key used in the Amazon DynamoDB table (for example, Artist). 
        keyval - the key value that represents the item to get (for example, Famous Band).
    """

   if (args.size != 3) {
        println(usage)
        exitProcess(0)
   }

    val tableName = args[0]
    val key = args[1]
    val keyVal = args[2]
    val ddb = DynamoDbClient{ region = "us-east-1" }
    getSpecificItem(ddb, tableName, key, keyVal)
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.get_item.main]
suspend fun getSpecificItem(ddb: DynamoDbClient,
                        tableNameVal: String,
                        keyName: String,
                        keyVal: String) {

        val keyToGet = mutableMapOf<String, AttributeValue>()
        keyToGet[keyName] = AttributeValue.S(keyVal)

        val request = GetItemRequest {
            key = keyToGet
            tableName = tableNameVal
        }

        try {
            val returnedItem = ddb.getItem(request)
            val numbersMap = returnedItem.item

                numbersMap?.forEach { key1 ->
                    println(key1.key)
                    println(key1.value)
                }


        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
 }
// snippet-end:[dynamodb.kotlin.get_item.main]