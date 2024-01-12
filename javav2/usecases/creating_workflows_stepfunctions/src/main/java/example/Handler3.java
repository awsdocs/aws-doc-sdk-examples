// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.IOException;

// Handler value: example.Handler
public class Handler3 implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String event, Context context) {
        LambdaLogger logger = context.getLogger();
        String email = event;

        // log execution details
        logger.log("Email value " + email);

        SendMessage msg = new SendMessage();
        try {
            msg.sendMessage(email);

        } catch (IOException e) {
            e.getStackTrace();
        }

        return "";

    }
}
