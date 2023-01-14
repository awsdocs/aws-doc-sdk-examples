// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace KeyspacesBasics;

/// <summary>
/// Some useful methods to make screen display easier.
/// </summary>
public class UiMethods
{
    public readonly string SepBar = new string('-', Console.WindowWidth);

    /// <summary>
    /// Show information about the scenario.
    /// </summary>
    public void DisplayOverview()
    {
        Console.Clear();
        DisplayTitle("Welcome to the Amazon Keyspaces (for Apache Cassandra) Demo");

        Console.WriteLine("This example application will do the following:");
        Console.WriteLine("\t 1. Create a keyspace.");
        Console.WriteLine("\t 2. List existing keyspaces.");
        Console.WriteLine("\t 3. Create a table to store movie data.");
        Console.WriteLine("\t 4. Display the table's schema.");
        Console.WriteLine("\t 5. List all tables in the Amazon keyspace.");
        Console.WriteLine("\t 6. Perform some simple CRUD operations on the table.");
        Console.WriteLine("\t 7. Update the table's schema.");
        Console.WriteLine("\t 8. Show the new schema.");
        Console.WriteLine("\t 9. Update some of the records in the table.");
        Console.WriteLine("\t10. List the modified records.");
        Console.WriteLine("\t11. Offer the user the opportunity to restore the original schema.");
        Console.WriteLine("\t12. Delete the resources used by the demo.");
    }

    /// <summary>
    /// Display a message and wait until the user presses enter.
    /// </summary>
    public void PressEnter()
    {
        Console.Write("\nPlease press <Enter> to continue.");
        _ = Console.ReadLine();
    }

    /// <summary>
    /// Pad a string with spaces to center it on the console display.
    /// </summary>
    /// <param name="strToCenter"></param>
    /// <returns></returns>
    public string CenterString(string strToCenter)
    {
        var padAmount = (Console.WindowWidth - strToCenter.Length) / 2;
        var leftPad = new string(' ', padAmount);
        return $"{leftPad}{strToCenter}";
    }

    /// <summary>
    /// Display a line of hyphens, the centered text of the title and another
    /// line of hyphens.
    /// </summary>
    /// <param name="strTitle">The string to be displayed.</param>
    public void DisplayTitle(string strTitle)
    {
        Console.WriteLine(SepBar);
        Console.WriteLine(CenterString(strTitle));
        Console.WriteLine(SepBar);
    }
}
