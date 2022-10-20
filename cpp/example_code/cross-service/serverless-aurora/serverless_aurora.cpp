/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "ItemTrackerHTTPServer.h"
#include "RDSDataHandler.h"

static const Aws::String TABLE_NAME("items");

void runServerLessAurora(const Aws::String& database,
                         const Aws::String& resourceArn,
                         const Aws::String& secretArn,
                         const Aws::Client::ClientConfiguration& clientConfiguration)
{
    AwsDoc::CrossService::RDSDataHandler rdsDataHandler(database, resourceArn,
                                                        secretArn, TABLE_NAME,
                                                        clientConfiguration);
    AwsDoc::CrossService::ItemTrackerHTTPServer itemTrackerHttpServer(rdsDataHandler);

    char* argv[1] = {"run_serverless_aurora"};
    itemTrackerHttpServer.run(1, argv);
}

int main(int argc, char** argv)
{
    if (argc != 5)
    {
        std::cout << "Usage: run_serverless_aurora <database> <resource_arn>"
                     << "<secret_arn> <email>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String database = argv[1];
        Aws::String resourceArn = argv[2];
        Aws::String secretArn = argv[3];
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        runServerLessAurora(database, resourceArn, secretArn, clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}