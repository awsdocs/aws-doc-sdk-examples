using System;
using Amazon;
using Amazon.Athena;

namespace AthenaSamples1
{
    class AthenaClient
    {
        // This code shows how to create and configure an Amazon Athena client.
        public static void Example()
        {
            // Create an Amazon Athena client
            var athenaConfig = new AmazonAthenaConfig
            {
                RegionEndpoint = RegionEndpoint.USEast1,
                Timeout = TimeSpan.FromMilliseconds(ExampleConstants.CLIENT_EXECUTION_TIMEOUT)
            };
            var athenaClient = new AmazonAthenaClient(config: athenaConfig);
        }
    }
}

