//snippet-sourcedescription:[SWFWorkflowDemo.java demonstrates how to register a domain, activity type and a workflow type.]
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
// snippet-start:[swf.java2.activity_types.complete]
package com.example.helloswf;

// snippet-start:[swf.java2.activity_types.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.TypeAlreadyExistsException;
import software.amazon.awssdk.services.swf.model.RegisterDomainRequest;
import software.amazon.awssdk.services.swf.model.DomainAlreadyExistsException;
import software.amazon.awssdk.services.swf.model.RegisterActivityTypeRequest;
import software.amazon.awssdk.services.swf.model.TaskList;
import software.amazon.awssdk.services.swf.model.RegisterWorkflowTypeRequest;
import software.amazon.awssdk.services.swf.model.ChildPolicy;
// snippet-end:[swf.java2.activity_types.import]


public class SWFWorkflowDemo {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    HelloTypes <domain><taskList><workflow><workflowVersion><activity><activityVersion> \n\n" +
                "Where:\n" +
                "    domain - The domain to use (ie, mydomain) \n" +
                "    taskList - The taskList to use (ie, HelloTasklist)  \n" +
                "    workflow - the name of the workflow (ie, myworkflow)\n" +
                "    workflowVersion - The workflow version \n" +
                "    activity - The activity to use (ie, GrayscaleTransform)  \n" +
                "    activityVersion - The activity version\n";

        if (args.length < 6) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String taskList = args[1];
        String workflow = args[2];
        String workflowVersion = args[3];
        String activity = args[4];
        String activityVersion = args[5];

        Region region = Region.US_EAST_1;
        SwfClient swf = SwfClient.builder().region(region).build();

        registerDomain(swf, domain);
        registerWorkflowType(swf, domain, workflow, workflowVersion, taskList);
        registerActivityType(swf, domain, activity, activityVersion, taskList);
    }

    // snippet-start:[swf.java2.activity_types.main]
    public static void registerDomain(SwfClient swf, String domain) {
        try {
            System.out.println("** Registering the domain '" + domain + "'.");
            swf.registerDomain(RegisterDomainRequest.builder()
                    .name(domain)
                    .workflowExecutionRetentionPeriodInDays("1").build());
        } catch (DomainAlreadyExistsException e) {
            System.out.println("** Domain already exists!");
            System.exit(1);
        }
    }

    public static void registerWorkflowType(SwfClient swf,
                                            String domain,
                                            String workflow,
                                            String workflowVersion,
                                            String taskList) {
        try {
            System.out.println("** Registering the workflow type '" + workflow +
                    "-" + workflowVersion + "'.");
            swf.registerWorkflowType(RegisterWorkflowTypeRequest.builder()
                    .domain(domain)
                    .name(workflow)
                    .version(workflowVersion)
                    .defaultChildPolicy(ChildPolicy.TERMINATE)
                    .defaultTaskList(TaskList.builder().name(taskList).build())
                    .defaultTaskStartToCloseTimeout("30")
                    .build());
        } catch (TypeAlreadyExistsException e) {
            System.out.println("** Workflow type already exists!");
            System.exit(1);
        }
    }

    public static void registerActivityType(SwfClient swf,
                                            String domain,
                                            String activity,
                                            String activityVersion,
                                            String taskList) {
        try {
            System.out.println("** Registering the activity type '" + activity +
                    "-" + activityVersion + "'.");
            swf.registerActivityType(RegisterActivityTypeRequest.builder()
                    .domain(domain)
                    .name(activity)
                    .version(activityVersion)
                    .defaultTaskList(TaskList.builder().name(taskList).build())
                    .defaultTaskScheduleToStartTimeout("30")
                    .defaultTaskStartToCloseTimeout("600")
                    .defaultTaskScheduleToCloseTimeout("630")
                    .defaultTaskHeartbeatTimeout("10")
                    .build());
        } catch (TypeAlreadyExistsException e) {
            System.out.println("** Activity type already exists!");
            System.exit(1);
        }
    }
}
// snippet-end:[swf.java2.activity_types.main]
// snippet-end:[swf.java2.activity_types.complete]
