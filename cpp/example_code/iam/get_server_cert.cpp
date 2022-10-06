/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[iam.cpp.get_server_cert.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetServerCertificateRequest.h>
#include <aws/iam/model/GetServerCertificateResult.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.get_server_cert.inc]


/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates getting a server certificate.
 *
 */

// snippet-start:[iam.cpp.get_server_cert.code]
//! Gets a server certificate.
/*!
  \sa getServerCertificate()
  \param certificateName: The certificate name.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

bool AwsDoc::IAM::getServerCertificate(const Aws::String& certificateName,
                          const Aws::Client::ClientConfiguration &clientConfig)
{
    Aws::IAM::IAMClient iam;
    Aws::IAM::Model::GetServerCertificateRequest request;
    request.SetServerCertificateName(certificateName);

    auto outcome = iam.GetServerCertificate(request);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error getting server certificate " << certificateName <<
                  ": " << outcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        const auto &certificate = outcome.GetResult().GetServerCertificate();
        std::cout << "Name: " <<
                  certificate.GetServerCertificateMetadata().GetServerCertificateName()
                  << std::endl << "Body: " << certificate.GetCertificateBody() <<
                  std::endl << "Chain: " << certificate.GetCertificateChain() <<
                  std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.get_server_cert.code]

/*
 *
 *  main function
 *
 * Usage: 'run_get_server_cert <cert_name>'
 *
 */

#ifndef TESTING_BUILD
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: run_get_server_cert <cert_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String certificateName(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::getServerCertificate(certificateName, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}
#endif  // TESTING_BUILD

