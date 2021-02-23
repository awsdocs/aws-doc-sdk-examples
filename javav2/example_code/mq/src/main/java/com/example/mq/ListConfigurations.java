//snippet-sourcedescription:[ListConfigurations.java demonstrates how to list existing Amazon MQ configurations.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon MQ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/18/2021]
//snippet-sourceauthor:[fararmin-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[mq.java2.list_configurations.complete]
package com.example.mq;

// snippet-start:[mq.java2.list_configurations.import]
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;
// snippet-end:[mq.java2.list_configurations.import]

public class ListConfigurations {
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
    
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        // Applies only to Amazon MQ for ActiveMQ brokers.
        List<Configuration> results = listConfigurations(mqClient);

        for (Configuration result : results) {
            System.out.println("Name: " + result.name() + " | " +
                "ID: " + result.id() + "\n");
        }

        mqClient.close();

    }
    // snippet-start:[mq.java2.list_configurations.main]
    public static List<Configuration> listConfigurations(MqClient mqClient) {

        List<Configuration> configurationsList = new ArrayList<>();

        try {
            ListConfigurationsResponse response = mqClient.listConfigurations();
            configurationsList = response.configurations();

            return configurationsList;

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return configurationsList;
    }
    // snippet-end:[mq.java2.list_configurations.main]
}
// snippet-end:[mq.java2.list_configurations.complete]