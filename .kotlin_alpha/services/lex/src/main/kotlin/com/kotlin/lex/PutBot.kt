//snippet-sourcedescription:[PutBot.kt demonstrates how to creates an Amazon Lex conversational bot.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Lex]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/25/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lex

// snippet-start:[lex.kotlin.create_bot.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Intent
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Message
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Statement
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.ContentType
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.PutBotRequest
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.Locale
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.LexModelBuildingException
import java.util.ArrayList
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.create_bot.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    
    Usage: 
        <botName> <intentName> <intentVersion> 

    Where:
        botName - the name of bot (for example, BookHotel).
        intentName - the name of an existing intent (for example, BookHotel).
        intentVersion - the version of the intent (for example, 1).

    """

   if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val botName = args[0]
    val intentName = args[1]
    val intentVersion = args[2]

    val lexModel = LexModelBuildingClient{region="us-west-2"}
    createBot(lexModel, botName, intentName, intentVersion)
    lexModel.close()
}

// snippet-start:[lex.kotlin.create_bot.main]
suspend fun createBot(
    lexClient: LexModelBuildingClient,
    botNameVal: String?,
    intentNameVal: String?,
    intentVersionVal: String?
) {
    try {

        // Create an Intent object for the bot.
        val weatherIntent = Intent {
            intentName = intentNameVal
            intentVersion = intentVersionVal
        }

        val intentObs: MutableList<Intent> = mutableListOf()
        intentObs.add(weatherIntent)

        val msg = Message {
            content = "I do not understand you!"
            contentType = ContentType.PlainText
        }

        val abortMsg: ArrayList<Message> = ArrayList<Message>()
        abortMsg.add(msg)

        val statement = Statement {
            messages = abortMsg
        }

        val botRequest = PutBotRequest {
            abortStatement = statement
            description = "Created by using the Amazon Lex Kotlin API"
            childDirected = true
            locale = Locale.fromValue("en-US")
            name = botNameVal
            intents = intentObs
        }

        lexClient.putBot(botRequest)
        println("The Amazon Lex bot was successfully created")

    } catch (ex:  LexModelBuildingException) {
        println(ex.message)
        lexClient.close()
        exitProcess(0)
    }
}
// snippet-end:[lex.kotlin.create_bot.main]