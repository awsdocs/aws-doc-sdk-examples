// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

// snippet-start:[ec2.cpp.create_key_pair.inc]
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateKeyPairRequest.h>
#include <iostream>
#include <fstream>
// snippet-end:[ec2.cpp.create_key_pair.inc]
#include "ec2_samples.h"

// snippet-start:[cpp.example_code.ec2,CreateKeyPair]
//! Create an Amazon Elastic Compute Cloud (Amazon EC2) instance key pair.
/*!
  \param keyPairName: A name for a key pair.
  \param keyFilePath: File path where the credentials are stored. Ignored if it is an empty string;
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::createKeyPair(const Aws::String &keyPairName, const Aws::String &keyFilePath,
                                const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.create_key_pair.code]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);
    Aws::EC2::Model::CreateKeyPairRequest request;
    request.SetKeyName(keyPairName);

    Aws::EC2::Model::CreateKeyPairOutcome outcome = ec2Client.CreateKeyPair(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to create key pair - "  << keyPairName << ". " <<
                  outcome.GetError().GetMessage() << std::endl;
    } else {
        std::cout << "Successfully created key pair named " <<
                  keyPairName << std::endl;
        if (!keyFilePath.empty()) {
            std::ofstream keyFile(keyFilePath.c_str());
            keyFile << outcome.GetResult().GetKeyMaterial();
            keyFile.close();
            std::cout << "Keys written to the file " <<
                      keyFilePath << std::endl;
        }

    }
    // snippet-end:[ec2.cpp.create_key_pair.code]

    return outcome.IsSuccess();

}
// snippet-end:[cpp.example_code.ec2,CreateKeyPair]

/*
*  main function
*
*  Usage: 'run_create_key_pair <key_pair_name> [key_file_path]'
*
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 2) {
        std::cout << "Usage: 'run_create_key_pair <key_pair_name> [key_file_path]"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::String keyPairName = argv[1];
        Aws::String keyFilePath;
        if (argc > 2) {
            keyFilePath = argv[2];
        }

        AwsDoc::EC2::createKeyPair(keyPairName, keyFilePath, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
