/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 *  This is the entry point for the Lambda function.
 */

public class Handler {

     public Void handleRequest(Context context) {
        LambdaLogger logger = context.getLogger();
        ScanEmployees scanEmployees = new ScanEmployees();
       Boolean ans =  scanEmployees.sendEmployeMessage();
        if (ans)
            logger.log("Messages sent: " + ans);
        return null;
    }
}
