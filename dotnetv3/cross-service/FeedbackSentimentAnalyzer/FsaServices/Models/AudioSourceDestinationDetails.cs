// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

namespace FsaServices.Models;

/// <summary>
/// Model class for audio source and destination details.
/// </summary>
public class AudioSourceDestinationDetails
{
    /// <summary>
    /// The bucket to use for output.
    /// </summary>
    public string Bucket { get; set; } = null!;

    /// <summary>
    /// The text to synthesize.
    /// </summary>
    public string SourceText { get; set; } = null!;

    /// <summary>
    /// The output object key.
    /// </summary>
    public string ObjectKey { get; set; } = null!;
}