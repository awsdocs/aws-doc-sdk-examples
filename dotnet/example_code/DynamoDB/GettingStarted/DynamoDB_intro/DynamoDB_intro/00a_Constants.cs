// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.00a_Constants] 
using System.Collections.Generic;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
  public partial class DdbIntro
  {
    public const string CommaSep = ", ";
    public const string StepString =
      "\n--------------------------------------------------------------------------------------" +
      "\n    STEP {0}:  {1}" +
      "\n--------------------------------------------------------------------------------------";

    /*---------------------------------------------------------
     *    1.  The data used to create a new table
     *---------------------------------------------------------*/
    public const string MoviesTableName = "Movies";

    // key names for the Movies table
    public const string PartitionKeyName = "year";
    public const string SortKeyName      = "title";

    // movie_items_attributes
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

    // movies_key_schema
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
      = new ProvisionedThroughput( 1, 1 );

    /*---------------------------------------------------------
     *    The JSON movies data file to load
     *---------------------------------------------------------*/
    public const string MovieDataPath = "./.moviedata.json";
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.00a_Constants]