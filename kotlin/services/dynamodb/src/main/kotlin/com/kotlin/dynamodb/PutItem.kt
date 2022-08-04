// snippet-sourcedescription:[PutItem.kt demonstrates how to place an item into an Amazon DynamoDB table.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.dynamodb

// snippet-start:[dynamodb.kotlin.put_item.import]
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import kotlin.system.exitProcess
// snippet-end:[dynamodb.kotlin.put_item.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <tableName> <key> <keyVal> <albumtitle> <albumtitleval> <awards> <awardsval> <Songtitle> <songtitleval>

        Where:
            tableName - The Amazon DynamoDB table in which an item is placed (for example, Music3).
            key - The key used in the Amazon DynamoDB table (for example, Artist).
            keyval - The key value that represents the item to get (for example, Famous Band).
            albumTitle - The album title (for example, AlbumTitle).
            AlbumTitleValue - The name of the album (for example, Songs About Life).
            Awards - The awards column (for example, Awards).
            AwardVal - The value of the awards (for example, 10).
            SongTitle - The song title (for example, SongTitle).
            SongTitleVal - The value of the song title (for example, Happy Day).
            """"

    if (args.size != 9) {
        println(usage)
        exitProcess(0)
    }

    val tableName = args[0]
    val key = args[1]
    val keyVal = args[2]
    val albumTitle = args[3]
    val albumTitleValue = args[4]
    val awards = args[5]
    val awardVal = args[6]
    val songTitle = args[7]
    val songTitleVal = args[8]

    putItemInTable(tableName, key, keyVal, albumTitle, albumTitleValue, awards, awardVal, songTitle, songTitleVal)
}

// snippet-start:[dynamodb.kotlin.put_item.main]
suspend fun putItemInTable(
    tableNameVal: String,
    key: String,
    keyVal: String,
    albumTitle: String,
    albumTitleValue: String,
    awards: String,
    awardVal: String,
    songTitle: String,
    songTitleVal: String
) {
    val itemValues = mutableMapOf<String, AttributeValue>()

    // Add all content to the table.
    itemValues[key] = AttributeValue.S(keyVal)
    itemValues[songTitle] = AttributeValue.S(songTitleVal)
    itemValues[albumTitle] = AttributeValue.S(albumTitleValue)
    itemValues[awards] = AttributeValue.S(awardVal)

    val request = PutItemRequest {
        tableName = tableNameVal
        item = itemValues
    }

    DynamoDbClient { region = "us-east-1" }.use { ddb ->
        ddb.putItem(request)
        println(" A new item was placed into $tableNameVal.")
    }
}
// snippet-end:[dynamodb.kotlin.put_item.main]
