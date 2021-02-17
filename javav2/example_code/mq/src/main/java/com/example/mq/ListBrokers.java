package com.example.mq;

import java.util.*;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

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
}
