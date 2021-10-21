//snippet-sourcedescription:[QueryTable.kt demonstrates how to query an Amazon DynamoDB table.]
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

// snippet-start:[dynamodb.kotlin.query.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.query.import]

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
        partitionKeyName - the partition key name of the Amazon DynamoDB table (for example, Artist).
        partitionKeyVal - value of the partition key that should match (for example, Famous Band).
    """

    if (args.size != 3) {
         println(usage)
         exitProcess(0)
     }

    val tableName = args[0]
    val partitionKeyName = args[1]
    val partitionKeyVal = args[2]

    // For more information about an alias, see:
    // https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.ExpressionAttributeNames.html
    val partitionAlias = "#a"
    val ddb = DynamoDbClient{ region = "us-east-1" }
    val count =  queryDynTable(ddb, tableName, partitionKeyName, partitionKeyVal, partitionAlias);
    print("There is $count item in $tableName")
    ddb.close()
}

// snippet-start:[dynamodb.kotlin.query.main]
suspend fun queryDynTable(
        ddb: DynamoDbClient,
        tableNameVal: String,
        partitionKeyName: String,
        partitionKeyVal: String,
        partitionAlias: String
    ): Int {

        val attrNameAlias = mutableMapOf<String, String>()
        attrNameAlias[partitionAlias] = partitionKeyName

        // Set up mapping of the partition name with the value.
        val attrValues = mutableMapOf<String, AttributeValue>()
        attrValues[":$partitionKeyName"] = AttributeValue.S(partitionKeyVal)

        val queryReq = QueryRequest {
            tableName = tableNameVal
            keyConditionExpression = "$partitionAlias = :$partitionKeyName"
            expressionAttributeNames = attrNameAlias
            this.expressionAttributeValues = attrValues
        }

        try {
            val response = ddb.query(queryReq)
            return response.count

        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
        return -1
  }

// snippet-end:[dynamodb.kotlin.query.main]