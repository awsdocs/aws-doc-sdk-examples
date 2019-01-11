// snippet-sourcedescription:[LowLevelParallelScan.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.af029570-47d8-42a9-b7b2-6d54bf875a25] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Runtime;

namespace com.amazonaws.codesamples
{
    class LowLevelParallelScan
    {
        private static AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        private static string tableName = "ProductCatalog";
        private static int exampleItemCount = 100;
        private static int scanItemLimit = 10;
        private static int totalSegments = 5;


        static void Main(string[] args)
        {
            try
            {
                DeleteExampleTable();
                CreateExampleTable();
                UploadExampleData();
                ParallelScanExampleTable();
            }
            catch (AmazonDynamoDBException e) { Console.WriteLine(e.Message); }
            catch (AmazonServiceException e) { Console.WriteLine(e.Message); }
            catch (Exception e) { Console.WriteLine(e.Message); }

            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();
        }

        private static void ParallelScanExampleTable()
        {
            Console.WriteLine("\n*** Creating {0} Parallel Scan Tasks to scan {1}", totalSegments, tableName);
            Task[] tasks = new Task[totalSegments];
            for (int segment = 0; segment < totalSegments; segment++)
            {
                int tmpSegment = segment;
                Task task = Task.Factory.StartNew(() =>
                                  {
                                      ScanSegment(totalSegments, tmpSegment);
                                  });

                tasks[segment] = task;
            }

            Console.WriteLine("All scan tasks are created, waiting for them to complete.");
            Task.WaitAll(tasks);

            Console.WriteLine("All scan tasks are completed.");
        }

        private static void ScanSegment(int totalSegments, int segment)
        {
            Console.WriteLine("*** Starting to Scan Segment {0} of {1} out of {2} total segments ***", segment, tableName, totalSegments);
            Dictionary<string, AttributeValue> lastEvaluatedKey = null;
            int totalScannedItemCount = 0;
            int totalScanRequestCount = 0;
            do
            {
                var request = new ScanRequest
                {
                    TableName = tableName,
                    Limit = scanItemLimit,
                    ExclusiveStartKey = lastEvaluatedKey,
                    Segment = segment,
                    TotalSegments = totalSegments
                };

                var response = client.Scan(request);
                lastEvaluatedKey = response.LastEvaluatedKey;
                totalScanRequestCount++;
                totalScannedItemCount += response.ScannedCount;
                foreach (var item in response.Items)
                {
                    Console.WriteLine("Segment: {0}, Scanned Item with Title: {1}", segment, item["Title"].S);
                }
            } while (lastEvaluatedKey.Count != 0);

            Console.WriteLine("*** Completed Scan Segment {0} of {1}. TotalScanRequestCount: {2}, TotalScannedItemCount: {3} ***", segment, tableName, totalScanRequestCount, totalScannedItemCount);
        }

        private static void UploadExampleData()
        {
            Console.WriteLine("\n*** Uploading {0} Example Items to {1} Table***", exampleItemCount, tableName);
            Console.Write("Uploading Items: ");
            for (int itemIndex = 0; itemIndex < exampleItemCount; itemIndex++)
            {
                Console.Write("{0}, ", itemIndex);
                CreateItem(itemIndex.ToString());
            }
            Console.WriteLine();
        }

        private static void CreateItem(string itemIndex)
        {
            var request = new PutItemRequest
            {
                TableName = tableName,
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
            client.PutItem(request);
        }

        private static void CreateExampleTable()
        {
            Console.WriteLine("\n*** Creating {0} Table ***", tableName);
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
                TableName = tableName
            };

            var response = client.CreateTable(request);

            var result = response;
            var tableDescription = result.TableDescription;
            Console.WriteLine("{1}: {0} \t ReadsPerSec: {2} \t WritesPerSec: {3}",
                      tableDescription.TableStatus,
                      tableDescription.TableName,
                      tableDescription.ProvisionedThroughput.ReadCapacityUnits,
                      tableDescription.ProvisionedThroughput.WriteCapacityUnits);

            string status = tableDescription.TableStatus;
            Console.WriteLine(tableName + " - " + status);

            WaitUntilTableReady(tableName);
        }

        private static void DeleteExampleTable()
        {
            try
            {
                Console.WriteLine("\n*** Deleting {0} Table ***", tableName);
                var request = new DeleteTableRequest
                {
                    TableName = tableName
                };

                var response = client.DeleteTable(request);
                var result = response;
                Console.WriteLine("{0} is being deleted...", tableName);
                WaitUntilTableDeleted(tableName);
            }
            catch (ResourceNotFoundException)
            {
                Console.WriteLine("{0} Table delete failed: Table does not exist", tableName);
            }
        }

        private static void WaitUntilTableReady(string tableName)
        {
            string status = null;
            // Let us wait until table is created. Call DescribeTable.
            do
            {
                System.Threading.Thread.Sleep(5000); // Wait 5 seconds.
                try
                {
                    var res = client.DescribeTable(new DescribeTableRequest
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
                    // DescribeTable is eventually consistent. So you might
                    // get resource not found. So we handle the potential exception.
                }
            } while (status != "ACTIVE");
        }

        private static void WaitUntilTableDeleted(string tableName)
        {
            string status = null;
            // Let us wait until table is deleted. Call DescribeTable.
            do
            {
                System.Threading.Thread.Sleep(5000); // Wait 5 seconds.
                try
                {
                    var res = client.DescribeTable(new DescribeTableRequest
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
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.af029570-47d8-42a9-b7b2-6d54bf875a25]