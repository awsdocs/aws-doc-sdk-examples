// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[Bedrock.ConverseTool.dotnetv3.ToolResponse]

namespace ConverseToolScenario;

/// <summary>
/// Response object for the tool results.
/// </summary>
public class ToolResponse
{
    public string ToolUseId { get; set; } = null!;
    public dynamic Content { get; set; } = null!;
}
// snippet-end:[Bedrock.ConverseTool.dotnetv3.ToolResponse]