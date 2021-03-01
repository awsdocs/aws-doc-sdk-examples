//snippet-sourcedescription:[ListBrokers.java demonstrates how to list existing Amazon MQ brokers.]
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

// snippet-start:[mq.java2.list_brokers.complete]
package com.example.mq;

// snippet-start:[mq.java2.list_brokers.import]
import java.util.List;
import java.util.ArrayList;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.BrokerSummary;
import software.amazon.awssdk.services.mq.model.ListBrokersResponse;
import software.amazon.awssdk.services.mq.model.MqException;
// snippet-end:[mq.java2.list_brokers.import]

public class ListBrokers {
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
    
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();

        List<BrokerSummary> results = listBrokers(mqClient);

        for (BrokerSummary result : results) {
            System.out.print("Name: " + result.brokerName() + " | " +
                "ID: " + result.brokerId() + " | " + result.brokerState() + "\n");
        }
    
        mqClient.close();
    }
    // snippet-start:[mq.java2.list_brokers.main]
    public static List<BrokerSummary> listBrokers(MqClient mqClient) {
        List<BrokerSummary> brokerList = new ArrayList<>();
        try {
            ListBrokersResponse response = mqClient.listBrokers();
            brokerList = response.brokerSummaries();

            return brokerList;

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return brokerList;
    }
    // snippet-end:[mq.java2.list_brokers.main]
}
// snippet-end:[mq.java2.list_brokers.complete]
