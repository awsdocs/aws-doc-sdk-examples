// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Runtime.CompilerServices;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Threading.Channels;
using Amazon;
using Amazon.BedrockRuntime;
using Amazon.BedrockRuntime.Model;
using Amazon.Runtime.EventStreams;
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

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.ClaudeWithResponseStream]

        /// <summary>
        /// Asynchronously invokes the Anthropic Claude 2 model to run an inference based on the provided input and process the response stream.
        /// </summary>
        /// <param name="prompt">The prompt that you want Claude to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Anthropic Claude, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-claude.html
        /// </remarks>
        public static async IAsyncEnumerable<string> InvokeClaudeWithResponseStreamAsync(string prompt, [EnumeratorCancellation] CancellationToken cancellationToken = default)
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

            InvokeModelWithResponseStreamResponse? response = null;

            try
            {
                response = await client.InvokeModelWithResponseStreamAsync(new InvokeModelWithResponseStreamRequest()
                {
                    ModelId = claudeModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });
            }
            catch (AmazonBedrockRuntimeException e)
            {
                Console.WriteLine(e.Message);
            }

            if (response is not null && response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                // create a buffer to write the event in to move from a push mode to a pull mode
                Channel<string> buffer = Channel.CreateUnbounded<string>();
                bool isStreaming = true;

                response.Body.ChunkReceived += BodyOnChunkReceived;
                response.Body.StartProcessing();

                while ((!cancellationToken.IsCancellationRequested && isStreaming) || (!cancellationToken.IsCancellationRequested && buffer.Reader.Count > 0))
                {
                    // pull the completion from the buffer and add it to the IAsyncEnumerable collection
                    yield return await buffer.Reader.ReadAsync(cancellationToken);
                }
                response.Body.ChunkReceived -= BodyOnChunkReceived;

                yield break;

                // handle the ChunkReceived events
                async void BodyOnChunkReceived(object? sender, EventStreamEventReceivedArgs<PayloadPart> e)
                {
                    var streamResponse = JsonSerializer.Deserialize<JsonObject>(e.EventStreamEvent.Bytes) ?? throw new NullReferenceException($"Unable to deserialize {nameof(e.EventStreamEvent.Bytes)}");

                    if (streamResponse["stop_reason"]?.GetValue<string?>() != null)
                    {
                        isStreaming = false;
                    }

                    // write the received completion chunk into the buffer
                    await buffer.Writer.WriteAsync(streamResponse["completion"]?.GetValue<string>(), cancellationToken);
                }
            }
            else if (response is not null)
            {
                Console.WriteLine("InvokeModelAsync failed with status code " + response.HttpStatusCode);
            }

            yield break;
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.ClaudeWithResponseStream]

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

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.TitanTextG1]

        /// <summary>
        /// Asynchronously invokes the Amazon Titan Text G1 Express model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that you want Amazon Titan Text G1 Express to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Amazon Titan Text G1 Express, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-text.html
        /// </remarks>
        public static async Task<string> InvokeTitanTextG1Async(string prompt)
        {
            string titanTextG1ModelId = "amazon.titan-text-express-v1";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USEast1);

            string payload = new JsonObject()
            {
                { "inputText", prompt },
                { "textGenerationConfig", new JsonObject()
                    {
                        { "maxTokenCount", 512 },
                        { "temperature", 0f },
                        { "topP", 1f }
                    }
                }
            }.ToJsonString();

            string generatedText = "";
            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = titanTextG1ModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    var results = JsonNode.ParseAsync(response.Body).Result?["results"]?.AsArray();

                    return results is null ? "" : string.Join(" ", results.Select(x => x?["outputText"]?.GetValue<string?>()));
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

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.TitanTextG1]

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Mistral7B]

        /// <summary>
        /// Asynchronously invokes the Mistral 7B model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that you want Mistral 7B to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Mistral 7B, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-mistral.html
        /// </remarks>
        public static async Task<List<string?>> InvokeMistral7BAsync(string prompt)
        {
            string mistralModelId = "mistral.mistral-7b-instruct-v0:2";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USWest2);

            string payload = new JsonObject()
            {
                { "prompt", prompt },
                { "max_tokens", 200 },
                { "temperature", 0.5 }
            }.ToJsonString();

            List<string?>? generatedText = null;
            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = mistralModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    var results = JsonNode.ParseAsync(response.Body).Result?["outputs"]?.AsArray();

                    generatedText = results?.Select(x => x?["text"]?.GetValue<string?>())?.ToList();
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
            return generatedText ?? [];
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Mistral7B]

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Mixtral8x7B]

        /// <summary>
        /// Asynchronously invokes the Mixtral 8x7B model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that you want Mixtral 8x7B to complete.</param>
        /// <returns>The inference response from the model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Mixtral 8x7B, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-mistral.html
        /// </remarks>
        public static async Task<List<string?>> InvokeMixtral8x7BAsync(string prompt)
        {
            string mixtralModelId = "mistral.mixtral-8x7b-instruct-v0:1";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USWest2);

            string payload = new JsonObject()
            {
                { "prompt", prompt },
                { "max_tokens", 200 },
                { "temperature", 0.5 }
            }.ToJsonString();

            List<string?>? generatedText = null;
            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = mixtralModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    var results = JsonNode.ParseAsync(response.Body).Result?["outputs"]?.AsArray();

                    generatedText = results?.Select(x => x?["text"]?.GetValue<string?>())?.ToList();
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
            return generatedText ?? [];
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.Mixtral8x7B]

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.TitanImageGeneratorG1]

        /// <summary>
        /// Asynchronously invokes the Amazon Titan Image Generator G1 model to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that describes the image Amazon Titan Image Generator G1 has to generate.</param>
        /// <returns>A base-64 encoded image generated by model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Amazon Titan Image Generator G1, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-image.html
        /// </remarks>
        public static async Task<string?> InvokeTitanImageGeneratorG1Async(string prompt, int seed)
        {
            string titanImageGeneratorG1ModelId = "amazon.titan-image-generator-v1";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USEast1);

            string payload = new JsonObject()
            {
                { "taskType", "TEXT_IMAGE" },
                { "textToImageParams", new JsonObject()
                    {
                        { "text", prompt }
                    }
                },
                { "imageGenerationConfig", new JsonObject()
                    {
                        { "numberOfImages", 1 },
                        { "quality", "standard" },
                        { "cfgScale", 8.0f },
                        { "height", 512 },
                        { "width", 512 },
                        { "seed", seed }
                    }
                }
            }.ToJsonString();

            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = titanImageGeneratorG1ModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    var results = JsonNode.ParseAsync(response.Body).Result?["images"]?.AsArray();

                    return results?[0]?.GetValue<string>();
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
            return null;
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.TitanImageGeneratorG1]

        // snippet-start:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.StableDiffusionXL]

        /// <summary>
        /// Asynchronously invokes the Stability.ai Stable Diffusion XLmodel to run an inference based on the provided input.
        /// </summary>
        /// <param name="prompt">The prompt that describes the image Stability.ai Stable Diffusion XL has to generate.</param>
        /// <returns>A base-64 encoded image generated by model</returns>
        /// <remarks>
        /// The different model providers have individual request and response formats.
        /// For the format, ranges, and default values for Stability.ai Stable Diffusion XL, refer to:
        ///     https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-stability-diffusion.html
        /// </remarks>
        public static async Task<string?> InvokeStableDiffusionXLG1Async(string prompt, int seed, string? stylePreset = null)
        {
            string stableDiffusionXLModelId = "stability.stable-diffusion-xl";

            AmazonBedrockRuntimeClient client = new(RegionEndpoint.USEast1);

            var jsonPayload = new JsonObject()
            {
                { "text_prompts", new JsonArray() {
                    new JsonObject()
                        {
                            { "text", prompt }
                        }
                    }
                },
                { "seed", seed }
            };

            if (!string.IsNullOrEmpty(stylePreset))
            {
                jsonPayload.Add("style_preset", stylePreset);
            }

            string payload = jsonPayload.ToString();

            try
            {
                InvokeModelResponse response = await client.InvokeModelAsync(new InvokeModelRequest()
                {
                    ModelId = stableDiffusionXLModelId,
                    Body = AWSSDKUtils.GenerateMemoryStreamFromString(payload),
                    ContentType = "application/json",
                    Accept = "application/json"
                });

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    var results = JsonNode.ParseAsync(response.Body).Result?["artifacts"]?.AsArray();

                    return results?[0]?["base64"]?.GetValue<string>();
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
            return null;
        }

        // snippet-end:[BedrockRuntime.dotnetv3.BedrockRuntimeActions.InvokeModelAsync.StableDiffusionXL]
    }
}