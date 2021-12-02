/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.demo

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@Controller
class MessageResource {

    @Autowired
    lateinit var dbService: DynamoDBService

    @Autowired
    lateinit var sendMsg: SendMessage

    @GetMapping("/")
    fun root(): String? {
        return "index"
    }

    @GetMapping("/add")
    fun designer(): String? {
        return "add"
    }

    @GetMapping("/items")
    fun items(): String? {
        return "items"
    }

    // Adds a new item to the DynamoDB database.
    @RequestMapping(value = ["/additems"], method = [RequestMethod.POST])
    @ResponseBody
    fun addItems(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking{

        val nameVal = "user"
        val guideVal = request.getParameter("guide")
        val descriptionVal = request.getParameter("description")
        val statusVal = request.getParameter("status")

        // Create a Work Item object.
        val myWork = WorkItem()
        myWork.guide = guideVal
        myWork.description = descriptionVal
        myWork.status = statusVal
        myWork.name = nameVal
        val id =  dbService.putItemInTable(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Retrieve items.
    @RequestMapping(value = ["/retrieve"], method = [RequestMethod.POST])
    @ResponseBody
    fun retrieveItems(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking{
        val type = request.getParameter("type")

        // Pass back items from the DynamoDB table.
        var xml: String?
        if (type.compareTo("archive") == 0)
            xml = dbService.getOpenItems(false)
        else
            xml = dbService.getOpenItems(true)

        return@runBlocking xml
    }

    // Returns a work item to modify.
    @RequestMapping(value = ["/modify"], method = [RequestMethod.POST])
    @ResponseBody
    fun modifyWork(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val id = request.getParameter("id")
        return@runBlocking dbService.getItem(id)
    }

    // Modifies the value of a work item.
    @RequestMapping(value = ["/modstatus"], method = [RequestMethod.POST])
    @ResponseBody
    fun changeWorkItem(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val id = request.getParameter("id")
        val status = request.getParameter("stat")
        dbService.updateTableItem(id, status)
        return@runBlocking id
    }

    // Archives a work item.
    @RequestMapping(value = ["/archive"], method = [RequestMethod.POST])
    @ResponseBody
    fun archieveWorkItem(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking{
        val id = request.getParameter("id")
        dbService.archiveItemEC(id)
        return@runBlocking id
    }

    // Emails a report.
    @RequestMapping(value = ["/report"], method = [RequestMethod.POST])
    @ResponseBody
    fun getReport(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val email = request.getParameter("email")
        val xml = dbService.getOpenItems(true)
        try {
            sendMsg.send(email, xml)
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking "Report was sent"
    }
}
