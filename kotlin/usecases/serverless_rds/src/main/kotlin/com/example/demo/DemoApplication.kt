/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.demo

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@SpringBootApplication
open class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
class MessageResource {

    @Autowired
    private lateinit var wi: WorkItemRepository

    @Autowired
    private lateinit var sendMsg: SendMessage

    // Add a new item.
    @PostMapping("api/items")
    fun addItems(@RequestBody payLoad: Map<String, Any>): String = runBlocking {
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
        val id = wi.injestNewSubmission(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Retrieve items.
    @GetMapping("api/items")
    fun getItems(@RequestParam(required = false) archived: String?): MutableList<WorkItem> = runBlocking {
        val wi = WorkItemRepository()
        val list: MutableList<WorkItem>
        if (archived != null) {
            list = wi.getItemsDataSQL(archived)
        } else {
            list = wi.getItemsDataSQL("")
        }
        return@runBlocking list
    }

    // Flip an item from Active to Archive.
    @PutMapping("api/items/{id}:archive")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun modUser(@PathVariable id: String) = runBlocking {
        wi.flipItemArchive(id)
        return@runBlocking
    }

    @PostMapping("api/items:report")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun sendReport(@RequestBody body: Map<String, String>) = runBlocking {
        val email = body.get("email")
        val xml = wi.getItemsDataSQLReport("0")
        try {
            if (email != null) {
                sendMsg.send(email, xml)
            }
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking
    }
}
