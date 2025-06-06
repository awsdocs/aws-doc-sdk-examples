// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinQueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

public class NeptuneLambdaHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();

        String NEPTUNE_ENDPOINT = "https://neptunecluster65.cluster-ro-csf1if1wwrox.us-east-1.neptune.amazonaws.com:8182";

        NeptunedataClient neptunedataClient = NeptunedataClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(NEPTUNE_ENDPOINT))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(10))
                        .socketTimeout(Duration.ofSeconds(30)))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(30))
                        .build())
                .build();

        // Execute Gremlin Query
        logger.log("Executing Gremlin PROFILE query...\n");

        ExecuteGremlinQueryRequest queryRequest = ExecuteGremlinQueryRequest.builder()
                .gremlinQuery("g.V().hasLabel('person').values('name')")
                .build();

        ExecuteGremlinQueryResponse response = neptunedataClient.executeGremlinQuery(queryRequest);

        // Log full response as JSON
        logger.log("Full Response:\n");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            logger.log(jsonResponse + "\n");
        } catch (Exception e) {
            logger.log("Failed to serialize response: " + e.getMessage() + "\n");
        }

        // Log result specifically
        if (response.result() != null) {
            logger.log("Query Result:\n" + response.result().toString() + "\n");
        } else {
            logger.log("No result returned from the query.\n");
        }

        return "Done";
    }
}
