//snippet-sourcedescription:[GetSlotTypes.kt demonstrates how to return slot type information.]
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


// snippet-start:[lex.kotlin.get_slot_types.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.GetSlotTypesRequest
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.LexModelBuildingException
import kotlin.system.exitProcess
// snippet-end:[lex.kotlin.get_slot_types.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val lexModel = LexModelBuildingClient{region="us-west-2"}
    getSlotsInfo(lexModel)
}

// snippet-start:[lex.kotlin.get_slot_types.main]
suspend fun getSlotsInfo(lexClient: LexModelBuildingClient) {
    try {
        val response = lexClient.getSlotTypes(GetSlotTypesRequest{ })
        response.slotTypes?.forEach { slot ->
              println("Slot name is ${slot.name}.")
              println("Slot description is ${slot.description}.")
              println("Slot version is ${slot.version}.")
       }

    } catch (ex:  LexModelBuildingException) {
        println(ex.message)
        lexClient.close()
        exitProcess(0)
    }
}
// snippet-end:[lex.kotlin.get_slot_types.main]