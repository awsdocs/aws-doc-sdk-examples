//snippet-sourcedescription:[ActivityWorker.java demonstrates how to implement an activity worker that polls for tasks in a task list and executes its task.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Workflow Service (Amazon SWF)]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.helloswf;

// snippet-start:[swf.java2.activity_worker.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskResponse;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskRequest;
import software.amazon.awssdk.services.swf.model.TaskList;
import software.amazon.awssdk.services.swf.model.RespondActivityTaskCompletedRequest;
import software.amazon.awssdk.services.swf.model.RespondActivityTaskFailedRequest;
// snippet-end:[swf.java2.activity_worker.import]

// snippet-start:[swf.java2.activity_worker.main]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ActivityWorker {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    HelloTypes <domain> <taskList> \n\n" +
                "Where:\n" +
                "    domain - The domain to use (for example, mydomain). \n" +
                "    taskList - The taskList to use (for example, HelloTasklist).  \n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String domain = args[0];
        String taskList = args[1];

        Region region = Region.US_EAST_1;
        SwfClient swf = SwfClient.builder()
                .region(region)
                .build();

        getPollData(swf, domain, taskList) ;
        swf.close();
    }

    public static void getPollData( SwfClient swf, String domain, String taskList) {

        System.out.println("Polling for an activity task from the tasklist '"
                    + taskList + "' in the domain '" +
                    domain + "'.");

            PollForActivityTaskResponse task = swf.pollForActivityTask(
                    PollForActivityTaskRequest.builder()
                            .domain(domain)
                            .taskList(
                                    TaskList.builder().name(taskList).build())
                            .build());

            String taskToken = task.taskToken();

            if (taskToken != null) {
                String result = null;
                Throwable error = null;

               System.out.println("Executing the activity task with input '" +task.input() + "'.");
               result = sayHello(task.input());

                if (error == null) {
                    System.out.println("The activity task succeeded with result '"
                            + result + "'.");
                    swf.respondActivityTaskCompleted(
                            RespondActivityTaskCompletedRequest.builder()
                                    .taskToken(taskToken)
                                    .result(result).build());
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


    private static String sayHello(String input) {
        return "Hello, " + input + "!";
    }
}
// snippet-end:[swf.java2.activity_worker.main]
