// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.LowLevelParallelScan]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace LowLevelParallelScan
{
    public class LowLevelParallelScan
    {        
        private static string _tableName = "ProductCatalog";
        private static int _exampleItemCount = 100;
        private static int _scanItemLimit = 10;
        private static int _totalSegments = 5;

        public static void ParallelScanExampleTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Creating {0} Parallel Scan Tasks to scan {1}", _totalSegments, _tableName);
            Task[] tasks = new Task[_totalSegments];

            for (int segment = 0; segment < _totalSegments; segment++)
            {
                int tmpSegment = segment;
                Task task = Task.Factory.StartNew(() =>
                {
                    ScanSegment(client, _totalSegments, tmpSegment);
                });

                tasks[segment] = task;
            }

            Console.WriteLine("All scan tasks are created, waiting for them to complete.");
            Task.WaitAll(tasks);

            Console.WriteLine("All scan tasks are completed.");
        }

        public static async void ScanSegment(AmazonDynamoDBClient client, int totalSegments, int segment)
        {
            Console.WriteLine("*** Starting to Scan Segment {0} of {1} out of {2} total segments ***", segment, _tableName, totalSegments);
            Dictionary<string, AttributeValue> lastEvaluatedKey = null;
            int totalScannedItemCount = 0;
            int totalScanRequestCount = 0;
            do
            {
                var request = new ScanRequest
                {
                    TableName = _tableName,
                    Limit = _scanItemLimit,
                    ExclusiveStartKey = lastEvaluatedKey,
                    Segment = segment,
                    TotalSegments = totalSegments
                };

                var response = await client.ScanAsync(request);
                lastEvaluatedKey = response.LastEvaluatedKey;
                totalScanRequestCount++;
                totalScannedItemCount += response.ScannedCount;
                foreach (var item in response.Items)
                {
                    Console.WriteLine("Segment: {0}, Scanned Item with Title: {1}", segment, item["Title"].S);
                }
            } while (lastEvaluatedKey.Count != 0);

            Console.WriteLine("*** Completed Scan Segment {0} of {1}. TotalScanRequestCount: {2}, TotalScannedItemCount: {3} ***", segment, _tableName, totalScanRequestCount, totalScannedItemCount);
        }

        public static async Task<bool> UploadExampleData(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Uploading {0} Example Items to {1} Table***", _exampleItemCount, _tableName);
            Console.Write("Uploading Items: ");

            for (int itemIndex = 0; itemIndex < _exampleItemCount; itemIndex++)
            {
                Console.Write("{0}, ", itemIndex);
                bool result = await CreateItem(client, itemIndex.ToString());

                if (!result) return false;
            }

            Console.WriteLine();

            return true;
        }

        public static async Task<bool> CreateItem(AmazonDynamoDBClient client, string itemIndex)
        {
            var request = new PutItemRequest
            {
                TableName = _tableName,
                Item = new Dictionary<string, AttributeValue>()
            {
                { "Id", new AttributeValue {
                      N = itemIndex
                  }},
                { "Title", new AttributeValue {
                      S = "Book " + itemIndex + " Title"
                  }},
                { "ISBN", new AttributeValue {
                      S = "11-11-11-11"
                  }},
                { "Authors", new AttributeValue {
                      SS = new List<string>{"Author1", "Author2" }
                  }},
                { "Price", new AttributeValue {
                      N = "20.00"
                  }},
                { "Dimensions", new AttributeValue {
                      S = "8.5x11.0x.75"
                  }},
                { "InPublication", new AttributeValue {
                      BOOL = false
                  } }
            }
            };
            
            await client.PutItemAsync(request);

            return true;
        }

        public static async Task<bool> CreateExampleTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Creating {0} Table ***", _tableName);
            var request = new CreateTableRequest
            {
                AttributeDefinitions = new List<AttributeDefinition>()
            {
                new AttributeDefinition
                {
                    AttributeName = "Id",
                    AttributeType = "N"
                }
            },
                KeySchema = new List<KeySchemaElement>
            {
                new KeySchemaElement
                {
                    AttributeName = "Id",
                    KeyType = "HASH" //Partition key
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

            var result = response;
            var tableDescription = result.TableDescription;
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

        public static async Task<bool> DeleteExampleTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("\n*** Deleting {0} Table ***", _tableName);
            
            var request = new DeleteTableRequest
            {
                TableName = _tableName
            };
            
            await client.DeleteTableAsync(request);
            
            Console.WriteLine("{0} is being deleted...", _tableName);
            WaitUntilTableDeleted(client, _tableName);

            return true;
        }

        private static async void WaitUntilTableReady(AmazonDynamoDBClient client)
        {
            string status = null;
            // Let us wait until table is created. Call DescribeTable.
            do
            {
                System.Threading.Thread.Sleep(5000); // Wait 5 seconds.
                try
                {
                    var res = await client.DescribeTableAsync(new DescribeTableRequest
                    {
                        TableName = _tableName
                    });

                    Console.WriteLine("Table name: {0}, status: {1}",
                              res.Table.TableName,
                              res.Table.TableStatus);
                    status = res.Table.TableStatus;
                }
                catch (ResourceNotFoundException)
                {
                    // DescribeTable is eventually consistent. So you might
                    // get resource not found. So we handle the potential exception.
                }
            } while (status != "ACTIVE");
        }

        private static async void WaitUntilTableDeleted(AmazonDynamoDBClient client, string tableName)
        {
            string status;
            // Let us wait until table is deleted. Call DescribeTable.
            do
            {
                System.Threading.Thread.Sleep(5000); // Wait 5 seconds.
                try
                {
                    var res = await client.DescribeTableAsync(new DescribeTableRequest
                    {
                        TableName = tableName
                    });

                    Console.WriteLine("Table name: {0}, status: {1}",
                              res.Table.TableName,
                              res.Table.TableStatus);
                    status = res.Table.TableStatus;
                }
                catch (ResourceNotFoundException)
                {
                    Console.WriteLine("Table name: {0} is not found. It is deleted", tableName);
                    return;
                }
            } while (status == "DELETING");
        }

        static void Main()
        {
            var client = new AmazonDynamoDBClient();

            var result = CreateExampleTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not create table, bye");
                return;
            }

            result = UploadExampleData(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not upload data to table");
                Console.WriteLine("You'll have to delete the " + _tableName + " table yourself");
                return;
            }

            ParallelScanExampleTable(client);

            result = DeleteExampleTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not delete table");
                Console.WriteLine("You'll have to delete the " + _tableName + " table yourself");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.LowLevelParallelScan]