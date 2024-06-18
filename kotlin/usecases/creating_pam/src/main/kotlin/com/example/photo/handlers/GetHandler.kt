// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.photo.WorkCount
import com.example.photo.services.DynamoDBService
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.util.TreeMap

class GetHandler : RequestHandler<APIGatewayProxyRequestEvent?, APIGatewayProxyResponseEvent?> {
    val corsHeaderMap: Map<String, String> = java.util.Map.of("Access-Control-Allow-Origin", "*")

    fun toJson(src: Any?): String? {
        val gson = Gson()
        return gson.toJson(src)
    }

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context): APIGatewayProxyResponseEvent = runBlocking {
        context.getLogger().log("In Labels handler")
        val dbService = DynamoDBService()
        val map: Map<String, WorkCount> = dbService.scanPhotoTable()
        context.getLogger().log("Retrieved photos: " + map.size)
        val data: MutableMap<String, Map<String, WorkCount>> = TreeMap()
        data["labels"] = map
        return@runBlocking makeResponse(data)
    }

    fun makeResponse(src: Any?): APIGatewayProxyResponseEvent = APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withHeaders(corsHeaderMap)
        .withBody(toJson(src))
        .withIsBase64Encoded(false)
}
