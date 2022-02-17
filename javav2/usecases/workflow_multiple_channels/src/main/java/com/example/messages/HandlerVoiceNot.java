/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.messages;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.jdom2.JDOMException;
import javax.mail.MessagingException;
import java.io.IOException;

public class HandlerVoiceNot implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String event, Context context) {

        LambdaLogger logger = context.getLogger();
        String xml = event;
        String num = "";
        SendNotifications sn = new SendNotifications();

        try {

           sn.handleTextMessage(xml);
           sn.handleVoiceMessage(xml);
           num = sn.handleEmailMessage(xml);
           logger.log("email: " + num);
        } catch (JDOMException | IOException | MessagingException e) {
            e.printStackTrace();
        }
        return num;
    }
}
