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
    public void ConverseDoesNotThrow(Type type)
    {
        Thread.Sleep(5000);
        var entryPoint = type.Assembly.EntryPoint!;
        var exception = Record.Exception(() => entryPoint.Invoke(null, [Array.Empty<string>()]));
        Assert.Null(exception);
    }
}