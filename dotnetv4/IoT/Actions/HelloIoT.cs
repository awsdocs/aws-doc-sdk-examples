// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.IoT;
using Amazon.IoT.Model;

namespace IoTActions;

// snippet-start:[iot.dotnetv4.Hello]
/// <summary>
/// Hello AWS IoT example.
/// </summary>
public class HelloIoT
{
    /// <summary>
    /// Main method to run the Hello IoT example.
    /// </summary>
    /// <param name="args">Command line arguments.</param>
    /// <returns>A Task object.</returns>
    public static async Task Main(string[] args)
    {
        var iotClient = new AmazonIoTClient();

        try
        {
            Console.WriteLine("Hello AWS IoT! Let's list a few of your IoT Things:");
            Console.WriteLine(new string('-', 80));

            var request = new ListThingsRequest
            {
                MaxResults = 10
            };

            var response = await iotClient.ListThingsAsync(request);

            if (response.Things is { Count: > 0 })
            {
                Console.WriteLine($"Found {response.Things.Count} IoT Things:");
                foreach (var thing in response.Things)
                {
                    Console.WriteLine($"- Thing Name: {thing.ThingName}");
                    Console.WriteLine($"  Thing ARN: {thing.ThingArn}");
                    Console.WriteLine($"  Thing Type: {thing.ThingTypeName ?? "No type specified"}");
                    Console.WriteLine($"  Version: {thing.Version}");

                    if (thing.Attributes?.Count > 0)
                    {
                        Console.WriteLine("  Attributes:");
                        foreach (var attr in thing.Attributes)
                        {
                            Console.WriteLine($"    {attr.Key}: {attr.Value}");
                        }
                    }
                    Console.WriteLine();
                }
            }
            else
            {
                Console.WriteLine("No IoT Things found in your account.");
                Console.WriteLine("You can create IoT Things using the IoT Basics scenario example.");
            }

            Console.WriteLine("Hello IoT completed successfully.");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error: {ex.Message}");
        }
        finally
        {
            iotClient.Dispose();
        }
    }
}
// snippet-end:[iot.dotnetv4.Hello]