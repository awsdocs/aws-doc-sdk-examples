//snippet-sourcedescription:[WorkflowStarter.java demonstrates how to how to start an Amazon Simple Workflow Service (Amazon SWF) workflow.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Workflow Service]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.helloswf;
// snippet-start:[swf.java2.start_workflow.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.WorkflowType;
import software.amazon.awssdk.services.swf.model.StartWorkflowExecutionResponse;
import software.amazon.awssdk.services.swf.model.SwfException;
import software.amazon.awssdk.services.swf.model.StartWorkflowExecutionRequest;
// snippet-end:[swf.java2.start_workflow.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class WorkflowStarter {

    public static final String WORKFLOW_EXECUTION = "HelloWorldWorkflowExecution";

    public static void main(String[] args) {

         final String USAGE = "\n" +
                "Usage:\n" +
                "    <domain> <workflowInput> <workflow> <workflowVersion> \n\n" +
                "Where:\n" +
                "    domain - the domain to use (ie, mydomain). \n" +
                "    workflowInput - the input to the workflow (ie, ProcessFile).  \n" +
                "    workflow - the name of the workflow (ie, myworkflow).\n" +
                "    workflowVersion - the workflow version. \n" ;

        if (args.length < 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String workflowInput = args[1];
        String workflow = args[2];
        String workflowVersion = args[3];

        Region region = Region.US_EAST_1;
        SwfClient swf = SwfClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        System.out.println("Starting the workflow execution '" + WORKFLOW_EXECUTION +
                "' with input '" + workflowInput + "'.");

        startWorkflow(swf, workflowInput, domain, workflow,workflowVersion) ;
        swf.close();
    }

    // snippet-start:[swf.java2.start_workflow.main]
    public static void startWorkflow(SwfClient swf,
                                      String workflowInput,
                                      String domain,
                                      String workflow,
                                      String workflowVersion) {

        try {
            WorkflowType wfType = WorkflowType.builder()
                    .name(workflow)
                    .version(workflowVersion)
                    .build();

            StartWorkflowExecutionResponse run = swf.startWorkflowExecution(StartWorkflowExecutionRequest.builder()
                    .domain(domain)
                    .workflowType(wfType)
                    .workflowId(WORKFLOW_EXECUTION)
                    .input(workflowInput)
                    .executionStartToCloseTimeout("90")
                    .build());

            System.out.println("Workflow execution started with the run id '" +
                    run.runId() + "'.");

        } catch (SwfException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[swf.java2.start_workflow.main]
