// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.MidLevelScanOnly]
using System;
using System.Collections.Generic;
using System.Linq;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;

namespace MidLevelScanOnly
{
    public class MidLevelScanOnly
    {
        public static async void FindProductsWithNegativePrice(AmazonDynamoDBClient client, Table productCatalogTable)
        {
            // Assume there is a price error. So we scan to find items priced < 0.
            ScanFilter scanFilter = new ScanFilter();
            scanFilter.AddCondition("Price", ScanOperator.LessThan, 0);

            Search search = productCatalogTable.Scan(scanFilter);

            do
            {
                var documentList = await search.GetNextSetAsync();
                Console.WriteLine("\nFindProductsWithNegativePrice: printing ............");

                foreach (var document in documentList)
                    PrintDocument(document);

            } while (!search.IsDone);
        }

        public static async void FindProductsWithNegativePriceWithConfig(AmazonDynamoDBClient client, Table productCatalogTable)
        {
            // Assume there is a price error. So we scan to find items priced < 0.
            ScanFilter scanFilter = new ScanFilter();
            scanFilter.AddCondition("Price", ScanOperator.LessThan, 0);

            ScanOperationConfig config = new ScanOperationConfig()
            {
                Filter = scanFilter,
                Select = SelectValues.SpecificAttributes,
                AttributesToGet = new List<string> { "Title", "Id" }
            };

            Search search = productCatalogTable.Scan(config);

            do
            {
                var documentList = await search.GetNextSetAsync();
                Console.WriteLine("\nFindProductsWithNegativePriceWithConfig: printing ............");

                foreach (var document in documentList)
                    PrintDocument(document);

            } while (!search.IsDone);
        }

        public static void PrintDocument(Document document)
        {
            //   count++;
            Console.WriteLine();
            foreach (var attribute in document.GetAttributeNames())
            {
                string stringValue = null;
                var value = document[attribute];
                if (value is Primitive)
                    stringValue = value.AsPrimitive().Value.ToString();
                else if (value is PrimitiveList)
                    stringValue = string.Join(",", (from primitive
                                    in value.AsPrimitiveList().Entries
                                                    select primitive.Value).ToArray());
                Console.WriteLine("{0} - {1}", attribute, stringValue);
            }
        }
        static void Main()
        {
            var client = new AmazonDynamoDBClient();

            Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
            
            FindProductsWithNegativePrice(client, productCatalogTable);
            FindProductsWithNegativePriceWithConfig(client, productCatalogTable);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.MidLevelScanOnly]