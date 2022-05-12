// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace AddItem
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Globalization;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnet35.AddItemExample]

    /// <summary>
    /// Add an item to an Amazon DynamoDB table.
    /// </summary>
    public class AddItem
    {
        public static async Task Main(string[] args)
        {
            var configfile = "app.config";
            var id = string.Empty;
            var keys = string.Empty;
            var values = string.Empty;

            // Get default Region and table from config file
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile,
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            string region;
            string table;
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
                    case "-k":
                        i++;
                        keys = args[i];
                        break;
                    case "-v":
                        i++;
                        values = args[i];
                        break;
                }

                i++;
            }

            if ((keys == string.Empty) || (values == string.Empty) || (id == string.Empty))
            {
                Console.WriteLine("You must supply a comma-separated list of " +
                    "keys (-k \"key 1 ... keyN\"), a comma-separated list of " +
                    "values (-v \"value1 ... valueN\"), and an ID (-i ID)");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var success = await AddItemAsync(client, table, id, keys, values);

            if (success)
            {
                Console.WriteLine($"Added item to {table} in {region}.");
            }
            else
            {
                Console.WriteLine($"Did not add item to {table} in {region}.");
            }
        }

        /// <summary>
        /// Adds a new item to a DynamoDB table.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client.</param>
        /// <param name="table">A DynamoDB table object.</param>
        /// <param name="id">The Id to use when adding the new item to the table.</param>
        /// <param name="keystring">A string representing the keys passed to the
        /// program on the command line.</param>
        /// <param name="valuestring">A string representing the values passed to
        /// the program on the command line.</param>
        /// <returns>A Boolean value representing the success or failure of the
        /// add operation.</returns>
        public static async Task<bool> AddItemAsync(IAmazonDynamoDB client, string table, string id, string keystring, string valuestring)
        {
            // Get individual keys and values.
            string[] keys = keystring.Split(",");
            string[] values = valuestring.Split(",");

            var item = new Dictionary<string, AttributeValue>
            {
                { "ID", new AttributeValue { S = id } },
            };

            for (int i = 0; i < keys.Length; i++)
            {
                /* Customer:
                     Area,Customer_ID,Customer_Name,Customer_Address,Customer_Email
                     Customer_ID is an int; all others are strings

                   Order:
                     Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status
                     Order_ID, Order_Customer, Order_Product are ints
                     Order_Date is a date; all others are strings

                   Product:
                     Area,Product_ID,Product_Description,Product_Quantity,Product_Cost
                     Product_ID, Product_Quantity, and Product_Cost are int; all others are strings
                */

                if ((keys[i] == "Customer_ID") || (keys[i] == "Order_ID") || (keys[i] == "Order_Customer") || (keys[i] == "Order_Product") || (keys[i] == "Product_ID") || (keys[i] == "Product_Quantity") || (keys[i] == "Product_Cost"))
                {
                    item.Add(keys[i], new AttributeValue { N = values[i] });
                }
                else if (keys[i] == "Order_Date")
                {
                    // The datetime format is:
                    // YYYY-MM-DD HH:MM:SS
                    DateTime myDateTime = DateTime.ParseExact(values[i], "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

                    TimeSpan timeSpan = myDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

                    item.Add(keys[i], new AttributeValue { N = ((long)timeSpan.TotalSeconds).ToString() });
                }
                else
                {
                    item.Add(keys[i], new AttributeValue { S = values[i] });
                }
            }

            PutItemRequest request = new PutItemRequest
            {
                TableName = table,
                Item = item,
            };

            var success = false;

            try
            {
                await client.PutItemAsync(request);
                success = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Caught exception adding item to table:");
                Console.WriteLine(e.Message);
            }

            return success;
        }
    }

    // snippet-end:[dynamodb.dotnet35.AddItemExample]
}
