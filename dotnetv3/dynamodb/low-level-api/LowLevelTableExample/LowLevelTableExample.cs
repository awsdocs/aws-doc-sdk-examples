// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[lambda.dotnet35.LowLevelTableExample]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace LowLevelTableExample
{
    public class LowLevelTableExample
    {
        private static string _tableName = "ExampleTable";

        public static async Task<bool> CreateExampleTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Creating table ***");
            var request = new CreateTableRequest
            {
                AttributeDefinitions = new List<AttributeDefinition>()
            {
                new AttributeDefinition
                {
                    AttributeName = "Id",
                    AttributeType = "N"
                },
                new AttributeDefinition
                {
                    AttributeName = "ReplyDateTime",
                    AttributeType = "N"
                }
            },
                KeySchema = new List<KeySchemaElement>
            {
                new KeySchemaElement
                {
                    AttributeName = "Id",
                    KeyType = "HASH" //Partition key
                },
                new KeySchemaElement
                {
                    AttributeName = "ReplyDateTime",
                    KeyType = "RANGE" //Sort key
                }
            },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 5,
                    WriteCapacityUnits = 6
                },
                TableName = _tableName
            };

            var response = await client.CreateTableAsync(request);

            var tableDescription = response.TableDescription;
            Console.WriteLine("{1}: {0} \t ReadsPerSec: {2} \t WritesPerSec: {3}",
                      tableDescription.TableStatus,
                      tableDescription.TableName,
                      tableDescription.ProvisionedThroughput.ReadCapacityUnits,
                      tableDescription.ProvisionedThroughput.WriteCapacityUnits);

            string status = tableDescription.TableStatus;
            Console.WriteLine(_tableName + " - " + status);

            WaitUntilTableReady(client);

            return true;
        }

        public static async Task<bool> ListMyTables(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** listing tables ***");
            string lastTableNameEvaluated = null;
            do
            {
                var request = new ListTablesRequest
                {
                    Limit = 2,
                    ExclusiveStartTableName = lastTableNameEvaluated
                };

                var response = await client.ListTablesAsync(request);
                foreach (string name in response.TableNames)
                    Console.WriteLine(name);

                lastTableNameEvaluated = response.LastEvaluatedTableName;
            } while (lastTableNameEvaluated != null);

            return true;
        }

        public static async Task<bool> GetTableInformation(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Retrieving table information ***");
            var request = new DescribeTableRequest
            {
                TableName = _tableName
            };

            var response = await client.DescribeTableAsync(request);

            TableDescription description = response.Table;
            Console.WriteLine("Name: {0}", description.TableName);
            Console.WriteLine("# of items: {0}", description.ItemCount);
            Console.WriteLine("Provision Throughput (reads/sec): {0}",
                      description.ProvisionedThroughput.ReadCapacityUnits);
            Console.WriteLine("Provision Throughput (writes/sec): {0}",
                      description.ProvisionedThroughput.WriteCapacityUnits);

            return true;
        }

        public static async Task<bool> UpdateExampleTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Updating table ***");
            var request = new UpdateTableRequest()
            {
                TableName = _tableName,
                ProvisionedThroughput = new ProvisionedThroughput()
                {
                    ReadCapacityUnits = 6,
                    WriteCapacityUnits = 7
                }
            };

            await client.UpdateTableAsync(request);

            WaitUntilTableReady(client);

            return true;
        }

        public static async Task<bool> DeleteExampleTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Deleting table ***");
            var request = new DeleteTableRequest
            {
                TableName = _tableName
            };

            await client.DeleteTableAsync(request);

            Console.WriteLine("Table is being deleted...");

            return true;
        }

        private static async void WaitUntilTableReady(AmazonDynamoDBClient client)
        {
            string status;
            // Let us wait until table is created. Call DescribeTable.
            do
            {
                System.Threading.Thread.Sleep(5000); // Wait 5 seconds.
                var res = await client.DescribeTableAsync(new DescribeTableRequest
                {
                    TableName = _tableName
                });

                Console.WriteLine("Table name: {0}, status: {1}",
                          res.Table.TableName,
                          res.Table.TableStatus);
                status = res.Table.TableStatus;

            } while (status != "ACTIVE");
        }

        static void Main()
        {
            var client = new AmazonDynamoDBClient();

            var result = CreateExampleTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not create example table.");
                return;
            }

            result = ListMyTables(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not list tables.");
                Console.WriteLine("You must delete the " + _tableName + " table yourself");
                return;
            }

            result = GetTableInformation(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not get table information.");
                Console.WriteLine("You must delete the " + _tableName + " table yourself");
                return;
            }

            result = UpdateExampleTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not update example table.");
                Console.WriteLine("You must delete the " + _tableName + " table yourself");
                return;
            }

            result = DeleteExampleTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not delete example table.");
                Console.WriteLine("You must delete the " + _tableName + " table yourself");
            }
        }
    }
}
// snippet-end:[lambda.dotnet35.LowLevelTableExample]