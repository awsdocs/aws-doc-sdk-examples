// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace ListItems
{
    using System;
    using System.Configuration;
    using System.Text;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnet35.ListItemsExample]

    /// <summary>
    /// Lists the items in an Amazon DynamoDB table. The name of the table and
    /// the AWS Region are defined in the application configuration file.
    /// This example was created using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class ListItems
    {
        /// <summary>
        /// Retrieves the configuration information using the Microsoft
        /// ConfigurationManager, initializes the DynamoDB client, retrieves the
        /// items in the table using GetItemsAsync, and finally, displays the
        /// list of items in the table.
        /// </summary>
        public static async Task Main()
        {
            var configfile = "app.config";
            var region = string.Empty;
            var table = string.Empty;

            // Get default AWS Region and table from config file.
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
            }
            else
            {
                Console.WriteLine($"Could not find {configfile}");
                return;
            }

            var empty = false;
            var sb = new StringBuilder("You must supply a non-empty ");

            if (table == string.Empty)
            {
                empty = true;
                sb.Append("table name (-t TABLE), ");
            }

            if (region == string.Empty)
            {
                empty = true;
                sb.Append("region -r (REGION)");
            }

            if (empty)
            {
                Console.WriteLine(sb.ToString());
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var response = await GetItemsAsync(client, table);

            Console.WriteLine($"Found {response.Items.Count} items in {table} table in region {region}\n");

            StringBuilder output;

            foreach (var item in response.Items)
            {
                output = new StringBuilder();

                foreach (string attr in item.Keys)
                {
                    if (item[attr].S != null)
                    {
                        output.Append(attr + ": " + item[attr].S + ", ");
                    }
                    else if (item[attr].N != null)
                    {
                        output.Append(attr + ": " + item[attr].N + ", ");
                    }
                }

                Console.WriteLine(output.ToString());
            }
        }

        /// <summary>
        /// Calls the ScanAsync method of the DynamoDB client object.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="tableName">The name of the table to scan.</param>
        /// <returns>The response from the ScanAsync method.</returns>
        public static async Task<ScanResponse> GetItemsAsync(IAmazonDynamoDB client, string tableName)
        {
            var response = await client.ScanAsync(new ScanRequest
            {
                TableName = tableName,
            });

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnet35.ListItemsExample]
}
