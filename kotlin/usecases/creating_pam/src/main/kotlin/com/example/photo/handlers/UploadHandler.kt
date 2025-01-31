// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.photo.services.S3Service
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.util.UUID

class UploadHandler : RequestHandler<APIGatewayProxyRequestEvent?, APIGatewayProxyResponseEvent?> {
    val corsHeaderMap: Map<String, String> = java.util.Map.of("Access-Control-Allow-Origin", "*")

    fun toJson(src: Any?): String? {
        val gson = Gson()
        return gson.toJson(src)
    }

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context): APIGatewayProxyResponseEvent = runBlocking {
        context.getLogger().log("In UploadHandler handler")
        val body: org.json.JSONObject = org.json.JSONObject(input!!.body)
        context.logger.log("Got body: $body")
        val fileName = body.getString("file_name")
        context.logger.log("Building URL for $fileName")

        if (fileName == null || fileName == "") {
            return@runBlocking APIGatewayProxyResponseEvent()
                .withStatusCode(400)
                .withHeaders(corsHeaderMap)
                .withBody("{\"error\":\"Missing filename\"}")
                .withIsBase64Encoded(false)
        }

        val uuid = UUID.randomUUID()
        val uniqueFileName = "$uuid-$fileName"
        val s3Service = S3Service()
        val signedURL = s3Service.signObjectToUpload(uniqueFileName)
        val data = UploadResponse.from(signedURL)
        return@runBlocking makeResponse(data)
    }

    fun makeResponse(src: Any?): APIGatewayProxyResponseEvent = APIGatewayProxyResponseEvent()
        .withStatusCode(200)
        .withHeaders(corsHeaderMap)
        .withBody(toJson(src))
        .withIsBase64Encoded(false)
}

internal class UploadResponse private constructor(val uRL: String) {
    companion object {
        fun from(url: String): UploadResponse = UploadResponse(url)
    }
}
