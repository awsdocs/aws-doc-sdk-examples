// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.UpdateItemDataModel]
using System;
using System.Configuration;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

namespace DynamoDBCRUD
{
    // If you change the table name elsewhere
    // (as in an app.config in another project),
    // you'll have to change it here and rebuild.
    [DynamoDBTable("CustomersOrdersProducts")]
    public class Entry
    {
        [DynamoDBHashKey] // The partition key for the table.
        public string ID
        {
            get; set;
        }
        [DynamoDBRangeKey] // The sort key for the table.
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

    public class UpdateItemDataModel
    {
        public static async Task<Entry> UpdateTableItemAsync(IDynamoDBContext context, string id, string status)
        {
            // Retrieve the existing order.
            Entry orderRetrieved = await context.LoadAsync<Entry>(id, "Order");

            // Trap any nulls.
            if (null == orderRetrieved)
            {
                throw new ArgumentException("The ID " + id + " did not identify any current order");
            }

            // Make sure it's an order.
            if (orderRetrieved.Area != "Order")
            {
                throw new ArgumentException("The ID " + id + " did NOT identify an order, but instead identified a " + orderRetrieved.Area);
            }

            // Update the status of the order.
            orderRetrieved.Order_Status = status;
            await context.SaveAsync(orderRetrieved);

            // Retrieve the updated item.
            Entry updatedOrder = await context.LoadAsync<Entry>(id, "Order", new DynamoDBOperationConfig
            {
                ConsistentRead = true,
            },
            new System.Threading.CancellationToken());

            return updatedOrder;
        }

        static void Main(string[] args)
        {
            var configfile = "app.config";
            var region = "";
            var id = "";
            var status = "";

            // Get the default AWS Region from the config file.
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                region = appSettings.Settings["Region"].Value;

                if (region == "")
                {
                    Console.WriteLine("You must specify a Region in " + configfile);
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
            var client = new AmazonDynamoDBClient(newRegion);
            var context = new DynamoDBContext(client);

            var response = UpdateTableItemAsync(context, id, status);

            // Update the status regardless of whether id identifies an order.
            if (response.Result.Order_Status == status)
            {
                Console.WriteLine("Successfully updated the order's status.");
            }
            else
            {
                Console.WriteLine("Could not update the order's status.");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.UpdateItemDataModel]