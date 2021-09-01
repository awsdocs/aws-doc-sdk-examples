//snippet-sourcedescription:[ListTables.kt demonstrates how to list all Amazon DynamoDB tables.]
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

// snippet-start:[dynamodb.kotlin.list_tables.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.ListTablesRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.list_tables.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val ddb = DynamoDbClient{ region = "us-east-1" }
    listAllTables(ddb );
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.list_tables.main]
suspend fun listAllTables(ddb: DynamoDbClient) {

        try {
            val response =  ddb.listTables(ListTablesRequest{})
            val tableNameVals = response.tableNames

            if (tableNameVals != null) {
                   for (curName:String in tableNameVals) {
                        println("Table name is $curName")
                    }
           }

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
  }
// snippet-end:[dynamodb.kotlin.list_tables.main]
