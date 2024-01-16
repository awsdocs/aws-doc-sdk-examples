// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.Lambda.SQSEvents;

namespace SageMakerLambda;

/// <summary>
/// The custom pipeline request object for an Amazon SageMaker pipeline.
/// </summary>
public class PipelineRequest : SQSEvent
{
    /// <summary>
    /// The Role Amazon Resource Name (ARN) parameter.
    /// </summary>
    public string Role { get; set; } = null!;

    /// <summary>
    /// A custom Region, optional parameter.
    /// </summary>
    public string Region { get; set; } = null!;

    /// <summary>
    /// The name of the Vector Enrichment Job.
    /// </summary>
    public string vej_name { get; set; } = null!;

    /// <summary>
    /// The ARN of the Vector Enrichment Job.
    /// </summary>
    public string vej_arn { get; set; } = null!;

    /// <summary>
    /// The input configuration for the Vector Enrichment Job.
    /// </summary>
    public string vej_input_config { get; set; } = null!;

    /// <summary>
    /// The job configuration for the Vector Enrichment Job.
    /// </summary>
    public string vej_config { get; set; } = null!;

    /// <summary>
    /// The export configuration for the Vector Enrichment Job.
    /// </summary>
    public string vej_export_config { get; set; } = null!;

    /// <summary>
    /// The queue URL, optional parameter.
    /// </summary>
    public string queue_url { get; set; } = null!;

}