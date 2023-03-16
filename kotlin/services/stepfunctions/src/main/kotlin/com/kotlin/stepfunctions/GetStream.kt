/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.stepfunctions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream

class GetStream {
    suspend fun getStream(): String {
        // Get JSON to use for the state machine and place the activityArn value into it.
        val input: InputStream = this::class.java.classLoader.getResourceAsStream("chat_sfn_state_machine.json")
        val mapper = ObjectMapper()
        val jsonNode: JsonNode = mapper.readValue(input, JsonNode::class.java)
        return mapper.writeValueAsString(jsonNode)
    }
}
