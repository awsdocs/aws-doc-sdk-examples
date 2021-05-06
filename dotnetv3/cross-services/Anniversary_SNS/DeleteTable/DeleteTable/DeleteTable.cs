using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using System;
using System.Threading.Tasks;

namespace DeleteTable
{
    class DeleteTable
    {
        static async Task Main()
        {
            var dbClient = new AmazonDynamoDBClient();

            var tableName = "EmployeeTable";

            try
            {
                var response = await dbClient.DeleteTableAsync(new DeleteTableRequest
                {
                    TableName = tableName
                });

                Console.WriteLine($"Successfully deleted DynamoDB table {tableName}.");
            }
            catch (ResourceNotFoundException)
            {
                Console.WriteLine($"There is no {tableName} table.");
            }

        }
    }
}
