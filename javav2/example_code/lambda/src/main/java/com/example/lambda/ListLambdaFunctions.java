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
// snippet-sourcedescription:[ListLambdaFunctions.java demonstrates how to list all functions speciic to the account by using the LambdaClient object]
// snippet-service:[Lambda]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-19]
// snippet-sourceauthor:[AWS-scmacdon]

// snippet-start:[lambda.java2.ListLambdaFunctions.complete]
package com.example.lambda;

// snippet-start:[lambda.java2.list.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.ServiceException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[lambda.java2.list.import]


public class ListLambdaFunctions {

    public static void main(String[] args) {

        // snippet-start:[lambda.java2.list.main]
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
        // snippet-end:[lambda.java2.list.main]
    }
}
// snippet-end:[lambda.java2.ListLambdaFunctions.complete]
