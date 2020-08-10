//snippet-sourcedescription:[WorkflowWorker.java demonstrates how to poll for a decision task in a task list.]
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

package com.example.helloswf;

// snippet-start:[swf.java2.task_request.import]
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.PollForDecisionTaskRequest;
import software.amazon.awssdk.services.swf.model.TaskList;
import software.amazon.awssdk.services.swf.model.PollForDecisionTaskResponse;
import software.amazon.awssdk.services.swf.model.Decision;
import software.amazon.awssdk.services.swf.model.HistoryEvent;
import software.amazon.awssdk.services.swf.model.DecisionType;
import software.amazon.awssdk.services.swf.model.CompleteWorkflowExecutionDecisionAttributes;
import software.amazon.awssdk.services.swf.model.ScheduleActivityTaskDecisionAttributes;
import software.amazon.awssdk.services.swf.model.ActivityType;
import software.amazon.awssdk.services.swf.model.RespondDecisionTaskCompletedRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
// snippet-end:[swf.java2.task_request.import]

public class WorkflowWorker {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    WorkflowWorker <domain><taskList><activity><activityVersion> \n\n" +
                "Where:\n" +
                "    domain - The domain to use (ie, mydomain) \n" +
                "    taskList - The taskList to use (ie, HelloTasklist)  \n" +
                "    activity - The activity to use (ie, GrayscaleTransform)  \n" +
                "    activityVersion - The activity version\n";

        if (args.length < 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String taskList = args[1];
        String activity = args[2];
        String activityVersion = args[3];

        SwfClient swf = SwfClient.builder().build();
        pollADecision(swf, domain, taskList, activity, activityVersion);
    }

    // snippet-start:[swf.java2.task_request.main]
    public static void pollADecision( SwfClient swf,
                                      String domain,
                                      String taskList,
                                      String activity,
                                      String activityVersion ) {

        PollForDecisionTaskRequest taskRequest =
                PollForDecisionTaskRequest.builder()
                        .domain(domain)
                        .taskList(TaskList.builder().name(taskList).build())
                        .build();

           System.out.println("Polling for a decision task from the tasklist '" +
                            taskList + "' in the domain '" +
                            domain + "'.");

            PollForDecisionTaskResponse task = swf.pollForDecisionTask(taskRequest);
            String taskToken = task.taskToken();

            if (taskToken != null) {
                   executeDecisionTask(swf, taskToken, task.events(), activity, activityVersion);
             }
        }

    /**
     * The goal of this workflow is to execute at least one HelloActivity successfully.
     *
     * We pass the workflow execution's input to the activity, and we use the activity's result
     * as the output of the workflow.
     */
    private static void executeDecisionTask(SwfClient swf,
                                            String taskToken,
                                            List<HistoryEvent> events,
                                            String activity,
                                            String activityVersion) {
        List<Decision> decisions = new ArrayList<Decision>();
        String workflowInput = null;
        int scheduledActivities = 0;
        int openActivities = 0;
        boolean activityCompleted = false;
        String result = null;

        System.out.println("Executing the decision task for the history events: [");
        for (HistoryEvent event : events) {
            System.out.println("  " + event);
            String myType = event.eventType().toString();
            System.out.println("Event type is "+myType) ;

            switch(event.eventType()) {
                case WORKFLOW_EXECUTION_STARTED:
                    workflowInput =
                            event.workflowExecutionStartedEventAttributes()
                                    .input();
                    break;
                case ACTIVITY_TASK_SCHEDULED:
                    scheduledActivities++;
                    break;
                case SCHEDULE_ACTIVITY_TASK_FAILED:
                    scheduledActivities--;
                    break;
                case ACTIVITY_TASK_STARTED:
                    scheduledActivities--;
                    openActivities++;
                    break;
                case ACTIVITY_TASK_COMPLETED:
                    openActivities--;
                    activityCompleted = true;
                    result = event.activityTaskCompletedEventAttributes()
                            .result();
                    break;
                case ACTIVITY_TASK_FAILED:
                    openActivities--;
                    break;
                case ACTIVITY_TASK_TIMED_OUT:
                    openActivities--;
                    break;
            }
        }
        System.out.println("]");

        if (activityCompleted) {
            decisions.add(
                    Decision.builder()
                            .decisionType(DecisionType.COMPLETE_WORKFLOW_EXECUTION)
                            .completeWorkflowExecutionDecisionAttributes(
                                    CompleteWorkflowExecutionDecisionAttributes.builder()
                                            .result(result)
                                            .build())
                            .build());
        } else {
            if (openActivities == 0 && scheduledActivities == 0) {

                ScheduleActivityTaskDecisionAttributes attrs =
                        ScheduleActivityTaskDecisionAttributes.builder()
                                .activityType(ActivityType.builder()
                                        .name(activity)
                                        .version(activityVersion)
                                        .build())
                                .activityId(UUID.randomUUID().toString())
                                .input(workflowInput)
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
    }
}
// snippet-end:[swf.java2.task_request.main]
// snippet-end:[swf.java2.task_request.complete]
