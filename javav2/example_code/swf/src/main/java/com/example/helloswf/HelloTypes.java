//snippet-sourcedescription:[HelloTypes.java demonstrates how to register a domain, activity type and a workflow type.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[swf]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
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
package com.example.helloswf;

import software.amazon.awssdk.services.swf.SWFClient;
import software.amazon.awssdk.services.swf.model.*;

public class HelloTypes {
    public static final String DOMAIN = "HelloDomain";
    public static final String TASKLIST = "HelloTasklist";
    public static final String WORKFLOW = "HelloWorkflow";
    public static final String WORKFLOW_VERSION = "1.0";
    public static final String ACTIVITY = "HelloActivity";
    public static final String ACTIVITY_VERSION = "1.0";

    private static final SWFClient swf =
    		SWFClient.builder().build();

    public static void registerDomain() {
        try {
            System.out.println("** Registering the domain '" + DOMAIN + "'.");
            swf.registerDomain(RegisterDomainRequest.builder()
                .name(DOMAIN)
                .workflowExecutionRetentionPeriodInDays("1").build());
        } catch (DomainAlreadyExistsException e) {
            System.out.println("** Domain already exists!");
        }
    }

    public static void registerActivityType() {
        try {
            System.out.println("** Registering the activity type '" + ACTIVITY +
                "-" + ACTIVITY_VERSION + "'.");
            swf.registerActivityType(RegisterActivityTypeRequest.builder()
                .domain(DOMAIN)
                .name(ACTIVITY)
                .version(ACTIVITY_VERSION)
                .defaultTaskList(TaskList.builder().name(TASKLIST).build())
                .defaultTaskScheduleToStartTimeout("30")
                .defaultTaskStartToCloseTimeout("600")
                .defaultTaskScheduleToCloseTimeout("630")
                .defaultTaskHeartbeatTimeout("10")
                .build());
        } catch (TypeAlreadyExistsException e) {
            System.out.println("** Activity type already exists!");
        }
    }

    public static void registerWorkflowType() {
        try {
            System.out.println("** Registering the workflow type '" + WORKFLOW +
                "-" + WORKFLOW_VERSION + "'.");
            swf.registerWorkflowType(RegisterWorkflowTypeRequest.builder()
                .domain(DOMAIN)
                .name(WORKFLOW)
                .version(WORKFLOW_VERSION)
                .defaultChildPolicy(ChildPolicy.TERMINATE)
                .defaultTaskList(TaskList.builder().name(TASKLIST).build())
                .defaultTaskStartToCloseTimeout("30")
                .build());
        } catch (TypeAlreadyExistsException e) {
            System.out.println("** Workflow type already exists!");
        }
    }

    public static void main(String[] args) {
        registerDomain();
        registerWorkflowType();
        registerActivityType();
    }
}
