// snippet-sourcedescription:[GetIntent.kt demonstrates how to get intent information from an Amazon Lex conversational bot.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Lex]

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
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    
    Usage: 
        <intentName> <intentVersion> 

    Where:
        intentName - The name of an existing intent (for example, BookHotel).
        intentVersion - The version of the intent (for example, 1).

    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val intentName = args[0]
    val intentVersion = args[1]
    getSpecificIntent(intentName, intentVersion)
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
