// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[dynamodb.dotnetv3.GetOrdersForProductGSIExample]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Threading.Tasks;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace GetOrdersForProductGSI
{
    public class GetOrdersForProductGsi
    {
        // Get the orders for product with ID productId
        // DynamoDB equivalent of:
        //   select* from Orders where Order_Product = '3'
        public static async Task<QueryResponse> GetProductOrdersAsync(IAmazonDynamoDB client, string table, string index, string productId)
        {
            var response = await client.QueryAsync(new QueryRequest
            {
                TableName = table,
                IndexName = index,
                KeyConditionExpression = "Area = :v_area and Order_Product = :v_product",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":v_area", new AttributeValue { S =  "Order" }},
                    {":v_product", new AttributeValue { N =  productId }}
                },
            });

            return response;
        }

        static void Main()
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            var index = "";
            var id = "3";

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
                index = appSettings.Settings["Index"].Value;
                id = appSettings.Settings["ProductID"].Value;

                if ((region == "") || (table == "") || (index == "") || (id == ""))
                {
                    Console.WriteLine("You must specify Region, Table, Index, and ProductID values in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            int val;

            try
            {
                val = int.Parse(id);

                if (val < 1)
                {
                    Console.WriteLine("The product ID must be > 0");
                    return;
                }
            }
            catch (FormatException)
            {
                Console.WriteLine(id + " is not an integer");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var response = GetProductOrdersAsync(client, table, index, id);

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
// snippet-end:[dynamodb.dotnetv3.GetOrdersForProductGSIExample]
