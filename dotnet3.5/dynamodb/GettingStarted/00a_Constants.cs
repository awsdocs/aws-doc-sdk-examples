// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.00a_Constants]
using System.Collections.Generic;
using Amazon.DynamoDBv2.Model;

namespace GettingStarted
{
    public partial class DdbIntro
    {
        /*==========================================================================
         * Constant or static values used in code example.
         *==========================================================================*/
        public const string CommaSep = ", ";
        public const string StepString =
          "\n--------------------------------------------------------------------------------------" +
          "\n    STEP {0}:  {1}" +
          "\n--------------------------------------------------------------------------------------";

        /*---------------------------------------------------------
         * The data used to create a new table.
         *---------------------------------------------------------*/
        public const string MoviesTableName = "Movies";

        // key names for the Movies table
        public const string PartitionKeyName = "year";
        public const string SortKeyName = "title";

        public static List<AttributeDefinition> MovieItemsAttributes
            = new List<AttributeDefinition>
            {
                new AttributeDefinition
                {
                    AttributeName = PartitionKeyName,
                    AttributeType = "N"
                },
                new AttributeDefinition
                {
                    AttributeName = SortKeyName,
                    AttributeType = "S"
                }
        };

        public static List<KeySchemaElement> MoviesKeySchema
          = new List<KeySchemaElement>
          {
              new KeySchemaElement
              {
                  AttributeName = PartitionKeyName,
                  KeyType = "HASH"
              },
              new KeySchemaElement
              {
                  AttributeName = SortKeyName,
                  KeyType = "RANGE"
              }
        };

        public static ProvisionedThroughput MoviesTableProvisionedThroughput
          = new ProvisionedThroughput(1, 1);

        /*---------------------------------------------------------
         *    The path to the JSON movies data file to load.
         *---------------------------------------------------------*/
        public const string MovieDataPath = "./.moviedata.json";
    }
}
// snippet-end:[dynamodb.dotnet35.00a_Constants]