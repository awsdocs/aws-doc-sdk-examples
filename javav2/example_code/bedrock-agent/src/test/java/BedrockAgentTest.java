// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockagent.GetAgent;
import com.example.bedrockagent.ListAgents;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BedrockAgentTest {

    private static BedrockAgentClient client;
    private static String agentId = "";

    @BeforeAll
    static void setup() {
        Region region = Region.US_EAST_1;
        client = BedrockAgentClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        try (InputStream input = BedrockAgentTest.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            var prop = new Properties();
            prop.load(input);
            agentId = prop.getProperty("agentId");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void listAgents() {
        var agents = ListAgents.listAgents(client);
        assertNotNull(agents);
        System.out.println("Test ListAgents() passed.");
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void getAgent() {
        assertDoesNotThrow(() -> GetAgent.getAgent(client, agentId));
        System.out.println("Test GetAgent() passed.");
    }
}
