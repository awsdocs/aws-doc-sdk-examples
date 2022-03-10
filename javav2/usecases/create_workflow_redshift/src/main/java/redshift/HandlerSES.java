/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package redshift;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

public class HandlerSES implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String event, Context context)
    {
        LambdaLogger logger = context.getLogger();
        String val = event ;

        SendMessage msg = new SendMessage();
        String sender = "scmacdon@amazon.com" ;
        String recipient = "scmacdon@amazon.com" ;
        String subject = "Deleted Amazon Redshift Record" ;

        Region region = Region.US_EAST_1;
        SesClient client = SesClient.builder()
                .region(region)
                .build();

        // The HTML body of the email
        String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                + "<p> Amazon Redshift record "+val +" was deleted!</p>" + "</body>" + "</html>";

        try {
            msg.sendMessage(client, sender, recipient, subject, bodyHTML);

        } catch (javax.mail.MessagingException e)
        {
            e.getStackTrace();
        }

        return "Ok" ;
    }
}
