package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

import java.util.ArrayList;
import java.util.List;

public class ListConfigurations {
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
    
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        

        List<Configuration> results = listConfigurations(mqClient);

        for (Configuration result : results) {
            System.out.println("Name: " + result.name() + " | " +
                "ID: " + result.id() + "\n");
        }

        mqClient.close();

    }
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
}
