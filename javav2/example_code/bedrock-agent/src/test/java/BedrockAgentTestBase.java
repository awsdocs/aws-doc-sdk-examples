import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentAsyncClient;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.Agent;
import software.amazon.awssdk.services.bedrockagent.model.GetAgentRequest;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class BedrockAgentTestBase {
    protected static BedrockAgentClient client;
    protected static BedrockAgentAsyncClient asyncClient;

    protected static String region = "us-east-1";
    protected static String foundationModel = "anthropic.claude-v2";

    protected static String agentId;
    protected static String agentVersion;
    protected static String agentRoleArn;

    private final Object lock = new Object();

    protected static String randomPostfix() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder(6);

        for (int i = 0; i < 8; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            randomString.append(chars.charAt(randomIndex));
        }
        return randomString.toString();
    }

    protected void waitForStatus(AwsClient client, Agent agent, String expectedStatus) {
        Function<GetAgentRequest, Agent> getAgent;
        GetAgentRequest request = GetAgentRequest.builder().agentId(agent.agentId()).build();

        if (client instanceof BedrockAgentClient finalClient) {
            getAgent = req -> finalClient.getAgent(request).agent();
        } else if (client instanceof BedrockAgentAsyncClient finalClient) {
            getAgent = req -> {
                try {
                    return finalClient.getAgent(request).get().agent();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            throw new IllegalArgumentException(
                    "Client must be one of 'BedrockAgentClient' or 'BedrockAgentAsyncClient', but was '%s'"
                            .formatted(client.getClass().toString())
            );
        }

        synchronized (lock) {
            while (!getAgent.apply(request).agentStatus().toString().equals(expectedStatus)) {
                try {
                    lock.wait(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
