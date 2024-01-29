// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[swf.java.activity_worker.complete]
package aws.example.helloswf;

// snippet-start:[swf.java.activity_worker.import]
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClientBuilder;
import com.amazonaws.services.simpleworkflow.model.*;
// snippet-end:[swf.java.activity_worker.import]

public class ActivityWorker {
        // snippet-start:[swf.java.activity_worker.client]
        private static final AmazonSimpleWorkflow swf = AmazonSimpleWorkflowClientBuilder.standard()
                        .withRegion(Regions.DEFAULT_REGION).build();
        // snippet-end:[swf.java.activity_worker.client]

        // snippet-start:[swf.java.activity_worker.sayHello]
        private static String sayHello(String input) throws Throwable {
                return "Hello, " + input + "!";
        }
        // snippet-end:[swf.java.activity_worker.sayHello]

        public static void main(String[] args) {
                // snippet-start:[swf.java.activity_worker.main]
                while (true) {
                        // snippet-start:[swf.java.activity_worker.poll_method]
                        System.out.println("Polling for an activity task from the tasklist '"
                                        + HelloTypes.TASKLIST + "' in the domain '" +
                                        HelloTypes.DOMAIN + "'.");

                        ActivityTask task = swf.pollForActivityTask(
                                        new PollForActivityTaskRequest()
                                                        .withDomain(HelloTypes.DOMAIN)
                                                        .withTaskList(
                                                                        new TaskList().withName(HelloTypes.TASKLIST)));

                        String task_token = task.getTaskToken();
                        // snippet-end:[swf.java.activity_worker.poll_method]

                        // snippet-start:[swf.java.activity_worker.process_tasks]
                        if (task_token != null) {
                                String result = null;
                                Throwable error = null;

                                try {
                                        System.out.println("Executing the activity task with input '" +
                                                        task.getInput() + "'.");
                                        result = sayHello(task.getInput());
                                } catch (Throwable th) {
                                        error = th;
                                }

                                if (error == null) {
                                        System.out.println("The activity task succeeded with result '"
                                                        + result + "'.");
                                        swf.respondActivityTaskCompleted(
                                                        new RespondActivityTaskCompletedRequest()
                                                                        .withTaskToken(task_token)
                                                                        .withResult(result));
                                } else {
                                        System.out.println("The activity task failed with the error '"
                                                        + error.getClass().getSimpleName() + "'.");
                                        swf.respondActivityTaskFailed(
                                                        new RespondActivityTaskFailedRequest()
                                                                        .withTaskToken(task_token)
                                                                        .withReason(error.getClass().getSimpleName())
                                                                        .withDetails(error.getMessage()));
                                }
                        }
                        // snippet-end:[swf.java.activity_worker.process_tasks]
                }
                // snippet-end:[swf.java.activity_worker.main]
        }
}
// snippet-end:[swf.java.activity_worker.complete]