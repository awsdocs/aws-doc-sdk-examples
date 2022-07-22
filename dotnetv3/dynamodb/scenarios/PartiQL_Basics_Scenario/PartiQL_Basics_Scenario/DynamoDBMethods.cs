// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace PartiQL_Basics_Scenario
{
    /// <summary>
    /// This class is used to create the Amazon DynamoDB table that is used by
    /// the PartiQL scenario. The methods of this class create the movies
    /// table and clean up resources after the scenario is complete.
    /// </summary>
    public class DynamoDBMethods
    {
        private static readonly AmazonDynamoDBClient Client = new AmazonDynamoDBClient();

        /// <summary>
        /// Creates a new DynamoDB table and then waits for the new
        /// table to become active.
        /// </summary>
        /// <param name="tableName">The name of the table to create.</param>
        /// <returns>A Boolean value that indicates the success of the operation.</returns>
        public static async Task<bool> CreateMovieTableAsync(string tableName)
        {
            var response = await Client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = new List<AttributeDefinition>()
                {
                    new AttributeDefinition
                    {
                        AttributeName = "title",
                        AttributeType = "S",
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "year",
                        AttributeType = "N",
                    },
                },
                KeySchema = new List<KeySchemaElement>()
                {
                    new KeySchemaElement
                    {
                        AttributeName = "year",
                        KeyType = "HASH",
                    },
                    new KeySchemaElement
                    {
                        AttributeName = "title",
                        KeyType = "RANGE",
                    },
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 5,
                    WriteCapacityUnits = 5,
                },
            });

            // Wait until the table is ACTIVE and then report success.
            Console.Write("Waiting for table to become active...");

            var request = new DescribeTableRequest
            {
                TableName = response.TableDescription.TableName,
            };

            TableStatus status;

            int sleepDuration = 2000;

            do
            {
                System.Threading.Thread.Sleep(sleepDuration);

                var describeTableResponse = await Client.DescribeTableAsync(request);
                status = describeTableResponse.Table.TableStatus;

                Console.Write(".");
            }
            while (status != "ACTIVE");
            Console.WriteLine();

            return status == TableStatus.ACTIVE;
        }

        /// <summary>
        /// Deletes the DynamoDB table of movie information when the scenario
        /// is complete.
        /// </summary>
        /// <param name="tableName">The name of the table to delete.</param>
        /// <returns>A Boolean value that indicates the success or failure of
        /// the delete operation.</returns>
        public static async Task<bool> DeleteTableAsync(string tableName)
        {
            var request = new DeleteTableRequest
            {
                TableName = tableName,
            };

            var response = await Client.DeleteTableAsync(request);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Table {response.TableDescription.TableName} successfully deleted.");
                return true;
            }
            else
            {
                Console.WriteLine("Could not delete table.");
                return false;
            }
        }
    }
}