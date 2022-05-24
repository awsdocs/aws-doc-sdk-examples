// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace GetOrdersExample
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnetv3.GetOrdersForProduct]

    /// <summary>
    /// Shows how to scan an Amazon DynamoDB table looking for information.
    /// The example was created using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class GetOrders
    {
        public static async Task Main()
        {
            var configfile = "app.config";
            var region = string.Empty;
            var table = string.Empty;
            var id = "3";

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
                id = appSettings.Settings["ProductID"].Value;

                if (region == string.Empty || table == string.Empty || id == string.Empty)
                {
                    Console.WriteLine("You must specify Region, Table, and ProductID values in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine($"Could not find {configfile}");
                return;
            }

            try
            {
                var val = int.Parse(id);

                if (val < 1)
                {
                    Console.WriteLine("The product ID must be > 0");
                    return;
                }
            }
            catch (FormatException)
            {
                Console.WriteLine($"{id} is not an integer.");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var response = await GetProductOrdersAsync(client, table, id);

            // To adjust date/time value.
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

            // Display the orders returned from table scan.
            foreach (var item in response.Items)
            {
                foreach (string attr in item.Keys)
                {
                    if (item[attr].S is not null)
                    {
                        Console.WriteLine(attr + ": " + item[attr].S);
                    }
                    else if (item[attr].N != null)
                    {
                        // If the attribute contains the string "date", process it differently
                        if (attr.ToLower().Contains("date"))
                        {
                            long span = long.Parse(item[attr].N);
                            DateTime theDate = epoch.AddSeconds(span);

                            Console.WriteLine($"{attr}: {theDate.ToLongDateString()}");
                        }
                        else
                        {
                            Console.WriteLine($"{attr}: {item[attr].N}");
                        }
                    }
                }

                Console.WriteLine(string.Empty);
            }
        }

        /// <summary>
        /// Get the orders for product with ID productId DynamoDB equivalent of
        /// the SQL statement:
        ///   SELECT * FROM Orders WHERE Order_Product = '3'
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="table">The name of the table table to search.</param>
        /// <param name="productId">The Id of the product for which to retrieve
        /// orders.</param>
        /// <returns>A ScanResponse object with the results of the call to the
        /// ScanAsync call.</returns>
        public static async Task<ScanResponse> GetProductOrdersAsync(IAmazonDynamoDB client, string table, string productId)
        {
            var response = await client.ScanAsync(new ScanRequest
            {
                TableName = table,
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
        {
            { ":val", new AttributeValue { N = productId } },
        },
                FilterExpression = "Order_Product = :val",
                ProjectionExpression = "Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status",
            });

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnetv3.GetOrdersForProduct]
}
