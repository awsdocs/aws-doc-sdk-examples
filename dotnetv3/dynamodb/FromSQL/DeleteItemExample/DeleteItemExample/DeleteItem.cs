// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DeleteItemExample
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Net;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnet35.DeleteItemExample]

    /// <summary>
    /// Shows how to remove an item from an Amazon DynamoDB table. This example
    /// was created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteItem
    {
        public static async Task Main(string[] args)
        {
            var configfile = "app.config";
            var region = string.Empty;
            var table = string.Empty;
            var partition = string.Empty;
            var sort = string.Empty;

            // Get default Region and table from config file.
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile,
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                region = appSettings.Settings["Region"].Value;
                table = appSettings.Settings["Table"].Value;

                if ((region == string.Empty) || (table == string.Empty))
                {
                    Console.WriteLine($"You must specify a Region and Table value in {configfile}");
                    return;
                }
            }
            else
            {
                Console.WriteLine($"Could not find {configfile}");
                return;
            }

            // Get command line arguments for item to delete.
            int i = 0;
            while (i < args.Length)
            {
                switch (args[i])
                {
                    case "-p":
                        i++;
                        partition = args[i];
                        break;
                    case "-s":
                        i++;
                        sort = args[i];
                        break;
                }

                i++;
            }

            if ((partition == string.Empty) || (sort == string.Empty))
            {
                Console.WriteLine("You must supply a partition key (-p KEY) and sort key (-s KEY)");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var resp = await RemoveItemAsync(client, table, partition, sort);

            if (resp.HttpStatusCode == HttpStatusCode.OK)
            {
                Console.WriteLine($"Removed item from {table} table in {region} region");
            }
        }

        /// <summary>
        /// Delete item from a DynamoDB table.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client.</param>
        /// <param name="table">The table from which the item will be removed.</param>
        /// <param name="id">The Id of the item to remove.</param>
        /// <param name="area">A string representing the value of the Area attribute.</param>
        /// <returns>A DeleteItemResponse object representing the response from the
        /// DeleteItemAsync call.</returns>
        public static async Task<DeleteItemResponse> RemoveItemAsync(
          IAmazonDynamoDB client,
          string table,
          string id,
          string area)
        {
            var request = new DeleteItemRequest
            {
                TableName = table,
                Key = new Dictionary<string, AttributeValue>()
        {
          {
            "ID",
            new AttributeValue { S = id }
          },
          {
            "Area",
            new AttributeValue { S = area }
          },
        },
            };

            var response = await client.DeleteItemAsync(request);

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnet35.DeleteItemExample]
}
