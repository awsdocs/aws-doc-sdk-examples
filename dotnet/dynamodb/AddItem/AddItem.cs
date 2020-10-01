// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.AddItem]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Globalization;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class AddItem
    {
        public static async Task<bool> AddItemAsync(IAmazonDynamoDB client, string table, string id, string keystring, string valuestring)
        {
            // Get individual keys and values
            string[] keys = keystring.Split(",");
            string[] values = valuestring.Split(",");

            var item = new Dictionary<string, AttributeValue>
            {
                { "ID", new AttributeValue { S = id } }
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
                    DateTime MyDateTime = DateTime.ParseExact(values[i], "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

                    TimeSpan timeSpan = MyDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

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
                Item = item
            };

            var response = false;

            try
            {
                await client.PutItemAsync(request);
                response = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Caught exception adding item to table:");
                Console.WriteLine(e.Message);
            }

            return response;

        }

        static void Main(string[] args)
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            var id = "";
            var keys = "";
            var values = "";

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
                    default:
                        break;
                }

                i++;
            }

            if ((keys == "") || (values == "") || (id == ""))
            {
                Console.WriteLine("You must supply a comma-separated list of keys (-k \"key 1 ... keyN\") a comma-separated list of values (-v \"value1 ... valueN\") and an ID (-i ID)");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var result = AddItemAsync(client, table, id, keys, values);

            if (result.Result)
            {
                Console.WriteLine("Added item to " + table + " in " + region);
            }
            else
            {
                Console.WriteLine("Did not add item to " + table + " in " + region);
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.AddItem]
