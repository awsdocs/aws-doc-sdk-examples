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
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/acm/ACMClient.h>
#include <aws/acm/model/ImportCertificateRequest.h>
#include <fstream>
#include "acm_samples.h"

static bool FileExists(const char* fileName)
{
    std::ifstream ifile;
    ifile.open(fileName);

    if (ifile)
    {
        return true;
    }
    else
    {
        return false;
    }
}

// Helper function for Aws::ACM::ACMClient::ImportCertificate.
bool AwsDoc::ACM::ImportCertificate(const Aws::String& certificateFile,
                                    const Aws::String& privateKeyFile,
                                    const Aws::String& certificateChainFile,
                                    const Aws::Client::ClientConfiguration &clientConfiguration)
{
    if (!FileExists(certificateFile.c_str()))
    {
        std::cout << "Error: The certificate file '" << certificateFile <<
                  "' does not exist." << std::endl;

        return false;
    }

    if (!FileExists(privateKeyFile.c_str()))
    {
        std::cout << "Error: The private key file '" << privateKeyFile <<
                  "' does not exist." << std::endl;

        return false;
    }

    if (!FileExists(certificateChainFile.c_str()))
    {
        std::cout << "Error: The certificate chain file '"
                  << certificateChainFile << "' does not exist." << std::endl;

        return false;
    }

    std::ifstream cert_ifs(certificateFile.c_str());
    std::ifstream pk_ifs(privateKeyFile.c_str());
    std::ifstream cert_chain_ifs(certificateChainFile.c_str());

    Aws::String certificate;
    certificate.assign(std::istreambuf_iterator<char>(cert_ifs),
                       std::istreambuf_iterator<char>());

    Aws::String privateKey;
    privateKey.assign(std::istreambuf_iterator<char>(pk_ifs),
                      std::istreambuf_iterator<char>());

    Aws::String certificateChain;
    certificateChain.assign(std::istreambuf_iterator<char>(cert_chain_ifs),
                            std::istreambuf_iterator<char>());

    Aws::ACM::ACMClient acm_client(clientConfiguration);

    Aws::ACM::Model::ImportCertificateRequest request;

    request.WithCertificate(Aws::Utils::ByteBuffer((unsigned char*)
                                                           certificate.c_str(), certificate.size()))
            .WithPrivateKey(Aws::Utils::ByteBuffer((unsigned char*)
                                                           privateKey.c_str(), privateKey.size()))
            .WithCertificateChain(Aws::Utils::ByteBuffer((unsigned char*)
                                                                 certificateChain.c_str(), certificateChain.size()));

    Aws::ACM::Model::ImportCertificateOutcome outcome =
            acm_client.ImportCertificate(request);

    if (!outcome.IsSuccess())
    {
        std::cout << "Error: ImportCertificate: " <<
                  outcome.GetError().GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Success: Certificate associated with ARN '" <<
                  outcome.GetResult().GetCertificateArn() << "' imported."
                  << std::endl;

        return true;
    }
}

/*
*
*  main function
*
*  Usage: 'run_'
*
*  Prerequisites: .
*
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD

