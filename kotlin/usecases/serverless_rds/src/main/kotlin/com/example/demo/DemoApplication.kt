/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.demo

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@SpringBootApplication
open class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("api/")
class MessageResource {

    // Add a new item.
    @PostMapping("/add")
    fun addItems(@RequestBody payLoad: Map<String, Any>): String = runBlocking {
        val injectWorkService = InjectWorkService()
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
        val id = injectWorkService.injestNewSubmission(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Retrieve items.
    @GetMapping("items/{state}")
    fun getItems(@PathVariable state: String): MutableList<WorkItem> = runBlocking {
        val retrieveItems = RetrieveItems()
        val list: MutableList<WorkItem>
        val name = "user"
        if (state.compareTo("archive") == 0) {
            list = retrieveItems.getItemsDataSQL(name, 1)
        } else {
            list = retrieveItems.getItemsDataSQL(name, 0)
        }
        return@runBlocking list
    }

    // Flip an item from Active to Archive.
    @PutMapping("mod/{id}")
    fun modUser(@PathVariable id: String): String = runBlocking {
        val retrieveItems = RetrieveItems()
        retrieveItems.flipItemArchive(id)
        return@runBlocking id
    }

    // Send a report through Amazon SES.
    @PutMapping("report/{email}")
    fun sendReport(@PathVariable email: String): String = runBlocking {
        val retrieveItems = RetrieveItems()
        val nameVal = "user"
        val sendMsg = SendMessage()
        val xml = retrieveItems.getItemsDataSQLReport(nameVal, 0)
        try {
            sendMsg.send(email, xml)
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking "Report was sent"
    }
}
