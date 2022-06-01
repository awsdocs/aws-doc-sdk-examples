// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace ListTables
{
    using System;
    using System.Configuration;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnetv3.ListTablesExample]

    /// <summary>
    /// Lists the Amazon DynamoDB tables in a specific AWS Region. This
    /// example was created using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class ListTables
    {
        /// <summary>
        /// Retrieves the Amazon DynamoDB configuration, calls for a list
        /// of tables in the region specified in the configuration, and then
        /// displays the list on the console.
        /// </summary>
        public static async Task Main()
        {
            var region = string.Empty;
            var configfile = "app.config";

            // Get default Region from config file.
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile,
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                region = appSettings.Settings["Region"].Value;

                if (region == string.Empty)
                {
                    Console.WriteLine("You must set a Region value in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            // Retrieve the list of DynamoDB tables.
            var response = await ShowTablesAsync(client);

            // Display the list on the console.
            Console.WriteLine($"Found {response.TableNames.Count.ToString()} tables in {region}region:");

            foreach (var table in response.TableNames)
            {
                Console.WriteLine($"\t{table}");
            }
        }

        /// <summary>
        /// Retrieves the list of DynamoDB tables that exist in the AWS Region
        /// of the DynamoDB client.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <returns>A ListTableResponse object that includes the list of tables.</returns>
        public static async Task<ListTablesResponse> ShowTablesAsync(IAmazonDynamoDB client)
        {
            var response = await client.ListTablesAsync(new ListTablesRequest());

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnetv3.ListTablesExample]
}
