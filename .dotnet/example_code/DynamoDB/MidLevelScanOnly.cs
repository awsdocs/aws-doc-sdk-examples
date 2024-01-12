// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.MidLevelScanOnly] 
using System;
using System.Collections.Generic;
using System.Linq;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;

namespace com.amazonaws.codesamples
{
    class MidLevelScanOnly
    {
        private static AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        static void Main(string[] args)
        {
            Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
            // Scan example.
            FindProductsWithNegativePrice(productCatalogTable);
            FindProductsWithNegativePriceWithConfig(productCatalogTable);

            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();
        }

        private static void FindProductsWithNegativePrice(Table productCatalogTable)
        {
            // Assume there is a price error. So we scan to find items priced < 0.
            ScanFilter scanFilter = new ScanFilter();
            scanFilter.AddCondition("Price", ScanOperator.LessThan, 0);

            Search search = productCatalogTable.Scan(scanFilter);

            List<Document> documentList = new List<Document>();
            do
            {
                documentList = search.GetNextSet();
                Console.WriteLine("\nFindProductsWithNegativePrice: printing ............");
                foreach (var document in documentList)
                    PrintDocument(document);
            } while (!search.IsDone);
        }

        private static void FindProductsWithNegativePriceWithConfig(Table productCatalogTable)
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

            List<Document> documentList = new List<Document>();
            do
            {
                documentList = search.GetNextSet();
                Console.WriteLine("\nFindProductsWithNegativePriceWithConfig: printing ............");
                foreach (var document in documentList)
                    PrintDocument(document);
            } while (!search.IsDone);
        }

        private static void PrintDocument(Document document)
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
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.MidLevelScanOnly]