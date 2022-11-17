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
#include <aws/cloudformation/CloudFormationClient.h>
#include <aws/cloudformation/model/CreateStackRequest.h>
#include <aws/cloudformation/model/DescribeStacksRequest.h>
#include <vector>
#include <fstream>

namespace AwsDoc {
    namespace Glue {
        static const Aws::String STACK_NAME("doc-example-glue-scenario-stack");

        Aws::String askQuestion(const Aws::String &string);

        std::map<Aws::String, Aws::String> createCloudFormationResource(
                const Aws::Client::ClientConfiguration &clientConfig,
                const Aws::String &templateFilePath);
    } // Glue
} // AwsDoc



#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    Aws::SDKOptions options;

    Aws::InitAPI(options);

    {
        Aws::String roleName;
        Aws::String bucketName;
        Aws::Client::ClientConfiguration clientConfig;
        if (argc == 1) {
            Aws::String answer = AwsDoc::Glue::askQuestion(
                    "Create the resources using");

            if (answer == "y") {
                std::map<Aws::String, Aws::String> result = AwsDoc::Glue::createCloudFormationResource(
                        clientConfig,
                        CLOUD_FORMATION_TEMPLATE_FILE); // defined in CMakeLists.txt

                if (!result.empty())
                {

                }


            }

        }

    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


Aws::String AwsDoc::Glue::askQuestion(const Aws::String &string) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
    } while (result.empty());

    return result;
}

std::map<Aws::String, Aws::String>
AwsDoc::Glue::createCloudFormationResource(
        const Aws::Client::ClientConfiguration &clientConfig,
        const Aws::String &templateFilePath) {
    std::map<Aws::String, Aws::String> result;
    Aws::CloudFormation::CloudFormationClient client(clientConfig);

    Aws::CloudFormation::Model::CreateStackRequest request;

    std::ifstream ifstream(templateFilePath);
    if (!ifstream)
    {
        std::cerr << "Could not load file '" << templateFilePath << "'" << std::endl;
        return result;
    }
    std::ostringstream templateStream;
    templateStream << ifstream.rdbuf();
    request.SetTemplateBody(templateStream.str());
    request.SetStackName(STACK_NAME);
    request.SetCapabilities({Aws::CloudFormation::Model::Capability::CAPABILITY_IAM});

    Aws::CloudFormation::Model::CreateStackOutcome outcome = client.CreateStack(request);

    if (outcome.IsSuccess())
    {
        Aws::CloudFormation::Model::DescribeStacksRequest waitRequest;
        waitRequest.SetStackName(STACK_NAME);


        for (int i = 0; i < 20; ++i)
        {
            Aws::CloudFormation::Model::DescribeStacksOutcome waitOutcome = client.DescribeStacks(waitRequest);
            if (waitOutcome.IsSuccess())
            {
                // to be continued
                std::cout << "wait result " << waitOutcome.GetResult().GetStacks();
            }
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }

    }

    return result;
}
