// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
get_server_cert.cpp demonstrates how to retrieve information about an IAM SSL/TLS server certificate.]
*/



//snippet-start:[iam.cpp.get_server_cert.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetServerCertificateRequest.h>
#include <aws/iam/model/GetServerCertificateResult.h>
#include <iostream>
//snippet-end:[iam.cpp.get_server_cert.inc]

/**
 * Gets a server certificate, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: get_server_cert <cert_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String cert_name(argv[1]);

        // snippet-start:[iam.cpp.get_server_cert.code]
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::GetServerCertificateRequest request;
        request.SetServerCertificateName(cert_name);

        auto outcome = iam.GetServerCertificate(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error getting server certificate " << cert_name <<
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
        // snippet-end:[iam.cpp.get_server_cert.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

