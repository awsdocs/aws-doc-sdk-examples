// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseOutput;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification;
import software.amazon.awssdk.services.bedrockruntime.model.ToolUseBlock;

// snippet-start:[bedrock.converseTool.javav2.scenario]
/*
 This demo illustrates a tool use scenario using Amazon Bedrock's Converse API and a weather tool.
 The program interacts with a foundation model on Amazon Bedrock to provide weather information based on user
 input. It uses the Open-Meteo API (https://open-meteo.com) to retrieve current weather data for a given location.
 */
public class BedrockScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static String modelId = "anthropic.claude-3-sonnet-20240229-v1:0";
    private static String defaultPrompt = "What is the weather like in Seattle?";
    private static WeatherTool weatherTool = new WeatherTool();

    // The maximum number of recursive calls allowed in the tool use function.
    // This helps prevent infinite loops and potential performance issues.
    private static int maxRecursions = 5;
    static BedrockActions bedrockActions = new BedrockActions();
    public static boolean interactive = true;

    private static final String systemPrompt = """
        You are a weather assistant that provides current weather data for user-specified locations using only
        the Weather_Tool, which expects latitude and longitude. Infer the coordinates from the location yourself.
        If the user provides coordinates, infer the approximate location and refer to it in your response.
        To use the tool, you strictly apply the provided tool specification.

        - Explain your step-by-step process, and give brief updates before each step.
        - Only use the Weather_Tool for data. Never guess or make up information. 
        - Repeat the tool use for subsequent requests if necessary.
        - If the tool errors, apologize, explain weather is unavailable, and suggest other options.
        - Report temperatures in °C (°F) and wind in km/h (mph). Keep weather reports concise. Sparingly use
          emojis where appropriate.
        - Only respond to weather queries. Remind off-topic users of your purpose. 
        - Never claim to search online, access external data, or use tools besides Weather_Tool.
        - Complete the entire process until you have all required data before sending the complete response.
        """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
         =================================================
         Welcome to the Amazon Bedrock Tool Use demo!
         =================================================
                    
         This assistant provides current weather information for user-specified locations.
         You can ask for weather details by providing the location name or coordinates.
                    
         Example queries:
         - What's the weather like in New York?
         - Current weather for latitude 40.70, longitude -74.01
         - Is it warmer in Rome or Barcelona today?
                    
         To exit the program, simply type 'x' and press Enter.
                    
         P.S.: You're not limited to single locations, or even to using English!
         Have fun and experiment with the app!
         """);
        System.out.println(DASHES);

        try {
            runConversation(scanner);

        } catch (Exception ex) {
            System.out.println("There was a problem running the scenario: "+ ex.getMessage());
        }

        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("Amazon Bedrock Converse API with Tool Use Feature Scenario is complete.");
        System.out.println(DASHES);
    }

    /**
     Starts the conversation with the user and handles the interaction with Bedrock.
     */
    private static List<Message> runConversation(Scanner scanner) {
        List<Message> conversation = new ArrayList<>();

        // Get the first user input
        String userInput = getUserInput("Your weather info request:", scanner);
        System.out.println(userInput);

        while (userInput != null) {
            ContentBlock block = ContentBlock.builder()
                .text(userInput)
                .build();

            List<ContentBlock> blockList = new ArrayList<>();
            blockList.add(block);

            Message message = Message.builder()
                .role(ConversationRole.USER)
                .content(blockList)
                .build();

            conversation.add(message);

            // Send the conversation to Amazon Bedrock.
            ConverseResponse bedrockResponse = sendConversationToBedrock(conversation);

            // Recursively handle the model's response until the model has returned its final response or the recursion counter has reached 0.
            processModelResponse(bedrockResponse, conversation, maxRecursions);

            // Repeat the loop until the user decides to exit the application.
            userInput = getUserInput("Your weather info request:", scanner);
        }
        printFooter();
        return conversation;
    }

    /**
     * Processes the response from the model and updates the conversation accordingly.
     *
     * @param modelResponse   the response from the model
     * @param conversation   the ongoing conversation
     * @param maxRecursion   the maximum number of recursions allowed
     */
    private static void processModelResponse(ConverseResponse modelResponse, List<Message> conversation, int maxRecursion) {
        if (maxRecursion <= 0) {
            // Stop the process, the number of recursive calls could indicate an infinite loop
            System.out.println("\tWarning: Maximum number of recursions reached. Please try again.");
        }

        // Append the model's response to the ongoing conversation
        conversation.add(modelResponse.output().message());

        String modelResponseVal = modelResponse.stopReasonAsString();
        if (modelResponseVal.compareTo("tool_use") == 0) {
            // If the stop reason is "tool_use", forward everything to the tool use handler
            handleToolUse(modelResponse.output(), conversation, maxRecursion - 1);
        }

        if (modelResponseVal.compareTo ("end_turn") ==0) {
            // If the stop reason is "end_turn", print the model's response text, and finish the process
            PrintModelResponse(modelResponse.output().message().content().get(0).text());
            if (!interactive) {
                defaultPrompt = "x";
            }
        }
    }

    /**
     * Handles the use of a tool by the model in a conversation.
     *
     * @param modelResponse the response from the model, which may include a tool use request
     * @param conversation the current conversation, which will be updated with the tool use results
     * @param maxRecursion the maximum number of recursive calls allowed to handle the model's response
     */
    private static void handleToolUse(ConverseOutput modelResponse, List<Message> conversation, int maxRecursion) {
        List<ContentBlock> toolResults = new ArrayList<>();

        // The model's response can consist of multiple content blocks
        for (ContentBlock contentBlock : modelResponse.message().content()) {
            if (contentBlock.text() != null && !contentBlock.text().isEmpty()) {
                // If the content block contains text, print it to the console
                PrintModelResponse(contentBlock.text());
            }

            if (contentBlock.toolUse() != null) {
                ToolResponse toolResponse = invokeTool(contentBlock.toolUse());

                // Add the tool use ID and the tool's response to the list of results
                List<ToolResultContentBlock> contentBlockList = new ArrayList<>();
                ToolResultContentBlock block = ToolResultContentBlock.builder()
                    .json(toolResponse.getContent())
                    .build();
                contentBlockList.add(block);

                ToolResultBlock toolResultBlock = ToolResultBlock.builder()
                    .toolUseId(toolResponse.getToolUseId())
                    .content(contentBlockList)
                    .build();

                ContentBlock contentBlock1 = ContentBlock.builder()
                    .toolResult(toolResultBlock)
                    .build();

                toolResults.add(contentBlock1);
            }
        }

        // Embed the tool results in a new user message
        Message message = Message.builder()
            .role(ConversationRole.USER)
            .content(toolResults)
            .build();

        // Append the new message to the ongoing conversation
        //conversation.add(message);
        conversation.add(message);

        // Send the conversation to Amazon Bedrock
        var response =  sendConversationToBedrock(conversation);

        // Recursively handle the model's response until the model has returned its final response or the recursion counter has reached 0
        processModelResponse(response, conversation, maxRecursion);
    }

    // Invokes the specified tool with the given payload and returns the tool's response.
    // If the requested tool does not exist, an error message is returned.
    private static ToolResponse invokeTool(ToolUseBlock payload) {
        String toolName = payload.name();

        if (Objects.equals(toolName, "Weather_Tool")){
            Map<String, Document> inputData = payload.input().asMap();
            printToolUse(toolName, inputData);

            // Invoke the weather tool with the input data provided
            Document weatherResponse = weatherTool.fetchWeatherData(inputData.get("latitude").toString(), inputData.get("longitude").toString());

            ToolResponse toolResponse = new ToolResponse();
            toolResponse.setContent(weatherResponse);
            toolResponse.setToolUseId(payload.toolUseId());
            return toolResponse;
        } else {
            String errorMessage = "The requested tool with name "+toolName +" does not exist.";
            System.out.println(errorMessage);
            return null;
        }
    }

    public static void printToolUse(String toolName, Map<String, Document> inputData) {
        System.out.println("Invoking tool: " + toolName + " with input: " + inputData.get("latitude").toString() + ", " + inputData.get("longitude").toString() + "...");
    }

    private static void PrintModelResponse(String message) {
        System.out.println("\tThe model's response:\n");
        System.out.println(message);
        System.out.println("");
    }

    private static ConverseResponse sendConversationToBedrock(List<Message> conversation) {
        System.out.println("Calling Bedrock...");

        // Send the conversation, system prompt, and tool configuration, and return the response
        return bedrockActions.sendConverseRequestAsync(modelId, systemPrompt, conversation, weatherTool.getToolSpec());
    }

    private static ConverseResponse sendConversationToBedrockwithSpec(List<Message> conversation, ToolSpecification toolSpec) {
        System.out.println("Calling Bedrock...");

        // Send the conversation, system prompt, and tool configuration, and return the response
        return bedrockActions.sendConverseRequestAsync(modelId, systemPrompt, conversation, toolSpec);
    }

    public static String getUserInput(String prompt, Scanner scanner) {
        String userInput = defaultPrompt;
        if (interactive) {
            System.out.println("*".repeat(80));
            System.out.println(prompt + " (x to exit): \n\t");
            userInput = scanner.nextLine();
        }

        if (userInput == null || userInput.trim().isEmpty()) {
            return getUserInput("\tPlease enter your weather info request, e.g., the name of a city", scanner);
        }

        if (userInput.equalsIgnoreCase("x")) {
            return null;
        }

        return userInput;
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    public static void printFooter()
    {
        System.out.println("""
        =================================================
        Thank you for checking out the Amazon Bedrock Tool Use demo. We hope you
        learned something new, or got some inspiration for your own apps today!

        For more Bedrock examples in different programming languages, have a look at:
        https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
        =================================================
        """);
    }
}
// snippet-end:[bedrock.converseTool.javav2.scenario]