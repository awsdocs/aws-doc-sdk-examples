//snippet-sourcedescription:[GetBotStatus.kt demonstrates how to get the status of an Amazon Lex bot.]
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

// snippet-start:[lex.kotlin.get_status.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.*
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.get_status.import]

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
        botName - the name of the bot (for example, BookHotel).

    """

    if (args.size != 1) {
         println(usage)
         exitProcess(0)
    }

    val botName = args[0]
    val lexModel = LexModelBuildingClient{region="us-west-2"}
    getStatus(lexModel, botName)
}

// snippet-start:[lex.kotlin.get_status.main]
suspend fun getStatus(lexClient: LexModelBuildingClient, botName: String?) {

    val botRequest = GetBotRequest {
         name = botName
         versionOrAlias = "\$LATEST"
    }
    try {
        var status: String

        // Loop until the bot is in a ready status.
        do {

            // Wait 2 secs
            delay(2000)
            val response: GetBotResponse = lexClient.getBot(botRequest)
            status = response.status.toString()
            println("The status is $status")

        } while (status.compareTo("READY") != 0)

    } catch (ex:  LexModelBuildingException) {
        println(ex.message)
        lexClient.close()
        exitProcess(0)
    } catch (e: InterruptedException) {
        println(e.localizedMessage)
        exitProcess(1)
    }
}
// snippet-end:[lex.kotlin.get_status.main]