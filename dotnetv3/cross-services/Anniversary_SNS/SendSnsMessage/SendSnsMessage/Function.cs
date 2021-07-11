using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;
using Amazon.Lambda.Core;
using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace SendSnsMessage
{
    public class Function
    {
        
        /// <summary>
        /// This method retrieves any items from the DynamoDB table for people
        /// whose HireDate is one year ago today. If any are returned, it will
        /// send the person a text message, using Amazon Simple Notification
        /// Service, congratulating them on their one-year anniversary.
        /// </summary>
        /// <param name="context"></param>
        public async Task FunctionHandler(ILambdaContext context)
        {
            // Get any employees for whom today is their work anniversary.
            var targetDate = $"{DateTime.Now.Year - 1}-{DateTime.Now.Month}-{DateTime.Now.Day}";

            var DbClient = new AmazonDynamoDBClient();

            var tableName = "EmployeeTable";

            Dictionary<string, AttributeValue> lastKeyEvaluated = null;
            do
            {
                var request = new ScanRequest
                {
                    TableName = tableName,
                    Limit = 2,
                    ExclusiveStartKey = lastKeyEvaluated,
                    ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":val", new AttributeValue {
                         S = targetDate
                     }}
                },
                    FilterExpression = "HireDate = :val",

                    ProjectionExpression = "FirstName, Phone"
                };

                var response = await DbClient.ScanAsync(request);

                foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
                {
                    await SendMessageAsync(item);
                }

                lastKeyEvaluated = response.LastEvaluatedKey;

            } while (lastKeyEvaluated != null && lastKeyEvaluated.Count != 0);

        }

        /// <summary>
        /// This method is responsible for sending an anniversary text message
        /// to those employees whose hire date is one year before today's date.
        /// </summary>
        /// <param name="item">Information about one employee that will be used
        /// to send that person a text message congratulating them on their
        /// one-year anniversary.</param>
        public async Task SendMessageAsync(Dictionary<string, AttributeValue> item)
        {
            var snsClient = new AmazonSimpleNotificationServiceClient();

            // Send congratulatory text messages to the employees.
            var firstName = item["FirstName"].S;
            var phone = item["Phone"].S;

            string message = $"{firstName}, happy one-year anniversary! We are so glad you have joined us.";

            var request = new PublishRequest
            {
                Message = message,
                PhoneNumber = phone
            };

            try
            {
                var response = await snsClient.PublishAsync(request);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error sending message: {ex}");
            }
        }
    }
}
