package com.example.stepfunctions;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.ListStateMachinesRequest;
import software.amazon.awssdk.services.sfn.model.ListStateMachinesResponse;
import software.amazon.awssdk.services.sfn.model.SfnException;
import software.amazon.awssdk.services.sfn.model.StateMachineListItem;

import java.util.List;

// snippet-start:[stepfunctions.java2.hello.main]
public class HelloStepFunctions {

    public static void main(String[]args) throws Exception {
        Region region = Region.US_EAST_1;
        SfnClient sfnClient = SfnClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listMachines(sfnClient);

    }

    public static void listMachines(SfnClient sfnClient) {
        try {
            ListStateMachinesRequest request = ListStateMachinesRequest.builder()
                .maxResults(10)
                .build();

            ListStateMachinesResponse response = sfnClient.listStateMachines(request);
            List<StateMachineListItem> machines = response.stateMachines();
            for (StateMachineListItem machine :machines) {
                System.out.println("The name of the state machine is: "+machine.name());
                System.out.println("The ARN value is : "+machine.stateMachineArn());
            }

        } catch (SfnException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[stepfunctions.java2.hello.main]