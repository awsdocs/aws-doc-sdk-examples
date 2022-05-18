// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
namespace UpdateItem
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnet35.UpdateItemExample]

    /// <summary>
    /// Shows how to update an item in an Amazon DynamoDB table. This example
    /// was created using AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class UpdateItem
    {
        /// <summary>
        /// Retrieves configuration information, parses the application command
        /// line, and then updates the item specified.
        /// </summary>
        /// <param name="args">Command line arguments.</param>
        public static async Task Main(string[] args)
        {
            var configfile = "app.config";
            var region = string.Empty;
            var table = string.Empty;
            var id = string.Empty;
            var status = string.Empty;

            // Get default Region and table from config file
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
                    Console.WriteLine("You must specify a Region and Table value in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            int i = 0;
            while (i < args.Length)
            {
                switch (args[i])
                {
                    case "-i":
                        i++;
                        id = args[i];
                        break;
                    case "-s":
                        i++;
                        status = args[i];
                        break;
                }

                i++;
            }

            if ((status == string.Empty) || (id == string.Empty) || ((status != "backordered") && (status != "delivered") && (status != "delivering") && (status != "pending")))
            {
                Console.WriteLine("You must supply a partition number (-i ID), and status value (-s STATUS) of backordered, delivered, delivering, or pending");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            // Silenty ignores issue if id does not identify an order
            var response = await ModifyOrderStatusAsync(client, table, id, status);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Successfully updated item in " + table + " in region " + region);
            }
            else
            {
                Console.WriteLine("Could not update order status");
            }
        }

        /// <summary>
        /// Updates the status of the specified item in the specified DynamoDB table.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="table">The DynamoDB table containing the item to be updated.</param>
        /// <param name="id">The Id of the item to be udpated.</param>
        /// <param name="status">The status value to set.</param>
        /// <returns>An UpdateItemResponse object which contains the results of the
        /// update operation.</returns>
        public static async Task<UpdateItemResponse> ModifyOrderStatusAsync(IAmazonDynamoDB client, string table, string id, string status)
        {
            var request = new UpdateItemRequest
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
            new AttributeValue { S = "Order" }
          },
        },
                ExpressionAttributeNames = new Dictionary<string, string>()
        {
          { "#S", "Order_Status" },
        },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>()
        {
          {
            ":s",
            new AttributeValue { S = status }
          },
        },
                ReturnValues = "UPDATED_NEW",
                UpdateExpression = "SET #S = :s",
            };

            var response = await client.UpdateItemAsync(request);

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnet35.UpdateItemExample]
}
