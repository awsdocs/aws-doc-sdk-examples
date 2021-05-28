// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.CreateTablesLoadData]
using System;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;

namespace CreateTablesLoadData
{
    public class CreateTablesLoadData
    {
        public static async Task<DeleteTableResponse> DeleteTable(IAmazonDynamoDB client, string tableName)
        {
            try
            {
                var response = await client.DeleteTableAsync(new DeleteTableRequest
                {
                    TableName = tableName
                });

                return response;
            }
            catch (ResourceNotFoundException)
            {
                // There is no such table.
                return null;
            }
        }

        public static async Task<DescribeTableResponse> CreateTableProductCatalog(IAmazonDynamoDB client)
        {
            string tableName = "ProductCatalog";

            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = new List<AttributeDefinition>()
                {
                    new AttributeDefinition
                    {
                        AttributeName = "Id",
                        AttributeType = "N"
                    }
                },
                KeySchema = new List<KeySchemaElement>()
                {
                    new KeySchemaElement
                    {
                        AttributeName = "Id",
                        KeyType = "HASH"
                    }
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5
                }
            });

            var result = await WaitTillTableCreated(client, tableName, response);

            return result;
        }

        public static async Task<DescribeTableResponse> CreateTableForum(IAmazonDynamoDB client)
        {
            string tableName = "Forum";

            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = new List<AttributeDefinition>()
                {
                    new AttributeDefinition
                    {
                        AttributeName = "Name",
                        AttributeType = "S"
                    }
                },
                KeySchema = new List<KeySchemaElement>()
                {
                    new KeySchemaElement
                    {
                        AttributeName = "Name", // forum Title
                        KeyType = "HASH"
                    }
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5
                }
            });

            var result = await WaitTillTableCreated(client, tableName, response);

            return result;
        }

        public static async Task<DescribeTableResponse> CreateTableThread(IAmazonDynamoDB client)
        {
            string tableName = "Thread";

            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = new List<AttributeDefinition>()
                {
                    new AttributeDefinition
                    {
                        AttributeName = "ForumName", // Hash attribute
                        AttributeType = "S"
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "Subject",
                        AttributeType = "S"
                    }
                },
                KeySchema = new List<KeySchemaElement>()
                {
                    new KeySchemaElement
                    {
                        AttributeName = "ForumName", // Hash attribute
                        KeyType = "HASH"
                    },
                    new KeySchemaElement
                    {
                        AttributeName = "Subject", // Range attribute
                        KeyType = "RANGE"
                    }
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5
                }
            });

            var result = await WaitTillTableCreated(client, tableName, response);

            return result;
        }

        public static async Task<DescribeTableResponse> CreateTableReply(IAmazonDynamoDB client)
        {
            string tableName = "Reply";
            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = new List<AttributeDefinition>()
                {
                    new AttributeDefinition
                    {
                        AttributeName = "Id",
                        AttributeType = "S"
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "ReplyDateTime",
                        AttributeType = "S"
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "PostedBy",
                        AttributeType = "S"
                    }
                },
                KeySchema = new List<KeySchemaElement>()
                {
                    new KeySchemaElement()
                    {
                        AttributeName = "Id",
                        KeyType = "HASH"
                    },
                    new KeySchemaElement()
                    {
                        AttributeName = "ReplyDateTime",
                        KeyType = "RANGE"
                    }
                },
                LocalSecondaryIndexes = new List<LocalSecondaryIndex>()
                {
                    new LocalSecondaryIndex()
                    {
                        IndexName = "PostedBy_index",
                        KeySchema = new List<KeySchemaElement>() {
                            new KeySchemaElement() {
                                AttributeName = "Id", KeyType = "HASH"
                            },
                            new KeySchemaElement() {
                                AttributeName = "PostedBy", KeyType = "RANGE"
                            }
                        },
                        Projection = new Projection() {
                            ProjectionType = ProjectionType.KEYS_ONLY
                        }
                    }
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5
                }
            });

            var result = await WaitTillTableCreated(client, tableName, response);

            return result;
        }

        public static async Task<DescribeTableResponse> WaitTillTableCreated(IAmazonDynamoDB client, string tableName,
                             CreateTableResponse response)
        {
            DescribeTableResponse resp = new DescribeTableResponse();

            var tableDescription = response.TableDescription;

            string status = tableDescription.TableStatus;

            Int32 sleepDuration = 1000; // One second

            while ((status != "ACTIVE") && (sleepDuration < 10000)) // Don't wait more than 10 seconds
            {
                System.Threading.Thread.Sleep(sleepDuration);

                resp = await client.DescribeTableAsync(new DescribeTableRequest
                {
                    TableName = tableName
                });

                status = resp.Table.TableStatus;

                sleepDuration *= 2;
            }

            return resp;
        }

        public static async Task<DescribeTableResponse> WaitTillTableDeleted(IAmazonDynamoDB client, string tableName,
                             DeleteTableResponse response)
        {
            DescribeTableResponse resp = new DescribeTableResponse();
            var tableDescription = response.TableDescription;

            string status = tableDescription.TableStatus;

            Int32 sleepDuration = 1000; // One second

            while ((status == "DELETING") && (sleepDuration < 10000))
            {
                System.Threading.Thread.Sleep(sleepDuration);

                resp = await client.DescribeTableAsync(new DescribeTableRequest
                {
                    TableName = tableName
                });

                status = resp.Table.TableStatus;

                sleepDuration *= 2;
            }

            return resp;
        }

        public static void LoadSampleProducts(IAmazonDynamoDB client)
        {
            Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
            // ********** Add Books *********************
            var book1 = new Document();
            book1["Id"] = 101;
            book1["Title"] = "Book 101 Title";
            book1["ISBN"] = "111-1111111111";
            book1["Authors"] = new List<string> { "Author 1" };
            book1["Price"] = -2; // *** Intentional value. Later used to illustrate scan.
            book1["Dimensions"] = "8.5 x 11.0 x 0.5";
            book1["PageCount"] = 500;
            book1["InPublication"] = true;
            book1["ProductCategory"] = "Book";

            productCatalogTable.PutItemAsync(book1);

            var book2 = new Document
            {
                ["Id"] = 102,
                ["Title"] = "Book 102 Title",
                ["ISBN"] = "222-2222222222",
                ["Authors"] = new List<string> { "Author 1", "Author 2" }
            };
            
            book2["Price"] = 20;
            book2["Dimensions"] = "8.5 x 11.0 x 0.8";
            book2["PageCount"] = 600;
            book2["InPublication"] = true;
            book2["ProductCategory"] = "Book";

            productCatalogTable.PutItemAsync(book2);

            var book3 = new Document();
            book3["Id"] = 103;
            book3["Title"] = "Book 103 Title";
            book3["ISBN"] = "333-3333333333";
            book3["Authors"] = new List<string> { "Author 1", "Author2", "Author 3" };
            book3["Price"] = 2000;
            book3["Dimensions"] = "8.5 x 11.0 x 1.5";
            book3["PageCount"] = 700;
            book3["InPublication"] = false;
            book3["ProductCategory"] = "Book";

            productCatalogTable.PutItemAsync(book3);

            // ************ Add bikes. *******************
            var bicycle1 = new Document();
            bicycle1["Id"] = 201;
            bicycle1["Title"] = "18-Bike 201"; // size, followed by some title.
            bicycle1["Description"] = "201 description";
            bicycle1["BicycleType"] = "Road";
            bicycle1["Brand"] = "Brand-Company A"; // Trek, Specialized.
            bicycle1["Price"] = 100;
            bicycle1["Color"] = new List<string> { "Red", "Black" };
            bicycle1["ProductCategory"] = "Bike";

            productCatalogTable.PutItemAsync(bicycle1);

            var bicycle2 = new Document();
            bicycle2["Id"] = 202;
            bicycle2["Title"] = "21-Bike 202Brand-Company A";
            bicycle2["Description"] = "202 description";
            bicycle2["BicycleType"] = "Road";
            bicycle2["Brand"] = "";
            bicycle2["Price"] = 200;
            bicycle2["Color"] = new List<string> { "Green", "Black" };
            bicycle2["ProductCategory"] = "Bicycle";

            productCatalogTable.PutItemAsync(bicycle2);

            var bicycle3 = new Document();
            bicycle3["Id"] = 203;
            bicycle3["Title"] = "19-Bike 203";
            bicycle3["Description"] = "203 description";
            bicycle3["BicycleType"] = "Road";
            bicycle3["Brand"] = "Brand-Company B";
            bicycle3["Price"] = 300;
            bicycle3["Color"] = new List<string> { "Red", "Green", "Black" };
            bicycle3["ProductCategory"] = "Bike";

            productCatalogTable.PutItemAsync(bicycle3);

            var bicycle4 = new Document();
            bicycle4["Id"] = 204;
            bicycle4["Title"] = "18-Bike 204";
            bicycle4["Description"] = "204 description";
            bicycle4["BicycleType"] = "Mountain";
            bicycle4["Brand"] = "Brand-Company B";
            bicycle4["Price"] = 400;
            bicycle4["Color"] = new List<string> { "Red" };
            bicycle4["ProductCategory"] = "Bike";

            productCatalogTable.PutItemAsync(bicycle4);

            var bicycle5 = new Document();
            bicycle5["Id"] = 205;
            bicycle5["Title"] = "20-Title 205";
            bicycle4["Description"] = "205 description";
            bicycle5["BicycleType"] = "Hybrid";
            bicycle5["Brand"] = "Brand-Company C";
            bicycle5["Price"] = 500;
            bicycle5["Color"] = new List<string> { "Red", "Black" };
            bicycle5["ProductCategory"] = "Bike";

            productCatalogTable.PutItemAsync(bicycle5);
        }

        public static void LoadSampleForums(IAmazonDynamoDB client)
        {
            Table forumTable = Table.LoadTable(client, "Forum");

            var forum1 = new Document();
            forum1["Name"] = "Amazon DynamoDB"; // PK
            forum1["Category"] = "Amazon Web Services";
            forum1["Threads"] = 2;
            forum1["Messages"] = 4;
            forum1["Views"] = 1000;

            forumTable.PutItemAsync(forum1);

            var forum2 = new Document();
            forum2["Name"] = "Amazon S3"; // PK
            forum2["Category"] = "Amazon Web Services";
            forum2["Threads"] = 1;

            forumTable.PutItemAsync(forum2);
        }

        public static void LoadSampleThreads(IAmazonDynamoDB client)
        {
            Table threadTable = Table.LoadTable(client, "Thread");

            // Thread 1.
            var thread1 = new Document();
            thread1["ForumName"] = "Amazon DynamoDB"; // Hash attribute.
            thread1["Subject"] = "DynamoDB Thread 1"; // Range attribute.
            thread1["Message"] = "DynamoDB thread 1 message text";
            thread1["LastPostedBy"] = "User A";
            thread1["LastPostedDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(14, 0, 0, 0));
            thread1["Views"] = 0;
            thread1["Replies"] = 0;
            thread1["Answered"] = false;
            thread1["Tags"] = new List<string> { "index", "primarykey", "table" };

            threadTable.PutItemAsync(thread1);

            // Thread 2.
            var thread2 = new Document();
            thread2["ForumName"] = "Amazon DynamoDB"; // Hash attribute.
            thread2["Subject"] = "DynamoDB Thread 2"; // Range attribute.
            thread2["Message"] = "DynamoDB thread 2 message text";
            thread2["LastPostedBy"] = "User A";
            thread2["LastPostedDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(21, 0, 0, 0));
            thread2["Views"] = 0;
            thread2["Replies"] = 0;
            thread2["Answered"] = false;
            thread2["Tags"] = new List<string> { "index", "primarykey", "rangekey" };

            threadTable.PutItemAsync(thread2);

            // Thread 3.
            var thread3 = new Document();
            thread3["ForumName"] = "Amazon S3"; // Hash attribute.
            thread3["Subject"] = "S3 Thread 1"; // Range attribute.
            thread3["Message"] = "S3 thread 3 message text";
            thread3["LastPostedBy"] = "User A";
            thread3["LastPostedDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(7, 0, 0, 0));
            thread3["Views"] = 0;
            thread3["Replies"] = 0;
            thread3["Answered"] = false;
            thread3["Tags"] = new List<string> { "largeobjects", "multipart upload" };
            threadTable.PutItemAsync(thread3);
        }

        public static void LoadSampleReplies(IAmazonDynamoDB client)
        {
            Table replyTable = Table.LoadTable(client, "Reply");

            // Reply 1 - thread 1.
            var thread1Reply1 = new Document();
            thread1Reply1["Id"] = "Amazon DynamoDB#DynamoDB Thread 1"; // Hash attribute.
            thread1Reply1["ReplyDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(21, 0, 0, 0)); // Range attribute.
            thread1Reply1["Message"] = "DynamoDB Thread 1 Reply 1 text";
            thread1Reply1["PostedBy"] = "User A";

            replyTable.PutItemAsync(thread1Reply1);

            // Reply 2 - thread 1.
            var thread1Reply2 = new Document
            {
                ["Id"] = "Amazon DynamoDB#DynamoDB Thread 1", // Hash attribute.
                ["ReplyDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(14, 0, 0, 0)), // Range attribute.
                ["Message"] = "DynamoDB Thread 1 Reply 2 text",
                ["PostedBy"] = "User B"
            };

            replyTable.PutItemAsync(thread1Reply2);

            // Reply 3 - thread 1.
            var thread1Reply3 = new Document
            {
                ["Id"] = "Amazon DynamoDB#DynamoDB Thread 1", // Hash attribute.
                ["ReplyDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(7, 0, 0, 0)), // Range attribute.
                ["Message"] = "DynamoDB Thread 1 Reply 3 text",
                ["PostedBy"] = "User B"
            };

            replyTable.PutItemAsync(thread1Reply3);

            // Reply 1 - thread 2.
            var thread2Reply1 = new Document
            {
                ["Id"] = "Amazon DynamoDB#DynamoDB Thread 2", // Hash attribute.
                ["ReplyDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(7, 0, 0, 0)), // Range attribute.
                ["Message"] = "DynamoDB Thread 2 Reply 1 text",
                ["PostedBy"] = "User A"
            };

            replyTable.PutItemAsync(thread2Reply1);

            // Reply 2 - thread 2.
            var thread2Reply2 = new Document
            {
                ["Id"] = "Amazon DynamoDB#DynamoDB Thread 2", // Hash attribute.
                ["ReplyDateTime"] = DateTime.UtcNow.Subtract(new TimeSpan(1, 0, 0, 0)), // Range attribute.
                ["Message"] = "DynamoDB Thread 2 Reply 2 text",
                ["PostedBy"] = "User A"
            };

            replyTable.PutItemAsync(thread2Reply2);
        }

        public static void Main()
        {
            AmazonDynamoDBClient client = new AmazonDynamoDBClient();

            // Delete tables

            Console.WriteLine("Deleting ProductCatalog table");
            var deleteTable = DeleteTable(client, "ProductCatalog");

            if (deleteTable.Result.HttpStatusCode != HttpStatusCode.OK)
            {
                Console.WriteLine("Could not delete ProductCatalog table");
            }

            Console.WriteLine("Deleting Forum table");
            deleteTable = DeleteTable(client, "Forum");

            if (deleteTable.Result.HttpStatusCode != HttpStatusCode.OK)
            {
                Console.WriteLine("Could not delete Forum table");
            }

            Console.WriteLine("Deleting Thread table");
            deleteTable = DeleteTable(client, "Thread");

            if (deleteTable.Result.HttpStatusCode != HttpStatusCode.OK)
            {
                Console.WriteLine("Could not delete Thread table");
            }

            Console.WriteLine("Deleting Reply table");
            deleteTable = DeleteTable(client, "Reply");

            if (deleteTable.Result.HttpStatusCode != HttpStatusCode.OK)
            {
                Console.WriteLine("Could not delete Reply table");
            }

            // Create tables

            Console.WriteLine("Creating ProductCatalog table");
            var createTableResponse = CreateTableProductCatalog(client);

            Console.WriteLine("The status of the ProductCatalog table is " + createTableResponse.Status);

            Console.WriteLine("Creating Forum table");
            createTableResponse = CreateTableForum(client);

            Console.WriteLine("The status of the Forum table is " + createTableResponse.Status);

            Console.WriteLine("Creating Thread table");
            createTableResponse = CreateTableThread(client);

            Console.WriteLine("The status of the Thread table is " + createTableResponse.Status);

            Console.WriteLine("Creating Reply table");
            createTableResponse = CreateTableReply(client);

            Console.WriteLine("The status of the Reply table is " + createTableResponse.Status);

            Console.WriteLine();
            Console.WriteLine("Press ENTER to continue");
            Console.ReadLine();

            // Load data into tables

            Console.WriteLine("Loading data into ProductCatalog table");
            LoadSampleProducts(client);

            Console.WriteLine("Loading data into Forum table");
            LoadSampleForums(client);

            Console.WriteLine("Loading data into Thread table");
            LoadSampleThreads(client);

            Console.WriteLine("Loading data into Reply table");
            LoadSampleReplies(client);

            Console.WriteLine();
            Console.WriteLine("Sample complete!");
            Console.WriteLine("Press ENTER to continue");
            Console.ReadLine();
        }  
    }
}
// snippet-end:[dynamodb.dotnet35.CreateTablesLoadData]