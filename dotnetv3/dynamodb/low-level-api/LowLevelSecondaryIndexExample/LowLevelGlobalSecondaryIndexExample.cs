// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[dynamodb.dotnetv3.LowLevelGlobalSecondaryIndexExample]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace LowLevelGlobalSecondaryIndexExample
{
    public class LowLevelGlobalSecondaryIndexExample
    {
        private static readonly string TableName = "Issues";

        public static async Task<bool> CreateTable(AmazonDynamoDBClient client)
        {
            // Attribute definitions
            var attributeDefinitions = new List<AttributeDefinition>()
        {
            {new AttributeDefinition {
                 AttributeName = "IssueId", AttributeType = "S"
             }},
            {new AttributeDefinition {
                 AttributeName = "Title", AttributeType = "S"
             }},
            {new AttributeDefinition {
                 AttributeName = "CreateDate", AttributeType = "S"
             }},
            {new AttributeDefinition {
                 AttributeName = "DueDate", AttributeType = "S"
             }}
        };

            // Key schema for table
            var tableKeySchema = new List<KeySchemaElement>() {
            {
                new KeySchemaElement {
                    AttributeName= "IssueId",
                    KeyType = "HASH" //Partition key
                }
            },
            {
                new KeySchemaElement {
                    AttributeName = "Title",
                    KeyType = "RANGE" //Sort key
                }
            }
        };

            // Initial provisioned throughput settings for the indexes
            var ptIndex = new ProvisionedThroughput
            {
                ReadCapacityUnits = 1L,
                WriteCapacityUnits = 1L
            };

            // CreateDateIndex
            var createDateIndex = new GlobalSecondaryIndex()
            {
                IndexName = "CreateDateIndex",
                ProvisionedThroughput = ptIndex,
                KeySchema = {
                new KeySchemaElement {
                    AttributeName = "CreateDate", KeyType = "HASH" //Partition key
                },
                new KeySchemaElement {
                    AttributeName = "IssueId", KeyType = "RANGE" //Sort key
                }
            },
                Projection = new Projection
                {
                    ProjectionType = "INCLUDE",
                    NonKeyAttributes = {
                    "Description", "Status"
                }
                }
            };

            // TitleIndex
            var titleIndex = new GlobalSecondaryIndex()
            {
                IndexName = "TitleIndex",
                ProvisionedThroughput = ptIndex,
                KeySchema = {
                new KeySchemaElement {
                    AttributeName = "Title", KeyType = "HASH" //Partition key
                },
                new KeySchemaElement {
                    AttributeName = "IssueId", KeyType = "RANGE" //Sort key
                }
            },
                Projection = new Projection
                {
                    ProjectionType = "KEYS_ONLY"
                }
            };

            // DueDateIndex
            var dueDateIndex = new GlobalSecondaryIndex()
            {
                IndexName = "DueDateIndex",
                ProvisionedThroughput = ptIndex,
                KeySchema = {
                new KeySchemaElement {
                    AttributeName = "DueDate",
                    KeyType = "HASH" //Partition key
                }
            },
                Projection = new Projection
                {
                    ProjectionType = "ALL"
                }
            };

            var createTableRequest = new CreateTableRequest
            {
                TableName = TableName,
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 1,
                    WriteCapacityUnits = 1
                },
                AttributeDefinitions = attributeDefinitions,
                KeySchema = tableKeySchema,
                GlobalSecondaryIndexes = {
                createDateIndex, titleIndex, dueDateIndex
            }
            };

            Console.WriteLine("Creating table " + TableName + "...");
            await client.CreateTableAsync(createTableRequest);

            WaitUntilTableReady(client, TableName);

            return true;
        }

        public static async Task<bool> LoadData(AmazonDynamoDBClient client)
        {
            Console.WriteLine("Loading data into table " + TableName + "...");

            // IssueId, Title,
            // Description,
            // CreateDate, LastUpdateDate, DueDate,
            // Priority, Status

            var result = await PutItem(client, "A-101", "Compilation error",
                "Can't compile Project X - bad version number. What does this mean?",
                "2013-11-01", "2013-11-02", "2013-11-10",
                1, "Assigned");

            if (!result) return false;

            result = await PutItem(client, "A-102", "Can't read data file",
                "The main data file is missing, or the permissions are incorrect",
                "2013-11-01", "2013-11-04", "2013-11-30",
                2, "In progress");

            if (!result) return false;

            result = await PutItem(client, "A-103", "Test failure",
                "Functional test of Project X produces errors",
                "2013-11-01", "2013-11-02", "2013-11-10",
                1, "In progress");

            if (!result) return false;

            result = await PutItem(client, "A-104", "Compilation error",
                "Variable 'messageCount' was not initialized.",
                "2013-11-15", "2013-11-16", "2013-11-30",
                3, "Assigned");

            if (!result) return false;

            result = await PutItem(client, "A-105", "Network issue",
                "Can't ping IP address 127.0.0.1. Please fix this.",
                "2013-11-15", "2013-11-16", "2013-11-19",
                5, "Assigned");

            if (!result) return false;

            return true;
        }

        private static async Task<bool> PutItem(
            AmazonDynamoDBClient client,
            String issueId, String title,
            String description,
            String createDate, String lastUpdateDate, String dueDate,
            Int32 priority, String status)
        {
            var item = new Dictionary<string, AttributeValue>
            {
                {
                    "IssueId",
                    new AttributeValue
                    {
                        S = issueId
                    }
                },

                {
                    "Title",
                    new AttributeValue
                    {
                        S = title
                    }
                },

                {
                    "Description",
                    new AttributeValue
                    {
                        S = description
                    }
                },

                {
                    "CreateDate",
                    new AttributeValue
                    {
                        S = createDate
                    }
                },

                {
                    "LastUpdateDate",
                    new AttributeValue
                    {
                        S = lastUpdateDate
                    }
                },

                {
                    "DueDate",
                    new AttributeValue
                    {
                        S = dueDate
                    }
                },

                {
                    "Priority",
                    new AttributeValue
                    {
                        N = priority.ToString()
                    }
                },

                {
                    "Status",
                    new AttributeValue
                    {
                        S = status
                    }
                }
            };

            await client.PutItemAsync(new PutItemRequest
            {
                TableName = TableName,
                Item = item
            });

            return true;
        }

        public static async Task<bool> QueryIndex(AmazonDynamoDBClient client, string indexName)
        {
            Console.WriteLine
                ("\n***********************************************************\n");
            Console.WriteLine("Querying index " + indexName + "...");

            var queryRequest = new QueryRequest
            {
                TableName = TableName,
                IndexName = indexName,
                ScanIndexForward = true
            };


            String keyConditionExpression;
            var expressionAttributeValues = new Dictionary<string, AttributeValue>();

            if (indexName == "CreateDateIndex")
            {
                Console.WriteLine("Issues filed on 2013-11-01\n");

                keyConditionExpression = "CreateDate = :v_date and begins_with(IssueId, :v_issue)";
                expressionAttributeValues.Add(":v_date", new AttributeValue
                {
                    S = "2013-11-01"
                });
                expressionAttributeValues.Add(":v_issue", new AttributeValue
                {
                    S = "A-"
                });
            }
            else if (indexName == "TitleIndex")
            {
                Console.WriteLine("Compilation errors\n");

                keyConditionExpression = "Title = :v_title and begins_with(IssueId, :v_issue)";
                expressionAttributeValues.Add(":v_title", new AttributeValue
                {
                    S = "Compilation error"
                });
                expressionAttributeValues.Add(":v_issue", new AttributeValue
                {
                    S = "A-"
                });

                // Select
                queryRequest.Select = "ALL_PROJECTED_ATTRIBUTES";
            }
            else if (indexName == "DueDateIndex")
            {
                Console.WriteLine("Items that are due on 2013-11-30\n");

                keyConditionExpression = "DueDate = :v_date";
                expressionAttributeValues.Add(":v_date", new AttributeValue
                {
                    S = "2013-11-30"
                });

                // Select
                queryRequest.Select = "ALL_PROJECTED_ATTRIBUTES";
            }
            else
            {
                Console.WriteLine("\nNo valid index name provided");
                return false;
            }

            queryRequest.KeyConditionExpression = keyConditionExpression;
            queryRequest.ExpressionAttributeValues = expressionAttributeValues;

            var result = await client.QueryAsync(queryRequest);
            var items = result.Items;

            foreach (var currentItem in items)
            {
                foreach (string attr in currentItem.Keys)
                {
                    if (attr == "Priority")
                    {
                        Console.WriteLine(attr + "---> " + currentItem[attr].N);
                    }
                    else
                    {
                        Console.WriteLine(attr + "---> " + currentItem[attr].S);
                    }
                }

                Console.WriteLine();
            }

            return true;
        }

        public static async Task<bool> DeleteTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("Deleting table " + TableName + "...");
            await client.DeleteTableAsync(new DeleteTableRequest
            {
                TableName = TableName
            });

            WaitForTableToBeDeleted(client, TableName);

            return true;
        }

        private static async void WaitUntilTableReady(AmazonDynamoDBClient client, string tableName)
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

        private static async void WaitForTableToBeDeleted(AmazonDynamoDBClient client, string tableName)
        {
            bool tablePresent = true;

            while (tablePresent)
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
                }
                catch (ResourceNotFoundException)
                {
                    tablePresent = false;
                }
            }
        }


        static void Main()
        {
            var client = new AmazonDynamoDBClient();

            var result = CreateTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not create table, bye");
                return;
            }

            result = LoadData(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not load data into table, bye");
                return;
            }

            result = QueryIndex(client, "CreateDateIndex");

            if (!result.Result)
            {
                Console.WriteLine("Could not create date index");
                Console.WriteLine("You'll have to delete the " + TableName + " table yourself");
                return;
            }
            result = QueryIndex(client, "TitleIndex");

            if (!result.Result)
            {
                Console.WriteLine("Could not create title index");
                Console.WriteLine("You'll have to delete the " + TableName + " table yourself");
                return;
            }
            result = QueryIndex(client, "DueDateIndex");

            if (!result.Result)
            {
                Console.WriteLine("Could not create due date index");
                Console.WriteLine("You'll have to delete the " + TableName + " table yourself");
                return;
            }

            result = DeleteTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not delete table");
                Console.WriteLine("You'll have to delete the " + TableName + " table yourself");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnetv3.LowLevelGlobalSecondaryIndexExample]
