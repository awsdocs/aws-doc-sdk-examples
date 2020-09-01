using System;
using System.Configuration;
using System.Globalization;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDBCRUD
{
    class AddItem
    {        
        static async Task<bool> AddItemAsync(IAmazonDynamoDB client, string table, string keystring, string valuestring)
        {
            // Get individual keys and values
            string[] keys = keystring.Split(",");
            string[] values = valuestring.Split(",");

            if (keys.Length != values.Length)
            {
                Console.WriteLine("Unmatched number of keys and values");
                return false;
            }

            var theTable = Table.LoadTable(client, table);
            var item = new Document();

            for (int i = 0; i < keys.Length; i++)
            {
                // if the header contains the word "date", store the value as a long (number)
                if (keys[i].ToLower().Contains("date"))
                {
                    // The datetime format is:
                    // YYYY-MM-DD HH:MM:SS
                    DateTime MyDateTime = DateTime.ParseExact(values[i], "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

                    TimeSpan timeSpan = MyDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

                    item[keys[i]] = (long)timeSpan.TotalSeconds;
                }
                else
                {
                    // If it's a number, store it as such
                    try
                    {
                        int v = int.Parse(values[i]);
                        item[keys[i]] = v;
                    }
                    catch
                    {
                        item[keys[i]] = values[i];
                    }
                }
            }

            await theTable.PutItemAsync(item);

            return true;
        }

        static void Usage()
        {
            Console.WriteLine("Usage:");
            Console.WriteLine("AddItem.exe -k KEYS -v VALUES [-h]");
            Console.WriteLine("");
            Console.WriteLine("Both KEYS and VALUES are required");
            Console.WriteLine("Both should be a comma-separated list, and must have the same number of values");
            Console.WriteLine(" -h prints this message and quits");
        }

        static void Main(string[] args)
        {
            var configfile = "../../../app.config";
            var region = "";
            var table = "";
            var keys = "";
            var values = "";

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
                    case "-k":
                        i++;
                        keys = args[i];
                        break;
                    case "-v":
                        i++;
                        values = args[i];
                        break;
                    default:
                        break;
                }

                i++;
            }

            if ((keys == "") || (values == ""))
            {
                Console.WriteLine("You must supply a comma-separate list of keys (-k KEYS) and a comma-separated list of values (-v VALUES)");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            Task<bool> response = AddItemAsync(client, table, keys, values);

            Console.WriteLine("Added item to " + table + " in " + region);
        }
    }
}
