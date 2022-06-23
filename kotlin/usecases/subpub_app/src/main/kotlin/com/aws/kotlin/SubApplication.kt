/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.kotlin

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class SubApplication

fun main(args: Array<String>) {
    runApplication<SubApplication>(*args)
}

@Controller
class MessageResource {

    @Autowired
    var sns: SnsService? = null

    @GetMapping("/")
    fun root(): String? {
        return "index"
    }

    @GetMapping("/subscribe")
    fun add(): String? {
        return "sub"
    }


    @RequestMapping(value = ["/delSub"], method = [RequestMethod.POST])
    @ResponseBody
    fun delSub(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val email = request.getParameter("email")
        sns?.unSubEmail(email)
        return@runBlocking "$email was successfully deleted!"
    }

    @RequestMapping(value = ["/addEmail"], method = [RequestMethod.POST])
    @ResponseBody
    fun addItems(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val email = request.getParameter("email")
        return@runBlocking sns?.subEmail(email)
    }

    @RequestMapping(value = ["/addMessage"], method = [RequestMethod.POST])
    @ResponseBody
    fun addMessage(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val body = request.getParameter("body")
        val lang = request.getParameter("lang")
        return@runBlocking sns?.pubTopic(body,lang)
    }

    @RequestMapping(value = ["/getSubs"], method = [RequestMethod.GET])
    @ResponseBody
    fun getSubs(request: HttpServletRequest?, response: HttpServletResponse?): String? = runBlocking{
        return@runBlocking sns?.getAllSubscriptions()
    }
}
