//snippet-sourcedescription:[ListTopics.java demonstrates how to get a list of existing AWS SNS topics.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-07-20]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[sns.java2.ListTopics.complete]
package com.example.sns;

//snippet-start:[sns.java2.ListTopics.import]

        import software.amazon.awssdk.regions.Region;
        import software.amazon.awssdk.services.sns.SnsClient;
        import software.amazon.awssdk.services.sns.model.ListTopicsRequest;
        import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
//snippet-end:[sns.java2.ListTopics.import]

public class ListTopics {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "ListTopics - returns a list of Amazon SNS topics.\n" +
                "Usage: ListTopics \n\n";


        //snippet-start:[sns.java2.ListTopics.main]

        SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();

        ListTopicsRequest request = ListTopicsRequest.builder().build();

        ListTopicsResponse result = snsClient.listTopics(request);

        System.out.println(result.topics());
        //snippet-end:[sns.java2.ListTopics.main]
    }
}
//snippet-end:[sns.java2.ListTopics.complete]

