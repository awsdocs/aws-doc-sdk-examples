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

public class ActivityWorker {
    private static final SWFClient swf =
    		SWFClient.builder().build();

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
