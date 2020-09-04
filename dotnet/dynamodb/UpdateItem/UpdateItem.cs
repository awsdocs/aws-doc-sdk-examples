// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.dotnet35.UpdateItem]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Globalization;
using System.Net.Sockets;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    // DO NOT CHANGE THE TABLE ENTRY IN app.config!!!
    [DynamoDBTable("CustomersOrdersProducts")]
    public class Entry
    {
        [DynamoDBHashKey] // Partition key
        public string ID
        {
            get; set;
        }
        [DynamoDBRangeKey] // Sort key
        public string Area
        {
            get; set;
        }
        [DynamoDBProperty]
        public int Order_ID
        {
            get; set;
        }
        [DynamoDBProperty]
        public int Order_Customer
        {
            get; set;
        }
        [DynamoDBProperty]
        public int Order_Product
        {
            get; set;
        }
        [DynamoDBProperty]
        public long Order_Date
        {
            get; set;
        }
        [DynamoDBProperty]
        public string Order_Status
        {
            get; set;
        }
    }

    class UpdateItem
    {
        static async Task<UpdateItemResponse> ModifyOrderStatusAsync(IAmazonDynamoDB client, string table, string id, string status)
        {
            var request = new UpdateItemRequest
            {
                TableName = table,
                Key = new Dictionary<string, AttributeValue>() {
                {
                    "ID",
                    new AttributeValue { S = id }
                },
                {
                    "Area",
                    new AttributeValue { S = "Order"}
                },
            },
                ExpressionAttributeNames = new Dictionary<string, string>()
            {
                {"#S", "Order_Status"}
            },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>()
            {
                {
                    ":s",
                    new AttributeValue {S = status}
                }
            },
                ReturnValues = "UPDATED_NEW",
                UpdateExpression = "SET #S = :s",
            };

            var response = await client.UpdateItemAsync(request);

            return response;
        }


        static async Task<Entry> UpdateTableItemAsync(IAmazonDynamoDB client, string id, string status)
        {
            DynamoDBContext context = new DynamoDBContext(client);

            // Retrieve the existing order
            Entry orderRetrieved = await context.LoadAsync<Entry>(id, "Order");

            // Update status
            orderRetrieved.Order_Status = status;
            await context.SaveAsync(orderRetrieved);

            // Retrieve the updated item
            Entry updatedOrder = await context.LoadAsync<Entry>(id, "Order", new DynamoDBOperationConfig
            {
                ConsistentRead = true
            },
            new System.Threading.CancellationToken());

            return updatedOrder;
        }

        static void Main(string[] args)
        {
            var configfile = "../../../app.config";
            var region = "";
            var table = "";
            var id = "";
            var status = "";

            // Get default region and table from config file
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
                    case "-s":
                        i++;
                        status = args[i];
                        break;
                    default:
                        break;
                }

                i++;
            }

            if ((status == "") || (id == "") || ((status != "backordered") && (status != "delivered") && (status != "delivering") && (status != "pending")))
            {
                Console.WriteLine("You must supply a partition number (-i ID), and status value (-s STATUS) of backordered, delivered, delivering, or pending");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            if (status == "pending")
            {
                var response = UpdateTableItemAsync(client, id, status);

                // Throws an exception here if id does not identify an order
                if (response.Result.Order_Status == status)
                {
                    Console.WriteLine("Successfully updated item in " + table + " in region " + region);
                }
                else
                {
                    Console.WriteLine("Could not update order status");
                }
            }
            else
            {
                // Silenty eats issue if id does not identify an order
                var reply = ModifyOrderStatusAsync(client, table, id, status);

                if (reply.Result.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    Console.WriteLine("Successfully updated item in " + table + " in region " + region);
                }
                else
                {
                    Console.WriteLine("Could not update order status");
                }
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.UpdateItem]