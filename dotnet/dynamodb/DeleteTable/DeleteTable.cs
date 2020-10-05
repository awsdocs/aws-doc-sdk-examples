// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.DeleteTable]
using System;
using System.Configuration;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class DeleteTable
    {
        public static async Task<DeleteTableResponse> RemoveTableAsync(IAmazonDynamoDB client, string table)
        {
            var response = await client.DeleteTableAsync(new DeleteTableRequest
            {
                TableName = table
            });

            return response;
        }
                
        static void Main(string[] args)
        {
            var configfile = "app.config";
            var region = "";
            var table = "";

            // Get default Region and table from config file
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                region = appSettings.Settings["Region"].Value;
                table = appSettings.Settings["Table"].Value;

                if ((region == "") || (table == ""))
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

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            Task<DeleteTableResponse> response = RemoveTableAsync(client, table);

            if (response.Result.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("Removed " + table + " table in " + region + " region");
            }
            else
            {
                Console.WriteLine("Could not remove " + table + " table");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.DeleteTable]
