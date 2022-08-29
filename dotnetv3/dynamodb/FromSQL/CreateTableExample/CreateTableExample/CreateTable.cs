// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace CreateTable
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnetv3.CreateTableExample]

    /// <summary>
    /// Shows how to create an Amazon DynamoDB table. The example was created
    /// using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class CreateTable
    {
        /// <summary>
        /// Retrieves configuration information, creates the DynamoDB client,
        /// and calls MakeTableAsync to create the table.
        /// </summary>
        public static async Task Main()
        {
            var configfile = "app.config";
            string region;
            string table;

            // Get default AWS Region and table from config file.
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
                    Console.WriteLine("You must specify the AWS Region and Table values in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine($"Could not find: {configfile}");
                return;
            }

            IAmazonDynamoDB client = new AmazonDynamoDBClient();

            var response = await MakeTableAsync(client, table);

            Console.WriteLine($"Created table: {response.TableDescription.TableName}.");
        }

        /// <summary>
        /// Defines the attributes for the new table and then creates it.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="tableName">The name of the table to create.</param>
        /// <returns>A CreateTableResponse object representing the results of
        /// of the call to CreateTableAsync.</returns>
        public static async Task<CreateTableResponse> MakeTableAsync(
            IAmazonDynamoDB client,
            string tableName)
        {
            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = new List<AttributeDefinition>
                {
                    new AttributeDefinition
                    {
                        AttributeName = "ID",
                        AttributeType = "S",
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "Area",
                        AttributeType = "S",
                    },
                },
                KeySchema = new List<KeySchemaElement>
                {
                    new KeySchemaElement
                    {
                        AttributeName = "ID",
                        KeyType = "HASH",
                    },
                    new KeySchemaElement
                    {
                        AttributeName = "Area",
                        KeyType = "RANGE",
                    },
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5,
                },
            });

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnetv3.CreateTableExample]
}
