package com.example.bedrockruntime.models.amazon.titan.text;

import com.example.bedrockruntime.libs.ScenarioRunner;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.io.IOException;

/**
 * This program demonstrates how to use InvokeModel with Amazon Titan Text models on Amazon Bedrock,
 * using Titan's native request/response structure.
 * <p>
 * For more examples in different programming languages check out the Amazon Bedrock User Guide at:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
 */

public class InvokeModelScenarios {
    // snippet-start:[bedrock-runtime.java2.InvokeModel_TitanTextG1_SingleMessage]

    /**
     * Invoke Titan Text with a system prompt and additional inference parameters,
     * using Titan's native request/response structure.
     *
     * @param userPrompt   - The text prompt to send to the model.
     * @param systemPrompt - A system prompt to provide additional context and instructions.
     * @return The {@link JSONObject} representing the model's response.
     */
    public static JSONObject invokeWithText(String userPrompt, String systemPrompt) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Set the model ID, e.g., Titan Text Premier.
        var modelId = "amazon.titan-text-premier-v1:0";

        /* Assemble the input text.
         * For best results, use the following input text format:
         *     {{ system instruction }}
         *     User: {{ user input }}
         *     Bot:
         */
        var inputText = """
                %s
                User: %s
                Bot: 
                """.formatted(systemPrompt, userPrompt);

        // Format the request payload using the model's native structure.
        var nativeRequest = new JSONObject()
                .put("inputText", inputText)
                .put("textGenerationConfig", new JSONObject()
                        .put("maxTokenCount", 512)
                        .put("temperature", 0.7F)
                        .put("topP", 0.9F)
                )
                .toString();

        // Encode and send the request.
        var response = client.invokeModel(request -> {
            request.body(SdkBytes.fromUtf8String(nativeRequest));
            request.modelId(modelId);
        });

        // Decode the native response body.
        var nativeResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the response text.
        var responseText = nativeResponse.getJSONArray("results").getJSONObject(0).getString("outputText");
        System.out.println(responseText);

        // Return the model's native response.
        return nativeResponse;
    }
    // snippet-end:[bedrock-runtime.java2.InvokeModel_TitanTextG1_SingleMessage]

    // snippet-start:[bedrock-runtime.java2.InvokeModel_TitanTextG1_Conversation]

    /**
     * Create a chat-like experience with a conversation history, using Titan's native
     * request/response structure.
     *
     * @param prompt       - The text prompt to send to the model.
     * @param conversation - A String representing previous conversational turns in the format
     *                     User: {{ previous user prompt}}
     *                     Bot: {{ previous model response }}
     *                     ...
     * @return The {@link JSONObject} representing the model's response.
     */
    public static JSONObject invokeWithConversation(String prompt, String conversation) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Set the model ID, e.g., Titan Text Premier.
        var modelId = "amazon.titan-text-premier-v1:0";

        /* Append the new prompt to the conversation.
         * For best results, use the following text format:
         *     User: {{ previous user prompt}}
         *     Bot: {{ previous model response }}
         *     User: {{ new user prompt }}
         *     Bot: """
         */
        conversation = conversation + """
                User: %s
                Bot: 
                """.formatted(prompt);

        // Format the request payload using the model's native structure.
        var nativeRequest = new JSONObject().put("inputText", conversation);

        // Encode and send the request.
        var response = client.invokeModel(request -> {
            request.body(SdkBytes.fromUtf8String(nativeRequest.toString()));
            request.modelId(modelId);
        });

        // Decode the native response body.
        var nativeResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the response text.
        var responseText = nativeResponse.getJSONArray("results").getJSONObject(0).getString("outputText");
        System.out.println(responseText);

        // Return the model's native response.
        return nativeResponse;
    }
    // snippet-end:[bedrock-runtime.java2.InvokeModel_TitanTextG1_Conversation]

    public static void main(String[] args) throws IOException {
        new Demo().run();
    }

    private static class Demo {
        private final String FIRST_SCENARIO_TITLE = "How to add a system prompt and additional parameters";
        private final String SECOND_SCENARIO_TITLE = "How to use a conversation history to simulate a chat";

        private final ScenarioRunner scenario;

        Demo() {
            scenario = new ScenarioRunner();
            scenario.add(FIRST_SCENARIO_TITLE);
            scenario.add(SECOND_SCENARIO_TITLE);
        }

        void run() throws IOException {
            scenario.printHeader();

            scenario.promptUser("Press Enter to start the first scenario...");

            var firstPrompt = "Write a haiku about a sunset.";
            JSONObject firstResponse = runTextScenario(firstPrompt);

            scenario.printCurrentResponse(firstResponse);

            scenario.promptUser("Press Enter to start the next scenario...");

            var conversation = createConversationalTurn(firstPrompt, firstResponse);

            var secondPrompt = "Take the role of a poetry expert and explain the Haiku above.";
            var secondResponse = runConversationScenario(secondPrompt, conversation);
            scenario.printCurrentResponse(secondResponse);

            scenario.printFooter();
        }

        private String createConversationalTurn(String userPrompt, JSONObject modelResponse) {
            var responseText = modelResponse
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getString("outputText");

            return """
                    User: %s
                    Bot: %s
                    """.formatted(userPrompt, responseText);
        }

        private JSONObject runTextScenario(String userPrompt) {
            scenario.printScenarioHeader("Scenario 1 - %s:" .formatted(FIRST_SCENARIO_TITLE));

            var systemPrompt = "All your responses must contain the word 'developer'.";

            System.out.printf("%nUser prompt:   '%s'%n", userPrompt);
            System.out.printf("System prompt: '%s'%n%n", systemPrompt);

            System.out.printf("Waiting for the response...%n");
            return invokeWithText(userPrompt, systemPrompt);
        }

        private JSONObject runConversationScenario(String prompt, String conversation) {
            scenario.printScenarioHeader("Scenario 2 - %s:" .formatted(SECOND_SCENARIO_TITLE));

            System.out.printf("%nNext prompt: '%s'%n", prompt);
            System.out.println("Conversation history:");
            System.out.println(conversation);

            System.out.printf("%nWaiting for the response...%n");

            return invokeWithConversation(prompt, conversation);
        }
    }
}
