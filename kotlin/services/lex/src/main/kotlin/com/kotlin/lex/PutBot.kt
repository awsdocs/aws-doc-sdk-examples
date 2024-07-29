// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.lex

// snippet-start:[lex.kotlin.create_bot.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.ContentType
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Intent
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Locale
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Message
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.PutBotRequest
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Statement
import java.util.ArrayList
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.create_bot.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {
    val usage = """
    
    Usage: 
        <botName> <intentName> <intentVersion> 

    Where:
        botName - The name of the bot (for example, BookHotel).
        intentName - The name of an existing intent (for example, BookHotel).
        intentVersion - The version of the intent (for example, 1).

    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val botName = args[0]
    val intentName = args[1]
    val intentVersion = args[2]
    createBot(botName, intentName, intentVersion)
}

// snippet-start:[lex.kotlin.create_bot.main]
suspend fun createBot(
    botNameVal: String?,
    intentNameVal: String?,
    intentVersionVal: String?,
) {
    // Create an Intent object for the bot.
    val weatherIntent =
        Intent {
            intentName = intentNameVal
            intentVersion = intentVersionVal
        }

    val intentObs = mutableListOf<Intent>()
    intentObs.add(weatherIntent)

    val msg =
        Message {
            content = "I do not understand you!"
            contentType = ContentType.PlainText
        }

    val abortMsg: ArrayList<Message> = ArrayList<Message>()
    abortMsg.add(msg)

    val statement =
        Statement {
            messages = abortMsg
        }

    val request =
        PutBotRequest {
            abortStatement = statement
            description = "Created by using the Amazon Lex Kotlin API"
            childDirected = true
            locale = Locale.fromValue("en-US")
            name = botNameVal
            intents = intentObs
        }

    LexModelBuildingClient { region = "us-west-2" }.use { lexClient ->
        lexClient.putBot(request)
        println("The Amazon Lex bot was successfully created")
    }
}
// snippet-end:[lex.kotlin.create_bot.main]
