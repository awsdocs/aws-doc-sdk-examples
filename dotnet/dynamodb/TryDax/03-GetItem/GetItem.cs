// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.TryDaxGetItem]
using System;
using System.Collections.Generic;
using System.Configuration;
using Amazon;
using Amazon.DAX;
using Amazon.DAX.Model;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Runtime;

namespace DynamoDBCRUD
{
    public class TryDaxGetItem
    {
        public static readonly string _tableName = "TryDaxTable";

        public static async void GetKeys(AmazonDAXClient client)
        {
            var req = new DescribeClustersRequest();
            var resp = await client.DescribeClustersAsync(req);

            if (null == resp.Clusters || resp.Clusters.Count < 1)
            {
                Console.WriteLine("Could not find any DAX clusters");
                return;
            }

            foreach(var cluster in resp.Clusters)
            {
                Console.WriteLine("Cluster: " + cluster.ClusterName);
                Console.WriteLine("Endpoint: " + cluster.ClusterDiscoveryEndpoint);
            }


        }

        static void Main(string[] args)
        {
            var endpoint = "";

            // Get endpoint from config file.
            var configfile = "app.config";

            var efm = new ExeConfigurationFileMap
            {
                ExeConfigFilename = configfile
            };

            Configuration configuration = ConfigurationManager.OpenMappedExeConfiguration(efm, ConfigurationUserLevel.None);

            if (configuration.HasFile)
            {
                AppSettingsSection appSettings = configuration.AppSettings;
                endpoint = appSettings.Settings["Endpoint"].Value;

                if (endpoint == "")
                {
                    Console.WriteLine("You must specify an Endpoint value in " + configfile);
                    return;
                }
            }
            else
            {
                Console.WriteLine("Could not find " + configfile);
                return;
            }

            // endpoint must be an Amazon Virtual Private Cloud (Amazon VPC) endpoint, such as:
            //   myDAXcluster.2cmrwl.clustercfg.dax.use1.cache.amazonaws.com:8111

            String hostName = endpoint.Split(':')[0];
            int port = Int32.Parse(endpoint.Split(':')[1]);
            Console.WriteLine("Using DAX client - hostname=" + hostName + ", port=" + port);

            var clientConfig = new AmazonDAXConfig
            {
                ProxyHost = hostName,
                ProxyPort = port
            };
            
            var client = new AmazonDAXClient(clientConfig);

            GetKeys(client);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.TryDaxGetItem]