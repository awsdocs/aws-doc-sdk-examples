/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package example2

import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.PublishRequest
import aws.sdk.kotlin.services.sns.model.SnsException
import kotlin.system.exitProcess
class SendMessage {

    suspend fun send(phone: String ) {

        val snsClient = SnsClient{ region = "us-east-1" }

        try {
            val request = PublishRequest {
                message = "Hello, please check the database for new ticket assigned to you"
                phoneNumber = phone
            }

            val result = snsClient.publish(request)
            println("${result.messageId} message sent.")

        } catch (e: SnsException) {
            println(e.message)
         snsClient.close()
            exitProcess(0)
        }
    }
}
