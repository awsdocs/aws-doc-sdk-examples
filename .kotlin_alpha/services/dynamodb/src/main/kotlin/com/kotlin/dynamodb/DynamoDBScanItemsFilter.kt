//snippet-sourcedescription:[DynamoDBScanItems.kt demonstrates how to return items from an Amazon DynamoDB table using a filter expression.]
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

// snippet-start:[dynamodb.kotlin.scan_items_filter.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import java.util.HashMap
// snippet-end:[dynamodb.kotlin.scan_items_filter.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <tableName>

    Where:
        tableName - the Amazon DynamoDB table to scan (for example, Music3).
    """

   if (args.size != 1) {
        println(usage)
        exitProcess(0)
   }

    val tableName = args[0]
    val ddb = DynamoDbClient{ region = "us-east-1" }
    scanItemsUsingFilter(ddb, tableName);
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.scan_items_filter.main]
suspend fun scanItemsUsingFilter(ddb: DynamoDbClient, tableNameVal: String) {
    try {

        val myMap = HashMap<String, String>()
        myMap.put("#archive2", "archive")

        val myExMap = HashMap<String, AttributeValue>()
        myExMap.put(":val", AttributeValue.S("Open"))

        val scanRequest = ScanRequest {
            this.expressionAttributeNames = myMap
            this.expressionAttributeValues = myExMap
            tableName = tableNameVal
            filterExpression = "#archive2 = :val"
        }

        val response = ddb.scan(scanRequest)
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

    } catch (ex: DynamoDbException) {
        println(ex.message)
        ddb.close()
        exitProcess(0)
    }
}

fun splitMyString(str:String):String{

    var del1 = "="
    var del2 = ")"
    val parts = str.split(del1, del2)
    val myVal = parts[1]
    return myVal
}
// snippet-end:[dynamodb.kotlin.scan_items_filter.main]