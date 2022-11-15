// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[dynamodb.dotnetv3.LowLevelLocalSecondaryIndexExample]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace LowLevelLocalSecondaryIndexExample
{
    public class LowLevelLocalSecondaryIndexExample
    {
        private static readonly string _tableName = "CustomerOrders";

        public static async Task<bool> CreateTable(AmazonDynamoDBClient client)
        {
            var createTableRequest =
                new CreateTableRequest()
                {
                    TableName = _tableName,
                    ProvisionedThroughput =
                    new ProvisionedThroughput()
                    {
                        ReadCapacityUnits = 1,
                        WriteCapacityUnits = 1
                    }
                };

            var attributeDefinitions = new List<AttributeDefinition>()
            {
            // Attribute definitions for table primary key.
            { new AttributeDefinition() {
                  AttributeName = "CustomerId", AttributeType = "S"
              } },
            { new AttributeDefinition() {
                  AttributeName = "OrderId", AttributeType = "N"
              } },
            // Attribute definitions for index primary key.
            { new AttributeDefinition() {
                  AttributeName = "OrderCreationDate", AttributeType = "N"
              } },
            { new AttributeDefinition() {
                  AttributeName = "IsOpen", AttributeType = "N"
            }}};

            createTableRequest.AttributeDefinitions = attributeDefinitions;

            // Key schema for table.
            var tableKeySchema = new List<KeySchemaElement>()
            {
            { new KeySchemaElement() {
                  AttributeName = "CustomerId", KeyType = "HASH"
              } }, // Partition key.
            { new KeySchemaElement() {
                  AttributeName = "OrderId", KeyType = "RANGE"
              } } // Sort key.
        };

            createTableRequest.KeySchema = tableKeySchema;

            var localSecondaryIndexes = new List<LocalSecondaryIndex>();

            // OrderCreationDateIndex.
            var orderCreationDateIndex = new LocalSecondaryIndex()
            {
                IndexName = "OrderCreationDateIndex"
            };

            // Key schema for OrderCreationDateIndex.
            var indexKeySchema = new List<KeySchemaElement>()
        {
            { new KeySchemaElement() {
                  AttributeName = "CustomerId", KeyType = "HASH"
              } }, // Partition key.
            { new KeySchemaElement() {
                  AttributeName = "OrderCreationDate", KeyType = "RANGE"
              } } // Sort key.
        };

            orderCreationDateIndex.KeySchema = indexKeySchema;

            // Projection (with list of projected attributes) for
            // OrderCreationDateIndex.
            var projection = new Projection()
            {
                ProjectionType = "INCLUDE"
            };

            var nonKeyAttributes = new List<string>()
        {
            "ProductCategory",
            "ProductName"
        };
            projection.NonKeyAttributes = nonKeyAttributes;

            orderCreationDateIndex.Projection = projection;

            localSecondaryIndexes.Add(orderCreationDateIndex);

            // IsOpenIndex.
            var isOpenIndex
                = new LocalSecondaryIndex()
                {
                    IndexName = "IsOpenIndex"
                };

            // Key schema for IsOpenIndex.
            indexKeySchema = new List<KeySchemaElement>()
        {
            { new KeySchemaElement() {
                  AttributeName = "CustomerId", KeyType = "HASH"
              }},  // Partition key.
            { new KeySchemaElement() {
                  AttributeName = "IsOpen", KeyType = "RANGE"
              }} // Sort key.
        };

            // Projection (all attributes) for IsOpenIndex.
            projection = new Projection()
            {
                ProjectionType = "ALL"
            };

            isOpenIndex.KeySchema = indexKeySchema;
            isOpenIndex.Projection = projection;

            localSecondaryIndexes.Add(isOpenIndex);

            // Add index definitions to CreateTable request.
            createTableRequest.LocalSecondaryIndexes = localSecondaryIndexes;

            Console.WriteLine("Creating table " + _tableName + "...");
            await client.CreateTableAsync(createTableRequest);
            WaitUntilTableReady(client, _tableName);

            return true;
        }

        public static async Task<bool> Query(AmazonDynamoDBClient client, string indexName)
        {
            Console.WriteLine("\n***********************************************************\n");
            Console.WriteLine("Querying table " + _tableName + "...");

            var queryRequest = new QueryRequest()
            {
                TableName = _tableName,
                ConsistentRead = true,
                ScanIndexForward = true,
                ReturnConsumedCapacity = "TOTAL"
            };

            String keyConditionExpression = "CustomerId = :v_customerId";
            var expressionAttributeValues = new Dictionary<string, AttributeValue> {
                {":v_customerId", new AttributeValue {
                    S = "bob@example.com"
                }}
            };

            if (indexName == "IsOpenIndex")
            {
                Console.WriteLine("\nUsing index: '" + indexName
                          + "': Bob's orders that are open.");
                Console.WriteLine("Only a user-specified list of attributes are returned.\n");
                queryRequest.IndexName = indexName;

                keyConditionExpression += " and IsOpen = :v_isOpen";
                expressionAttributeValues.Add(":v_isOpen", new AttributeValue
                {
                    N = "1"
                });

                // ProjectionExpression
                queryRequest.ProjectionExpression = "OrderCreationDate, ProductCategory, ProductName, OrderStatus";
            }
            else if (indexName == "OrderCreationDateIndex")
            {
                Console.WriteLine("\nUsing index: '" + indexName
                          + "': Bob's orders that were placed after 01/31/2013.");
                Console.WriteLine("Only the projected attributes are returned.\n");
                queryRequest.IndexName = indexName;

                keyConditionExpression += " and OrderCreationDate > :v_Date";
                expressionAttributeValues.Add(":v_Date", new AttributeValue
                {
                    N = "20130131"
                });

                // Select
                queryRequest.Select = "ALL_PROJECTED_ATTRIBUTES";
            }
            else
            {
                Console.WriteLine("\nNo index: All of Bob's orders, by OrderId:\n");
            }

            queryRequest.KeyConditionExpression = keyConditionExpression;
            queryRequest.ExpressionAttributeValues = expressionAttributeValues;

            var result = await client.QueryAsync(queryRequest);
            var items = result.Items;

            foreach (var currentItem in items)
            {
                foreach (string attr in currentItem.Keys)
                {
                    if (attr == "OrderId" || attr == "IsOpen"
                        || attr == "OrderCreationDate")
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

            Console.WriteLine("\nConsumed capacity: " + result.ConsumedCapacity.CapacityUnits + "\n");

            return true;
        }

        public static async Task<bool> DeleteTable(AmazonDynamoDBClient client)
        {
            Console.WriteLine("Deleting table " + _tableName + "...");
            await client.DeleteTableAsync(new DeleteTableRequest()
            {
                TableName = _tableName
            });

            WaitForTableToBeDeleted(client, _tableName);

            return true;
        }

        public static async Task<bool> LoadData(AmazonDynamoDBClient client)
        {
            Console.WriteLine("Loading data into table " + _tableName + "...");

            var item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "alice@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "1"
                },

                ["IsOpen"] = new AttributeValue
                {
                    N = "1"
                },

                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130101"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Book"
                },

                ["ProductName"] = new AttributeValue
                {
                    S = "The Great Outdoors"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "PACKING ITEMS"
                }
            };

            /* no ShipmentTrackingId attribute */
            var putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "alice@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "2"
                },

                ["IsOpen"] = new AttributeValue
                {
                    N = "1"
                },

                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130221"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Bike"
                },

                ["ProductName"] = new AttributeValue
                {
                    S = "Super Mountain"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "ORDER RECEIVED"
                }
            };

            /* no ShipmentTrackingId attribute */
            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "alice@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "3"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130304"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Music"
                },
                ["ProductName"] = new AttributeValue
                {
                    S = "A Quiet Interlude"
                },
                ["OrderStatus"] = new AttributeValue
                {
                    S = "IN TRANSIT"
                },
                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "176493"
                }
            };

            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "1"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130111"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Movie"
                },

                ["ProductName"] = new AttributeValue
                {
                    S = "Calm Before The Storm"
                },
                ["OrderStatus"] = new AttributeValue
                {
                    S = "SHIPPING DELAY"
                },

                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "859323"
                }
            };

            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "2"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130124"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Music"
                },

                ["ProductName"] = new AttributeValue
                {
                    S = "E-Z Listening"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "DELIVERED"
                },

                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "756943"
                }
            };

            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "3"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130221"
                },


                ["ProductCategory"] = new AttributeValue
                {
                    S = "Music"
                },
                ["ProductName"] = new AttributeValue
                {
                    S = "Symphony 9"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "DELIVERED"
                },

                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "645193"
                }
            };
            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "4"
                },

                ["IsOpen"] = new AttributeValue
                {
                    N = "1"
                },

                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130222"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Hardware"
                },
                ["ProductName"] = new AttributeValue
                {
                    S = "Extra Heavy Hammer"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "PACKING ITEMS"
                }
            };

            /* no ShipmentTrackingId attribute */
            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "5"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130309"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Book"
                },

                ["ProductName"] = new AttributeValue
                {
                    S = "How To Cook"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "IN TRANSIT"
                },

                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "440185"
                }
            };

            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "6"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130318"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Luggage"
                },
                ["ProductName"] = new AttributeValue
                {
                    S = "Really Big Suitcase"
                },
                ["OrderStatus"] = new AttributeValue
                {
                    S = "DELIVERED"
                },

                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "893927"
                }
            };

            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            item = new Dictionary<string, AttributeValue>
            {
                ["CustomerId"] = new AttributeValue
                {
                    S = "bob@example.com"
                },

                ["OrderId"] = new AttributeValue
                {
                    N = "7"
                },

                /* no IsOpen attribute */
                ["OrderCreationDate"] = new AttributeValue
                {
                    N = "20130324"
                },

                ["ProductCategory"] = new AttributeValue
                {
                    S = "Golf"
                },

                ["ProductName"] = new AttributeValue
                {
                    S = "PGA Pro II"
                },

                ["OrderStatus"] = new AttributeValue
                {
                    S = "OUT FOR DELIVERY"
                },

                ["ShipmentTrackingId"] = new AttributeValue
                {
                    S = "383283"
                }
            };

            putItemRequest = new PutItemRequest
            {
                TableName = _tableName,
                Item = item,
                ReturnItemCollectionMetrics = "SIZE"
            };

            await client.PutItemAsync(putItemRequest);

            return true;
        }

        private static async void WaitUntilTableReady(AmazonDynamoDBClient client, string tableName)
        {
            string status = null;
            // Wait until table is created. Call DescribeTable.
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
                Console.WriteLine("Could not create table.");
                return;
            }

            result = LoadData(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not load data into table.");
                return;
            }

            result = Query(client, null);

            if (!result.Result)
            {
                Console.WriteLine("Could not query table.");
                return;
            }
            result = Query(client, "IsOpenIndex");

            if (!result.Result)
            {
                Console.WriteLine("Could not query table.");
                return;
            }
            result = Query(client, "OrderCreationDateIndex");

            if (!result.Result)
            {
                Console.WriteLine("Could not query table.");
                return;
            }

            result = DeleteTable(client);

            if (!result.Result)
            {
                Console.WriteLine("Could not delete table.");
                return;
            }
        }
    }
}
// snippet-end:[dynamodb.dotnetv3.LowLevelLocalSecondaryIndexExample]