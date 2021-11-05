//snippet-sourcedescription:[DeleteTable.kt demonstrates how to delete an Amazon DynamoDB table.]
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

// snippet-start:[dynamodb.kotlin.delete_table.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.DeleteTableRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.delete_table.import]

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
        tableName - the Amazon DynamoDB table to delete (for example, Music3).
              
    """

   if (args.size != 1) {
       println(usage)
       exitProcess(0)
   }

    val tableName = args[0]
    val ddb = DynamoDbClient{ region = "us-east-1" }
    deleteDynamoDBTable(ddb, tableName)
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.delete_table.main]
suspend fun deleteDynamoDBTable(ddb: DynamoDbClient, tableNameVal: String) {
        val request = DeleteTableRequest {
            tableName = tableNameVal
        }

        try {
            ddb.deleteTable(request)
            println("$tableNameVal was deleted")

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
  }
// snippet-end:[dynamodb.kotlin.delete_table.main]
