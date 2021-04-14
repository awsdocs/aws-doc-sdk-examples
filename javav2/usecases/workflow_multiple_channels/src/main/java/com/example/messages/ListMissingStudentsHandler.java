/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.messages;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.sql.SQLException;
import java.util.Map;

public class ListMissingStudentsHandler implements RequestHandler<Map<String,String>, String> {

    @Override
    public String handleRequest(Map<String,String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String date = event.get("date");
        logger.log("DATE: " + date);

        RDSGetStudents students = new RDSGetStudents();
        String xml = null;
        try {
            xml = students.getStudentsRDS(date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.log("XML: " + xml);
        return xml;
    }
}