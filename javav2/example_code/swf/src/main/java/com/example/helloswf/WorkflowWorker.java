//snippet-sourcedescription:[WorkflowWorker.java demonstrates how to poll for a decision task in a task list.]
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkflowWorker {
    private static final SWFClient swf = SWFClient.builder().build();

    public static void main(String[] args) {
        PollForDecisionTaskRequest task_request =
            PollForDecisionTaskRequest.builder()
                .domain(HelloTypes.DOMAIN)
                .taskList(TaskList.builder().name(HelloTypes.TASKLIST).build())
                .build();

        while (true) {
            System.out.println(
                    "Polling for a decision task from the tasklist '" +
                    HelloTypes.TASKLIST + "' in the domain '" +
                    HelloTypes.DOMAIN + "'.");

            PollForDecisionTaskResponse task = swf.pollForDecisionTask(task_request);

            String taskToken = task.taskToken();
            if (taskToken != null) {
                try {
                    executeDecisionTask(taskToken, task.events());
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
    }

    /**
     * The goal of this workflow is to execute at least one HelloActivity successfully.
     *
     * We pass the workflow execution's input to the activity, and we use the activity's result
     * as the output of the workflow.
     */
    private static void executeDecisionTask(String taskToken, List<HistoryEvent> events)
            throws Throwable {
        List<Decision> decisions = new ArrayList<Decision>();
        String workflow_input = null;
        int scheduled_activities = 0;
        int open_activities = 0;
        boolean activity_completed = false;
        String result = null;

        System.out.println("Executing the decision task for the history events: [");
        for (HistoryEvent event : events) {
            System.out.println("  " + event);
            switch(event.eventType()) {
                case WORKFLOW_EXECUTION_STARTED:
                    workflow_input =
                        event.workflowExecutionStartedEventAttributes()
                             .input();
                    break;
                case ACTIVITY_TASK_SCHEDULED:
                    scheduled_activities++;
                    break;
                case SCHEDULE_ACTIVITY_TASK_FAILED:
                    scheduled_activities--;
                    break;
                case ACTIVITY_TASK_STARTED:
                    scheduled_activities--;
                    open_activities++;
                    break;
                case ACTIVITY_TASK_COMPLETED:
                    open_activities--;
                    activity_completed = true;
                    result = event.activityTaskCompletedEventAttributes()
                                  .result();
                    break;
                case ACTIVITY_TASK_FAILED:
                    open_activities--;
                    break;
                case ACTIVITY_TASK_TIMED_OUT:
                    open_activities--;
                    break;
            }
        }
        System.out.println("]");

        if (activity_completed) {
            decisions.add(
                Decision.builder()
                    .decisionType(DecisionType.COMPLETE_WORKFLOW_EXECUTION)
                    .completeWorkflowExecutionDecisionAttributes(
                        CompleteWorkflowExecutionDecisionAttributes.builder()
                            .result(result)
                            .build())
                    .build());
        } else {
            if (open_activities == 0 && scheduled_activities == 0) {

                ScheduleActivityTaskDecisionAttributes attrs =
                    ScheduleActivityTaskDecisionAttributes.builder()
                        .activityType(ActivityType.builder()
                            .name(HelloTypes.ACTIVITY)
                            .version(HelloTypes.ACTIVITY_VERSION)
                            .build())
                        .activityId(UUID.randomUUID().toString())
                        .input(workflow_input)
                        .build();

                decisions.add(
                        Decision.builder()
                            .decisionType(DecisionType.SCHEDULE_ACTIVITY_TASK)
                            .scheduleActivityTaskDecisionAttributes(attrs).build());
            } else {
                // an instance of HelloActivity is already scheduled or running. Do nothing, another
                // task will be scheduled once the activity completes, fails or times out
            }
        }

        System.out.println("Exiting the decision task with the decisions " + decisions);

        swf.respondDecisionTaskCompleted(
             RespondDecisionTaskCompletedRequest.builder()
                .taskToken(taskToken)
                .decisions(decisions)
                .build());
    }
}
