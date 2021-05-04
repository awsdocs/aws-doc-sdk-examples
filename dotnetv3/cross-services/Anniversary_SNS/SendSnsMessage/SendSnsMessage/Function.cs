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
        /// A simple function that takes a string and does a ToUpper
        /// </summary>
        /// <param name="input"></param>
        /// <param name="context"></param>
        /// <returns></returns>
        public async Task FunctionHandler(ILambdaContext context)
        {
            // Get any employees for whom today is their work anniversary.
            //var targetDate = $"{DateTime.Now.Year - 1}-{DateTime.Now.Month}-{DateTime.Now.Day}";
            var targetDate = "2020-5-3";

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
                    FilterExpression = "HireDate < :val",

                    ProjectionExpression = "FirstName, Phone"
                };

                var response = await DbClient.ScanAsync(request);

                foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
                {
                    await SendMessageAsync(targetDate, item);
                }

                lastKeyEvaluated = response.LastEvaluatedKey;

            } while (lastKeyEvaluated != null && lastKeyEvaluated.Count != 0);

        }

        public async Task SendMessageAsync(string targetDate, Dictionary<string, AttributeValue> item)
        {
            var snsClient = new AmazonSimpleNotificationServiceClient();

            // Send congratulatory text messages to the employees.
            var specialMsg = (targetDate == "2020-5-4") ? "May the fourth be with you!" : "";
            var firstName = item["FirstName"];
            var phone = item["Phone"].ToString();

            string message = $"{firstName}, happy one-year anniversary! {specialMsg} We are so glad you have joined us.";

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
