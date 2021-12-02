/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package example2

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking

class HandlerTicket: RequestHandler<String, String> {

    override fun handleRequest(event: String, context: Context): String = runBlocking {

        var phoneNum = "<Enter Mobile number>"
        var phoneNum2 = "<Enter Mobile number>"
        val logger = context.logger
        val gson = GsonBuilder().create()

        val value: String = event
        logger.log("CASE is about to be assigned $value")

        // Create very simple logic to assign case to an employee
        val tmp = if (Math.random() <= 0.5) 1 else 2
        val perCase = PersistCase()
        logger.log("TMP IS $tmp")

        var phone = ""

        if (tmp == 1) {
            // assign to tblue
            phone = phoneNum
            perCase.putItemInTable(value, "Tom Blue", phone)
        } else {
            // assign to swhite
            phone = phoneNum2
            perCase.putItemInTable(value, "Sarah White", phone)
        }

        logger.log("Phone num IS $phone")
        return@runBlocking phone
    }
}
