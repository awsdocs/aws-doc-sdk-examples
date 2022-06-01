// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace AddItems
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Globalization;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnetv3.AddItemsExample]

    /// <summary>
    /// Add items to an Amazon DynamoDB table with information loaded from a
    /// comma-delimited text file. The example was created using the AWS SDK
    /// for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class AddItems
    {
        /// <summary>
        /// Retrieves the configuration file, parses the command line
        /// arguments, and displays the contents. Then it calls the
        /// AddItemsAsync method to add the list of new items to the
        /// DynamoDB table.
        /// </summary>
        /// <param name="args">Command line arguments. The only valid argument
        /// for this application is -d for debug mode.</param>
        public static async Task Main(string[] args)
        {
            var configfile = "app.config";
            string region;
            string table;
            string filename;
            string index;
            bool debug = false;

            // Get default Region and table from config file.
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
                index = appSettings.Settings["Index"].Value;
                filename = appSettings.Settings["Filename"].Value;

                if (string.IsNullOrEmpty(region) || string.IsNullOrEmpty(table) || string.IsNullOrEmpty(index) || string.IsNullOrEmpty(filename))
                {
                    Console.WriteLine($"You must specify a Region, Table, Index, and Filename in {configfile}");
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
                }

                i++;
            }

            // Make sure index is an int >= 0.
            int indexVal;

            try
            {
                indexVal = int.Parse(index);
            }
            catch
            {
                Console.WriteLine($"Could not parse {index} as an int.");
                return;
            }

            if (indexVal < 0)
            {
                Console.WriteLine($"The index value {index} is less than zero.");
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            // Open the file.
            // filename is the name of the .csv file that contains customer data.
            // Column1,...,ColumnN
            // in lines 2...N
            // Read the file and display it line by line.
            System.IO.StreamReader file =
                new System.IO.StreamReader(filename);

            DebugPrint(debug, "Opened file " + filename);

            // Store up to 25 at a time in an array.
            string[] inputs = new string[26];

            // Get column names from the first line.
            file.ReadLine();

            string line;
            var input = 1;

            while (((line = file.ReadLine()) != null) && (input < 26))
            {
                inputs[input] = line;
                input++;
            }

            await AddItemsAsync(debug, client, table, inputs, indexVal);

            Console.WriteLine("Done");
        }

        /// <summary>
        /// Adds a list of new items to a DynamoDB table.
        /// </summary>
        /// <param name="debug">A boolean value that is true if the application
        /// is in debug mode.</param>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="table">The name of the DynamoDB table to which the
        /// items will be added.</param>
        /// <param name="inputs">The list of strings that represent the new
        /// items to be added to the table.</param>
        /// <param name="index">An integer value that represents the initial
        /// index number for new items.</param>
        /// <returns>A BatchWriteItemResponse object that contains the
        /// results of the BatchWriteItemsAsync call.</returns>
        public static async Task<BatchWriteItemResponse> AddItemsAsync(
            bool debug,
            IAmazonDynamoDB client,
            string table,
            string[] inputs,
            int index)
        {
            var writeRequests = new List<WriteRequest>();

            string[] headers = inputs[0].Split(",");
            int numcolumns = headers.Length;

            string line;

            // Read the rest of the file, line by line.
            for (int input = 1; input < inputs.Length; input++)
            {
                line = inputs[input];

                // Split line into columns.
                string[] values = line.Split(',');

                // If we don't have the right number of parts, something's wrong.
                if (values.Length != numcolumns)
                {
                    Console.WriteLine("Did not have " + numcolumns.ToString() + " columns in: ");
                    Console.WriteLine(line);
                    return null;
                }

                var item = new Dictionary<string, AttributeValue>
                {
                    { "ID", new AttributeValue { S = index.ToString() } },
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
                        // The DateTime format is:
                        // YYYY-MM-DD HH:MM:SS
                        DateTime myDateTime = DateTime.ParseExact(values[i], "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

                        TimeSpan timeSpan = myDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

                        item.Add(headers[i], new AttributeValue { N = ((long)timeSpan.TotalSeconds).ToString() });
                        DebugPrint(debug, "Set " + headers[i] + " int (long) attribute to " + ((long)timeSpan.TotalSeconds).ToString());
                    }
                    else
                    {
                        item.Add(headers[i], new AttributeValue { S = values[i] });
                        DebugPrint(debug, "Set " + headers[i] + " string attribute to " + values[i]);
                    }
                }

                DebugPrint(debug, string.Empty);

                index++;

                WriteRequest putRequest = new WriteRequest(new PutRequest(item));

                writeRequests.Add(putRequest);
            }

            var requestItems = new Dictionary<string, List<WriteRequest>>
            {
                { table, writeRequests },
            };

            var request = new BatchWriteItemRequest
            {
                ReturnConsumedCapacity = "TOTAL",
                RequestItems = requestItems,
            };

            var response = await client.BatchWriteItemAsync(request);

            return response;
        }

        /// <summary>
        /// If in debugging mode, print the contents of the parameter string.
        /// </summary>
        /// <param name="debug">A boolean value indicating whether the
        /// application is in debug mode.</param>
        /// <param name="s">The value to print to the console.</param>
        public static void DebugPrint(bool debug, string s)
        {
            if (debug)
            {
                Console.WriteLine(s);
            }
        }
    }

    // snippet-end:[dynamodb.dotnetv3.AddItemsExample]
}
