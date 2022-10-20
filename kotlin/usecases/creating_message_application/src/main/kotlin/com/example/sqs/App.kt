/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sqs

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("chat/")
class MessageResource {

    // Get messages from the FIFO queue.
    @GetMapping("msgs")
    fun getItems(request: HttpServletRequest?, response: HttpServletResponse?): List<MessageData?>? = runBlocking {
        val msgService = SendReceiveMessages()
        return@runBlocking msgService.getMessages()
    }

    //  Purge the queue.
    @GetMapping("purge")
    fun purgeMessages(request: HttpServletRequest?, response: HttpServletResponse?): String? = runBlocking {
        val msgService = SendReceiveMessages()
        msgService.purgeMyQueue()
        return@runBlocking "Queue is purged"
    }

    // Adds a new message to the FIFO queue.
    @PostMapping("add")
    fun addItems(request: HttpServletRequest, response: HttpServletResponse?): List<MessageData?>? = runBlocking {
        val user = request.getParameter("user")
        val message = request.getParameter("message")
        val msgService = SendReceiveMessages()

        // Generate the ID.
        val uuid = UUID.randomUUID()
        val msgId = uuid.toString()
        val messageOb = MessageData()
        messageOb.id = msgId
        messageOb.name = user
        messageOb.body = message
        msgService.processMessage(messageOb)
        return@runBlocking msgService.getMessages()
    }
}
