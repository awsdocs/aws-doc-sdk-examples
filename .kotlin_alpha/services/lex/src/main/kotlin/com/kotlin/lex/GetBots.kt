//snippet-sourcedescription:[GetBots.kt demonstrates how to return information about Amazon Lex chatbots.]
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

// snippet-start:[lex.kotlin.get_bots.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.GetBotsRequest
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.LexModelBuildingException
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.get_bots.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val lexModel = LexModelBuildingClient{region="us-west-2"}
    getAllBots(lexModel)
}

// snippet-start:[lex.kotlin.get_bots.main]
suspend fun getAllBots(lexModel: LexModelBuildingClient) {

    try {

        val response = lexModel.getBots(GetBotsRequest{})
        response.bots?.forEach { bot ->
               println("The bot name is ${bot.name}")
                println("The bot version is ${bot.version}")
        }

    } catch (ex:  LexModelBuildingException) {
        println(ex.message)
        lexModel.close()
        exitProcess(0)
    }
}
// snippet-end:[lex.kotlin.get_bots.main]