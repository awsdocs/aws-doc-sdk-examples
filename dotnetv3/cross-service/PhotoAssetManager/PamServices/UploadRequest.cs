// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace PamServices;

/// <summary>
/// The request object for an upload.
/// </summary>
public class UploadRequest
{
    public string file_name { get; set; } = null!;
}