//snippet-sourcedescription:[PostText.kt demonstrates how to send text to an Amazon Lex conversational bot.]
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

// snippet-start:[lex.kotlin.text.import]
import aws.sdk.kotlin.services.lexruntimeservice.LexRuntimeClient
import aws.sdk.kotlin.services.lexruntimeservice.model.PostTextRequest
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.text.import]

/*
In this example, the Amazon Lex BookTrip example is used. For more information, see https://docs.aws.amazon.com/lex/latest/dg/ex-book-trip.html.

To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    
        Usage: 
            <botName> 

        Where:
          botName - the name of the bot (for example, BookHotel).
          botAlias - the bot alias.
    """

    val botName = args[0]
    val botAlias = args[1]
    val inputText = "I need a hotel room"
    val lexClient = LexRuntimeClient{region = "us-east-1"}
    val textReponse = getText(lexClient,inputText, botName,botAlias )
    println(textReponse)
}

// snippet-start:[lex.kotlin.text.main]
suspend fun getText(lexClient : LexRuntimeClient, text: String,botNameVal:String, botAliasVal:String ): String? {

    try{

        val userIdVal = "chatbot-demo"
        val sessionAttributesVal =  mutableMapOf<String, String>()
        val textRequest = PostTextRequest {
            botName = botNameVal
            botAlias = botAliasVal
            inputText=text
            userId = userIdVal
            sessionAttributes = sessionAttributesVal
         }

        val textResponse = lexClient.postText(textRequest)
        val message = textResponse.message
        return message

    } catch (e: InterruptedException) {
        System.out.println(e.localizedMessage)
        exitProcess(1)
    }
}
// snippet-end:[lex.kotlin.text.main]
