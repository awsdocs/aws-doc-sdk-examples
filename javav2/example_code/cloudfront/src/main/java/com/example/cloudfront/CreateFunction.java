//snippet-sourcedescription:[CreateFunction.java demonstrates how to create a CloudFront function.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon CloudFront]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2021]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.cloudfront;

// snippet-start:[cloudfront.java2.function.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.FunctionConfig;
import software.amazon.awssdk.services.cloudfront.model.FunctionRuntime;
import software.amazon.awssdk.services.cloudfront.model.CreateFunctionRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateFunctionResponse;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
// snippet-end:[cloudfront.java2.function.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateFunction {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <functionName> <filePath>\n\n" +
                "Where:\n" +
                "    functionName - The name of the function to create. \n"+
                "    filePath - The path to a file that contains the application logic for the function. \n" ;

         if (args.length != 2) {
             System.out.println(usage);
             System.exit(1);
         }

        String functionName = args[0];
        String filePath = args[1] ;
        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String funArn = createNewFunction(cloudFrontClient, functionName, filePath);
        System.out.println("The function ARN is "+funArn);
        cloudFrontClient.close();
    }

    // snippet-start:[cloudfront.java2.function.main]
    public static String createNewFunction(CloudFrontClient cloudFrontClient, String functionName, String filePath) {

        try {

            InputStream is = new FileInputStream(filePath);
            SdkBytes functionCode = SdkBytes.fromInputStream(is);

            FunctionConfig config = FunctionConfig.builder()
                    .comment("Created by using the CloudFront Java API")
                    .runtime(FunctionRuntime.CLOUDFRONT_JS_1_0)
                    .build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .name(functionName)
                    .functionCode(functionCode)
                    .functionConfig(config)
                    .build();

            CreateFunctionResponse response = cloudFrontClient.createFunction(functionRequest);
            return response.functionSummary().functionMetadata().functionARN();

        } catch (CloudFrontException | FileNotFoundException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[cloudfront.java2.function.main]
}

