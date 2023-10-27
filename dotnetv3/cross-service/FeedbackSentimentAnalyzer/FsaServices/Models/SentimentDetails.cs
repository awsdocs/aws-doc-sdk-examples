// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

namespace FsaServices.Models;

/// <summary>
/// The details of the analyzed sentiment.
/// </summary>
public class SentimentDetails
{
    /// <summary>
    /// The sentiment type of the analyzed text.
    /// </summary>
    public string sentiment { get; set; } = null!;

    /// <summary>
    /// The language code of the analyzed text.
    /// </summary>
    public string language_code { get; set; } = null!;
}