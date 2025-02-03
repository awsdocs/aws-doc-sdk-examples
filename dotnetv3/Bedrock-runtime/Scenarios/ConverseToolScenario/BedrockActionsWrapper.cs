// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using System.IO;
using System.Net.Http.Headers;
using System.Net;
using Amazon.BedrockRuntime;
using Amazon.BedrockRuntime.Model;
using Microsoft.Extensions.Logging;

namespace ConverseToolScenario;

/// <summary>
/// Wrapper class for interacting with the Amazon Bedrock Converse API.
/// </summary>
public class BedrockActionsWrapper
{
    private readonly IAmazonBedrockRuntime _bedrockClient;
    private readonly ILogger<BedrockActionsWrapper> _logger;

    /// <summary>
    /// Initializes a new instance of the <see cref="BedrockActionsWrapper"/> class.
    /// </summary>
    /// <param name="bedrockClient">The Bedrock Converse API client.</param>
    /// <param name="logger">The logger instance.</param>
    public BedrockActionsWrapper(IAmazonBedrockRuntime bedrockClient, ILogger<BedrockActionsWrapper> logger)
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
        catch (AmazonBedrockRuntimeException ex)
        {
            _logger.LogError(ex, "Error occurred while sending Converse request.");
            return false;
        }
    }

    public async Task<bool> ProcessMessages()
    {
        try
        {
            List<string> messages = new List<string>();
            // todo: populate messages list.
            foreach (var messageThread in messages)
            {
                // create a repo object
                    var repoDetails = new RepoDetails()
                    {
                        Name = repository.Name,
                        Language = repository.Language,
                        Url = repository.HtmlUrl,
                        LastModified = repository.UpdatedAt.Date,
                        Summary = repository.Description,
                        OpenIssuesCount = repository.OpenIssuesCount
                    };
                    foreach (var repoCriteria in searchConfig.RepoCriteria)
                    {
                        var value = repository.GetType().GetProperty(repoCriteria.DataField).GetValue(repository);
                        if (value is int)
                        {
                            repoDetails.AddCriterion(repoCriteria.Name,
                                repoCriteria.Description, repoCriteria.Weight, (int)value);
                        }
                    }

                    // Try to get the content of the README file, to be used to generate tool results with Bedrock.
                    Console.WriteLine($"Fetching README contents for repository {repoDetails.Name}.");
                    try
                    {
                        var readme =
                            await client.Repository.Content.GetReadme(repository.Id);
                        var readmeContent = readme.Content;

                        byte[] byteArray =
                            System.Text.Encoding.ASCII.GetBytes(readmeContent);
                        MemoryStream stream = new MemoryStream(byteArray);
                        Thread.Sleep(200);
                        var summaryResponseTask =
                            bedrockService.MakeRepoToolRequest(stream, repoDetails, searchConfig);
                        taskList.Add(summaryResponseTask);

                        var lastcommit =
                            await client.Repository.Commit.Get(repository.Id, "HEAD");

                        Console.WriteLine($"last commit for {repository.Name} at {lastcommit.Commit.Committer.Date.Date}");
                        repoDetails.LastModified = lastcommit.Commit.Committer.Date.Date;
                        if (repoDetails.LastModified >
                            DateTime.Today.AddYears(-yearsToInclude))
                        {
                            repoDetailsList.Add(repoDetails);
                        }
                        else
                        {
                            noNewCommits++;
                        }
                    }
                    catch (Exception ex)
                    {
                        noReadmeCount++;
                        Console.WriteLine(
                            $"unable to get readme for {repoDetails.Name}, {ex.Message}");
                    }
                }
                else
                {
                    notSdkLanguageCount++;
                }
            }

            var summaryResults = await Task.WhenAll(taskList);
            Console.WriteLine($"AI results returned true: {summaryResults.Count(s => s)}.");
        }
}