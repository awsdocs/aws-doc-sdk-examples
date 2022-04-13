// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelQueryAndScanExample
{
    using Amazon.DynamoDBv2.DataModel;

    // snippet-start:[dynamodb.dotnet35.HighLevelQueryAndScan.Book]

    /// <summary>
    /// Maps information for a book to the Amazon DynamoDB table ProductCatalog.
    /// </summary>
    [DynamoDBTable("ProductCatalog")]
    public class Book
    {
        [DynamoDBHashKey] // Partition key
        public int Id { get; set; }

        public string Title { get; set; }

        public string Isbn { get; set; }

        public int Price { get; set; }

        public string PageCount { get; set; }

        public string ProductCategory { get; set; }

        public bool InPublication { get; set; }
    }

    // snippet-end:[dynamodb.dotnet35.HighLevelQueryAndScan.Book]
}
