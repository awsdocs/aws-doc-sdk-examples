// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using FluentAssertions;

namespace BedrockRuntimeTests;

public class ActionTest_InvokeModel : ActionTest_Base
{
    const string ACTION = "InvokeModel";

    [Theory]
    [InlineData("Ai21LabsJurassic2")]
    [InlineData("AmazonTitanText")]
    [InlineData("AnthropicClaude")]
    [InlineData("CohereCommand", "Command")]
    [InlineData("CohereCommand", "Command_R")]
    [InlineData("MetaLlama", "Llama2")]
    [InlineData("MetaLlama", "Llama3")]
    [InlineData("Mistral")]
    [Trait("Category", "Integration")]
    public async void RunTest(string model, string? subDir = null)
    {
        var script = getPath(model, ACTION, subDir);
        var output = await test(script);
        output.Should().NotBeNullOrWhiteSpace("The output should contain text.");
    }
}