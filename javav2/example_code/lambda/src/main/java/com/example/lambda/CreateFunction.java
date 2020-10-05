// snippet-sourcedescription:[CreateFunction.java demonstrates how to create an AWS Lambda function by using the LambdaClient object.]
// snippet-service:[Lambda]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-10-02]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.example.lambda;

// snippet-start:[lambda.java2.create.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.Runtime;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
// snippet-end:[lambda.java2.create.import]

/**
 *  This code example requires a ZIP or JAR that represents the code of the Lambda function.
 *  If you do not have a ZIP or JAR, please refer to the following document:
 *
 *  https://github.com/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_workflows_stepfunctions
 */
public class CreateFunction {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateFunction <functionName><filePath><role><handler> \n\n" +
                "Where:\n" +
                "    functionName - the name of the Lambda function \n"+
                "    filePath - the path to the ZIP or JAR where the code is located \n"+
                "    role - the role ARN that has Lambda permissions \n"+
                "    handler - the fully qualifed method name (for example, example.Handler::handleRequest)  \n";

          if (args.length < 4) {
              System.out.println(USAGE);
              System.exit(1);
          }

        /* Read the name from command args*/
        String functionName = args[0];
        String filePath = args[1];
        String role = args[2];
        String handler = args[3];

        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
                .region(region)
                .build();

        /* Read the name from command args*/
        createLambdaFunction(awsLambda, functionName, filePath, role, handler);
    }

    // snippet-start:[lambda.java2.create.main]
    public static void createLambdaFunction(LambdaClient awsLambda,
                                            String functionName,
                                            String filePath,
                                            String role,
                                            String handler) {

        try {
            // Create a SdkBytes object that represents the Lambda code
            InputStream is = new FileInputStream(filePath); 
            SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

            // Create a FunctionCode object
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
            System.out.println("The function ARN is "+functionResponse.functionArn());

        } catch(LambdaException | FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lambda.java2.create.main]
}
