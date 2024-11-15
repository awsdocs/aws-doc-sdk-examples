// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeTests;

public class ActionTest_Converse
{
    [Theory, Trait("Category", "Integration")]
    [InlineData(typeof(Mistral.Converse))]
    [InlineData(typeof(MetaLlama.Converse))]
    [InlineData(typeof(CohereCommand.Converse))]
    [InlineData(typeof(AnthropicClaude.Converse))]
    [InlineData(typeof(AmazonTitanText.Converse))]
    [InlineData(typeof(Ai21LabsJurassic2.Converse))]
    public void ConverseDoesNotThrow(Type type)
    {
        var entryPoint = type.Assembly.EntryPoint!;
        var exception = Record.Exception(() => entryPoint.Invoke(null, [Array.Empty<string>()]));
        Assert.Null(exception);
    }
}