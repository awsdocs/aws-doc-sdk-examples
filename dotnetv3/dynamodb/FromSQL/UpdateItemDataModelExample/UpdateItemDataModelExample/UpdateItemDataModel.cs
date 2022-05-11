// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace UpdateItemDataModelExample
{
    using System;
    using System.Configuration;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.DataModel;

    // snippet-start:[dynamodb.dotnet35.UpdateItemDataModelExample]

    /// <summary>
    /// Update the model of items in an Amazon DynamoDB table. The
    /// example was created using the AWS SDK for .NET version 3.7
    /// and .NET Core 5.0.
    /// </summary>
    public class UpdateItemDataModel
    {
        /// <summary>
        /// Retrieves the configuration settings, parses the command line, and
        /// then UpdateTableItemAsync method.
        /// </summary>
        /// <param name="args">Command line arguments for the application.</param>
        public static async Task Main(string[] args)
        {
            var configfile = "app.config";
            var region = string.Empty;
            var id = string.Empty;
            var status = string.Empty;

            // Get the default AWS Region from the config file.
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
                }

                i++;
            }

            if ((status == string.Empty) || (id == string.Empty) || ((status != "backordered") && (status != "delivered") && (status != "delivering") && (status != "pending")))
            {
                Console.WriteLine("You must supply a partition number (-i ID), and status value (-s STATUS) of backordered, delivered, delivering, or pending.");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            var client = new AmazonDynamoDBClient(newRegion);
            var context = new DynamoDBContext(client);

            var response = await UpdateTableItemAsync(context, id, status);

            // Update the status regardless of whether id identifies an order.
            if (response.OrderStatus == status)
            {
                Console.WriteLine("Successfully updated the order's status.");
            }
            else
            {
                Console.WriteLine("Could not update the order's status.");
            }
        }

        /// <summary>
        /// Update an item in a DynamoDB table that uses the modified
        /// data model described in the Entry class.
        /// </summary>
        /// <param name="context">The initialized DynamoDB context used
        /// to update the item in the DynamoDB table.</param>
        /// <param name="id">The id of the item to be updated.</param>
        /// <param name="status">The new status value to write to the
        /// existing item.</param>
        /// <returns>An Entry object containing the updated data.</returns>
        public static async Task<Entry> UpdateTableItemAsync(IDynamoDBContext context, string id, string status)
        {
            // Retrieve the existing order.
            Entry orderRetrieved = await context.LoadAsync<Entry>(id, "Order");

            // Trap any nulls.
            if (orderRetrieved is null)
            {
                throw new ArgumentException("The ID " + id + " did not identify any current order");
            }

            // Make sure it's an order.
            if (orderRetrieved.Area != "Order")
            {
                throw new ArgumentException("The ID " + id + " did NOT identify an order, but instead identified a " + orderRetrieved.Area);
            }

            // Update the status of the order.
            orderRetrieved.OrderStatus = status;
            await context.SaveAsync(orderRetrieved);

            // Retrieve the updated item.
            Entry updatedOrder = await context.LoadAsync<Entry>(id, "Order", new DynamoDBOperationConfig
            {
                ConsistentRead = true,
            });

            return updatedOrder;
        }
    }

    // snippet-end:[dynamodb.dotnet35.UpdateItemDataModelExample]
}
