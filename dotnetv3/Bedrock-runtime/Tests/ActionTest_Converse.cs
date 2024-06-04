// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using FluentAssertions;

namespace BedrockRuntimeTests;

public class ActionTest_Converse : ActionTest_Base
{
    private const string ACTION = "Converse";

    [Theory]
    [InlineData("Ai21LabsJurassic2")]
    [InlineData("AmazonTitanText")]
    [InlineData("AnthropicClaude")]
    [InlineData("CohereCommand")]
    [InlineData("MetaLlama")]
    [InlineData("Mistral")]
    [Trait("Category", "Integration")]
    public async void RunTest(string model)
    {
        var script = getPath(model, ACTION);
        var output = await test(script);
        output.Should().NotBeNullOrWhiteSpace("The output should contain text.");
    }
}