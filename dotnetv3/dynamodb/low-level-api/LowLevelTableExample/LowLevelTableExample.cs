// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[dynamodb.dotnetv3.LowLevelTableExample]
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Runtime;

namespace LowLevelTableExample;

static class LowLevelTableExample
{
    private static readonly IAmazonDynamoDB Client = new AmazonDynamoDBClient();
    private const string ExampleTableName = "ExampleTable";

    static async Task Main()
    {
        try
        {
            await CreateExampleTable();
            await ListMyTables();
            await GetTableInformation();
            await UpdateExampleTable();
        }
        catch (AmazonDynamoDBException e)
        {
            Console.WriteLine(e.Message);
        }
        catch (AmazonServiceException e)
        {
            Console.WriteLine(e.Message);
        }
        catch (Exception e)
        {
            Console.WriteLine(e.Message);
        }
        finally
        {
            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();

            await DeleteExampleTable();
        }
    }

    // snippet-start:[dynamodb.dotnetv3.CreateExampleTable]
    private static async Task CreateExampleTable()
    {
        Console.WriteLine("\n*** Creating table ***");

        var response = await Client.CreateTableAsync(new CreateTableRequest
        {
            AttributeDefinitions = new List<AttributeDefinition>
            {
                new AttributeDefinition
                {
                    AttributeName = "Id",
                    AttributeType = ScalarAttributeType.N
                },
                new AttributeDefinition
                {
                    AttributeName = "ReplyDateTime",
                    AttributeType = ScalarAttributeType.N
                }
            },
            KeySchema = new List<KeySchemaElement>
            {
                new KeySchemaElement
                {
                    AttributeName = "Id",
                    KeyType = KeyType.HASH //Partition key
                },
                new KeySchemaElement
                {
                    AttributeName = "ReplyDateTime",
                    KeyType = KeyType.RANGE //Sort key
                }
            },
            ProvisionedThroughput = new ProvisionedThroughput
            {
                ReadCapacityUnits = 5,
                WriteCapacityUnits = 6
            },
            TableName = ExampleTableName
        });

        var tableDescription = response.TableDescription;
        Console.WriteLine($"{tableDescription.TableName}: {tableDescription.TableStatus} \t " +
                          $"ReadsPerSec: {tableDescription.ProvisionedThroughput.ReadCapacityUnits} \t " +
                          $"WritesPerSec: {tableDescription.ProvisionedThroughput.WriteCapacityUnits}");

        Console.WriteLine($"{ExampleTableName} - {tableDescription.TableStatus}");

        await WaitUntilTableReady(ExampleTableName);
    }
    // snippet-end:[dynamodb.dotnetv3.CreateExampleTable]

    // snippet-start:[dynamodb.dotnetv3.ListTableExample]
    private static async Task ListMyTables()
    {
        Console.WriteLine("\n*** Listing tables ***");

        string? lastTableNameEvaluated = null;
        do
        {
            var response = await Client.ListTablesAsync(new ListTablesRequest
            {
                Limit = 2,
                ExclusiveStartTableName = lastTableNameEvaluated
            });

            foreach (var name in response.TableNames)
            {
                Console.WriteLine(name);
            }

            lastTableNameEvaluated = response.LastEvaluatedTableName;
        } while (lastTableNameEvaluated != null);
    }
    // snippet-end:[dynamodb.dotnetv3.ListTableExample]

    // snippet-start:[dynamodb.dotnetv3.DescribeTableExample]
    private static async Task GetTableInformation()
    {
        Console.WriteLine("\n*** Retrieving table information ***");

        var response = await Client.DescribeTableAsync(new DescribeTableRequest
        {
            TableName = ExampleTableName
        });

        var table = response.Table;
        Console.WriteLine($"Name: {table.TableName}");
        Console.WriteLine($"# of items: {table.ItemCount}");
        Console.WriteLine($"Provision Throughput (reads/sec): " +
                          $"{table.ProvisionedThroughput.ReadCapacityUnits}");
        Console.WriteLine($"Provision Throughput (writes/sec): " +
                          $"{table.ProvisionedThroughput.WriteCapacityUnits}");
    }
    // snippet-end:[dynamodb.dotnetv3.DescribeTableExample]

    // snippet-start:[dynamodb.dotnetv3.UpdateExampleTable]
    private static async Task UpdateExampleTable()
    {
        Console.WriteLine("\n*** Updating table ***");

        await Client.UpdateTableAsync(new UpdateTableRequest
        {
            TableName = ExampleTableName,
            ProvisionedThroughput = new ProvisionedThroughput
            {
                ReadCapacityUnits = 6,
                WriteCapacityUnits = 7
            }
        });

        await WaitUntilTableReady(ExampleTableName);
    }
    // snippet-end:[dynamodb.dotnetv3.UpdateExampleTable]

    // snippet-start:[dynamodb.dotnetv3.DeleteExampleTable]
    private static async Task DeleteExampleTable()
    {
        Console.WriteLine("\n*** Deleting table ***");

        try
        {
            await Client.DeleteTableAsync(new DeleteTableRequest
            {
                TableName = ExampleTableName
            });

            Console.WriteLine("Table is being deleted...");
        }
        catch (ResourceNotFoundException)
        {
            // Something went wrong during CreateTable.
        }
    }
    // snippet-end:[dynamodb.dotnetv3.DeleteExampleTable]

    // snippet-start:[dynamodb.dotnetv3.WaitUntilTableReady]
    private static async Task WaitUntilTableReady(string tableName)
    {
        string? status = null;
        // Wait until table is created. Call DescribeTable.
        do
        {
            Thread.Sleep(5000); // Wait 5 seconds.
            try
            {
                var res = await Client.DescribeTableAsync(new DescribeTableRequest
                {
                    TableName = tableName
                });

                Console.WriteLine($"Table name: {res.Table.TableName}, status: {res.Table.TableStatus}");
                status = res.Table.TableStatus;
            }
            catch (ResourceNotFoundException)
            {
                // DescribeTable is eventually consistent. So you might
                // get resource not found. We handle the potential exception.
            }
        } while (status != "ACTIVE");
    }
    // snippet-end:[dynamodb.dotnetv3.WaitUntilTableReady]
}
// snippet-end:[dynamodb.dotnetv3.LowLevelTableExample]