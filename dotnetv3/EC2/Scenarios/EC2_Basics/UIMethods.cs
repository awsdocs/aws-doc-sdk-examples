// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Ec2_Basics;

public class UiMethods
{
    public readonly string SepBar = new string('-', Console.WindowWidth);

    /// <summary>
    /// Show information about the scenario.
    /// </summary>
    public void DisplayOverview()
    {
        Console.Clear();
        DisplayTitle("Welcome to the Amazon Elastic Compute Cloud (Amazon EC2) get started with instances demo.");

        Console.WriteLine("This example application does the following:");
        Console.WriteLine("\t 1. Creates an RSA key pair.");
        Console.WriteLine("\t 2. Saves the key pair to a file in a temporary folder.");
        Console.WriteLine("\t 3. Creates a security group with an inbound rule allowing this computer to SSH to the security group.");
        Console.WriteLine("\t 4. Displays information for the security group.");
        Console.WriteLine("\t 5. Gets a list of Amazon Linux 2 Amazon Machine Images (AMIs)\n\t\tand selects one.");
        Console.WriteLine("\t 6. Gets a list of instance types and selects one.");
        Console.WriteLine("\t 8. Creates an EC2 instance.");
        Console.WriteLine("\t 9. Waits for the instance to be ready and displays its information.");
        Console.WriteLine("\t10. Displays the SSH connection information.");
        Console.WriteLine("\t11. Stops the instance.");
        Console.WriteLine("\t12. Starts the instance again.");
        Console.WriteLine("\t13. Displays the SSH connection information again to show that it has changed.");
        Console.WriteLine("\t14. Allocates an Elastic IP and associates it to the instance.");
        Console.WriteLine("\t15. Displays the SSH connection information for the instance.");
        Console.WriteLine("\t16. Disassociates the Elastic IP and deletes it.");
        Console.WriteLine("\t17. Terminates the instance and waits for termination to be complete.");
        Console.WriteLine("\t18. Deletes the security group.");
        Console.WriteLine("\t19. Deletes the key pair.");
    }

    /// <summary>
    /// Display a message and wait until the user presses enter.
    /// </summary>
    public void PressEnter()
    {
        Console.Write("\nPlease press <Enter> to continue. ");
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
