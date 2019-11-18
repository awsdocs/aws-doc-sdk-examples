//snippet-sourcedescription:[CreateNamedQueryExample.cs demonstrates how to create a named query.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
﻿using System;
using Amazon;
using Amazon.Athena.Model;
using Amazon.Athena;

namespace AthenaSamples1
{
    /**
    * CreateNamedQueryExample
    * -------------------------------------
    * This code shows how to create a named query.
    */
    class CreateNamedQueryExample
    {
        public static void Example()
        {
            // Create an Amazon Athena client
            var athenaConfig = new AmazonAthenaConfig
            {
                RegionEndpoint = RegionEndpoint.USEast1,
                Timeout = TimeSpan.FromMilliseconds(ExampleConstants.CLIENT_EXECUTION_TIMEOUT)
            };
            var athenaClient = new AmazonAthenaClient(config: athenaConfig);

            // Create the named query request.
            var createNamedQueryRequest = new CreateNamedQueryRequest()
            {
                Database = ExampleConstants.ATHENA_DEFAULT_DATABASE,
                QueryString = ExampleConstants.ATHENA_SAMPLE_QUERY,
                Description = "Sample Description",
                Name = "SampleQuery2",
            };

            // Call Athena to create the named query. If it fails, an exception is thrown.
            var createNamedQueryResponse = athenaClient.CreateNamedQuery(createNamedQueryRequest);
        }
    }
}
