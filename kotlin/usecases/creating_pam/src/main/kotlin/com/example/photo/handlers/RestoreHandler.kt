// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.example.photo.endpoints.DownloadEndpoint
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import java.util.stream.Collectors

class RestoreHandler : RequestHandler<MutableMap<String, Any>, String> {
    fun toJson(src: Any?): String? {
        val gson = Gson()
        return gson.toJson(src)
    }

    override fun handleRequest(input: MutableMap<String, Any>?, context: Context?): String = runBlocking {
        try {
            if (context != null) {
                context.getLogger().log("***** RestoreHandler handleRequest" + toJson(input))
                val body = JSONObject(input)
                context.logger.log(body.toString())

                val labels = body.getJSONArray("labels")
                    .toList()
                    .stream()
                    .filter { o: Any? -> String::class.java.isInstance(o) }
                    .map { obj: Any? -> String::class.java.cast(obj) }
                    .collect(Collectors.toList())

                context.logger.log("Restoring labels " + toJson(labels))
                val downloadEndpoint = DownloadEndpoint()
                val url: String = downloadEndpoint.downloadFiles(labels)
                return@runBlocking "Labels archived to URL $url"
            }
        } catch (e: JSONException) {
            if (context != null) {
                context.logger.log(e.message)
            }
        }
        return@runBlocking ""
    }
}
