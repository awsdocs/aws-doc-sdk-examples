// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace GetItemsExample
{
  using System;
  using System.Collections.Generic;
  using System.Configuration;
  using System.Threading.Tasks;
  using Amazon;
  using Amazon.DynamoDBv2;
  using Amazon.DynamoDBv2.Model;

  // snippet-start:[dynamodb.dotnet35.GetItem]

  /// <summary>
  /// Shows how to retrieve data from an Amazon DynamoDB table. This example
  /// was created using the AWS SDK for .NET verion 3.7 and .NET Core 5.0.
  /// </summary>
  public class GetItems
  {
    public static async Task Main(string[] args)
    {
      var configfile = "app.config";
      var region = string.Empty;
      var table = string.Empty;
      var id = string.Empty;

      int i = 0;
      while (i < args.Length)
      {
        switch (args[i])
        {
          case "-i":
            i++;
            id = args[i];
            break;
          default:
            break;
        }

        i++;
      }

      if (id == string.Empty)
      {
        Console.WriteLine("You must supply an item ID (-i ID)");
        return;
      }

      // Get default Region and table from config file
      var efm = new ExeConfigurationFileMap
      {
        ExeConfigFilename = configfile,
      };

      Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

      if (configuration.HasFile)
      {
        AppSettingsSection appSettings = configuration.AppSettings;
        region = appSettings.Settings["Region"].Value;
        table = appSettings.Settings["Table"].Value;

        if ((region == string.Empty) || (table == string.Empty))
        {
          Console.WriteLine($"You must specify Region and Table values in {configfile}");
          return;
        }
      }
      else
      {
        Console.WriteLine("Could not find " + configfile);
        return;
      }

      var newRegion = RegionEndpoint.GetBySystemName(region);
      IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

      var keyValue = new AttributeValue();
      keyValue.N = "201";
      var key = new Dictionary<string, AttributeValue>
      {
        {
          "Id",
          keyValue
        },
      };

      var response = await GetItemAsync(client, table, key);

      foreach (var attr in response.Item)
      {
        Console.WriteLine($"{attr.Key}: {attr.Value}");
      }
    }

    /// <summary>
    /// Performs the query against the DynamoDB table, to retrieve the item
    /// passed in the id parameter.
    /// </summary>
    /// <param name="client">An initialized DynamoDB client object.</param>
    /// <param name="table">The name of the table against which to perform the
    /// query.</param>
    /// <param name="id">The id of the object to retrieve.</param>
    /// <returns>A query response object containing the resulting object.</returns>
    public static async Task<QueryResponse> GetItemAsync(IAmazonDynamoDB client, string table, string id)
    {
      var response = await client.QueryAsync(new QueryRequest
      {
        TableName = table,
        KeyConditionExpression = "ID = :v_Id",
        ExpressionAttributeValues = new Dictionary<string, AttributeValue>
        {
          {
            ":v_Id",
            new AttributeValue
            {
              S = id,
            }
          },
        },
      });

      return response;
    }
  }

  // snippet-end:[dynamodb.dotnet35.GetItem]
}
