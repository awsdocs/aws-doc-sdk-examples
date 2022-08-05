// snippet-sourcedescription:[GetSlotTypes.kt demonstrates how to return slot type information.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Code Sample]
// snippet-service:[Amazon Lex]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/27/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lex

// snippet-start:[lex.kotlin.get_slot_types.import]
import aws.sdk.kotlin.services.lexmodelbuildingservice.LexModelBuildingClient
import aws.sdk.kotlin.services.lexmodelbuildingservice.model.GetSlotTypesRequest
// snippet-end:[lex.kotlin.get_slot_types.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    getSlotsInfo()
}

// snippet-start:[lex.kotlin.get_slot_types.main]
suspend fun getSlotsInfo() {

    LexModelBuildingClient { region = "us-west-2" }.use { lexClient ->

        val response = lexClient.getSlotTypes(GetSlotTypesRequest { })
        response.slotTypes?.forEach { slot ->
            println("Slot name is ${slot.name}.")
            println("Slot description is ${slot.description}.")
            println("Slot version is ${slot.version}.")
        }
    }
}
// snippet-end:[lex.kotlin.get_slot_types.main]
