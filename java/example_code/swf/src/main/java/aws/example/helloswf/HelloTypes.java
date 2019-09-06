//snippet-sourcedescription:[HelloTypes.java demonstrates how to register a domain, activity type and a workflow type.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-service:[swf]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
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
//snippet-start:[swf.java.hello_types.complete]
//snippet-start:[swf.java.hello_types.package]
package aws.example.helloswf;
//snippet-end:[swf.java.hello_types.package]

//snippet-start:[swf.java.hello_types.import]
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClientBuilder;
import com.amazonaws.services.simpleworkflow.model.*;
//snippet-end:[swf.java.hello_types.import]

public class HelloTypes {
    //snippet-start:[swf.java.hello_types.string_declare]
    public static final String DOMAIN = "HelloDomain";
    public static final String TASKLIST = "HelloTasklist";
    public static final String WORKFLOW = "HelloWorkflow";
    public static final String WORKFLOW_VERSION = "1.0";
    public static final String ACTIVITY = "HelloActivity";
    public static final String ACTIVITY_VERSION = "1.0";
    //snippet-end:[swf.java.hello_types.string_declare]

    //snippet-start:[swf.java.hello_types.client]
    private static final AmazonSimpleWorkflow swf =
        AmazonSimpleWorkflowClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    //snippet-end:[swf.java.hello_types.client]

    public static void registerDomain() {
        //snippet-start:[swf.java.hello_types.new_function]
        try {
            System.out.println("** Registering the domain '" + DOMAIN + "'.");
            swf.registerDomain(new RegisterDomainRequest()
                .withName(DOMAIN)
                .withWorkflowExecutionRetentionPeriodInDays("1"));
        } catch (DomainAlreadyExistsException e) {
            System.out.println("** Domain already exists!");
        }
        //snippet-end:[swf.java.hello_types.new_function]
    }

    public static void registerActivityType() {
        //snippet-start:[swf.java.hello_types.new_activity_type]
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
        //snippet-end:[swf.java.hello_types.new_activity_type]
    }

    public static void registerWorkflowType() {
        //snippet-start:[swf.java.hello_types.new_workflow_type]
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
        //snippet-end:[swf.java.hello_types.new_workflow_type]
    }

    public static void main(String[] args) {
        //snippet-start:[swf.java.hello_types.main]
        registerDomain();
        registerWorkflowType();
        registerActivityType();
        //snippet-end:[swf.java.hello_types.main]
    }
}
//snippet-end:[swf.java.hello_types.complete]