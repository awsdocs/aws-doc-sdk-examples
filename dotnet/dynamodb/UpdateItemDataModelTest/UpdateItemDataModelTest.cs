// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DataModel;

using Moq;

using Xunit;
using Xunit.Abstractions;

namespace DynamoDBCRUD
{
    public class UpdateItemDataModelTest
    {
        private readonly ITestOutputHelper output;

        public UpdateItemDataModelTest(ITestOutputHelper output)
        {
            this.output = output;
        }
    
        readonly string _id = "16";
        readonly string _status = "pending";

        private IDynamoDBContext CreateMockDynamoDBContext()
        {
            var mockDynamoDBContext = new Mock<IDynamoDBContext>();

            mockDynamoDBContext.Setup(context => context.LoadAsync(
                It.IsAny<Entry>(),
                It.IsAny<CancellationToken>()))
                .Callback<Entry, CancellationToken>((request, token) =>
                { })
                .Returns((Entry e, CancellationToken token) =>
                {
                    return Task.FromResult(new Entry { ID = _id, Order_Status = _status });
                });

            return mockDynamoDBContext.Object;
        }

        [Fact]
        public async Task CheckUpdateItemDataModel()
        {
            IDynamoDBContext context = CreateMockDynamoDBContext();
            
            var result = await UpdateItemDataModel.UpdateTableItemAsync(context, _id, _status);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = (result.ID == _id) && (result.Order_Status == _status);
            Assert.True(ok, "Could NOT update item");

            output.WriteLine("Updated item");
        }
    }
}
