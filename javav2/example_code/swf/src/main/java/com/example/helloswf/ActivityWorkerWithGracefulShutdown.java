//snippet-sourcedescription:[ActivityWorkerWithGracefulShutdown.java demonstrates how to implement an activity worker with a graceful shutdown.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Workflow Service (Amazon SWF)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[swf.java2.poll_tasks.complete]
package com.example.helloswf;

// snippet-start:[swf.java2.poll_tasks.import]
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskRequest;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskResponse;
import software.amazon.awssdk.services.swf.model.RespondActivityTaskCompletedRequest;
import software.amazon.awssdk.services.swf.model.RespondActivityTaskFailedRequest;
import software.amazon.awssdk.services.swf.model.TaskList;
// snippet-end:[swf.java2.poll_tasks.import]

// snippet-start:[swf.java2.poll_tasks.main]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ActivityWorkerWithGracefulShutdown {

    private static CountDownLatch waitForTermination = new CountDownLatch(1);
    private static volatile boolean terminate = false;

    public static void main(String[] args) {

        final String USAGE = "\n" +
            "Usage:\n" +
            "    <domain> <taskList> \n\n" +
            "Where:\n" +
            "    domain - The domain to use (ie, mydomain). \n" +
            "    taskList - The taskList to use (ie, HelloTasklist).  \n" ;

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String taskList = args[1];
        Region region = Region.US_EAST_1;
        SwfClient swf = SwfClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

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
            swf.close();
        } finally {
            waitForTermination.countDown();
        }
    }

    public static void pollAndExecute(SwfClient swf, String domain, String taskList ) {
        while (!terminate) {
            System.out.println("Polling for an activity task from the tasklist '"
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