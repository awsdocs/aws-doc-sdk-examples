//snippet-sourcedescription:[CreateStateMachine.java demonstrates how to creates a state machine for AWS Step Functions.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Step Functions]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.stepfunctions;

// snippet-start:[stepfunctions.java2.create_machine.import]
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.CreateStateMachineRequest;
import software.amazon.awssdk.services.sfn.model.StateMachineType;
import software.amazon.awssdk.services.sfn.model.CreateStateMachineResponse;
import software.amazon.awssdk.services.sfn.model.SfnException;
import java.io.FileReader;
import java.io.IOException;
// snippet-end:[stepfunctions.java2.create_machine.import]

/**
 * To run this example, you need a JSON file that represents the Amazon States Language definition for the state machine.
 *
 * To see an Amazon States Language definition example that you can use, see "Getting started with AWS Step Functions" at https://docs.aws.amazon.com/step-functions/latest/dg/getting-started.html.
 *
 * Also, set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */


public class CreateStateMachine {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <jsonFile> <roleARN> <stateMachineName>\n\n" +
            "Where:\n" +
            "    jsonFile - A JSON file that represents the Amazon States Language definition of the state machine.\n\n" +
            "    roleARN - The Amazon Resource Name (ARN) of the IAM role to use for this state machine.\n" +
            "    stateMachineName - The name of the state machine to create.\n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String jsonFile = args[0];
        String roleARN = args[1];
        String stateMachineName = args[2];

        Region region = Region.US_EAST_1;
        SfnClient sfnClient = SfnClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String arnStateMachine = createMachine(sfnClient, roleARN, stateMachineName, jsonFile);
        System.out.println("The ARN of the new state machine is "+arnStateMachine);
        sfnClient.close();
    }

    // snippet-start:[stepfunctions.java2.create_machine.main]
    public static String createMachine( SfnClient sfnClient, String roleARN, String stateMachineName, String jsonFile) {

        String json = getJSONString(jsonFile);
        try {
           CreateStateMachineRequest machineRequest = CreateStateMachineRequest.builder()
               .definition(json)
               .name(stateMachineName)
               .roleArn(roleARN)
               .type(StateMachineType.STANDARD)
               .build();

           CreateStateMachineResponse response = sfnClient.createStateMachine(machineRequest);
           return response.stateMachineArn();

        } catch (SfnException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    private static String getJSONString(String path) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(new FileReader(path));//path to the JSON file.
            return data.toJSONString();

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    // snippet-end:[stepfunctions.java2.create_machine.main]
}


