using System;
using System.Configuration;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    class ListTables
    {
        static async Task<ListTablesResponse> ShowTablesAsync(IAmazonDynamoDB client)
        {
            var response = await client.ListTablesAsync(new ListTablesRequest { });

            return response;
        }

        static void Main(string[] args)
        {            
            var region = "";
            var configfile = "../../../app.config";

            // Get default region from config file
            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                region = appSettings.Settings["Region"].Value;

                if (region == "")
                {
                    Console.WriteLine("You must set a Region value in " + configfile);
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

            Task<ListTablesResponse> response = ShowTablesAsync(client);

            Console.WriteLine("Found " + response.Result.TableNames.Count.ToString() + " tables in " + region + " region:");

            foreach (var table in response.Result.TableNames)
            {
                Console.WriteLine("  " + table);
            }
        }
    }
}
