//snippet-sourcedescription:[ActivityWorkerWithGracefulShutdown.java demonstrates how to implement an activity worker with a graceful shutdown.]
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
// snippet-start:[swf.java2.poll_tasks.complete]
package com.example.helloswf;

// snippet-start:[swf.java2.poll_tasks.import]
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskRequest;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskResponse;
import software.amazon.awssdk.services.swf.model.RespondActivityTaskCompletedRequest;
import software.amazon.awssdk.services.swf.model.RespondActivityTaskFailedRequest;
import software.amazon.awssdk.services.swf.model.TaskList;
// snippet-end:[swf.java2.poll_tasks.import]

// snippet-start:[swf.java2.poll_tasks.main]
public class ActivityWorkerWithGracefulShutdown {

    private static CountDownLatch waitForTermination = new CountDownLatch(1);
    private static volatile boolean terminate = false;

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ActivityWorkerWithGracefulShutdown <domain><taskList> \n\n" +
                "Where:\n" +
                "    domain - The domain to use (i.e., mydomain) \n" +
                "    taskList - The taskList to use (i.e., HelloTasklist)  \n" ;

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String taskList = args[1];

        SwfClient swf = SwfClient.builder().build();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    terminate = true;
                    System.out.println("Waiting for the current poll request" +
                            " to return before shutting down.");
                    waitForTermination.await(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            pollAndExecute(swf, domain, taskList);
        } finally {
            waitForTermination.countDown();
        }
    }

    public static void pollAndExecute(SwfClient swf, String domain, String taskList ) {
        while (!terminate) {
            System.out.println("Polling for an activity task from the task list '"
                    + taskList + "' in the domain '" +
                    domain + "'.");

            PollForActivityTaskResponse task = swf.pollForActivityTask(PollForActivityTaskRequest.builder()
                    .domain(domain)
                    .taskList(TaskList.builder().name(taskList).build())
                    .build());

            String taskToken = task.taskToken();

            if (taskToken != null) {
                String result = null;
                Throwable error = null;

                System.out.println("Executing the activity task with input '" + task.input() + "'.");
                result = executeActivityTask(task.input());

                if (error == null) {
                    System.out.println("The activity task succeeded with result '"
                            + result + "'.");
                    swf.respondActivityTaskCompleted(
                            RespondActivityTaskCompletedRequest.builder()
                                    .taskToken(taskToken)
                                    .result(result)
                                    .build());
                } else {
                    System.out.println("The activity task failed with the error '"
                            + error.getClass().getSimpleName() + "'.");
                    swf.respondActivityTaskFailed(
                            RespondActivityTaskFailedRequest.builder()
                                    .taskToken(taskToken)
                                    .reason(error.getClass().getSimpleName())
                                    .details(error.getMessage())
                                    .build());
                }
            }
        }
    }

    private static String executeActivityTask(String input) {
        return "Hello, " + input + "!";
    }
}
// snippet-end:[swf.java2.poll_tasks.main]
// snippet-end:[swf.java2.poll_tasks.complete]
