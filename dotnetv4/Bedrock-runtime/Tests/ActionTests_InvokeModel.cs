﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

namespace BedrockRuntimeTests;

public class ActionTest_InvokeModel
{
    [Theory, Trait("Category", "Integration")]
    [InlineData(typeof(AmazonTitanText.InvokeModel))]
    [InlineData(typeof(Mistral.InvokeModel))]
    [InlineData(typeof(MetaLlama3.InvokeModel))]
    [InlineData(typeof(CohereCommand.InvokeModel))]
    [InlineData(typeof(CohereCommandR.InvokeModel))]
    [InlineData(typeof(AnthropicClaude.InvokeModel))]

    public void InvokeModelDoesNotThrow(Type type)
    {
        Thread.Sleep(5000);
        var entryPoint = type.Assembly.EntryPoint!;
        var exception = Record.Exception(() => entryPoint.Invoke(null, [Array.Empty<string>()]));
        Assert.Null(exception);
    }
}