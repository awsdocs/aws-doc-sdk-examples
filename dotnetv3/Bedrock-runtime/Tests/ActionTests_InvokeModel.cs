// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeTests;

public class ActionTest_InvokeModel
{
    [Theory, Trait("Category", "Integration")]
    [InlineData(typeof(Mistral.InvokeModel))]
    [InlineData(typeof(MetaLlama2.InvokeModel))]
    [InlineData(typeof(MetaLlama3.InvokeModel))]
    [InlineData(typeof(CohereCommand.InvokeModel))]
    [InlineData(typeof(CohereCommandR.InvokeModel))]
    [InlineData(typeof(AnthropicClaude.InvokeModel))]
    [InlineData(typeof(AmazonTitanText.InvokeModel))]
    [InlineData(typeof(Ai21LabsJurassic2.InvokeModel))]
    public void InvokeModelDoesNotThrow(Type type)
    {
        var entryPoint = type.Assembly.EntryPoint!;
        var exception = Record.Exception(() => entryPoint.Invoke(null, [Array.Empty<string>()]));
        Assert.Null(exception);
    }
}