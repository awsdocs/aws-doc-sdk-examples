// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0
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
}