/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "ItemTrackerHTTPHandler.h"
#include "RDSDataHandler.h"
#include "SES3EmailHandler.h"
#include "PocoHTTPServer.h"


static const Aws::String TABLE_NAME("items");

void runServerLessAurora(const Aws::String& database,
                         const Aws::String& resourceArn,
                         const Aws::String& secretArn,
                         const Aws::String& sesEmailAddress,
                         const Aws::Client::ClientConfiguration& clientConfiguration)
{
    AwsDoc::CrossService::RDSDataHandler rdsDataHandler(database, resourceArn,
                                                        secretArn, TABLE_NAME,
                                                        clientConfiguration);

    rdsDataHandler.initializeTable(false); // bool: recreate table.

    AwsDoc::CrossService::SES3EmailHandler sesEmailHandler(sesEmailAddress,
                                                           clientConfiguration);

    AwsDoc::CrossService::ItemTrackerHTTPHandler itemTrackerHttpServer(rdsDataHandler,
                                                                       sesEmailHandler);
    char* argv[1];
    char app_name[256];
    strncpy(app_name, "run_aurora_serverless", sizeof(app_name));
    argv[0] = app_name;
    AwsDoc::PocoImpl::PocoHTTPServer myServerApp(itemTrackerHttpServer);
    myServerApp.run(1, argv);

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
        Aws::String sesEmailAddress = argv[4];
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        runServerLessAurora(database, resourceArn, secretArn, sesEmailAddress, clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}