// snippet-sourcedescription:[GetBots.kt demonstrates how to return information about Amazon Lex chatbots.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Lex]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lex

// snippet-start:[lex.kotlin.get_bots.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.GetBotsRequest
// snippet-end:[lex.kotlin.get_bots.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    getAllBots()
}

// snippet-start:[lex.kotlin.get_bots.main]
suspend fun getAllBots() {

    LexModelBuildingClient { region = "us-west-2" }.use { lexClient ->
        val response = lexClient.getBots(GetBotsRequest {})
        response.bots?.forEach { bot ->
            println("The bot name is ${bot.name}")
            println("The bot version is ${bot.version}")
        }
    }
}
// snippet-end:[lex.kotlin.get_bots.main]
