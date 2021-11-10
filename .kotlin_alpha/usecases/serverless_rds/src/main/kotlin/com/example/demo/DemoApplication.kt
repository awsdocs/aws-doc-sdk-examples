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
    lateinit var injectItems: InjectWorkService

    @Autowired
    lateinit var sendMsg: SendMessage

    @Autowired
    lateinit var ri: RetrieveItems

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

    // Retrieve all items for a given user.
    @RequestMapping(value = ["/retrieve"], method = [RequestMethod.POST])
    @ResponseBody
    fun retrieveItems(request: HttpServletRequest, response: HttpServletResponse?): String?  = runBlocking {

        val type = request.getParameter("type")
        val name = "user"

        // Pass back all data from the database.
        val xml: String?

        return@runBlocking if (type == "active") {
            xml = ri.getItemsDataSQL(name, 0)
            xml
        } else {
            xml = ri.getItemsDataSQL(name, 1)
            xml
        }
    }

    // Add a new item to the database.
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
        val id =  injectItems.injestNewSubmission(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Return a work item to modify.
    @RequestMapping(value = ["/modify"], method = [RequestMethod.POST])
    @ResponseBody
    fun modifyWork(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val id = request.getParameter("id")
        return@runBlocking ri.getItemSQL(id)
    }

    // Modify the value of a work item.
    @RequestMapping(value = ["/modstatus"], method = [RequestMethod.POST])
    @ResponseBody
    fun changeWorkItem(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val id = request.getParameter("id")
        val status = request.getParameter("stat")
        injectItems.modifySubmission(id, status)
        return@runBlocking id
    }

    // Archive a work item.
    @RequestMapping(value = ["/archive"], method = [RequestMethod.POST])
    @ResponseBody
    fun archieveWorkItem(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking{
        val id = request.getParameter("id")
        ri.flipItemArchive(id)
        return@runBlocking id
    }

     // Email a report.
     @RequestMapping(value = ["/report"], method = [RequestMethod.POST])
     @ResponseBody
     fun getReport(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
         val email = request.getParameter("email")
         val xml = ri.getItemsDataSQLReport("user", 0)
         try {
             sendMsg.send(email, xml)
         } catch (e: Exception) {
             e.stackTrace
         }
         return@runBlocking "Report was sent"
     }
}
