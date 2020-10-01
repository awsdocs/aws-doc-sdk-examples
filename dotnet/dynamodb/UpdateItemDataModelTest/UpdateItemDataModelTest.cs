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

        // We need to mock IDynamoDBContext.LoadAsync<Entry>(string, string)
        private IDynamoDBContext CreateMockDynamoDBContext()
        {
            var mockDynamoDBContext = new Mock<IDynamoDBContext>();

            /*
             * Mock the following LoadAsync signatures:
             * LoadAsync(object, CancellationToken), where object is the hash key
             * LoadAsync(object, DynamoDBOperationConfig, CancellationToken), where object is the hash key
             * LoadAsync(object, object, CancellationToken), where object, object are the hash key and sort key
             * LoadAsync(object, object, DynamoDBOperationConfig, CancellationToken), where object, object are the hash key and sort key
             * LoadAsync(T, CancellationToken), where T should be an Entry object
             * LoadAsync(T, DynamoDBOperationConfig, CancellationToken), where T should be an Entry object
             */

            // Mock LoadAsync(object, CancellationToken), where object is the hash key
            mockDynamoDBContext.Setup(context => context.LoadAsync(
                It.IsAny<Object>(),
                It.IsAny<CancellationToken>()))
                .Callback<Object, CancellationToken>((hashKey, token) =>
                { })
                .Returns((Object e, CancellationToken token) =>
                {
                    return Task.FromResult((Object)new Entry { ID = _id, Order_Status = _status });
                });            

            // Mock LoadAsync(object, DynamoDBOperationConfig, CancellationToken), where object is the hash key
            mockDynamoDBContext.Setup(context => context.LoadAsync(
                It.IsAny<Object>(),
                It.IsAny<DynamoDBOperationConfig>(),
                It.IsAny<CancellationToken>()))
                .Callback<Object, DynamoDBOperationConfig, CancellationToken>((hashKey, config, token) => { })
                .Returns((Object hashKey, DynamoDBContextConfig config, CancellationToken token) =>
                {
                    return Task.FromResult((Object)new Entry { ID = _id, Order_Status = _status });
                });

            /*
             * 
            // Mock LoadAsync(object, object, CancellationToken), where object, object are the hash key and sort key
            mockDynamoDBContext.Setup(context => context.LoadAsync(
                It.IsAny<Object>(),
                It.IsAny<Object>(),  // Complains that Object isn't DynamoDBOperationConfig
                It.IsAny<CancellationToken>()))
                .Callback<Object, Object, CancellationToken>((hashKey, sortKey, token) =>
                { })
                .Returns((Object hashKey, Object sortKey, CancellationToken token) =>
                {
                    return Task.FromResult((Object)new Entry { ID = _id, Order_Status = _status });
                });

            */

            /*            

            // Mock LoadAsync(object, object, DynamoDBOperationConfig, CancellationToken), where object, object are the hash key and sort key
            // I see LoadAsync underlined in red and get:
            // CS0411: The type arguments for method 'IDynamoDBContext.LoadAsync<T>(object, object, DynamoDBOperationConfig, CancellationToken)' 
            // cannot be inferred from the usage. Try specifying the type arguments explicitly.
            mockDynamoDBContext.Setup(context => context.LoadAsync( // CS0411: 
                It.IsAny<Object>(),
                It.IsAny<Object>(),
                It.IsAny<DynamoDBOperationConfig>(),
                It.IsAny<CancellationToken>()))
                .Callback<Object, Object, DynamoDBOperationConfig, CancellationToken>((hashKey, sortKey, config, token) =>
                { })
                .Returns((Object hashKey, Object sortKey, DynamoDBContextConfig config, CancellationToken token) =>
                {
                    return Task.FromResult(new Entry { ID = _id, Order_Status = _status });
                });
            
            */

            // Mock LoadAsync(T, CancellationToken), where T should be an Entry object
            mockDynamoDBContext.Setup(context => context.LoadAsync(
                It.IsAny<Entry>(),
                It.IsAny<CancellationToken>()))
                .Callback<Entry, CancellationToken>((e, token) =>
                { })
                .Returns((Entry e, CancellationToken token) =>
                {
                    return Task.FromResult(new Entry { ID = _id, Order_Status = _status });
                });


            // Mock LoadAsync(T, DynamoDBOperationConfig, CancellationToken), where T should be an Entry object
            mockDynamoDBContext.Setup(context => context.LoadAsync(
                It.IsAny<Entry>(),
                It.IsAny<DynamoDBOperationConfig>(),
                It.IsAny<CancellationToken>()))
                .Callback<Entry, DynamoDBOperationConfig, CancellationToken>((entry, config, token) =>
                { })
                .Returns((Entry e, DynamoDBOperationConfig config, CancellationToken token) =>
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
