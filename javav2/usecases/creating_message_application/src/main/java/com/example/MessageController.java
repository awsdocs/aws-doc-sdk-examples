/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class MessageController {

    @Autowired
    SendReceiveMessages msgService;

    @GetMapping("/")
    public String root() {
        return "index";
    }

    // Gets messages
    @RequestMapping(value = "/populate", method = RequestMethod.GET)
    @ResponseBody
    String getItems(HttpServletRequest request, HttpServletResponse response) {

       String xml= msgService.getMessages();
       return xml;
    }


    //  Creates a new message
    @RequestMapping(value = "/purge", method = RequestMethod.GET)
    @ResponseBody
    String purgeMessages(HttpServletRequest request, HttpServletResponse response) {

        msgService.purgeMyQueue();
        return "Queue is purged";
    }



    //  Creates a new message
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {

        String user = request.getParameter("user");
        String message = request.getParameter("message");

        // generate the ID
        UUID uuid = UUID.randomUUID();
        String msgId = uuid.toString();

        Message messageOb = new Message();
        messageOb.setId(msgId);
        messageOb.setName(user);
        messageOb.setBody(message);

        msgService.processMessage(messageOb);
        String xml= msgService.getMessages();

        return xml;
    }

    @GetMapping("/message")
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Message());
        return "message";
    }
}