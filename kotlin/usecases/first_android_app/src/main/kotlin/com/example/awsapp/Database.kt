/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.awsapp

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess

class Database {

    suspend fun putItemInTable2(
        ddb: DynamoDbClient,
        tableNameVal: String,
        key: String,
        keyVal: String,
        moneyTotal: String,
        moneyTotalValue: String,
        name: String,
        nameValue: String,
        email: String,
        emailVal: String,
        date: String,
        dateVal: String,
    ) {
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues[key] = AttributeValue.S(keyVal)
        itemValues[moneyTotal] =  AttributeValue.S(moneyTotalValue)
        itemValues[name] = AttributeValue.S(nameValue)
        itemValues[email] = AttributeValue.S(emailVal)
        itemValues[date] = AttributeValue.S(dateVal)

        val request = PutItemRequest {
            tableName=tableNameVal
            item = itemValues
        }

        try {
            ddb.putItem(request)
            println(" A new item was placed into $tableNameVal.")

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
    }
}
