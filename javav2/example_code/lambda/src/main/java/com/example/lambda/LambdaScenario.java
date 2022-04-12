// snippet-sourcedescription:[LambdaScenario.java demonstrates how to perform various operations by using the LambdaClient object.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[04/14/2022]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.lambda;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.Runtime;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 *  Function names appear as arn:aws:lambda:us-west-2:335556666777:function:HelloFunction
 *  you can retrieve the value by looking at the function in the AWS Console
 *
 *  Before running this Java code example, set up your development environment, including your credentials.
 *
 *  For more information, see this documentation topic:
 *
 *  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 *  This example performs these tasks.
 *
 * 1. Create an AWS Lambda function.
 * 2. Gets a specific AWS Lambda function.
 * 3. List all AWS Lambda functions.
 * 4. Invokes an AWS Lambda function.
 * 5. Updates an AWS Lambda function's code.
 * 6. Updates an AWS Lambda function's configuration value.
 * 7. Deletes an AWS Lambda function.
 *
 */
public class LambdaScenario {

    public static void main(String[] args) throws InterruptedException {

        final String usage = "\n" +
                "Usage:\n" +
                "    <functionName> <filePath> <role> <handler> <bucketName> <key> \n\n" +
                "Where:\n" +
                "    functionName - The name of the AWS Lambda function. \n"+
                "    filePath - The path to the ZIP or JAR where the code is located. \n"+
                "    role - The AWS Identity and Access Management (IAM) service role that has AWS Lambda permissions. \n"+
                "    handler - The fully qualified method name (for example, example.Handler::handleRequest). \n"+
                "    bucketName - The Amazon Simple Storage Service (Amazon S3) bucket name that contains the ZIP or JAR used to update the Lambda function's code. \n"+
                "    key - The Amazon S3 key name that represents the ZIP or JAR file (for example, LambdaHello-1.0-SNAPSHOT.jar)." ;

        if (args.length != 6) {
            System.out.println(usage);
            System.exit(1);
        }

        String functionName = args[0];
        String filePath = args[1];
        String role = args[2];
        String handler = args[3];
        String bucketName = args[4];
        String key = args[5];
        Region region = Region.US_WEST_2;
        LambdaClient awsLambda = LambdaClient.builder()
                .region(region)
                .build();

        String funArn = createLambdaFunction(awsLambda, functionName, filePath, role, handler);
        System.out.println("The AWS Lambda ARN is "+funArn);

        // Get the Lambda function.
        System.out.println("Getting the " +functionName +" AWS Lambda function.");
        getFunction(awsLambda, functionName);

        // List the Lambda functions.
        System.out.println("Listing all AWS Lambda functions.");
        listFunctions(awsLambda);

        // Invoke the Lambda function.
        System.out.println("*** Wait for 2 MIN so the resource is available.");
        TimeUnit.MINUTES.sleep(2);
        invokeFunction(awsLambda, functionName);

        // Update the AWS Lambda function code.
        System.out.println("*** Update the AWS Lambda function code. ");
        updateFunctionCode(awsLambda, functionName, bucketName, key);
        TimeUnit.MINUTES.sleep(2);

        // Update the AWS Lambda function configuration.
        System.out.println("Update the run time of the function.");
        UpdateFunctionConfiguration(awsLambda, functionName, handler);

        // Invoke again with updated function code.
        TimeUnit.MINUTES.sleep(2);
        invokeFunction(awsLambda, functionName);

        // Delete the AWS Lambda function.
        System.out.println("Delete the AWS Lambda function.");
        deleteLambdaFunction(awsLambda, functionName );
        awsLambda.close();
    }

    public static String createLambdaFunction(LambdaClient awsLambda,
                                            String functionName,
                                            String filePath,
                                            String role,
                                            String handler) {

        try {
            InputStream is = new FileInputStream(filePath);
            SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(functionName)
                    .description("Created by the Lambda Java API")
                    .code(code)
                    .handler(handler)
                    .runtime(Runtime.JAVA8)
                    .role(role)
                    .build();

            CreateFunctionResponse functionResponse = awsLambda.createFunction(functionRequest);
            return functionResponse.functionArn();

        } catch(LambdaException | FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static void getFunction(LambdaClient awsLambda,  String functionName) {

        try {

            GetFunctionRequest functionRequest = GetFunctionRequest.builder()
                    .functionName(functionName)
                    .build();

            GetFunctionResponse response = awsLambda.getFunction(functionRequest);
            System.out.println("The runtime of this Lambda function is " +response.configuration().runtime());

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void listFunctions(LambdaClient awsLambda) {

        try {
            ListFunctionsResponse functionResult = awsLambda.listFunctions();
            List<FunctionConfiguration> list = functionResult.functions();

            for (FunctionConfiguration config: list) {
                System.out.println("The function name is "+config.functionName());
            }

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void invokeFunction(LambdaClient awsLambda, String functionName) {

        InvokeResponse res = null ;
        try {
            // Need a SdkBytes instance for the payload.
            String json = "{\"Hello \":\"Paris\"}";
            SdkBytes payload = SdkBytes.fromUtf8String(json) ;

            //Setup an InvokeRequest
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();

            res = awsLambda.invoke(request);
            String value = res.payload().asUtf8String() ;
            System.out.println(value);

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void updateFunctionCode(LambdaClient awsLambda, String functionName, String bucketName, String key) {

        try {

            UpdateFunctionCodeRequest functionCodeRequest = UpdateFunctionCodeRequest.builder()
                    .functionName(functionName)
                    .publish(true)
                    .s3Bucket(bucketName)
                    .s3Key(key)
                    .build();

            UpdateFunctionCodeResponse response = awsLambda.updateFunctionCode(functionCodeRequest) ;
            System.out.println("The last modified value is " +response.lastModified());

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void UpdateFunctionConfiguration(LambdaClient awsLambda, String functionName, String handler ){

        try {
            UpdateFunctionConfigurationRequest configurationRequest = UpdateFunctionConfigurationRequest.builder()
                    .functionName(functionName)
                    .handler(handler)
                    .runtime(Runtime.JAVA11 )
                    .build();

            awsLambda.updateFunctionConfiguration(configurationRequest);

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    public static void deleteLambdaFunction(LambdaClient awsLambda, String functionName ) {
        try {
            //Setup an DeleteFunctionRequest
            DeleteFunctionRequest request = DeleteFunctionRequest.builder()
                    .functionName(functionName)
                    .build();

            awsLambda.deleteFunction(request);
            System.out.println("The "+functionName +" function was deleted");

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
