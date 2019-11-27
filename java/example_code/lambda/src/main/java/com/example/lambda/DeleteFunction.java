/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[LambdaInvoke.java demonstrates how to invoke an AWS Lambda function by using the AWSLambda object]
// snippet-service:[Lambda]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-19]
// snippet-sourceauthor:[AWS-scmacdon]

package com.example.lambda;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;

public class DeleteFunction {

    public static void main(String[] args) {

        try {
        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(Regions.US_WEST_2).build();

        DeleteFunctionRequest delFunc = new DeleteFunctionRequest();
        delFunc.withFunctionName("somefunction");

        //Delete the functiom
        awsLambda.deleteFunction(delFunc);
        System.out.println("The function is deleted");
    }
    catch (
    ServiceException e) {
        System.out.println(e);
    }
   }
}

