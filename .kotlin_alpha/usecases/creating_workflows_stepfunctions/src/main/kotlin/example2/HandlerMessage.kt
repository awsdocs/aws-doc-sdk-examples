/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package example2

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import kotlinx.coroutines.runBlocking
import java.io.IOException

class HandlerMessage : RequestHandler<String, String> {

    override fun handleRequest(event: String, context: Context): String = runBlocking {
        val logger = context.logger

        val phone = event
        logger.log("Phone value $phone")
        val msg = SendMessage()

        try {
            msg.send(event)
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking phone
    }
 }

