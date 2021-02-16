//snippet-sourcedescription:[ListStateMachines.java demonstrates how to List existing state machines for AWS Step Functions.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Step Functions]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[01/28/2021]
//snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.stepfunctions;

// snippet-start:[stepfunctions.java2.list_machines.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.ListStateMachinesResponse;
import software.amazon.awssdk.services.sfn.model.SfnException;
import software.amazon.awssdk.services.sfn.model.StateMachineListItem;
import java.util.List;
// snippet-end:[stepfunctions.java2.list_machines.import]

public class ListStateMachines {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        SfnClient sfnClient = SfnClient.builder()
                .region(region)
                .build();

        listMachines(sfnClient);
        sfnClient.close();
    }

    // snippet-start:[stepfunctions.java2.list_machines.main]
    public static void listMachines(SfnClient sfnClient) {

        try {
        ListStateMachinesResponse response = sfnClient.listStateMachines();
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
    // snippet-end:[stepfunctions.java2.list_machines.main]
}
