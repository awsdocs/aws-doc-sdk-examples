using System;
using System.Configuration;
using System.Text;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    class ListItems
    {
        static async Task<ScanResponse> GetItemsAsync(IAmazonDynamoDB client, string table)
        {
            var response = await client.ScanAsync(new ScanRequest
            {
                TableName = table
            });

            return response;
        }

        static void Main(string[] args)
        {
            var configfile = "../../../app.config";
            var region = "";
            var table = "";

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
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            var empty = false;
            var sb = new StringBuilder("You must supply a non-empty ");

            if (table == "")
            {
                empty = true;
                sb.Append("table name (-t TABLE), ");
            }
            
            if (region == "")
            {
                empty = true;
                sb.Append("region -r (REGION)");
            }
            
            if (empty)
            {
                Console.WriteLine(sb.ToString());
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var response = GetItemsAsync(client, table);

            Console.WriteLine("Found " + response.Result.Items.Count.ToString() + " items in table " + table + " in region " + region + ":\n");

            StringBuilder output;

            foreach (var item in response.Result.Items)
            {
                output = new StringBuilder();

                foreach (string attr in item.Keys)
                {
                    if (item[attr].S != null)
                    {
                        output.Append(attr + ": " + item[attr].S + ", ");
                    }
                    else if (item[attr].N != null)
                    {
                        output.Append(attr + ": " + item[attr].N.ToString() + ", ");
                    }
                }

                Console.WriteLine(output.ToString());
            }
        }
    }
}
