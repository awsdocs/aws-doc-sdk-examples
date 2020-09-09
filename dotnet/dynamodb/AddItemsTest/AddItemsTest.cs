// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.VisualStudio.TestTools.UnitTesting.Logging;

using Moq;

using System.Net;
using System.Threading;
using System.Threading.Tasks;

namespace DynamoDBCRUD
{
    [TestClass]
    public class AddItemsTest
    {
        readonly string table = "testtable";
        readonly string keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        readonly string values = "Order,1,1,6,2020-07-04 12:00:00,pending";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.BatchWriteItemAsync(
                It.IsAny<BatchWriteItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<BatchWriteItemRequest, CancellationToken>((request, token) =>
                {
                    if (string.IsNullOrEmpty(table))
                    {
                        throw new System.ArgumentNullException("You must supply a table value");
                    }
                })
                .Returns((BatchWriteItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new BatchWriteItemResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [TestMethod]
        public async Task CheckAddItems()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            Logger.LogMessage("Calling AddItem.AddItemAsync(client, tableName, keys, values)");

            // Create random ID value
            var _random = new System.Random();
            int id = _random.Next(100, 1000);

            var inputs = new string[2];
            inputs[0] = keys;
            inputs[1] = values;

            var result = await AddItems.AddItemsAsync(false, client, table, inputs, id);

            if (result.HttpStatusCode == HttpStatusCode.OK)
            {
                Logger.LogMessage("Added items to table " + table);
            }
            else
            {
                Logger.LogMessage("Could NOT add items to table " + table);
            }
        }
    }
}
