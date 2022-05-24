// snippet-sourcedescription:[CreateTable.kt demonstrates how to create an Amazon DynamoDB table using a waiter.]
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

// snippet-start:[dynamodb.kotlin.create_table.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import aws.sdk.kotlin.services.dynamodb.model.KeyType
import aws.sdk.kotlin.services.dynamodb.model.CreateTableRequest
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableExists
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.create_table.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
         <tableName> <key> 

    Where:
        tableName - The Amazon DynamoDB table to create (for example, Music3).
        key - The key for the Amazon DynamoDB table (for example, Artist).
    """

   if (args.size != 2) {
         println(usage)
         exitProcess(0)
   }

   val tableName = args[0]
   val key = args[1]
   println("Creating an Amazon DynamoDB table named $tableName with a key named $key")
   val tableArn = createNewTable(tableName, key)
   println("The new table ARN is $tableArn")
}

// snippet-start:[dynamodb.kotlin.create_table.main]
suspend fun createNewTable(tableNameVal: String, key: String): String? {

        val  attDef = AttributeDefinition {
            attributeName = key
            attributeType = ScalarAttributeType.S
        }

        val keySchemaVal =  KeySchemaElement{
            attributeName = key
            keyType = KeyType.Hash
        }

        val provisionedVal =  ProvisionedThroughput {
            readCapacityUnits = 10
            writeCapacityUnits = 10
        }

        val request = CreateTableRequest {
            attributeDefinitions = listOf(attDef)
            keySchema = listOf(keySchemaVal)
            provisionedThroughput = provisionedVal
            tableName = tableNameVal
        }

       DynamoDbClient { region = "us-east-1" }.use { ddb ->

           var tableArn: String
           val response = ddb.createTable(request)
           ddb.waitUntilTableExists { // suspend call
               tableName = tableNameVal
           }
           tableArn = response.tableDescription!!.tableArn.toString()
           println("Table $tableArn is ready")
           return tableArn
       }
    }
// snippet-end:[dynamodb.kotlin.create_table.main]