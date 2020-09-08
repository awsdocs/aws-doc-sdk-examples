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
    public class AddItemTest
    {
        readonly string tableName = "testtable";
        readonly string keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        readonly string values = "Order,1,1,6,2020-07-04 12:00:00,pending";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.PutItemAsync(
                It.IsAny<PutItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<PutItemRequest, CancellationToken>((request, token) =>
                {
                    if (string.IsNullOrEmpty(tableName))
                    {
                        throw new System.ArgumentNullException("You must supply a table value");
                    }
                    else
                    { 
                        Assert.AreEqual(tableName, request.TableName);
                    }
                })
                .Returns((PutItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new PutItemResponse { HttpStatusCode = HttpStatusCode.OK  });
                });

            return mockDynamoDBClient.Object;
        }

        [TestMethod]
        public async Task CheckAddItem()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            Logger.LogMessage("Calling AddItem.AddItemAsync(client, tableName, keys, values)");

            // Create random ID value
            var _random = new System.Random();
            string id = _random.Next(100, 1000).ToString();        
            
            var result = await AddItem.AddItemAsync(client, tableName, id, keys, values);

            if (result)
            {
                Logger.LogMessage("Created table " + tableName);
            }
            else
            {
                Logger.LogMessage("Could NOT create table " + tableName);
            }
        }
    }
}
