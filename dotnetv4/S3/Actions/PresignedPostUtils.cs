// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

/// <summary>
/// Utility methods for working with presigned POST URLs.
/// </summary>
public static class PresignedPostUtils
{
    /// <summary>
    /// Display the fields from a presigned POST response.
    /// </summary>
    /// <param name="response">The CreatePresignedPostResponse to display.</param>
    public static void DisplayPresignedPostFields(CreatePresignedPostResponse response)
    {
        Console.WriteLine($"Presigned POST URL: {response.Url}");
        Console.WriteLine("Form fields to include:");
        
        foreach (var field in response.Fields)
        {
            Console.WriteLine($"  {field.Key}: {field.Value}");
        }
    }
}
