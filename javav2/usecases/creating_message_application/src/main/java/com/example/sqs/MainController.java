/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sqs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/chat")
public class MainController {

    @Autowired
    SendReceiveMessages msgService;

    // Adds a new message to the FIFO queue.
    @PostMapping("/add")
    List<MessageData> addItems(HttpServletRequest request, HttpServletResponse response) {
        String user = request.getParameter("user");
        String message = request.getParameter("message");

        // Generate the ID.
        UUID uuid = UUID.randomUUID();
        String msgId = uuid.toString();
        MessageData messageOb = new MessageData();
        messageOb.setId(msgId);
        messageOb.setName(user);
        messageOb.setBody(message);
        msgService.processMessage(messageOb);
        return msgService.getMessages();
    }

    //  Purge the queue.
    @RequestMapping(value = "/purge", method = RequestMethod.GET)
    @ResponseBody
    String purgeMessages(HttpServletRequest request, HttpServletResponse response) {
        msgService.purgeMyQueue();
        return "Queue is purged";
    }

    // Get messages from the FIFO queue.
    @RequestMapping(value = "/msgs", method = RequestMethod.GET)
    @ResponseBody
    List<MessageData> getItems(HttpServletRequest request, HttpServletResponse response) {
        List<MessageData> data =  msgService.getMessages();
        return data;
    }
}
