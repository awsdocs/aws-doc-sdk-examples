// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.scan_items_filter.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import java.util.HashMap
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.scan_items_filter.import]

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
        tableName - The Amazon DynamoDB table to scan (for example, Music3).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    scanItemsUsingFilter(tableName)
}

// snippet-start:[dynamodb.kotlin.scan_items_filter.main]
suspend fun scanItemsUsingFilter(tableNameVal: String) {
    val myMap = HashMap<String, String>()
    myMap.put("#archive2", "archive")

    val myExMap = HashMap<String, AttributeValue>()
    myExMap.put(":val", AttributeValue.S("Open"))

    val request =
        ScanRequest {
            this.expressionAttributeNames = myMap
            this.expressionAttributeValues = myExMap
            tableName = tableNameVal
            filterExpression = "#archive2 = :val"
        }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        val response = ddb.scan(request)
        println("#######################################")
        response.items?.forEach { item ->
            item.keys.forEach { key ->

                when (key) {
                    "date" -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                    }
                    "status" -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                    }
                    "username" -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                    }
                    "archive" -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                    }

                    "description" -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                    }
                    "id" -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                    }
                    else -> {
                        val myVal = splitMyString(item[key].toString())
                        println(myVal)
                        println("#######################################")
                    }
                }
            }
        }
    }
}

fun splitMyString(str: String): String {
    val del1 = "="
    val del2 = ")"
    val parts = str.split(del1, del2)
    val myVal = parts[1]
    return myVal
}
// snippet-end:[dynamodb.kotlin.scan_items_filter.main]
