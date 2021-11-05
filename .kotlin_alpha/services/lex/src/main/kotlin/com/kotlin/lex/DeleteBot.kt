//snippet-sourcedescription:[DeleteBot.kt demonstrates how to delete an Amazon Lex conversational bot.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Lex]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lex

// snippet-start:[lex.kotlin.delete_bot.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.DeleteBotRequest
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.LexModelBuildingException
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.delete_bot.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    
        Usage: 
            <botName> 

        Where:
          botName - the name of bot (for example, BookHotel).
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val botName = args[0]
    val lexModel = LexModelBuildingClient{region="us-west-2"}
    deleteSpecificBot(lexModel, botName)
}

// snippet-start:[lex.kotlin.delete_bot.main]
suspend fun deleteSpecificBot(lexClient: LexModelBuildingClient, botName: String) {
    try {
        val botRequest = DeleteBotRequest {
            name = botName
        }
        lexClient.deleteBot(botRequest)
        println("$botName was deleted!")

    } catch (ex: LexModelBuildingException) {
        println(ex.message)
        lexClient.close()
        exitProcess(0)
    }
}
// snippet-end:[lex.kotlin.delete_bot.main]