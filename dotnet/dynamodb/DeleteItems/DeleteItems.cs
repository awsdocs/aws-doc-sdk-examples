// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.dotnet35.DeleteItems]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class DeleteItems
    {
        public static async Task<BatchWriteItemResponse> RemoveItemsAsync(IAmazonDynamoDB client, string table, string idstring, string area)
        {
            var ids = idstring.Split(" ");

            var writeRequests = new List<WriteRequest>();
            var items = new Dictionary<string, List<WriteRequest>>();

            for (int i = 0; i < ids.Length; i++)
            {
                var writeRequest = new WriteRequest
                {
                    // For the operation to delete an item, if you provide a primary key value
                    // that does not exist in the table, there is no error. It is just a no-op.
                    DeleteRequest = new DeleteRequest
                    {
                        Key = new Dictionary<string, AttributeValue>()
                        {
                            { 
                                "ID",  new AttributeValue 
                                {
                                    S = ids[i]
                                }
                            },
                            { 
                                "Area", new AttributeValue 
                                {
                                    S = area
                                } 
                            }
                        }
                    }
                };

                writeRequests.Add(writeRequest);
            }

            items.Add(table, writeRequests);

            var request = new BatchWriteItemRequest(items);

            var response = await client.BatchWriteItemAsync(request);

            return response;
        }

        static void Main(string[] args)
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            var idstring = "";
            var area = "";

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
                    case "-a":
                        i++;
                        area = args[i];
                        break;
                    case "-i":
                        i++;
                        idstring = args[i];
                        break;
                    default:
                        break;
                }

                i++;
            }

            if ((area == "") || (idstring == ""))
            {
                Console.WriteLine("You must supply an area (-a AREA) and ids (-i \"id1 ... idN\")");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var resp = RemoveItemsAsync(client, table, idstring, area);

            //    Task<DeleteItemResponse> response = RemoveItemAsync(debug, client, table, partition, sort);

            if (resp.Result.HttpStatusCode == HttpStatusCode.OK)
            {
                Console.WriteLine("Removed item from " + table + " table in " + region + " region");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.DeleteItems]