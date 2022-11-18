/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 *  serverless_aurora.cpp
 *
 *  The code in this file contains a main routine for running the example as an application.
 *
 * To run the example, refer to the instructions in the README.
 *
 */

#include "ItemTrackerHTTPHandler.h"
#include "RDSDataHandler.h"
#include "SESV2EmailHandler.h"
#include "PocoHTTPServer.h"

static const Aws::String TABLE_NAME("items");

//! Routine which runs the Amazon Aurora Serverless example as an HTTP server using the Poco library.
/*!
 \sa runServerLessAurora
 \param database: The Amazon Relational Database Service (Amazon RDS) database name.
 \param resourceArn: The Amazon RDS database Amazon Resource Name (ARN).
 \param secretArn: The AWS Secrets Manager database ARN.
 \param sesEmailAddress: The Amazon Simple Email Service (Amazon SES) verified email address.
 \param clientConfig Aws client configuration.
 \return void:
 */
void runServerLessAurora(const Aws::String &database,
                         const Aws::String &resourceArn,
                         const Aws::String &secretArn,
                         const Aws::String &sesEmailAddress,
                         const Aws::Client::ClientConfiguration &clientConfiguration) {
    AwsDoc::CrossService::RDSDataHandler rdsDataHandler(database, resourceArn,
                                                        secretArn, TABLE_NAME,
                                                        clientConfiguration);

    rdsDataHandler.initializeTable(false); // bool: Recreate table.

    AwsDoc::CrossService::SESV2EmailHandler sesEmailHandler(sesEmailAddress,
                                                            clientConfiguration);

    AwsDoc::CrossService::ItemTrackerHTTPHandler itemTrackerHttpServer(rdsDataHandler,
                                                                       sesEmailHandler);
    char *argv[1];
    char app_name[256];
    strncpy(app_name, "run_aurora_serverless", sizeof(app_name));
    argv[0] = app_name;
    AwsDoc::PocoImpl::PocoHTTPServer myServerApp(itemTrackerHttpServer);
    myServerApp.run(1, argv);
}

/*
 *
 *  main function
 *
 *  Prerequisites: See the accompanying README.
 *
 * Usage: 'run_serverless_aurora <database> <resource_arn> <secret_arn> <email>'
 *
 */

int main(int argc, char **argv) {
    if (argc != 5) {
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
        runServerLessAurora(database, resourceArn, secretArn, sesEmailAddress,
                            clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}
