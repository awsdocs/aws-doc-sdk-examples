//snippet-sourcedescription:[WorkflowStarter.java demonstrates how to start a workflow.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Workflow Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/4/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.*
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// snippet-start:[swf.java2.start_workflow.complete]
package com.example.helloswf;
// snippet-start:[swf.java2.start_workflow.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.WorkflowType;
import software.amazon.awssdk.services.swf.model.StartWorkflowExecutionResponse;
import software.amazon.awssdk.services.swf.model.SwfException;
import software.amazon.awssdk.services.swf.model.StartWorkflowExecutionRequest;
// snippet-end:[swf.java2.start_workflow.import]

public class WorkflowStarter {

    public static final String WORKFLOW_EXECUTION = "HelloWorldWorkflowExecution";

    public static void main(String[] args) {

         final String USAGE = "\n" +
                "Usage:\n" +
                "    WorkflowStarter <domain><workflowInput><workflow><workflowVersion> \n\n" +
                "Where:\n" +
                "    domain - The domain to use (i.e., mydomain) \n" +
                "    workflowInput - The input to the workflow (i.e., ProcessFile)  \n" +
                "    workflow - the name of the workflow (i.e., myworkflow)\n" +
                "    workflowVersion - The workflow version \n" ;

        if (args.length < 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String workflowInput = args[1];
        String workflow = args[2];
        String workflowVersion = args[3];

        Region region = Region.US_EAST_1;
        SwfClient swf = SwfClient
                .builder()
                .region(region)
                .build();

        System.out.println("Starting the workflow execution '" + WORKFLOW_EXECUTION +
                "' with input '" + workflowInput + "'.");

        startWorkflow(swf, workflowInput, domain, workflow,workflowVersion) ;
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

            System.out.println("Workflow execution started with the run ID '" +
                    run.runId() + "'.");

        } catch (SwfException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[swf.java2.start_workflow.main]
// snippet-end:[swf.java2.start_workflow.complete]
