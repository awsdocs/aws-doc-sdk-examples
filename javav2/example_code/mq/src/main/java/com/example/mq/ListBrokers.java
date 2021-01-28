package com.example.mq;

import java.util.List;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class ListBrokers {
    public static void main(String[] args) {
        try {
            Region region = Region.US_WEST_2;
        
            MqClient mqClient = MqClient.builder()
                    .region(region)
                    .build();
            

            ListBrokersResponse response = mqClient.listBrokers();
            List<BrokerSummary> result = response.brokerSummaries();
            for (BrokerSummary broker : result) {
                System.out.print("Name: " + broker.brokerName() + " | " +
                    "ID: " + broker.brokerId() + " | " + broker.brokerState() + "\n");
            }
            mqClient.close();

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
