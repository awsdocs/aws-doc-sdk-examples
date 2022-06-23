// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace UpdateItemDataModelExample
{
    using Amazon.DynamoDBv2.DataModel;

    // snippet-start:[dynamodb.dotnet35.UpdateItemDataModel.Entry]

    /// <summary>
    /// If you change the table name elsewhere (for example, in an
    /// app.config in another project), you'll have to change it
    /// here and rebuild.
    /// </summary>
    [DynamoDBTable("CustomersOrdersProducts")]
    public class Entry
    {
        /// <summary>
        /// Gets or sets the partition key for the table.
        /// </summary>
        [DynamoDBHashKey]
        public string Id
        {
            get; set;
        }

        /// <summary>
        /// Gets or sets the sort key for the table.
        /// </summary>
        [DynamoDBRangeKey]
        public string Area
        {
            get; set;
        }

        [DynamoDBProperty]
        public int OrderId
        {
            get; set;
        }

        [DynamoDBProperty]
        public int OrderCustomer
        {
            get; set;
        }

        [DynamoDBProperty]
        public int OrderProduct
        {
            get; set;
        }

        [DynamoDBProperty]
        public long OrderDate
        {
            get; set;
        }

        [DynamoDBProperty]
        public string OrderStatus
        {
            get; set;
        }
    }

    // snippet-end:[dynamodb.dotnet35.UpdateItemDataModel.Entry]
}
