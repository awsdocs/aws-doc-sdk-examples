// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeTests;

public class ActionTest_InvokeModel : ActionTest_Base
{
    protected string _action;

    public ActionTest_InvokeModel()
    {
        _action = "InvokeModel";
    }

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
    public void RunTest(string model, string? subDir = null)
    {
        var file = getTestFilePath(model, _action, subDir);
        var (exitCode, standardOutput) = runTest(file);

        Assert.Equal(0, exitCode);
        Assert.NotEmpty(standardOutput);
    }
}