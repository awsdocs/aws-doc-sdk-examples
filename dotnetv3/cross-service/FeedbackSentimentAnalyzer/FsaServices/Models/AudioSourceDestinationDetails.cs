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
    public string bucket { get; set; } = null!;

    /// <summary>
    /// The translated text to synthesize.
    /// </summary>
    public string translated_text { get; set; } = null!;

    /// <summary>
    /// The output object key.
    /// </summary>
    public string Object { get; set; } = null!;
}