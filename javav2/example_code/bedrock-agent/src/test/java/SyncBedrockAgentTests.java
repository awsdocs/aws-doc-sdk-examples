// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockagent.sync.CreateAgent;
import com.example.bedrockagent.sync.GetAgent;
import com.example.bedrockagent.sync.ListAgentActionGroups;
import com.example.bedrockagent.sync.ListAgents;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SyncBedrockAgentTests extends BedrockAgentTestBase {

    @BeforeAll
    static void setup() {
        try (InputStream input = SyncBedrockAgentTests.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            var prop = new Properties();
            prop.load(input);
            region = prop.getProperty("region");
            foundationModel = prop.getProperty("foundationModel");
            agentId = prop.getProperty("agentId");
            agentVersion = prop.getProperty("agentVersion");
            agentRoleArn = prop.getProperty("agentRoleArn");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        newAgentName = "TestAgent_" + createRandomPostfix();

        client = BedrockAgentClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(1)
    @Tag("IntegrationTest")
    void listAgents() {
        var agents = ListAgents.listAgents(client);
        assertNotNull(agents);
        System.out.println("Test ListAgents passed.");
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void createAgent() {
        Agent agent = CreateAgent.createAgent(client, newAgentName, agentRoleArn, foundationModel);
        assertNotNull(agent);

        // Clean up
        System.out.print("Deleting agent... ");
        waitForStatus(client, agent, "NOT_PREPARED");
        client.deleteAgent(DeleteAgentRequest.builder()
                .agentId(agent.agentId())
                .build());
        System.out.print("Done.\n");
        System.out.println("Test CreateAgent passed.");
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    void getAgent() {
        assertDoesNotThrow(() -> GetAgent.getAgent(client, agentId));
        System.out.println("Test GetAgent passed.");
    }

    @Test
    @Order(4)
    @Tag("IntegrationTest")
    void listAgentActionGroups() {
        List<ActionGroupSummary> actionGroups = ListAgentActionGroups.listAgentActionGroups(
                client, agentId, agentVersion
        );
        assertNotNull(actionGroups);
        System.out.println("Test ListAgentActionGroups passed.");
    }
}
