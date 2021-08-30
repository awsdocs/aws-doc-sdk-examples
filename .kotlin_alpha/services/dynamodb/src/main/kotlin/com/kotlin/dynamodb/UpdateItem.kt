//snippet-sourcedescription:[UpdateItem.kt demonstrates how to update a value located in an Amazon DynamoDB table.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.update_item.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.AttributeValueUpdate
import aws.sdk.kotlin.services.dynamodb.model.AttributeAction
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.update_item.import]


suspend fun main(args: Array<String>) {

    val usage = """
      Usage:
        <tableName> <key> <keyVal> <name> <updateVal>

    Where:
        tableName - the Amazon DynamoDB table (for example, Music3).
        key - the name of the key in the table (for example, Artist).
        keyVal - the value of the key (for example, Famous Band).
        name - the name of the column where the value is updated (for example, Awards).
        updateVal - the value used to update an item (for example, 14).
        
        """

     if (args.size != 5) {
          println(usage)
         exitProcess(0)
    }

    val tableName = args[0]
    val key = args[1]
    val keyVal =  args[2]
    val name = args[3]
    val  updateVal = args[4]

    val ddb = DynamoDbClient{ region = "us-east-1" }
    updateTableItem(ddb, tableName, key, keyVal, name, updateVal);
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.update_item.main]
suspend fun updateTableItem(
        ddb: DynamoDbClient,
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
             attributeUpdates= updatedValues
        }

        try {
            ddb.updateItem(request)
            println("Item in $tableNameVal was updated")

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
}
// snippet-end:[dynamodb.kotlin.update_item.main]
