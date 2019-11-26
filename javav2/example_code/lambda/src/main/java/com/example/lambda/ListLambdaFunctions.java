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
// snippet-sourcedescription:[DynamoDBAsyncGetItem.java demonstrates how to get an item by using the DynamoDbAsyncClient object]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-19]
// snippet-sourceauthor:[AWS-scmacdon]

package com.example.lambda;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.ServiceException;
import java.util.Iterator;
import java.util.List;

public class ListLambdaFunctions {

    public static void main(String[] args) {

        ListFunctionsResponse functionResult = null ;
        try
        {
            Region region = Region.US_WEST_2;
            LambdaClient awsLambda = LambdaClient.builder().region(region).build();

            //Get a list of all functions
            functionResult = awsLambda.listFunctions();

            List<FunctionConfiguration> list = functionResult.functions();

            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                FunctionConfiguration config = (FunctionConfiguration)iter.next();
                System.out.println("The function name is "+config.functionName());
            }
        }
        catch(ServiceException e)
        {
            e.getStackTrace();
        }
    }
}
