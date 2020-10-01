// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.dotnet35.CreateIndex]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class CreateIndex
    {
        public static async Task<UpdateTableResponse> AddIndexAsync(IAmazonDynamoDB client, string table, string indexname, string partitionkey, string partitionkeytype, string sortkey, string sortkeytype)
        {
            if (null == client)
            {
                throw new ArgumentNullException("client parameter is null");
            }

            if (string.IsNullOrEmpty(table))
            {
                throw new ArgumentNullException("table parameter is null");
            }

            if (string.IsNullOrEmpty(indexname))
            {
                throw new ArgumentNullException("indexname parameter is null");
            }

            if (string.IsNullOrEmpty(partitionkey))
            {
                throw new ArgumentNullException("partitionkey parameter is null");
            }

            if (string.IsNullOrEmpty(sortkey))
            {
                throw new ArgumentNullException("sortkey parameter is null");
            }

            ProvisionedThroughput pt = new ProvisionedThroughput
            {
                ReadCapacityUnits = 10L,
                WriteCapacityUnits = 5L
            };

            KeySchemaElement kse1 = new KeySchemaElement
            {
                AttributeName = partitionkey,
                KeyType = "HASH"
            };

            KeySchemaElement kse2 = new KeySchemaElement
            {
                AttributeName = sortkey,
                KeyType = "RANGE"
            };

            List<KeySchemaElement> kses = new List<KeySchemaElement>
            {
                kse1,
                kse2
            };

            Projection p = new Projection
            {
                ProjectionType = "ALL"
            };

            var newIndex = new CreateGlobalSecondaryIndexAction()
            {
                IndexName = indexname,
                ProvisionedThroughput = pt,
                KeySchema = kses,
                Projection = p
            };

            GlobalSecondaryIndexUpdate update = new GlobalSecondaryIndexUpdate
            {
                Create = newIndex
            };

            List<GlobalSecondaryIndexUpdate> updates = new List<GlobalSecondaryIndexUpdate>
            {
                update
            };

            AttributeDefinition ad1;

            if (partitionkeytype == "string")
            {
                ad1 = new AttributeDefinition
                {
                    AttributeName = partitionkey,
                    AttributeType = "S"
                };
            }
            else
            {
                ad1 = new AttributeDefinition
                {
                    AttributeName = partitionkey,
                    AttributeType = "N"
                };
            }

            AttributeDefinition ad2;

            if (sortkeytype == "string")
            {
                ad2 = new AttributeDefinition
                {
                    AttributeName = sortkey,
                    AttributeType = "S"
                };
            }
            else
            {
                ad2 = new AttributeDefinition
                {
                    AttributeName = sortkey,
                    AttributeType = "N"
                };
            }

            UpdateTableRequest request = new UpdateTableRequest
            {
                TableName = table,
                AttributeDefinitions = {
                    ad1,
                    ad2
                },
                GlobalSecondaryIndexUpdates = updates
            };

            var response = await client.UpdateTableAsync(request);

            return response;
        }

        static void Main(string[] args)
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            var indexname = "";
            var mainkey = "";
            var mainkeytype = "";
            var secondarykey = "";
            var secondarykeytype = "";

            int i = 0;
            while (i < args.Length)
            {
                switch (args[i])
                {
                    case "-i":
                        i++;
                        indexname = args[i];
                        break;
                    case "-m":
                        i++;
                        mainkey = args[i];
                        break;
                    case "-k":
                        i++;
                        mainkeytype = args[i];
                        break;
                    case "-s":
                        i++;
                        secondarykey = args[i];
                        break;
                    case "-t":
                        i++;
                        secondarykeytype = args[i];
                        break;
                    default:
                        break;
                }

                i++;
            }

            bool empty = false;
            StringBuilder sb = new StringBuilder("You must supply a non-empty ");

            if (indexname == "")
            {
                empty = true;
                sb.Append("index name (-i INDEX), ");
            }
            
            if (mainkey == "")
            {
                empty = true;
                sb.Append("mainkey (-m PARTITION-KEY), ");
            }
            
            if (mainkeytype == "")
            {
                empty = true;
                sb.Append("main key type (-k TYPE), ");
            }

            if (secondarykey == "")
            {
                empty = true;
                sb.Append("secondary key (-s SORT-KEY), ");
            }
            
            if (secondarykeytype == "")
            {
                empty = true;
                sb.Append("secondary key type (-t TYPE), ");
            }

            if (empty)
            {
                Console.WriteLine(sb.ToString());
                return;
            }

            if ((mainkeytype != "string") && (mainkeytype != "number"))
            {
                Console.WriteLine("The main key type must be string or number");
                return;
            }

            if ((secondarykeytype != "string") && (secondarykeytype != "number"))
            {
                Console.WriteLine("The secondary key type must be string or number");
                return;
            }

            // Get default Region and table from config file
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                region = appSettings.Settings["Region"].Value;
                table = appSettings.Settings["Table"].Value;

                if ((region == "") || (table == ""))
                {
                    Console.WriteLine("You must specify Region and Table values in " + configfile);
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

            Task<UpdateTableResponse> response = AddIndexAsync(client, table, indexname, mainkey, mainkeytype, secondarykey, secondarykeytype);

            Console.WriteLine("Task status:   " + response.Status);
            Console.WriteLine("Result status: " + response.Result.HttpStatusCode);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.CreateIndex]