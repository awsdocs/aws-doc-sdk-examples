/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.aws.job

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class JobApp

    fun main(args: Array<String>) {
        runApplication<JobApp>(*args)
    }

@Controller
class MessageResource {

    @Autowired
    var rs: RedshiftService? = null

    @GetMapping("/")
    fun root(): String? {
        return "index"
    }

    @GetMapping("/add")
    fun add(): String? {
        return "add"
    }

    @GetMapping("/posts")
    fun post(): String? {
        return "post"
    }

    // Adds a new item to the database.
    @RequestMapping(value = ["/addPost"], method = [RequestMethod.POST])
    @ResponseBody
    fun addItems(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking{
        val name: String = "user"
        val title = request.getParameter("title")
        val body = request.getParameter("body")
        val myId = rs?.addRecord(name, title, body)
        return@runBlocking myId
    }

    // Queries items from the Redshift database.
    @RequestMapping(value = ["/getPosts"], method = [RequestMethod.POST])
    @ResponseBody
    fun getFivePosts(request: HttpServletRequest, response: HttpServletResponse?): String?  = runBlocking{
        val num = request.getParameter("number")
        val lang = request.getParameter("lang")
        return@runBlocking rs!!.getPosts(lang, num.toInt())
    }
}