// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CreateTable
{
    /// <summary>
    /// Creates an Amazon DynamoDB table called EmployeeTable. The table
    /// contains the following pieces of data: Id, FirstName, Phone, and
    /// HireDate.
    /// </summary>
    class CreateTable
    {
        public static async Task Main()
        {
            IAmazonDynamoDB client = new AmazonDynamoDBClient();
            const string tableName = "EmployeeTable";

            var tableAttributes = new List<AttributeDefinition>()
            {
                new AttributeDefinition
                {
                    AttributeName = "Id",
                    AttributeType = "N"
                },
                new AttributeDefinition
                {
                    AttributeName = "HireDate",
                    AttributeType = "S"
                }
            };

            var keySchema = new List<KeySchemaElement>()
            {
                new KeySchemaElement
                {
                    AttributeName = "Id",
                    KeyType = "HASH"
                },
                new KeySchemaElement
                {
                    AttributeName = "HireDate", // Range attribute
                    KeyType = "RANGE"
                },
            };

            var provisionedThroughput = new ProvisionedThroughput
            {
                ReadCapacityUnits = 10,
                WriteCapacityUnits = 5
            };

            var tableCreated = await CreateNewTableAsync(client, tableName, tableAttributes, keySchema, provisionedThroughput);

            if(tableCreated)
            {
                // Call AddSampleData with the array of documents to add
                // the data to the new table.
                await AddSampleData(client, tableName);
            }
        }

        /// <summary>
        /// This method creates a new DynamoDB table with the table name and
        /// attributes passed.
        /// </summary>
        /// <param name="client">The DynamoDB client object used to call the
        /// CreateTableAsync method.</param>
        /// <param name="tableName">A string representing the name of the table
        /// to create.</param>
        /// <param name="tableAttributes">A list of the attributes for the new
        /// table. Each attribute describes one aspect of a DynamoDB item, Name
        /// for example, and the type of value stored in that attribute.</param>
        /// <param name="keySchema">A list of KeySchema objects that describes the
        /// keys for he table.</param>
        /// <param name="throughput"></param>
        /// <returns></returns>
        public static async Task<bool> CreateNewTableAsync(
            IAmazonDynamoDB client,
            string tableName,
            List<AttributeDefinition> tableAttributes,
            List<KeySchemaElement> keySchema,
            ProvisionedThroughput throughput)
        {
            // Need to add a test to see if the table already exists.
            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = tableAttributes,
                KeySchema = keySchema,
                ProvisionedThroughput = throughput
            });

            // It can take time to create a table. Wait until the
            // table status is Active.
            var tableCreated = await WaitTilTableCreated(client, tableName, response.TableDescription);

            return tableCreated;
        }

        /// <summary>
        /// This method waits until the status of the table is "ACTIVE"
        /// to show that the table was created and is ready for us to
        /// add sample data.
        /// </summary>
        /// <param name="client">The DynamoDB client object used to call
        /// DescribeTableAsync.</param>
        /// <param name="tableName">A string representing the name of the table
        /// that was created.</param>
        /// <param name="tableDescription">A TableDescription object used
        /// to check the status of the new table.</param>
        /// <returns></returns>
        public static async Task<bool> WaitTilTableCreated(
            IAmazonDynamoDB client,
            string tableName,
            TableDescription tableDescription)
        {
            DescribeTableResponse resp = new DescribeTableResponse();
            string status = tableDescription.TableStatus;

            // One second.
            Int32 sleepDuration = 1000;

            // Wait for a status of "ACTIVE" to be returned.
            while ((status != "ACTIVE") && (sleepDuration < 10000)) // Don't wait more than 10 seconds.
            {
                System.Threading.Thread.Sleep(sleepDuration);

                resp = await client.DescribeTableAsync(new DescribeTableRequest
                {
                    TableName = tableName
                });

                status = resp.Table.TableStatus;

                sleepDuration *= 2;
            }

            return (status == "ACTIVE");
        }

        /// <summary>
        /// Creates a set of three items and adds each to an Amazon DynamoDB
        /// table. The first item in the list has its date set to one year
        /// before the current date to ensure that executing the Lambda
        /// function will find an employee to whom to send a text message.
        /// </summary>
        /// <param name="client">The DynamoDB client object used to call
        /// PutItemAsync to add each item to the table.</param>
        /// <param name="tableName">The name of the table to which the sample
        /// items will be added.</param>
        public static async Task AddSampleData(IAmazonDynamoDB client, string tableName)
        {
            Table employeeTable = Table.LoadTable(client, tableName);

            // Add sample employee items to the table.
            // Remember to edit the phone number for emp1 to a valid moble
            // number that can receive text messages. The hire date is
            // set to one year before the date that the application runs.
            var emp1 = new Document
            {
                ["Id"] = 101,
                ["FirstName"] = "Jadwiga",
                ["Phone"] = "11234567890",
                ["HireDate"] = $"{DateTime.Now.Year - 1}-{DateTime.Now.Month}-{DateTime.Now.Day}",
            };

            _ = await employeeTable.PutItemAsync(emp1);

            var emp2 = new Document
            {
                ["Id"] = 102,
                ["FirstName"] = "Denis",
                ["Phone"] = "11234567890",
                ["HireDate"] = "2017-3-9",
            };

            _ = await employeeTable.PutItemAsync(emp2);

            var emp3 = new Document
            {
                ["Id"] = 103,
                ["FirstName"] = "Sean",
                ["Phone"] = "11234567890",
                ["HireDate"] = "2013-5-13",
            };

            _ = await employeeTable.PutItemAsync(emp3);
        }
    }
}
