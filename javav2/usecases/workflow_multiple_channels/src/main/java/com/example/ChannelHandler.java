/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.jdom2.JDOMException;
import javax.mail.MessagingException;
import java.io.IOException;

public class ChannelHandler implements RequestHandler<String, String> {
    @Override
    public String handleRequest(String event, Context context) {
        LambdaLogger logger = context.getLogger();
        String xml = event;
        int num =0;
        SendNotifications sn = new SendNotifications();
        try {
           sn.handleTextMessage(xml);
           num = sn.handleEmailMessage(xml);
           logger.log("The workflow sent "+num +" email messages");
        } catch (JDOMException | IOException | MessagingException e) {
            e.printStackTrace();
        }
        return "The workflow sent "+num +" email messages";
    }
}
