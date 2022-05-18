// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.GetOrdersInDateRangeGSIExample]
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Globalization;
using System.Threading.Tasks;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace GetOrdersInDateRangeGSI
{
    public class GetOrdersInDateRangeGsi
    {
        // Get the orders made in range from start to end
        // DynamoDB equivalent of:
        //   select * from Orders where Order_Date between '2020-05-04 05:00:00' and '2020-08-13 09:00:00'
        public static async Task<QueryResponse> GetOrdersInDateRangeAsync(IAmazonDynamoDB client, string table, string index, string start, string end)
        {
            // Convert start and end strings to longs
            var startDateTime = DateTime.ParseExact(start, "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);
            var endDateTime = DateTime.ParseExact(end, "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

            TimeSpan startTimeSpan = startDateTime - new DateTime(1970, 1, 1, 0, 0, 0);
            TimeSpan endTimeSpan = endDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

            var begin = ((long)startTimeSpan.TotalSeconds).ToString();
            var finish = ((long)endTimeSpan.TotalSeconds).ToString();

            var response = await client.QueryAsync(new QueryRequest
            {
                TableName = table,
                IndexName = index,
                KeyConditionExpression = "Area = :v_area and Order_Date between :v_begin and :v_end",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":v_area", new AttributeValue { S =  "Order" }},
                    {":v_begin", new AttributeValue { N =  begin }},
                    {":v_end", new AttributeValue { N = finish }}
                },
                ScanIndexForward = true
            });

            return response;
        }

        static void Main()
        {
            var configfile = "app.config";
            var region = "";
            var table = "";
            var index = "";
            var starttime = "";
            var endtime = "";

            // Get default values from config file
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
                starttime = appSettings.Settings["StartTime"].Value;
                endtime = appSettings.Settings["EndTime"].Value;
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            // Make sure we have a table, Region, start time and end time
            if ((region == "") || (table == "") || (index == "") || (starttime == "") || (endtime == ""))
            {
                Console.WriteLine("You must specify Region, Table, Index, StartTime, and EndTime values in " + configfile);
                return;
            }

            var newRegion = RegionEndpoint.GetBySystemName(region);
            IAmazonDynamoDB client = new AmazonDynamoDBClient(newRegion);

            var response = GetOrdersInDateRangeAsync(client, table, index, starttime, endtime);

            // To adjust date/time value
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

            foreach (var item in response.Result.Items)
            {
                foreach (string attr in item.Keys)
                {
                    if (item[attr].S != null)
                    {
                        Console.WriteLine(attr + ": " + item[attr].S);
                    }
                    else if (item[attr].N != null)
                    {
                        // If the attribute contains the string "date", process it differently
                        if (attr.ToLower().Contains("date"))
                        {
                            long span = long.Parse(item[attr].N);
                            DateTime theDate = epoch.AddSeconds(span);

                            Console.WriteLine(attr + ": " + theDate.ToLongDateString());
                        }
                        else
                        {
                            Console.WriteLine(attr + ": " + item[attr].N);
                        }
                    }
                }

                Console.WriteLine("");
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.GetOrdersInDateRangeGSIExample]
