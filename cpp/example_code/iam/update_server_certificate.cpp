/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html.
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates updating a server certificate name.
 *
 */

//snippet-start:[iam.cpp.update_server_cert.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateServerCertificateRequest.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.update_server_cert.inc]

//! Updates a server certificate name.
/*!
  \sa updateServerCertificate()
  \param currentCertificateName: The IAM server certificate's current name.
  \param newCertificateName: The IAM server certificate's new name.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/
// snippet-start:[iam.cpp.update_server_cert.code]
bool AwsDoc::IAM::updateServerCertificate(const Aws::String &currentCertificateName,
                                          const Aws::String &newCertificateName,
                                          const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iam(clientConfig);
    Aws::IAM::Model::UpdateServerCertificateRequest request;
    request.SetServerCertificateName(currentCertificateName);
    request.SetNewServerCertificateName(newCertificateName);

    auto outcome = iam.UpdateServerCertificate(request);
    bool result = true;
    if (outcome.IsSuccess()) {
        std::cout << "Server certificate " << currentCertificateName
                  << " successfully renamed as " << newCertificateName
                  << std::endl;
    }
    else {
        if (outcome.GetError().GetErrorType() != Aws::IAM::IAMErrors::NO_SUCH_ENTITY) {
            std::cerr << "Error changing name of server certificate " <<
                      currentCertificateName << " to " << newCertificateName << ":" <<
                      outcome.GetError().GetMessage() << std::endl;
            result = false;
        }
        else {
            std::cout << "Certificate '" << currentCertificateName
                      << "' not found." << std::endl;
        }
    }

    return result;
}
// snippet-end:[iam.cpp.update_server_cert.code]

/*
 *
 *  main function
 *
 * Prerequisites: Existing server certificate.
 *
 * Usage: 'run_update_server_certificate <old_cert_name> <new_cert_name>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout <<
            "Usage: run_update_server_certificate <old_cert_name> <new_cert_name>"
            << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String currentCertificateName(argv[1]);
        Aws::String newCertificateName(argv[2]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::updateServerCertificate(currentCertificateName, newCertificateName,
                                             clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD
