/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * This example performs the following tasks:
 *
 * 1. Create a database.
 * 2. Create a crawler.
 * 3. Get a crawler.
 * 4. Start a crawler.
 * 5. Get a database.
 * 6. Get tables.
 * 7. Create a job.
 * 8. Start a job run.
 * 9. List all jobs.
 * 10. Get job runs.
 * 11. Delete a job.
 * 12. Delete a database.
 * 13. Delete a crawler.
 *
 */

#include <iostream>
#include <aws/core/Aws.h>

namespace AwsDoc {
    namespace Glue {
    } // Glue
} // AwsDoc



#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {

    if (argc != 3) {
        std::cout << "Usage:\n" <<
                  "    <uploadFilePath> <saveFilePath>\n\n" <<
                  "Where:\n" <<
                  "   uploadFilePath - The path where the file is located (for example, C:/AWS/book2.pdf).\n" <<
                  "   saveFilePath - The path where the file is saved after it's " <<
                  "downloaded (for example, C:/AWS/book2.pdf). " << std::endl;
        return 1;
    }

    Aws::String objectPath = argv[1];
    Aws::String savePath = argv[2];

    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        AwsDoc::S3::S3_GettingStartedScenario(objectPath, savePath, clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


