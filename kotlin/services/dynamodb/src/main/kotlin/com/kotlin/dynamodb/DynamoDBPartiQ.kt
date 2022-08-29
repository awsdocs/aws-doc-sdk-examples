// snippet-sourcedescription:[DynamoDBPartiQ.kt demonstrates how to work with PartiQL for Amazon DynamoDB.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon DynamoDB]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.partiql.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.dynamodb.model.ExecuteStatementResponse
// snippet-end:[dynamodb.kotlin.partiql.import]

/**
 Before running this Kotlin code example, set up your development environment,
 including your credentials.

 For more information, see the following documentation topic:
 https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

 You must also create the Music table as discussed in the following topic:
 https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/ql-gettingstarted.html
 */

// snippet-start:[dynamodb.kotlin.partiql.main]
suspend fun main() {

    val ddb = DynamoDbClient { region = "us-east-1" }
    val parameters = mutableListOf<AttributeValue>()
    parameters.add(AttributeValue.S("Acme Band"))
    parameters.add(AttributeValue.S("PartiQL Rocks"))

    // Retrieve an item from the Music table using the SELECT PartiQL statement.
    var response = executeStatementRequest(ddb, "SELECT * FROM Music  where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    // Update an item in the Music table using the UPDATE PartiQL statement.
    response = executeStatementRequest(ddb, "UPDATE Music SET AwardsWon=1 SET AwardDetail={'Grammys':[2020, 2018]}  where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music SET AwardDetail.Grammys =LIST_APPEND(AwardDetail.Grammys,[2016])  where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music SET BandMembers =<<'member1', 'member2'>> where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music SET AwardDetail.Grammys =list_append(AwardDetail.Grammys,[2016])  where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music REMOVE AwardDetail.Grammys[2]   where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music set AwardDetail.BillBoard=[2020] where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music SET BandMembers =<<'member1', 'member2'>> where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    response = executeStatementRequest(ddb, "UPDATE Music SET BandMembers =set_add(BandMembers, <<'newmember'>>) where Artist=? and SongTitle=?", parameters)
    if (response != null) {
        processResults(response)
    }

    println("This code example has completed")
}

suspend fun executeStatementRequest(
    ddb: DynamoDbClient,
    statementVal: String,
    parametersVal: List<AttributeValue>
): ExecuteStatementResponse? {

    val request = ExecuteStatementRequest {
        statement = statementVal
        parameters = parametersVal
    }

    return ddb.executeStatement(request)
}

fun processResults(executeStatementResult: ExecuteStatementResponse) {
    println("ExecuteStatement successful $executeStatementResult")
}
// snippet-end:[dynamodb.kotlin.partiql.main]
