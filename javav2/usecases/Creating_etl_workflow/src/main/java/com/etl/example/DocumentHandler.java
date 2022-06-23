/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.etl.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import jxl.read.biff.BiffException;
import java.io.IOException;
import java.util.Map;

public class DocumentHandler {

    public String handleRequest(Map<String,String> event, Context context) throws IOException, BiffException {

        LambdaLogger logger = context.getLogger();
        logger.log("Getting excel doc from the Amazon S3 bucket");

        // Get the Amazon S3 bucket name and MS Excel file name.
        String bucketName = event.get("bucketname");
        String object = event.get("objectname");

        // Get the XML that contains the Pop data.
        ExcelService excel = new ExcelService();
        String xml = excel.getData(bucketName, object);
        return xml;
    }
}
