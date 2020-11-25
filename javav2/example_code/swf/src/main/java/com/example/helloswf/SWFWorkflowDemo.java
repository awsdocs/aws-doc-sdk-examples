//snippet-sourcedescription:[SWFWorkflowDemo.java demonstrates how to register a domain, activity type, and a workflow type.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Workflow Service (Amazon SWF)]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
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
                "    SWFWorkflowDemo <domain> <taskList> <workflow> <workflowVersion> <activity> <activityVersion> \n\n" +
                "Where:\n" +
                "    domain - the domain to use (for example, mydomain). \n" +
                "    taskList - the task list to use (for example, HelloTasklist).  \n" +
                "    workflow - the name of the workflow (for example, myworkflow).\n" +
                "    workflowVersion - the workflow version. \n" +
                "    activity - the activity to use (for example, GrayscaleTransform).  \n" +
                "    activityVersion - the activity version.\n";

        if (args.length != 6) {
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
        SwfClient swf = SwfClient.builder()
                .region(region)
                .build();

        registerDomain(swf, domain);
        registerWorkflowType(swf, domain, workflow, workflowVersion, taskList);
        registerActivityType(swf, domain, activity, activityVersion, taskList);
        swf.close();
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