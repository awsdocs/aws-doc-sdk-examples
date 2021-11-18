//snippet-sourcedescription:[CreateTable.kt demonstrates how to create an Amazon DynamoDB table.]
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

// snippet-start:[dynamodb.kotlin.create_table.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import aws.sdk.kotlin.services.dynamodb.model.KeyType
import aws.sdk.kotlin.services.dynamodb.model.CreateTableRequest
import aws.sdk.kotlin.services.dynamodb.model.DescribeTableRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.create_table.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
         <tableName> <key> 

    Where:
        tableName - the Amazon DynamoDB table to create (for example, Music3).
        key - the key for the Amazon DynamoDB table (for example, Artist).
       
    """

    if (args.size != 2) {
         println(usage)
         exitProcess(0)
    }

    val tableName = args[0]
    val key = args[1]
    println("Creating an Amazon DynamoDB table named $tableName with a key named $key" )
    val ddb = DynamoDbClient{ region = "us-east-1" }
    val tableArn = createNewTable(ddb, tableName, key)
    println("The new table ARN is $tableArn")
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.create_table.main]
suspend fun createNewTable(ddb: DynamoDbClient, tableNameVal: String, key: String): String {

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

        try {
            val response = ddb.createTable(request)
            val tableActive = false

            // Wait until the table is in Active state.
            while (!tableActive)
            {
                val tableStatus = checkTableStatus(ddb, tableNameVal)
                if (tableStatus.equals("ACTIVE"))
                    break
                delay(500)
          }
            return response.tableDescription?.tableArn.toString()

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
    }

    suspend fun checkTableStatus(ddb: DynamoDbClient, tableNameVal: String) : String {

        val request = DescribeTableRequest {
            tableName= tableNameVal
        }

        try {
            val tableInfo = ddb.describeTable(request)
            return tableInfo.table?.tableStatus.toString()

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
    }
// snippet-end:[dynamodb.kotlin.create_table.main]