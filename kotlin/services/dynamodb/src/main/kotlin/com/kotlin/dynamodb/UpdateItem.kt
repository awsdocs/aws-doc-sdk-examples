// snippet-sourcedescription:[UpdateItem.kt demonstrates how to update a value located in an Amazon DynamoDB table.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.update_item.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeAction
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValueUpdate
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.update_item.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
      Usage:
        <tableName> <key> <keyVal> <name> <updateVal>

      Where:
        tableName - The Amazon DynamoDB table (for example, Music3).
        key - The name of the key in the table (for example, Artist).
        keyVal - The value of the key (for example, Famous Band).
        name - The name of the column where the value is updated (for example, Awards).
        updateVal - The value used to update an item (for example, 14).
        
        """

    if (args.size != 5) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    val key = args[1]
    val keyVal = args[2]
    val name = args[3]
    val updateVal = args[4]
    updateTableItem(tableName, key, keyVal, name, updateVal)
}

// snippet-start:[dynamodb.kotlin.update_item.main]
suspend fun updateTableItem(
    tableNameVal: String,
    keyName: String,
    keyVal: String,
    name: String,
    updateVal: String
) {

    val itemKey = mutableMapOf<String, AttributeValue>()
    itemKey[keyName] = AttributeValue.S(keyVal)

    val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
    updatedValues[name] = AttributeValueUpdate {
        value = AttributeValue.S(updateVal)
        action = AttributeAction.Put
    }

    val request = UpdateItemRequest {
        tableName = tableNameVal
        key = itemKey
        attributeUpdates = updatedValues
    }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        ddb.updateItem(request)
        println("Item in $tableNameVal was updated")
    }
}
// snippet-end:[dynamodb.kotlin.update_item.main]
