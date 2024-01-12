// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[swf.java.workflow_starter.complete]
package aws.example.helloswf;

// snippet-start:[swf.java.workflow_starter.import]
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClientBuilder;
import com.amazonaws.services.simpleworkflow.model.*;
// snippet-end:[swf.java.workflow_starter.import]

// snippet-start:[swf.java.workflow_starter.main]
public class WorkflowStarter {
        private static final AmazonSimpleWorkflow swf = AmazonSimpleWorkflowClientBuilder.standard()
                        .withRegion(Regions.DEFAULT_REGION).build();
        public static final String WORKFLOW_EXECUTION = "HelloWorldWorkflowExecution";

        public static void main(String[] args) {
                String workflow_input = "Amazon SWF";
                if (args.length > 0) {
                        workflow_input = args[0];
                }

                System.out.println("Starting the workflow execution '" + WORKFLOW_EXECUTION +
                                "' with input '" + workflow_input + "'.");

                WorkflowType wf_type = new WorkflowType()
                                .withName(HelloTypes.WORKFLOW)
                                .withVersion(HelloTypes.WORKFLOW_VERSION);

                Run run = swf.startWorkflowExecution(new StartWorkflowExecutionRequest()
                                .withDomain(HelloTypes.DOMAIN)
                                .withWorkflowType(wf_type)
                                .withWorkflowId(WORKFLOW_EXECUTION)
                                .withInput(workflow_input)
                                .withExecutionStartToCloseTimeout("90"));

                System.out.println("Workflow execution started with the run id '" +
                                run.getRunId() + "'.");
        }
}
// snippet-end:[swf.java.workflow_starter.main]
// snippet-end:[swf.java.workflow_starter.complete]
