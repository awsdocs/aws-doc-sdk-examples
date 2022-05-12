// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[dynamodb.dotnet35.GetLowProductStockExample]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Threading.Tasks;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace GetLowProductStock
{
    public class GetLowProductStock
    {
        // Get the products with fewer than minimum items in the warehouse
        // DynamoDB equivalent of:
        //   select* from Products where Product_Quantity < '100'
        public static async Task<ScanResponse> GetLowStockAsync(IAmazonDynamoDB client, string table, string minimum)
        {
            var response = await client.ScanAsync(new ScanRequest
            {
                TableName = table,
                ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":val", new AttributeValue { N = minimum }}
                },
                FilterExpression = "Product_Quantity < :val",
                ProjectionExpression = "Product_ID, Product_Description, Product_Quantity, Product_Cost"
            });

            return response;
        }

        static void Main()
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            string minimum = "";

            // Get default values from config file
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
                minimum = appSettings.Settings["Minimum"].Value;
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            // Make sure we have a table, Region, and minimum quantity
            if ((region == "") || (table == "") || (minimum == ""))
            {
                Console.WriteLine("You must specify Region, Table, and Minimum values in " + configfile);
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var response = GetLowStockAsync(client, table, minimum);

            // To adjust date/time value
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

            foreach (var item in response.Result.Items)
            {
                foreach (string attr in item.Keys)
                {
                    if (item[attr].S != null)
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

                            Console.WriteLine(attr + ": " + theDate.ToLongDateString());
                        }
                        else
                        {
                            Console.WriteLine(attr + ": " + item[attr].N);
                        }
                    }
                }

                Console.WriteLine("");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.GetLowProductStockExample]
