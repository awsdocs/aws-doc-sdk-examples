// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

namespace FsaServices.Models;

/// <summary>
/// The text and a source language code.
/// </summary>
public class TextWithSourceLanguage
{
    /// <summary>
    /// The extracted text.
    /// </summary>
    public string extracted_text { get; set; } = null!;

    /// <summary>
    /// The source language code.
    /// </summary>
    public string source_language_code { get; set; } = null!;
}