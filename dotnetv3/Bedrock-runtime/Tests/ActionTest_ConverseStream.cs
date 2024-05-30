// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeTests;

public class ActionTest_ConverseStream : ActionTest_Base
{
    protected string _action;

    public ActionTest_ConverseStream()
    {
        _action = "ConverseStream";
    }

    [Theory]
    [InlineData("AmazonTitanText")]
    [InlineData("AnthropicClaude")]
    [InlineData("CohereCommand")]
    [InlineData("MetaLlama")]
    [InlineData("Mistral")]
    [Trait("Category", "Integration")]
    public void RunTest(string model)
    {
        var file = getTestFilePath(model, _action);
        var (exitCode, standardOutput) = runTest(file);

        Assert.Equal(0, exitCode);
        Assert.NotEmpty(standardOutput);
    }
}