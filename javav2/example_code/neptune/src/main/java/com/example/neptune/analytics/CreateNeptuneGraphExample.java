package com.example.neptune.analytics;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunegraph.NeptuneGraphClient;
import software.amazon.awssdk.services.neptunegraph.model.CreateGraphRequest;
import software.amazon.awssdk.services.neptunegraph.model.CreateGraphResponse;
import software.amazon.awssdk.services.neptunegraph.model.GraphStatus;
import software.amazon.awssdk.services.neptunegraph.model.NeptuneGraphException;

public class CreateNeptuneGraphExample {

    public static void main(String[] args) {
        // Set the desired region
        Region region = Region.US_EAST_1;

        // Set the name for your new graph
        String graphName = "sample-analytics-graph";

        // Create the NeptuneGraph client
        NeptuneGraphClient client = NeptuneGraphClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        executeCreateGraph(client, graphName);

    }

    /**
     * Executes the process of creating a new Neptune graph.
     *
     * @param client        the Neptune graph client used to interact with the Neptune service
     * @param graphName     the name of the graph to be created
     * @throws NeptuneGraphException if an error occurs while creating the graph
     */
    public static void executeCreateGraph(NeptuneGraphClient client, String graphName) {
        try {
            // Create the graph request
            CreateGraphRequest request = CreateGraphRequest.builder()
                    .graphName(graphName)
                    .provisionedMemory(16)
                    .build();

            // Create the graph
            CreateGraphResponse response = client.createGraph(request);

            // Extract the graph name and ARN
            String createdGraphName = response.name();
            String graphArn = response.arn();
            String graphEndpoint = response.endpoint();

            System.out.println("Graph created successfully!");
            System.out.println("Graph Name: " + createdGraphName);
            System.out.println("Graph ARN: " + graphArn);
            System.out.println("Graph Endpoint: " +graphEndpoint );

        } catch (NeptuneGraphException e) {
            System.err.println("Failed to create graph: " + e.awsErrorDetails().errorMessage());
        } finally {
            client.close();
        }
   }
}

