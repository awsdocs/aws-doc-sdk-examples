// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[BedrockRuntime.dotnetv4.Hello]
// Use the Converse API to send a text message to Amazon Bedrock.

using Amazon;
using Amazon.BedrockRuntime;
using Amazon.BedrockRuntime.Model;

// Create a Bedrock Runtime client in the AWS Region you want to use.
// You can change the region to match your setup.
var client = new AmazonBedrockRuntimeClient(RegionEndpoint.USWest2);

// Set the model ID, e.g., Claude Haiku.
var modelId = "global.anthropic.claude-haiku-4-5-20251001-v1:0";

// Define the user message.
var userMessage = "Hi. In a short paragraph, explain what you can do.";

// Create a request with the model ID, the user message, and an inference configuration.
var request = new ConverseRequest
{
    ModelId = modelId,
    Messages = new List<Message>
    {
        new Message
        {
            Role = ConversationRole.User,
            Content = new List<ContentBlock> { new ContentBlock { Text = userMessage } }
        }
    }
};

try
{
    // Send the request to the Bedrock Runtime and wait for the response.
    var response = await client.ConverseAsync(request);

    // Extract and print the response text.
    string responseText = response?.Output?.Message?.Content?[0]?.Text ?? "";
    Console.WriteLine(responseText);
}
catch (AmazonBedrockRuntimeException e)
{
    Console.WriteLine($"ERROR: Can't invoke '{modelId}'. Reason: {e.Message}");
    throw;
}

// snippet-end:[BedrockRuntime.dotnetv4.Hello]

// Create a partial class to make the top-level script testable.
namespace BedrockRuntimeHello { public partial class Hello { } }
