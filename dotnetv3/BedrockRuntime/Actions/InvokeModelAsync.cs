// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Claude]
using System.Text.Json.Nodes;
using Amazon;
using Amazon.BedrockRuntime;
using Amazon.BedrockRuntime.Model;
using Amazon.Util;

namespace BedrockRuntimeActions
{
    public static class InvokeModelAsync
    {
        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Claude]

        /// <summary>
        /// Asynchronously invokes the Anthropic Claude 2 model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that you want Claude to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Anthropic Claude, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html
        /// </remarks>
        public static async Task<string> InvokeClaudeAsync(string prompt)
        {
            string claudeModelId = "anthropic.claude-v2";

            // Claude requires you to enclose the prompt as follows:
            string enclosedPrompt = "Human: " + prompt + "\n\nAssistant:";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USEast1);

            string payload = new JsonObject()
            {
                { "prompt", enclosedPrompt },
                { "max_tokens_to_sample", 200 },
                { "temperature", 0.5 },
                { "stop_sequences", new JsonArray("\n\nHuman:") }
            }.ToJsonString();

            string generatedText = "";
            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = claudeModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    return JsonNode.ParseAsync(response.Body).Result?["completion"]?.GetValue<string>() ?? "";
                }
                else
                {
                    Console.WriteLine("InvokeModelAsync failed with status code " + response.HttpStatusCode);
                }
            }
            catch (AmazonBedrockRuntimeException e)
            {
                Console.WriteLine(e.Message);
            }
            return generatedText;
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Claude]

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Jurassic2]

        /// <summary>
        /// Asynchronously invokes the AI21 Labs Jurassic-2 model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that you want Claude to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-jurassic2.html
        /// </remarks>
        public static async Task<string> InvokeJurassic2Async(string prompt)
        {
            string jurassic2ModelId = "ai21.j2-mid-v1";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USEast1);

            string payload = new JsonObject()
            {
                { "prompt", prompt },
                { "maxTokens", 200 },
                { "temperature", 0.5 }
            }.ToJsonString();

            string generatedText = "";
            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = jurassic2ModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    return JsonNode.ParseAsync(response.Body)
                        .Result?["completions"]?
                        .AsArray()[0]?["data"]?
                        .AsObject()["text"]?.GetValue<string>() ?? "";
                }
                else
                {
                    Console.WriteLine("InvokeModelAsync failed with status code " + response.HttpStatusCode);
                }
            }
            catch (AmazonBedrockRuntimeException e)
            {
                Console.WriteLine(e.Message);
            }
            return generatedText;
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Jurassic2]

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Llama2]

        /// <summary>
        /// Asynchronously invokes the Meta Llama 2 Chat model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that you want Llama 2 to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html
        /// </remarks>
        public static async Task<string> InvokeLlama2Async(string prompt)
        {
            string llama2ModelId = "meta.llama2-13b-chat-v1";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USEast1);

            string payload = new JsonObject()
            {
                { "prompt", prompt },
                { "max_gen_len", 512 },
                { "temperature", 0.5 },
                { "top_p", 0.9 }
            }.ToJsonString();

            string generatedText = "";
            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = llama2ModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    return JsonNode.ParseAsync(response.Body)
                        .Result?["generation"]?.GetValue<string>() ?? "";
                }
                else
                {
                    Console.WriteLine("InvokeModelAsync failed with status code " + response.HttpStatusCode);
                }
            }
            catch (AmazonBedrockRuntimeException e)
            {
                Console.WriteLine(e.Message);
            }
            return generatedText;
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Llama2]
    }
}