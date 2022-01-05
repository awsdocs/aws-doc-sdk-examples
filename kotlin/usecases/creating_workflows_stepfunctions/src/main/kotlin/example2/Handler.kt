/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package example2

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking


// Handler value: example.Handler
class Handler : RequestHandler<Map<String?, String>, String> {

    override fun handleRequest(event: Map<String?, String>, context: Context): String = runBlocking {
        val logger = context.logger
        val gson = GsonBuilder().create()

        // Log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()))
        logger.log("CONTEXT: " + gson.toJson(context))
        // process event
        logger.log("EVENT Data: " + gson.toJson(event))
        val myCaseID = event["inputCaseID"]!!
        logger.log("Case number: $myCaseID")
        return@runBlocking myCaseID
    }
}