//snippet-sourceauthor: [soo-aws]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
package aws.example.helloswf;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClientBuilder;
import com.amazonaws.services.simpleworkflow.model.*;

public class HelloTypes {
    public static final String DOMAIN = "HelloDomain";
    public static final String TASKLIST = "HelloTasklist";
    public static final String WORKFLOW = "HelloWorkflow";
    public static final String WORKFLOW_VERSION = "1.0";
    public static final String ACTIVITY = "HelloActivity";
    public static final String ACTIVITY_VERSION = "1.0";

    private static final AmazonSimpleWorkflow swf =
        AmazonSimpleWorkflowClientBuilder.defaultClient();

    public static void registerDomain() {
        try {
            System.out.println("** Registering the domain '" + DOMAIN + "'.");
            swf.registerDomain(new RegisterDomainRequest()
                .withName(DOMAIN)
                .withWorkflowExecutionRetentionPeriodInDays("1"));
        } catch (DomainAlreadyExistsException e) {
            System.out.println("** Domain already exists!");
        }
    }

    public static void registerActivityType() {
        try {
            System.out.println("** Registering the activity type '" + ACTIVITY +
                "-" + ACTIVITY_VERSION + "'.");
            swf.registerActivityType(new RegisterActivityTypeRequest()
                .withDomain(DOMAIN)
                .withName(ACTIVITY)
                .withVersion(ACTIVITY_VERSION)
                .withDefaultTaskList(new TaskList().withName(TASKLIST))
                .withDefaultTaskScheduleToStartTimeout("30")
                .withDefaultTaskStartToCloseTimeout("600")
                .withDefaultTaskScheduleToCloseTimeout("630")
                .withDefaultTaskHeartbeatTimeout("10"));
        } catch (TypeAlreadyExistsException e) {
            System.out.println("** Activity type already exists!");
        }
    }

    public static void registerWorkflowType() {
        try {
            System.out.println("** Registering the workflow type '" + WORKFLOW +
                "-" + WORKFLOW_VERSION + "'.");
            swf.registerWorkflowType(new RegisterWorkflowTypeRequest()
                .withDomain(DOMAIN)
                .withName(WORKFLOW)
                .withVersion(WORKFLOW_VERSION)
                .withDefaultChildPolicy(ChildPolicy.TERMINATE)
                .withDefaultTaskList(new TaskList().withName(TASKLIST))
                .withDefaultTaskStartToCloseTimeout("30"));
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

