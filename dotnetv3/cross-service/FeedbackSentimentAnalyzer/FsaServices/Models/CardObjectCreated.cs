// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

namespace FsaServices.Models;

/// <summary>
/// Model for card object created event.
/// </summary>
public class CardObjectCreated
{
    /// <summary>
    /// The region for the card object.
    /// </summary>
    public string Region { get; set; } = null!;

    /// <summary>
    /// The bucket for the card object.
    /// </summary>
    public string Bucket { get; set; } = null!;

    /// <summary>
    /// The object key for the card.
    /// </summary>
    public string Object { get; set; } = null!;
}