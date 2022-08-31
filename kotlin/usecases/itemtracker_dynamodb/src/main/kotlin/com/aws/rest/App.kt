/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("api/")
class MessageResource {

    // Adds a new item to the DynamoDB database.
    @RequestMapping(value = ["/add"], method = [RequestMethod.POST])
    @ResponseBody
    fun addItems(@RequestBody payLoad: Map<String, Any>): String = runBlocking {
        val dbService = DynamoDBService()
        val nameVal = "user"
        val guideVal = payLoad.get("guide").toString()
        val descriptionVal = payLoad.get("description").toString()
        val statusVal = payLoad.get("status").toString()

        // Create a Work Item object.
        val myWork = WorkItem()
        myWork.guide = guideVal
        myWork.description = descriptionVal
        myWork.status = statusVal
        myWork.name = nameVal
        val id = dbService.putItemInTable(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Retrieve items.
    @GetMapping("items/{state}")
    fun getItems(@PathVariable state: String): MutableList<WorkItem> = runBlocking {
        val dbService = DynamoDBService()
        val list: MutableList<WorkItem>
        if (state.compareTo("archive") == 0)
            list = dbService.getOpenItems(false)!!
        else
            list = dbService.getOpenItems(true)!!
        return@runBlocking list
    }

    // Flip an item from Active to Archive.
    @PutMapping("mod/{id}")
    fun modUser(@PathVariable id: String): String = runBlocking {
        val dbService = DynamoDBService()
        dbService.archiveItemEC(id)
        return@runBlocking id
    }

    // Emails a report.
    @PutMapping("report/{email}")
    fun sendReport(@PathVariable email: String): String = runBlocking {
        val dbService = DynamoDBService()
        val sendMsg = SendMessage()
        val xml = dbService.getOpenReport(true)
        try {
            sendMsg.send(email, xml)
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking "Report was sent"
    }
}
