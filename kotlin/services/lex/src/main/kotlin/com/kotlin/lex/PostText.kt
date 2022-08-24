// snippet-sourcedescription:[PostText.kt demonstrates how to send text to an Amazon Lex conversational bot.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Lex]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lex

// snippet-start:[lex.kotlin.text.import]
import aws.sdk.kotlin.services.lexruntimeservice.LexRuntimeClient
import aws.sdk.kotlin.services.lexruntimeservice.model.PostTextRequest
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.text.import]

/*
In this example, the Amazon Lex BookTrip example is used. For more information, see https://docs.aws.amazon.com/lex/latest/dg/ex-book-trip.html.


Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
    
        Usage: 
            <botName> 

        Where:
          botName - The name of the bot (for example, BookHotel).
          botAlias - The bot alias.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val botName = args[0]
    val botAlias = args[1]
    val inputText = "I need a hotel room"
    val textReponse = getText(inputText, botName, botAlias)
    println(textReponse)
}

// snippet-start:[lex.kotlin.text.main]
suspend fun getText(text: String, botNameVal: String, botAliasVal: String): String? {

    val userIdVal = "chatbot-demo"
    val sessionAttributesVal = mutableMapOf<String, String>()

    val request = PostTextRequest {
        botName = botNameVal
        botAlias = botAliasVal
        inputText = text
        userId = userIdVal
        sessionAttributes = sessionAttributesVal
    }

    LexRuntimeClient { region = "us-west-2" }.use { lexClient ->
        val textResponse = lexClient.postText(request)
        val message = textResponse.message
        return message
    }
}
// snippet-end:[lex.kotlin.text.main]
