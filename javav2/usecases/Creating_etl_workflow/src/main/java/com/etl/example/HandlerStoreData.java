/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.etl.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.jdom2.JDOMException;
import java.io.IOException;

public class HandlerStoreData  implements RequestHandler<String, String>{

    @Override
    public String handleRequest(String event, Context context) {

        LambdaLogger logger = context.getLogger();
        String xml = event ;
        DynamoDBService storeData = new DynamoDBService();
        try {

            storeData.injectETLData(xml);
            logger.log("data stored:");
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
        return "Data is stored successfully.";
    }
}