// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace PamApi;

/// <summary>
/// The download request from the UI.
/// </summary>
public class DownloadRequest
{
    public string[] labels { get; set; } = null!;
}