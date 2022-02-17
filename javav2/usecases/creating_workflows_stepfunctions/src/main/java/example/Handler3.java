/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.

 */


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
        String email = event ;

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
