// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
using System.Text;
using BedrockRuntimeActions;

namespace BedrockRuntimeTests;

public class TextToTextAsyncTest
{
    /// <summary>
    /// InvokeClaudeAsync result should not be null or empty
    /// </summary>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task InvokeClaudeAsync_ShouldNotBeNullOrEmpty()
    {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = await InvokeModelAsync.InvokeClaudeAsync(prompt);
        Assert.NotNull(generatedText);
        Assert.NotEmpty(generatedText);
    }

    /// <summary>
    /// InvokeJurassic2Async result should not be null or empty
    /// </summary>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task InvokeJurassic2Async_ShouldNotBeNullOrEmpty()
    {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = await InvokeModelAsync.InvokeJurassic2Async(prompt);
        Assert.NotNull(generatedText);
        Assert.NotEmpty(generatedText);
    }

    /// <summary>
    /// InvokeLlama2Async result should not be null or empty
    /// </summary>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task InvokeLlama2Async_ShouldNotBeNullOrEmpty()
    {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = await InvokeModelAsync.InvokeLlama2Async(prompt);
        Assert.NotNull(generatedText);
        Assert.NotEmpty(generatedText);
    }

    /// <summary>
    /// InvokeTitanTextG1Async result should not be null or empty
    /// </summary>
    [Fact]
    [Order(4)]
    [Trait("Category", "Integration")]
    public async Task InvokeTitanTextG1Async_ShouldNotBeNullOrEmpty()
    {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedText = await InvokeModelAsync.InvokeTitanTextG1Async(prompt);
        Assert.NotNull(generatedText);
        Assert.NotEmpty(generatedText);
    }

    /// <summary>
    /// InvokeClaudeWithResponseStreamAsync result should not be empty
    /// </summary>
    [Fact]
    [Order(5)]
    [Trait("Category", "Integration")]
    public async Task InvokeClaudeWithResponseStreamAsync_ShouldNotBeEmpty()
    {
        var prompt = "In one sentence, what is a large-language model?";
        var generatedTextBuilder = new StringBuilder();
        await foreach (var completionChunk in InvokeModelAsync.InvokeClaudeWithResponseStreamAsync(prompt))
        {
            generatedTextBuilder.Append(completionChunk);
        }
        var generatedText = generatedTextBuilder.ToString();
        Assert.NotEmpty(generatedText);
    }
}