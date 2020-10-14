// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.AddItems]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Globalization;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class AddItems
    {
        static void DebugPrint(bool debug, string s)
        {
            if(debug)
            {
                Console.WriteLine(s);
            }
        }
        public static async Task<BatchWriteItemResponse> AddItemsAsync(bool debug, IAmazonDynamoDB client, string table, string[] inputs, int index)
        {
            var writeRequests = new List<WriteRequest>();
                        
            string[] headers = inputs[0].Split(",");
            int numcolumns = headers.Length;

           string line;

            // Read the rest of the file, line by line
            for (int input = 1; input < inputs.Length; input++)
            {
                line = inputs[input];

                // Split line into columns
                string[] values = line.Split(',');

                // If we don't have the right number of parts, something's wrong
                if (values.Length != numcolumns)
                {
                    Console.WriteLine("Did not have " + numcolumns.ToString() + " columns in: ");
                    Console.WriteLine(line);
                    return null;
                }

                var item = new Dictionary<string, AttributeValue>
                {
                    { "ID", new AttributeValue { S = index.ToString() } }
                };

                DebugPrint(debug, "Set ID string attribute to " + index.ToString());

                for (int i = 0; i < numcolumns; i++)
                {
                    if ((headers[i] == "Customer_ID") || (headers[i] == "Order_ID") || (headers[i] == "Order_Customer") || (headers[i] == "Order_Product") || (headers[i] == "Product_ID") || (headers[i] == "Product_Quantity") || (headers[i] == "Product_Cost"))
                    {
                        item.Add(headers[i], new AttributeValue { N = values[i] });
                        DebugPrint(debug, "Set " + headers[i] + " int attribute to " + values[i]);
                    }
                    else if (headers[i] == "Order_Date")
                    {
                        // The datetime format is:
                        // YYYY-MM-DD HH:MM:SS
                        DateTime MyDateTime = DateTime.ParseExact(values[i], "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

                        TimeSpan timeSpan = MyDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

                        item.Add(headers[i], new AttributeValue { N = ((long)timeSpan.TotalSeconds).ToString() });
                        DebugPrint(debug, "Set " + headers[i] + " int (long) attribute to " + ((long)timeSpan.TotalSeconds).ToString());
                    }
                    else
                    {
                        item.Add(headers[i], new AttributeValue { S = values[i] });
                        DebugPrint(debug, "Set " + headers[i] + " string attribute to " + values[i]);
                    }
                }

                DebugPrint(debug, "");

                index++;

                WriteRequest putRequest = new WriteRequest(new PutRequest(item));

                writeRequests.Add(putRequest);
            }

            var requestItems = new Dictionary<string, List<WriteRequest>>
            {
                { table, writeRequests }
            };

            var request = new BatchWriteItemRequest
            {
                ReturnConsumedCapacity = "TOTAL",
                RequestItems = requestItems
            };

            var response = await client.BatchWriteItemAsync(request);

            return response;
        }
        

        //static async Task Main(string[] args)
        static void Main(string[] args)
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            string filename = "";
            string index = "";
            bool debug = false;
            
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
                index = appSettings.Settings["Index"].Value;
                filename = appSettings.Settings["Filename"].Value;

                if ((region == "") || (table == "") || (index == "") || (filename == ""))
                {
                    Console.WriteLine("You must specify a Region, Table, Index, and Filename in " + configfile);
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
                    case "-d":
                        debug = true;
                        break;
                    default:
                        break;
                }

                i++;
            }

            // Make sure index is an int >= 0
            int indexVal;

            try
            {
                indexVal = int.Parse(index);
            }
            catch
            {
                Console.WriteLine("Could not parse " + index + " as an int");
                return;
            }

            if (indexVal < 0)
            {
                Console.WriteLine("The index value " + index + " is less than zero");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            // Open the file
            // filename is the name of the .csv file that contains customer data
            // Column1,...,ColumnN
            // in lines 2...N
            // Read the file and display it line by line.  
            System.IO.StreamReader file =
                new System.IO.StreamReader(filename);

            DebugPrint(debug, "Opened file " + filename);

            // Store up to 25 at a time in an array
            string[] inputs = new string[26];

            // Get column names from the first line
            string headerline = file.ReadLine();            

            var line = "";
            var input = 1;

            while (((line = file.ReadLine()) != null) && (input < 26))
            {
                inputs[input] = line;
                input++;
            }
            
            AddItemsAsync(debug, client, table, inputs, indexVal).Wait();

            Console.WriteLine("Done");            
        }
    }
}
// snippet-end:[dynamodb.dotnet35.AddItems]
