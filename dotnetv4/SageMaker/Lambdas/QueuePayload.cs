// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace SageMakerLambda;

/// <summary>
/// The queue payload object.
/// </summary>
public class QueuePayload
{
    /// <summary>
    /// The payload job token.
    /// </summary>
    public string token { get; set; } = null!;

    /// <summary>
    /// The Amazon Resource Name (ARN) of the pipeline run.
    /// </summary>
    public string pipelineExecutionArn { get; set; } = null!;

    /// <summary>
    /// The status of the job.
    /// </summary>
    public string status { get; set; } = null!;

    /// <summary>
    /// A dictionary of payload arguments.
    /// </summary>
    public Dictionary<string, string> arguments { get; set; } = null!;
}