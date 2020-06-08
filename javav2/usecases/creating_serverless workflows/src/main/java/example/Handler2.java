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

// Handler value: example.Handler
public class Handler2 implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String event, Context context) {

        PersistCase perCase = new PersistCase();

        LambdaLogger logger = context.getLogger();
        String val = event ;
       logger.log("CASE is about to be assigned " +val);

       // Create very simple logic to assign case to an employee
        int tmp = (Math.random() <= 0.5) ? 1 : 2;

        logger.log("TMP IS " +tmp);

        String emailEmp= "";

        if (tmp == 1) {
            // assign to tblue
            emailEmp = "tblue@noServer.com";
            perCase.putRecord(val, "Tom Blue", emailEmp );
        } else {
            // assign to swhite
            emailEmp = "swhite@noServer.com";
            perCase.putRecord(val, "Sarah White", emailEmp);
        }

        logger.log("emailEmp IS " +emailEmp);
        //return email - used in the next step
        return emailEmp;
        }
}
