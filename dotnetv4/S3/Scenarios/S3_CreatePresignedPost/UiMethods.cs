// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Scenarios;

/// <summary>
/// UI helper methods for the S3 presigned POST scenario.
/// </summary>
public class UiMethods
{
    public readonly string SepBar = new string('-', 88);

    /// <summary>
    /// Show information about the scenario.
    /// </summary>
    public void DisplayOverview()
    {
        DisplayTitle("Welcome to the Amazon S3 Presigned POST URL Scenario");

        Console.WriteLine("This example application does the following:");
        Console.WriteLine("\t 1. Creates an S3 bucket with a unique name");
        Console.WriteLine("\t 2. Creates a presigned POST URL for the bucket");
        Console.WriteLine("\t 3. Displays the URL and form fields needed for browser uploads");
        Console.WriteLine("\t 4. Uploads a test file using the presigned POST URL");
        Console.WriteLine("\t 5. Verifies the file was successfully uploaded to S3");
        Console.WriteLine("\t 6. Cleans up the resources (bucket and test file)");
    }

    /// <summary>
    /// Display a message and wait until the user presses enter if in interactive mode.
    /// </summary>
    public void PressEnter(bool interactive)
    {
        Console.Write("\nPlease press <Enter> to continue. ");
        if (interactive)
            _ = Console.ReadLine();
    }

    /// <summary>
    /// Display a line of hyphens, the text of the title and another
    /// line of hyphens.
    /// </summary>
    /// <param name="strTitle">The string to be displayed.</param>
    public void DisplayTitle(string strTitle)
    {
        Console.WriteLine(SepBar);
        Console.WriteLine(strTitle);
        Console.WriteLine(SepBar);
    }
}
