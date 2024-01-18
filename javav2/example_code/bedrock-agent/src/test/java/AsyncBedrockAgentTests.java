// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.bedrockagent.async.CreateAgentAsync;
import com.example.bedrockagent.async.GetAgentAsync;
import com.example.bedrockagent.async.ListAgentActionGroupsAsync;
import com.example.bedrockagent.async.ListAgentsAsync;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentAsyncClient;
import software.amazon.awssdk.services.bedrockagent.model.ActionGroupSummary;
import software.amazon.awssdk.services.bedrockagent.model.Agent;
import software.amazon.awssdk.services.bedrockagent.model.DeleteAgentRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AsyncBedrockAgentTests extends BedrockAgentTestBase {

    @BeforeAll
    static void setup() {
        try (InputStream input = AsyncBedrockAgentTests.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            var prop = new Properties();
            prop.load(input);
            region = prop.getProperty("region");
            agentId = prop.getProperty("agentId");
            agentVersion = prop.getProperty("agentVersion");
            agentRoleArn = prop.getProperty("agentRoleArn");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        newAgentName = "TestAgent_" + createRandomPostfix();

        asyncClient = BedrockAgentAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Test
    @Order(0)
    @Tag("IntegrationTest")
    void listAgents() {
        var agents = ListAgentsAsync.listAgents(asyncClient);
        assertNotNull(agents);
        System.out.println("Test ListAgentsAsync passed.");
    }

    @Test
    @Order(2)
    @Tag("IntegrationTest")
    void createAgent() {
        Agent agent = CreateAgentAsync.createAgent(asyncClient, newAgentName, agentRoleArn, foundationModel);
        assertNotNull(agent);

        // Clean up
        System.out.print("Deleting agent... ");
        waitForStatus(asyncClient, agent, "NOT_PREPARED");
        try {
            asyncClient.deleteAgent(DeleteAgentRequest.builder()
                    .agentId(agent.agentId())
                    .build()).whenComplete((response, exception) -> {
                if (exception != null) {
                    System.out.println(exception.getMessage());
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.print("Done.\n");
    }

    @Test
    @Order(3)
    @Tag("IntegrationTest")
    void getAgent() {
        assertDoesNotThrow(() -> GetAgentAsync.getAgent(asyncClient, agentId));
        System.out.println("Test GetAgentAsync passed.");
    }

    @Test
    @Order(4)
    @Tag("IntegrationTest")
    void listAgentActionGroups() {
        List<ActionGroupSummary> actionGroups = ListAgentActionGroupsAsync.listAgentActionGroups(
                asyncClient, agentId, agentVersion
        );
        assertNotNull(actionGroups);
        System.out.println("Test ListAgentActionGroups passed.");
    }
}
