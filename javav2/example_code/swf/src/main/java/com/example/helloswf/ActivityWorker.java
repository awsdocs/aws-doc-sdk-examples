//snippet-sourcedescription:[ActivityWorker.java demonstrates how to implement an activity worker that polls for tasks in a task list and executes its task.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[swf]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[swf.java.activity_worker.complete]
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
// snippet-start:[swf.java.activity_worker.import]
package com.example.helloswf;

import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.*;

// snippet-end:[swf.java.activity_worker.import]
// snippet-start:[swf.java.activity_worker.main]
public class ActivityWorker {
    private static final SwfClient swf =
            SwfClient.builder().build();

    private static String sayHello(String input) throws Throwable {
        return "Hello, " + input + "!";
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("Polling for an activity task from the tasklist '"
                    + HelloTypes.TASKLIST + "' in the domain '" +
                    HelloTypes.DOMAIN + "'.");

            PollForActivityTaskResponse task = swf.pollForActivityTask(
                PollForActivityTaskRequest.builder()
                    .domain(HelloTypes.DOMAIN)
                    .taskList(
                       TaskList.builder().name(HelloTypes.TASKLIST).build())
                    .build());

            String task_token = task.taskToken();

            if (task_token != null) {
                String result = null;
                Throwable error = null;

                try {
                    System.out.println("Executing the activity task with input '" +
                            task.input() + "'.");
                    result = sayHello(task.input());
                } catch (Throwable th) {
                    error = th;
                }

                if (error == null) {
                    System.out.println("The activity task succeeded with result '"
                            + result + "'.");
                    swf.respondActivityTaskCompleted(
                        RespondActivityTaskCompletedRequest.builder()
                            .taskToken(task_token)
                            .result(result).build());
                } else {
                    System.out.println("The activity task failed with the error '"
                            + error.getClass().getSimpleName() + "'.");
                    swf.respondActivityTaskFailed(
                        RespondActivityTaskFailedRequest.builder()
                            .taskToken(task_token)
                            .reason(error.getClass().getSimpleName())
                            .details(error.getMessage())
                            .build());
                }
            }
        }
    }
}
// snippet-end:[swf.java.activity_worker.main]
// snippet-end:[swf.java.activity_worker.complete]