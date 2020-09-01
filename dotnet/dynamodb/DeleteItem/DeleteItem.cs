using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    class DeleteItem
    {
        static async Task<DeleteItemResponse> RemoveItemAsync(IAmazonDynamoDB client, string table, string id, string area)
        {
            var request = new DeleteItemRequest
            {
                TableName = table,
                Key = new Dictionary<string, AttributeValue>() 
                {
                    {
                        "ID",
                        new AttributeValue { S = id }
                    },
                    {
                        "Area",
                        new AttributeValue { S = area }
                    },
                }                
            };
            
            var response = await client.DeleteItemAsync(request);

            return response;
        }
     

        static void Main(string[] args)
        {
            var configfile = "../../../app.config";
            var region = "";
            var table = "";
            var partition = "";
            var sort = "";

            // Get default region and table from config file
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
                    Console.WriteLine("You must specify a Region and Table value in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            int i = 0;
            while (i < args.Length)
            {
                switch (args[i])
                {
                   case "-p":
                        i++;
                        partition = args[i];
                        break;
                    case "-s":
                        i++;
                        sort = args[i];
                        break;
                    default:
                        break;
                }

                i++;
            }

            if ((partition == "") || (sort == ""))
            {
                Console.WriteLine("You must supply a partition key (-p KEY) and sort key (-s KEY)");
               return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var resp = RemoveItemAsync(client, table, partition, sort);

            //    Task<DeleteItemResponse> response = RemoveItemAsync(debug, client, table, partition, sort);

            if (resp.Result.HttpStatusCode == HttpStatusCode.OK)
            {
                Console.WriteLine("Removed item from " + table + " table in " + region + " region");
            }
        }
    }
}
