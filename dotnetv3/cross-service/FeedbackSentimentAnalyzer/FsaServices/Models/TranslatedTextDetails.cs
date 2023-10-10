// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

namespace FsaServices.Models;

/// <summary>
/// Model containing translated text details.
/// </summary>
public class TranslatedTextDetails
{
    /// <summary>
    /// The translated text.
    /// </summary>
    public string translated_text { get; set; } = null!;
}