/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.handlingformsubmission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GreetingController {

    @Autowired
    private DynamoDBEnhanced dde;

    @Autowired
    private PublishTextSMS msg;

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting) {

        // Persist submitted data into a DynamoDB table using the Enhanced client.
        dde.injectDynamoItem(greeting);

        // Send a mobile notification.
        msg.sendMessage(greeting.getId());

        return "result";
    }
}
