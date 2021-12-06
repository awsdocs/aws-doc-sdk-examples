//snippet-sourcedescription:[GetIntent.kt demonstrates how to get intent information from an Amazon Lex conversational bot.]
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

// snippet-start:[lex.kotlin.get_intent.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.GetIntentRequest
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.get_intent.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    
    Usage: 
        <intentName> <intentVersion> 

    Where:
        intentName - the name of an existing intent (for example, BookHotel).
        intentVersion - the version of the intent (for example, 1).

    """

    if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val intentName = args[0]
    val intentVersion = args[1]
    getSpecificIntent(intentName,intentVersion )
    }

// snippet-start:[lex.kotlin.get_intent.main]
suspend fun getSpecificIntent(intentName: String?, intentVersion: String?) {

    val request = GetIntentRequest {
        name = intentName
        version = intentVersion
    }

    LexModelBuildingClient { region = "us-west-2" }.use { lexClient ->
        val intentResponse = lexClient.getIntent(request)
        println("The description is  ${intentResponse.description}.")
    }
}
// snippet-end:[lex.kotlin.get_intent.main]