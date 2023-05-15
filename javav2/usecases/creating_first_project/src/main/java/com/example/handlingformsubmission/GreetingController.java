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
    private final DynamoDBEnhanced dde;
    private final PublishTextSMS msg;

    @Autowired
    GreetingController(
        DynamoDBEnhanced dde,
        PublishTextSMS msg
    ) {
        this.dde = dde;
        this.msg = msg;
    }

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute Greeting greeting) {
        
        // Persist submitted data into a DynamoDB table.
        dde.injectDynamoItem(greeting);

        // Send a mobile notification.
        msg.sendMessage(greeting.getId());
        return "result";
    }
}