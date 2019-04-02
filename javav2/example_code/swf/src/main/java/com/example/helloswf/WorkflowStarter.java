//snippet-sourcedescription:[WorkflowStarter.java demonstrates how to how to start a workflow.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[swf]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[swf.java.start_workflow.complete]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.*
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
// snippet-start:[swf.java.start_workflow.import]
package com.example.helloswf;

import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.*;

// snippet-end:[swf.java.start_workflow.import]
// snippet-start:[swf.java.start_workflow.main]
public class WorkflowStarter {
    private static final SwfClient swf = SwfClient.builder().build();
    public static final String WORKFLOW_EXECUTION = "HelloWorldWorkflowExecution";

    public static void main(String[] args) {
        String workflow_input = "Amazon SWF";
        if (args.length > 0) {
            workflow_input = args[0];
        }

        System.out.println("Starting the workflow execution '" + WORKFLOW_EXECUTION +
                "' with input '" + workflow_input + "'.");

        WorkflowType wf_type = WorkflowType.builder()
            .name(HelloTypes.WORKFLOW)
            .version(HelloTypes.WORKFLOW_VERSION)
            .build();

        StartWorkflowExecutionResponse run = swf.startWorkflowExecution(StartWorkflowExecutionRequest.builder()
            .domain(HelloTypes.DOMAIN)
            .workflowType(wf_type)
            .workflowId(WORKFLOW_EXECUTION)
            .input(workflow_input)
            .executionStartToCloseTimeout("90")
            .build());

        System.out.println("Workflow execution started with the run id '" +
                run.runId() + "'.");
    }
}
// snippet-end:[swf.java.start_workflow.main]
// snippet-end:[swf.java.start_workflow.complete]