//snippet-sourcedescription:[ActivityWorker.java demonstrates how to implement an activity worker that polls for tasks in a task list and executes its task.]
//snippet-keyword:[Java]
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
//snippet-start:[swf.java.activity_worker.complete]
package aws.example.helloswf;

//snippet-start:[swf.java.activity_worker.import]
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClientBuilder;
import com.amazonaws.services.simpleworkflow.model.*;
//snippet-end:[swf.java.activity_worker.import]

public class ActivityWorker {
    //snippet-start:[swf.java.activity_worker.client]
    private static final AmazonSimpleWorkflow swf =
            AmazonSimpleWorkflowClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    //snippet-end:[swf.java.activity_worker.client]

    //snippet-start:[swf.java.activity_worker.sayHello]
    private static String sayHello(String input) throws Throwable {
        return "Hello, " + input + "!";
    }
    //snippet-end:[swf.java.activity_worker.sayHello]

    public static void main(String[] args) {
        //snippet-start:[swf.java.activity_worker.main]
        while (true) {
            //snippet-start:[swf.java.activity_worker.poll_method]
            System.out.println("Polling for an activity task from the tasklist '"
                    + HelloTypes.TASKLIST + "' in the domain '" +
                    HelloTypes.DOMAIN + "'.");

            ActivityTask task = swf.pollForActivityTask(
                new PollForActivityTaskRequest()
                    .withDomain(HelloTypes.DOMAIN)
                    .withTaskList(
                        new TaskList().withName(HelloTypes.TASKLIST)));

            String task_token = task.getTaskToken();
            //snippet-end:[swf.java.activity_worker.poll_method]

            //snippet-start:[swf.java.activity_worker.process_tasks]
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
            //snippet-end:[swf.java.activity_worker.process_tasks]
        }
        //snippet-end:[swf.java.activity_worker.main]
    }
}
//snippet-end:[swf.java.activity_worker.complete]