 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[swf]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkflowWorker {
    private static final AmazonSimpleWorkflow swf =
        AmazonSimpleWorkflowClientBuilder.defaultClient();

    public static void main(String[] args) {
        PollForDecisionTaskRequest task_request =
            new PollForDecisionTaskRequest()
                .withDomain(HelloTypes.DOMAIN)
                .withTaskList(new TaskList().withName(HelloTypes.TASKLIST));

        while (true) {
            System.out.println(
                    "Polling for a decision task from the tasklist '" +
                    HelloTypes.TASKLIST + "' in the domain '" +
                    HelloTypes.DOMAIN + "'.");

            DecisionTask task = swf.pollForDecisionTask(task_request);

            String taskToken = task.getTaskToken();
            if (taskToken != null) {
                try {
                    executeDecisionTask(taskToken, task.getEvents());
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
            switch(event.getEventType()) {
                case "WorkflowExecutionStarted":
                    workflow_input =
                        event.getWorkflowExecutionStartedEventAttributes()
                             .getInput();
                    break;
                case "ActivityTaskScheduled":
                    scheduled_activities++;
                    break;
                case "ScheduleActivityTaskFailed":
                    scheduled_activities--;
                    break;
                case "ActivityTaskStarted":
                    scheduled_activities--;
                    open_activities++;
                    break;
                case "ActivityTaskCompleted":
                    open_activities--;
                    activity_completed = true;
                    result = event.getActivityTaskCompletedEventAttributes()
                                  .getResult();
                    break;
                case "ActivityTaskFailed":
                    open_activities--;
                    break;
                case "ActivityTaskTimedOut":
                    open_activities--;
                    break;
            }
        }
        System.out.println("]");

        if (activity_completed) {
            decisions.add(
                new Decision()
                    .withDecisionType(DecisionType.CompleteWorkflowExecution)
                    .withCompleteWorkflowExecutionDecisionAttributes(
                        new CompleteWorkflowExecutionDecisionAttributes()
                            .withResult(result)));
        } else {
            if (open_activities == 0 && scheduled_activities == 0) {

                ScheduleActivityTaskDecisionAttributes attrs =
                    new ScheduleActivityTaskDecisionAttributes()
                        .withActivityType(new ActivityType()
                            .withName(HelloTypes.ACTIVITY)
                            .withVersion(HelloTypes.ACTIVITY_VERSION))
                        .withActivityId(UUID.randomUUID().toString())
                        .withInput(workflow_input);

                decisions.add(
                        new Decision()
                            .withDecisionType(DecisionType.ScheduleActivityTask)
                            .withScheduleActivityTaskDecisionAttributes(attrs));
            } else {
                // an instance of HelloActivity is already scheduled or running. Do nothing, another
                // task will be scheduled once the activity completes, fails or times out
            }
        }

        System.out.println("Exiting the decision task with the decisions " + decisions);

        swf.respondDecisionTaskCompleted(
            new RespondDecisionTaskCompletedRequest()
                .withTaskToken(taskToken)
                .withDecisions(decisions));
    }
}

