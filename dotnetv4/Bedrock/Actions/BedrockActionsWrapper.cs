// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using Amazon.Bedrock;
using Microsoft.Extensions.Logging;

namespace BedrockActions;

/// <summary>
/// Wrapper class for interacting with the Amazon Bedrock Converse API.
/// </summary>
public class BedrockActionsWrapper
{
    private readonly IAmazonBedrock _bedrockClient;
    private readonly ILogger<BedrockActionsWrapper> _logger;

    /// <summary>
    /// Initializes a new instance of the <see cref="BedrockActionsWrapper"/> class.
    /// </summary>
    /// <param name="bedrockClient">The Bedrock Converse API client.</param>
    /// <param name="logger">The logger instance.</param>
    public BedrockActionsWrapper(IAmazonBedrock bedrockClient, ILogger<BedrockActionsWrapper> logger)
    {
        _bedrockClient = bedrockClient;
        _logger = logger;
    }

    /// <summary>
    /// Sends a Converse request to the Amazon Bedrock Converse API.
    /// </summary>
    /// <param name="systemContent">The system content for the Converse request.</param>
    /// <param name="documentContent">The document content for the Converse request.</param>
    /// <param name="inferenceConfiguration">The inference configuration for the Converse request.</param>
    /// <param name="toolSpecification">The tool specification for the Converse request.</param>
    /// <returns>True if the Converse request was successful, false otherwise.</returns>
    public async Task<bool> SendConverseRequestAsync(string systemContent, string documentContent, InferenceConfiguration inferenceConfiguration, ToolSpecification toolSpecification)
    {
        try
        {
            var converseRequest = new ConverseRequest
            {
                SystemContent = systemContent,
                DocumentContent = documentContent,
                InferenceConfiguration = inferenceConfiguration,
                ToolSpecification = toolSpecification
            };

            var response = await _bedrockClient.ConverseAsync(converseRequest);

            if (response.IsSuccessful)
            {
                _logger.LogInformation("Converse request was successful.");
                return true;
            }
            else
            {
                _logger.LogError($"Converse request failed with error: {response.Error.Message}");
                return false;
            }
        }
        catch (AmazonBedrockException ex)
        {
            _logger.LogError(ex, "Error occurred while sending Converse request.");
            return false;
        }
    }

    /// <summary>
    /// Make the Bedrock Converse tool request with the README document as an attachment.
    /// </summary>
    /// <param name="contentMemoryStream">Memory stream of the README contents.</param>
    /// <param name="details">The object wrapping the repository details information.</param>
    /// <param name="config">Common search config, containing the criteria.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> MakeRepoToolRequest(MemoryStream contentMemoryStream, RepoDetails details, SearchConfig config)
    {
        // Set the model ID, e.g., Claude 3 sonnet
        var modelId = config.BedrockModel;

        ToolSpecification toolSpecification = new ToolSpecification();

        toolSpecification.InputSchema = new ToolInputSchema() { Json = BedrockService.ToolSpec };
        toolSpecification.Name = bedrockToolName;
        toolSpecification.Description = "Tool to summarize a github repository by its README contents";

        // Create a request with the model ID and the model's native request payload.
        var request = new ConverseRequest()
        {
            ModelId = modelId,
            System = new List<SystemContentBlock>()
            {
                new SystemContentBlock()
                {
                    Text = config.GenAiSystemText
                }
            },
            Messages = new List<Message>()
            {
                new Message()
                {
                    Role = ConversationRole.User,
                    Content = new List<ContentBlock>()
                    {
                        new ContentBlock()
                        {
                            Text = config.GenAiContentText
                        },
                        new ContentBlock()
                        {
                            Document = new DocumentBlock()
                            {
                                Format = DocumentFormat.Md,
                                Name = "README",
                                Source = new DocumentSource()
                                {
                                    Bytes = contentMemoryStream
                                }
                            }
                        }
                    }
                }
            },
            InferenceConfig = new InferenceConfiguration()
            {
                MaxTokens = 2000,
                Temperature = 0
            },
            ToolConfig = new ToolConfiguration()
            {
                Tools = new List<Tool>()
                {
                    new Tool()
                    {
                        ToolSpec = toolSpecification
                    }
                },
                ToolChoice = new ToolChoice()
                {
                    Tool = new SpecificToolChoice()
                    {
                        Name = bedrockToolName
                    }
                }
            }
        };

        try
        {
            // Send the request to the Bedrock Runtime and wait for the response.
            Console.WriteLine($"Making Bedrock Converse request for {details.Name}.");
            var response = await _amazonBedrockRuntime.ConverseAsync(request);
            if (response.StopReason == StopReason.Tool_use)
            {
                var responseContent = response.Output.Message.Content;
                // First tool use contains the responses for that tool.
                var toolUseBlock = responseContent.First(t =>
                    t.ToolUse.Name == bedrockToolName);

                Console.WriteLine($"Finished Bedrock Converse request for {details.Name}.");
                var toolOutputs = toolUseBlock.ToolUse.Input.AsDictionary();

                if (toolOutputs.ContainsKey("serviceNames") && toolOutputs["serviceNames"].IsList())
                {
                    details.ServiceNames = string.Join(',',
                        toolOutputs["serviceNames"].AsList().Select(s => s.ToString()));
                }

                if (toolOutputs.ContainsKey("isDeprecated") && toolOutputs["isDeprecated"].IsBool())
                {
                    details.IsDeprecated = toolOutputs["isDeprecated"].AsBool();
                }

                foreach (var cr in config.GenAiCriteria)
                {
                    if (toolOutputs.ContainsKey(cr.Name))
                    {
                        if (cr.Type == "boolean")
                        {
                            int boolVal = 0;
                            if (toolOutputs[cr.Name].IsBool())
                            {
                                boolVal = toolOutputs[cr.Name].AsBool() ? 1 : 0;
                            }

                            details.AddCriterion(cr.Name, cr.Description, cr.Weight,
                                boolVal);
                        }
                        else if (cr.Type == "integer")
                        {
                            var intVal = 0;
                            if (toolOutputs[cr.Name].IsInt())
                            {
                                intVal = toolOutputs[cr.Name].AsInt();
                            }

                            details.AddCriterion(cr.Name, cr.Description, cr.Weight,
                                intVal);
                        }
                        else if (cr.Type == "string")
                        {
                            details.GenAiSummary = toolOutputs[cr.Name].ToString();
                        }
                    }
                }

                return true;
            }
        }
        catch (AmazonBedrockRuntimeException e)
        {
            Console.WriteLine($"Bedrock ERROR: Can't invoke '{modelId}'. Reason: {e.Message}");
        }
        catch (Exception e)
        {
            Console.WriteLine($"ERROR: Failure making the request. Reason: {e.Message}");
        }

        return false;
    }
}